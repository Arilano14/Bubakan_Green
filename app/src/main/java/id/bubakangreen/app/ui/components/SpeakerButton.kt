package id.bubakangreen.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest

/**
 * Accessible 48x48dp interactive button for Mandarin pronunciation playback.
 * Enforces single-play, strictly user-triggered audio contract.
 */
@Composable
fun MandarinSpeakerButton(
    audioState: AudioState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isPlaying = audioState is AudioState.Playing
    val isLoading = audioState is AudioState.Loading

    val infiniteTransition = rememberInfiniteTransition(label = "speakerPulse")
    val scale by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(400),
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

    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (enabled) PrimaryForest else PrimaryForest.copy(alpha = 0.4f))
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .semantics {
                contentDescription = "Putar audio pelafalan bahasa Mandarin"
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = PrimaryContainerMint,
                    strokeWidth = 2.5.dp
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = OnPrimaryWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
