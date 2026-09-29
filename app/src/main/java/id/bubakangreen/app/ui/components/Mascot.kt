package id.bubakangreen.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.R

/**
 * MascotType: Semantic emotional and contextual poses for Bubakan Green Mascot.
 * Fully mapped to the verified local raster assets in drawable-nodpi.
 */
enum class MascotType {
    DEFAULT,
    GREETING,
    LEARNING,
    HAPPY,
    THINKING,
    WARNING,
    POINTING,
    CTA_PROCESS,
    SPLASH
}

/**
 * Reusable Bubakan Green Mascot Component.
 * Renders verified local raster assets with strictly maintained 1:1 aspect ratio,
 * zero layout shift, ContentScale.Fit, and optional native Compose micro-motion.
 *
 * Micro-motion specs:
 * - rotationZ: -2° to +2°
 * - translationY: 0dp to -4dp
 * - scale: 1.00 to 1.015
 * - duration: ~2100ms (FastOutSlowInEasing)
 */
@Composable
fun Mascot(
    type: MascotType,
    size: Dp = 160.dp,
    modifier: Modifier = Modifier,
    baseRotation: Float = 0f,
    animateIdle: Boolean = false,
    contentDescription: String? = null
) {
    @DrawableRes
    val drawableRes = when (type) {
        MascotType.DEFAULT -> R.drawable.mascot_default
        MascotType.GREETING -> R.drawable.mascot_greeting
        MascotType.LEARNING -> R.drawable.mascot_learning
        MascotType.HAPPY -> R.drawable.mascot_happy
        MascotType.THINKING -> R.drawable.mascot_thinking
        MascotType.WARNING -> R.drawable.mascot_warning
        MascotType.POINTING -> R.drawable.mascot_pointing
        MascotType.CTA_PROCESS -> R.drawable.mascot_cta_process
        MascotType.SPLASH -> R.drawable.mascot_splashscreen
    }

    val desc = contentDescription ?: when (type) {
        MascotType.DEFAULT -> "Maskot Bubakan Green"
        MascotType.GREETING -> "Maskot Bubakan Green Menyapa"
        MascotType.LEARNING -> "Maskot Bubakan Green Belajar Tanaman"
        MascotType.HAPPY -> "Maskot Bubakan Green Merayakan Pembelajaran"
        MascotType.THINKING -> "Maskot Bubakan Green Berpikir"
        MascotType.WARNING -> "Maskot Bubakan Green Khawatir"
        MascotType.POINTING -> "Maskot Bubakan Green Menunjuk Petunjuk"
        MascotType.CTA_PROCESS -> "Maskot Bubakan Green Panduan Proses"
        MascotType.SPLASH -> "Maskot Bubakan Green Selamat Datang"
    }

    val animModifier = if (animateIdle) {
        val infiniteTransition = rememberInfiniteTransition(label = "mascot_idle")
        val animRotation by infiniteTransition.animateFloat(
            initialValue = -2f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_rotation"
        )
        val animTranslationY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -4f,
            animationSpec = infiniteRepeatable(
                animation = tween(2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_translationY"
        )
        val animScale by infiniteTransition.animateFloat(
            initialValue = 1.00f,
            targetValue = 1.015f,
            animationSpec = infiniteRepeatable(
                animation = tween(2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mascot_scale"
        )

        Modifier.graphicsLayer {
            rotationZ = baseRotation + animRotation
            translationY = animTranslationY.dp.toPx()
            scaleX = animScale
            scaleY = animScale
        }
    } else if (baseRotation != 0f) {
        Modifier.graphicsLayer {
            rotationZ = baseRotation
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(animModifier)
            .requiredSize(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = desc,
            contentScale = ContentScale.Fit,
            modifier = Modifier.requiredSize(size)
        )
    }
}
