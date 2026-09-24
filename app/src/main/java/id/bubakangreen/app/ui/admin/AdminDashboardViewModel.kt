package id.bubakangreen.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class AdminDashboardData(
    val pendingLocations: List<Location>,
    val totalPublished: Int,
    val totalMasterPlants: Int
)

class AdminDashboardViewModel(
    private val locationRepository: LocationRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AdminDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AdminDashboardData>> = _uiState.asStateFlow()

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.currentUserSession.collect { userSession ->
                _session.value = userSession

                val pendingResult = locationRepository.getPendingLocations()
                val pendingList = (pendingResult as? Result.Success)?.data ?: emptyList()

                val pubResult = locationRepository.getPublishedLocations().firstOrNull()
                val publishedCount = (pubResult as? Result.Success)?.data?.size ?: 0

                val plantsResult = plantRepository.getAllMasterPlants().firstOrNull()
                val masterCount = (plantsResult as? Result.Success)?.data?.size ?: 0

                _uiState.value = UiState.Success(
                    AdminDashboardData(
                        pendingLocations = pendingList,
                        totalPublished = publishedCount,
                        totalMasterPlants = masterCount
                    )
                )
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onSignedOut()
        }
    }
}
