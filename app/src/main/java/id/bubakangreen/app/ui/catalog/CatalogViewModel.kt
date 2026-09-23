package id.bubakangreen.app.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val plants: UiState<List<MasterPlant>> = UiState.Loading,
    val searchQuery: String = "",
    val isOffline: Boolean = false
)

@OptIn(FlowPreview::class)
class CatalogViewModel(
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private var allPlantsCache: List<MasterPlant> = emptyList()

    init {
        loadAllPlants()
        setupDebouncedSearch()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _queryFlow.value = query
    }

    fun resetSearch() {
        onSearchQueryChanged("")
    }

    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            _queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    filterPlants(query)
                }
        }
    }

    fun loadAllPlants() {
        viewModelScope.launch {
            _uiState.update { it.copy(plants = UiState.Loading) }
            plantRepository.getAllMasterPlants().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(plants = UiState.Loading) }
                    }
                    is Result.Success -> {
                        allPlantsCache = result.data
                        filterPlants(_uiState.value.searchQuery)
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                plants = UiState.Error(
                                    result.message ?: "Gagal memuat katalog tanaman."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun filterPlants(query: String) {
        val trimmed = query.trim()
        val filtered = if (trimmed.isBlank()) {
            allPlantsCache
        } else {
            allPlantsCache.filter { plant ->
                plant.nameId.contains(trimmed, ignoreCase = true) ||
                    plant.nameLatin.contains(trimmed, ignoreCase = true) ||
                    plant.description.contains(trimmed, ignoreCase = true) ||
                    (plant.nameMandarin?.contains(trimmed, ignoreCase = true) == true) ||
                    (plant.pinyin?.contains(trimmed, ignoreCase = true) == true)
            }
        }

        if (filtered.isEmpty()) {
            _uiState.update {
                it.copy(
                    plants = UiState.Empty(
                        if (trimmed.isBlank()) "Belum ada tanaman terdata."
                        else "Tidak ada tanaman yang cocok dengan \"$trimmed\"."
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(plants = UiState.Success(filtered))
            }
        }
    }
}
