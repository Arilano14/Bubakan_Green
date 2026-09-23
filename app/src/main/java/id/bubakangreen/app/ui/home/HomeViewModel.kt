package id.bubakangreen.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val featuredLocations: UiState<List<Location>> = UiState.Loading,
    val popularPlants: UiState<List<MasterPlant>> = UiState.Loading,
    val isOffline: Boolean = false
)

class HomeViewModel(
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository(),
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        loadFeaturedLocations()
        loadPopularPlants()
    }

    private fun loadFeaturedLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(featuredLocations = UiState.Loading) }
            locationRepository.getFeaturedLocations().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(featuredLocations = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val locations = result.data
                        if (locations.isEmpty()) {
                            _uiState.update {
                                it.copy(featuredLocations = UiState.Empty("Belum ada lokasi unggulan terdaftar."))
                            }
                        } else {
                            _uiState.update {
                                it.copy(featuredLocations = UiState.Success(locations))
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                featuredLocations = UiState.Error(
                                    result.message ?: "Gagal memuat lokasi unggulan. Periksa koneksi internet."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadPopularPlants() {
        viewModelScope.launch {
            _uiState.update { it.copy(popularPlants = UiState.Loading) }
            plantRepository.getAllMasterPlants().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(popularPlants = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val plants = result.data
                        if (plants.isEmpty()) {
                            _uiState.update {
                                it.copy(popularPlants = UiState.Empty("Belum ada tanaman terdata."))
                            }
                        } else {
                            _uiState.update {
                                it.copy(popularPlants = UiState.Success(plants.take(5)))
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                popularPlants = UiState.Error(
                                    result.message ?: "Gagal memuat katalog tanaman."
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
