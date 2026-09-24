package id.bubakangreen.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoginNavigationEvent {
    data object NavigateToPicDashboard : LoginNavigationEvent
    data object NavigateToAdminDashboard : LoginNavigationEvent
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signedInSession: UserSession? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun signIn() {
        val currentState = _uiState.value
        val email = currentState.email.trim()
        val password = currentState.password.trim()

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Kata sandi tidak boleh kosong.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signInWithEmail(email, password)) {
                is Result.Success -> {
                    val session = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            signedInSession = session,
                            errorMessage = null
                        )
                    }
                    when (session.role) {
                        UserRole.ADMIN -> _navigationEvent.emit(LoginNavigationEvent.NavigateToAdminDashboard)
                        UserRole.PIC -> _navigationEvent.emit(LoginNavigationEvent.NavigateToPicDashboard)
                        UserRole.PUBLIC -> {
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Akun Anda belum memiliki hak akses Petugas atau Admin."
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Gagal masuk. Periksa kembali email dan kata sandi Anda."
                        )
                    }
                }
            }
        }
    }
}
