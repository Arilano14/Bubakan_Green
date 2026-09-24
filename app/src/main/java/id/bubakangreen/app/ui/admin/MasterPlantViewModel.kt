package id.bubakangreen.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MasterPlantFormState(
    val plantId: String? = null,
    val nameId: String = "",
    val nameLatin: String = "",
    val nameMandarin: String = "",
    val pinyin: String = "",
    val description: String = "",
    val primaryPhotoUrl: String = "",
    val mandarinAudioUrl: String = "",
    val isSaving: Boolean = false,
    val validationError: String? = null
)

class MasterPlantViewModel(
    private val plantRepository: PlantRepository,
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MasterPlantFormState())
    val state: StateFlow<MasterPlantFormState> = _state.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent: SharedFlow<Unit> = _successEvent.asSharedFlow()

    fun loadPlant(plantId: String?) {
        if (plantId.isNullOrBlank()) return
        viewModelScope.launch {
            val result = plantRepository.getMasterPlantById(plantId).firstOrNull()
            if (result is Result.Success && result.data != null) {
                val p = result.data
                _state.update {
                    it.copy(
                        plantId = p.id,
                        nameId = p.nameId,
                        nameLatin = p.nameLatin,
                        nameMandarin = p.nameMandarin ?: "",
                        pinyin = p.pinyin ?: "",
                        description = p.description,
                        primaryPhotoUrl = p.primaryPhotoUrl ?: "",
                        mandarinAudioUrl = p.mandarinAudioUrl ?: ""
                    )
                }
            }
        }
    }

    fun onNameIdChange(value: String) = _state.update { it.copy(nameId = value, validationError = null) }
    fun onNameLatinChange(value: String) = _state.update { it.copy(nameLatin = value, validationError = null) }
    fun onNameMandarinChange(value: String) = _state.update { it.copy(nameMandarin = value) }
    fun onPinyinChange(value: String) = _state.update { it.copy(pinyin = value) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value, validationError = null) }
    fun onPhotoUrlChange(value: String) = _state.update { it.copy(primaryPhotoUrl = value) }
    fun onAudioUrlChange(value: String) = _state.update { it.copy(mandarinAudioUrl = value) }

    fun savePlant(adminUid: String) {
        val current = _state.value
        if (current.nameId.trim().isBlank()) {
            _state.update { it.copy(validationError = "Nama tanaman (Indonesia) tidak boleh kosong.") }
            return
        }
        if (current.nameLatin.trim().isBlank()) {
            _state.update { it.copy(validationError = "Nama ilmiah (Latin) tidak boleh kosong.") }
            return
        }
        if (current.description.trim().isBlank()) {
            _state.update { it.copy(validationError = "Deskripsi manfaat tanaman tidak boleh kosong.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, validationError = null) }

            val plantId = current.plantId ?: "PLANT_${System.currentTimeMillis()}"
            val plant = MasterPlant(
                id = plantId,
                nameId = current.nameId.trim(),
                nameLatin = current.nameLatin.trim(),
                nameMandarin = current.nameMandarin.trim().ifBlank { null },
                pinyin = current.pinyin.trim().ifBlank { null },
                description = current.description.trim(),
                primaryPhotoUrl = current.primaryPhotoUrl.trim().ifBlank { null },
                mandarinAudioUrl = current.mandarinAudioUrl.trim().ifBlank { null }
            )

            val result = if (current.plantId != null) {
                plantRepository.updateMasterPlant(plant)
            } else {
                plantRepository.createMasterPlant(plant)
            }

            when (result) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = if (current.plantId != null) "MASTER_PLANT_UPDATED" else "MASTER_PLANT_CREATED",
                            targetEntityId = plantId,
                            targetEntityType = "MASTER_PLANT",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Spesies tanaman: ${plant.nameId} (${plant.nameLatin})"
                        )
                    )
                    _state.update { it.copy(isSaving = false) }
                    _successEvent.emit(Unit)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            validationError = result.message ?: "Gagal menyimpan spesies master tanaman."
                        )
                    }
                }
            }
        }
    }
}
