package id.bubakangreen.app.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationDetailUiState(
    val location: UiState<Location> = UiState.Loading,
    val plants: UiState<List<MasterPlant>> = UiState.Loading,
    val isOffline: Boolean = false
)

class LocationDetailViewModel(
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository(),
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationDetailUiState())
    val uiState: StateFlow<LocationDetailUiState> = _uiState.asStateFlow()

    fun loadLocationDetail(locationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(location = UiState.Loading, plants = UiState.Loading) }

            // Fetch location details
            locationRepository.getLocationById(locationId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(location = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val loc = result.data
                        if (loc == null) {
                            _uiState.update {
                                it.copy(location = UiState.Empty("Lokasi kebun tidak ditemukan."))
                            }
                        } else {
                            _uiState.update { it.copy(location = UiState.Success(loc)) }
                            loadPlantsForLocation(locationId)
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                location = UiState.Error(
                                    result.message ?: "Gagal memuat detail lokasi kebun."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadPlantsForLocation(locationId: String) {
        viewModelScope.launch {
            plantRepository.getPlantsAtLocation(locationId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(plants = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val junctions = result.data
                        if (junctions.isEmpty()) {
                            _uiState.update {
                                it.copy(plants = UiState.Empty("Belum ada tanaman terdata di kebun ini."))
                            }
                        } else {
                            // Resolve master plant species
                            val masterPlants = junctions.mapNotNull { junction ->
                                when (val masterRes = plantRepository.getMasterPlantById(junction.masterPlantId).firstOrNull()) {
                                    is Result.Success -> masterRes.data
                                    else -> null
                                }
                            }
                            _uiState.update { it.copy(plants = UiState.Success(masterPlants)) }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                plants = UiState.Error(
                                    result.message ?: "Gagal memuat daftar tanaman di lokasi ini."
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
