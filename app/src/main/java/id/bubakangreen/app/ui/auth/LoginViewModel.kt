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
    val username: String = "",
    val email: String = username,
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

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername, email = newUsername, errorMessage = null) }
    }

    fun onEmailChange(newEmail: String) {
        onUsernameChange(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun signIn() {
        val currentState = _uiState.value
        val rawInput = currentState.username.trim()
        val password = currentState.password.trim()

        if (rawInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Kata sandi tidak boleh kosong.") }
            return
        }


        // Section 4: Map UI username "admin" internally to configured admin Firebase Auth account
        val targetEmail = if (rawInput.contains("@")) {
            rawInput
        } else {
            "${rawInput.lowercase()}@bubakangreen.id"
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signInWithEmail(targetEmail, password)) {
                is Result.Success -> {
                    val session = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            signedInSession = session,
                            errorMessage = null
                        )
                    }
                    // Section 5: Authorization role verification
                    if (session.role == UserRole.ADMIN && session.isActive) {
                        _navigationEvent.emit(LoginNavigationEvent.NavigateToAdminDashboard)
                    } else if (session.role == UserRole.PIC && session.isActive) {
                        _navigationEvent.emit(LoginNavigationEvent.NavigateToPicDashboard)
                    } else {
                        // Section 5: If role != ADMIN or isActive != true, deny admin access
                        authRepository.signOut()
                        _uiState.update {
                            it.copy(
                                signedInSession = null,
                                errorMessage = "Akun ini tidak memiliki akses admin."
                            )
                        }
                    }
                }
                is Result.Error -> {
                    val msg = result.message ?: result.exception.message ?: ""
                    val friendlyError = when {
                        result.exception is SecurityException ||
                        msg.contains("tidak memiliki akses admin", ignoreCase = true) ||
                        msg.contains("akses admin", ignoreCase = true) ->
                            "Akun ini tidak memiliki akses admin."
                        msg.contains("salah", ignoreCase = true) ||
                        msg.contains("invalid", ignoreCase = true) ||
                        msg.contains("credential", ignoreCase = true) ||
                        msg.contains("password", ignoreCase = true) ->
                            "Username atau password salah."
                        msg.contains("koneksi", ignoreCase = true) ||
                        msg.contains("network", ignoreCase = true) ->
                            "Koneksi internet diperlukan untuk masuk."
                        else -> if (msg.isNotBlank()) msg else "Username atau password salah."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyError
                        )
                    }
                }

                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}

