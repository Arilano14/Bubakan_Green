package id.bubakangreen.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.ui.theme.AccentDewContainer
import id.bubakangreen.app.ui.theme.AccentDewTeal
import id.bubakangreen.app.ui.theme.OnDewTealDark
import id.bubakangreen.app.ui.theme.OnPrimaryWhite

/**
 * Tactile audio speaker button for Mandarin pronunciation playback.
 * Enforces single-play, strictly user-triggered audio contract with rich ripple animation.
 */
@Composable
fun MandarinSpeakerButton(
    audioState: AudioState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 52.dp
) {
    val isPlaying = audioState is AudioState.Playing
    val isLoading = audioState is AudioState.Loading

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "speakerPulse")
    val pulseScale by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.14f,
            animationSpec = infiniteRepeatable(
                animation = tween(450),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        rememberInfiniteTransition(label = "idleTransition").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "staticScale"
        )
    }

    val pressScale = if (isPressed && enabled && !isLoading) 0.94f else 1f
    val currentScale = pulseScale * pressScale

    Box(
        modifier = modifier
            .size(size)
            .scale(currentScale)
            .clip(CircleShape)
            .background(if (enabled) AccentDewTeal else AccentDewTeal.copy(alpha = 0.4f))
            .border(3.dp, if (isPlaying) AccentDewContainer else Color(0x33000000), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .semantics {
                contentDescription = "Putar audio pelafalan bahasa Mandarin"
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = OnPrimaryWhite,
                    strokeWidth = 2.5.dp
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = OnPrimaryWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
