package id.bubakangreen.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Empathetic error state layout with patient companion Bubakan Green and prominent retry action.
 * Delegates to reusable EmptyState component.
 */
@Composable
fun StateErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Oops! Ada Sedikit Kendala"
) {
    EmptyState(
        title = title,
        message = message,
        actionLabel = "Coba Lagi",
        onActionClick = onRetry,
        modifier = modifier
    )
}
