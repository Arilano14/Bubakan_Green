package id.bubakangreen.app.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ViewMode {
    LIST,
    MAP
}

data class LocationsUiState(
    val locations: UiState<List<Location>> = UiState.Loading,
    val selectedType: LocationType? = null,
    val viewMode: ViewMode = ViewMode.LIST,
    val selectedMapLocation: Location? = null,
    val isOffline: Boolean = false
)

class LocationsViewModel(
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    fun setCategoryFilter(type: LocationType?) {
        _uiState.update { it.copy(selectedType = type, selectedMapLocation = null) }
        loadLocations()
    }

    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun selectMapLocation(location: Location?) {
        _uiState.update { it.copy(selectedMapLocation = location) }
    }

    fun loadLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(locations = UiState.Loading) }
            val flow = when (val type = _uiState.value.selectedType) {
                null -> locationRepository.getPublishedLocations()
                else -> locationRepository.getLocationsByType(type)
            }

            flow.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(locations = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val list = result.data
                        if (list.isEmpty()) {
                            _uiState.update {
                                it.copy(
                                    locations = UiState.Empty("Belum ada kebun terdaftar untuk kategori ini."),
                                    selectedMapLocation = null
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    locations = UiState.Success(list),
                                    selectedMapLocation = it.selectedMapLocation ?: list.firstOrNull()
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                locations = UiState.Error(
                                    result.message ?: "Gagal memuat data lokasi kebun."
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
