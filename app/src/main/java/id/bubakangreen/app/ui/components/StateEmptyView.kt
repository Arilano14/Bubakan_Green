package id.bubakangreen.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Playful, friendly empty state layout guided by Bubakan Green companion.
 * Delegates to reusable EmptyState component.
 */
@Composable
fun StateEmptyView(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {
    EmptyState(
        title = title,
        message = message,
        actionLabel = actionLabel,
        onActionClick = onActionClick,
        modifier = modifier
    )
}
