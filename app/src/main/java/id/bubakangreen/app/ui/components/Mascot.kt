package id.bubakangreen.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.R

/**
 * MascotType: Semantic emotional and contextual poses for Bubakan Green Mascot.
 * Complies with Section 3 and Section 4 of the specification.
 */
enum class MascotType {
    DEFAULT,
    GREETING,
    LEARNING,
    HAPPY,
    THINKING,
    WARNING,
    POINTING
}

/**
 * Reusable Bubakan Green Mascot Component.
 * Renders verified local raster assets with strictly maintained 1:1 aspect ratio,
 * zero layout shift, ContentScale.Fit, and no external dependencies.
 */
@Composable
fun Mascot(
    type: MascotType,
    size: Dp = 160.dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    @DrawableRes
    val drawableRes = when (type) {
        MascotType.DEFAULT -> R.drawable.mascot_default
        MascotType.GREETING -> R.drawable.mascot_greeting
        // LEARNING holds the potted botanical plant for lessons & learning cards
        MascotType.LEARNING -> R.drawable.mascot_thinking
        // HAPPY celebration (joyful greeting pose to avoid the prohibited coin "R" logo)
        MascotType.HAPPY -> R.drawable.mascot_greeting
        // THINKING pose has curious hand-on-chin for trivia & questions
        MascotType.THINKING -> R.drawable.mascot_learning
        // WARNING pose with concerned eyes & clasped hands for empty/error state
        MascotType.WARNING -> R.drawable.mascot_warning
        // POINTING pose directs user attention for QR guidance & directional helpers
        MascotType.POINTING -> R.drawable.mascot_pointing
    }

    val desc = contentDescription ?: when (type) {
        MascotType.DEFAULT -> "Maskot Bubakan Green"
        MascotType.GREETING -> "Maskot Bubakan Green Menyapa"
        MascotType.LEARNING -> "Maskot Bubakan Green Belajar Tanaman"
        MascotType.HAPPY -> "Maskot Bubakan Green Merayakan"
        MascotType.THINKING -> "Maskot Bubakan Green Berpikir"
        MascotType.WARNING -> "Maskot Bubakan Green Khawatir"
        MascotType.POINTING -> "Maskot Bubakan Green Menunjuk"
    }

    Box(
        modifier = modifier.requiredSize(size),
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
