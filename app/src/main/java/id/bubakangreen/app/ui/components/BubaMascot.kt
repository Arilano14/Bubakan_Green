package id.bubakangreen.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Legacy compatibility enum for BubaState.
 * Automatically mapped to the official MascotType.
 */
enum class BubaState {
    GREETING,
    LISTENING,
    THINKING,
    CELEBRATING,
    SEARCHING,
    RESTING
}

/**
 * BubaMascot wrapper that seamlessly delegates to the official Mascot component.
 */
@Composable
fun BubaMascot(
    state: BubaState = BubaState.GREETING,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val mascotType = when (state) {
        BubaState.GREETING -> MascotType.GREETING
        BubaState.LISTENING -> MascotType.LEARNING
        BubaState.THINKING -> MascotType.THINKING
        BubaState.CELEBRATING -> MascotType.HAPPY
        BubaState.SEARCHING, BubaState.RESTING -> MascotType.WARNING
    }
    Mascot(
        type = mascotType,
        size = size,
        modifier = modifier
    )
}
