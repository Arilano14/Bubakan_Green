package id.bubakangreen.app.ui.common

/**
 * Standard presentation state wrapper for all public UI screens.
 * Explicitly models Loading, Success (with offline cache indicator), Empty, and Error states.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : UiState<T>
    data class Empty(val message: String) : UiState<Nothing>
    data class Error(val message: String, val canRetry: Boolean = true) : UiState<Nothing>
}
