package id.bubakangreen.app.ui.pic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PicDashboardViewModel(
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Location>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Location>>> = _uiState.asStateFlow()

    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.currentUserSession.collect { session ->
                _userSession.value = session
                val picUid = session.uid.ifBlank { "system_preview" }
                when (val result = locationRepository.getAssignedLocations(picUid)) {
                    is Result.Success -> {
                        val locations = result.data
                        if (locations.isEmpty()) {
                            _uiState.value = UiState.Empty("Belum ada kebun yang terdaftar untuk penugasan Anda.")
                        } else {
                            _uiState.value = UiState.Success(locations)
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Error(result.message ?: "Gagal memuat daftar kebun binaan.")
                    }
                }
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
