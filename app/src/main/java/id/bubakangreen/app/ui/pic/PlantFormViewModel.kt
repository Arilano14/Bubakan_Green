package id.bubakangreen.app.ui.pic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlantFormState(
    val location: Location? = null,
    val masterPlants: List<MasterPlant> = emptyList(),
    val existingLocationPlants: List<LocationPlant> = emptyList(),
    val selectedMasterPlantId: String = "",
    val quantityNote: String = "",
    val featuredForQr: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class PlantFormViewModel(
    private val locationId: String,
    private val locationRepository: LocationRepository,
    private val plantRepository: PlantRepository,
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PlantFormState>>(UiState.Loading)
    val uiState: StateFlow<UiState<PlantFormState>> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(PlantFormState())
    val formState: StateFlow<PlantFormState> = _formState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val locResult = locationRepository.getLocationById(locationId).firstOrNull()
            val location = (locResult as? Result.Success)?.data

            val masterResult = plantRepository.getAllMasterPlants().firstOrNull()
            val masterPlants = (masterResult as? Result.Success)?.data ?: emptyList()

            val locPlantsResult = plantRepository.getPlantsAtLocation(locationId).firstOrNull()
            val locPlants = (locPlantsResult as? Result.Success)?.data ?: emptyList()

            val initialForm = PlantFormState(
                location = location,
                masterPlants = masterPlants,
                existingLocationPlants = locPlants,
                selectedMasterPlantId = masterPlants.firstOrNull()?.id ?: ""
            )
            _formState.value = initialForm
            _uiState.value = UiState.Success(initialForm)
        }
    }

    fun onMasterPlantSelect(plantId: String) {
        _formState.update { it.copy(selectedMasterPlantId = plantId) }
    }

    fun onQuantityNoteChange(note: String) {
        _formState.update { it.copy(quantityNote = note) }
    }

    fun onFeaturedForQrToggle(featured: Boolean) {
        _formState.update { it.copy(featuredForQr = featured) }
    }

    fun addPlantToLocation(picUid: String) {
        val current = _formState.value
        if (current.selectedMasterPlantId.isBlank()) {
            _formState.update { it.copy(errorMessage = "Pilih tanaman dari katalog terlebih dahulu.") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, errorMessage = null) }

            val newPlant = LocationPlant(
                id = "LOC_PLANT_${System.currentTimeMillis()}",
                locationId = locationId,
                masterPlantId = current.selectedMasterPlantId,
                quantityNote = current.quantityNote.ifBlank { null },
                featuredForQr = current.featuredForQr
            )

            when (val result = plantRepository.addPlantToLocation(newPlant)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "PLANT_ADDED_TO_LOCATION",
                            targetEntityId = newPlant.id,
                            targetEntityType = "LOCATION_PLANT",
                            actorUid = picUid,
                            actorRole = "PIC",
                            details = "Menambahkan tanaman ke kebun $locationId"
                        )
                    )
                    loadData()
                    _formState.update { it.copy(isSaving = false, quantityNote = "", errorMessage = null) }
                }
                is Result.Error -> {
                    _formState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.message ?: "Gagal menambahkan tanaman."
                        )
                    }
                }
            }
        }
    }

    fun removePlant(plantId: String, picUid: String) {
        viewModelScope.launch {
            when (plantRepository.removePlantFromLocation(plantId)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "PLANT_REMOVED_FROM_LOCATION",
                            targetEntityId = plantId,
                            targetEntityType = "LOCATION_PLANT",
                            actorUid = picUid,
                            actorRole = "PIC"
                        )
                    )
                    loadData()
                }
                is Result.Error -> {}
            }
        }
    }
}
