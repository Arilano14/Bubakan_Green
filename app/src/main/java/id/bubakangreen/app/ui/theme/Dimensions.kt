package id.bubakangreen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Bubakan Green — Responsive Spacing and Adaptive Component Dimensions.
 * Eliminates hardcoded pixel placement across screen sizes (360x800, 1080x2400, 1440x3200).
 */
data class Dimensions(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val massive: Dp = 48.dp,

    // Adaptive Component Spacing
    val screenPaddingHorizontal: Dp = 20.dp,
    val screenPaddingVertical: Dp = 16.dp,
    val cardRadius: Dp = 22.dp,
    val cardRadiusSmall: Dp = 16.dp,
    val cardRadiusPill: Dp = 28.dp,
    val cardPadding: Dp = 18.dp,
    val cardBorderWidth: Dp = 1.5.dp,

    // Button & Touch Target Ergonomics (Fitts's Law >= 48dp)
    val buttonHeight: Dp = 54.dp,
    val buttonHeightSmall: Dp = 44.dp,
    val minTouchTarget: Dp = 48.dp,
    val iconSizeSmall: Dp = 18.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,

    // Mascot & Hero Scales
    val mascotSmall: Dp = 80.dp,
    val mascotMedium: Dp = 120.dp,
    val mascotLarge: Dp = 160.dp,
    val bottomBarHeight: Dp = 72.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }

/**
 * Convenient accessor via MaterialTheme.spacing
 */
val MaterialTheme.spacing: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

val MaterialTheme.dimens: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current
