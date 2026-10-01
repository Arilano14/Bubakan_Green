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
    val space12: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val space20: Dp = 20.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val massive: Dp = 48.dp,

    // Centralized Spacing Scale (4, 8, 12, 16, 20, 24, 32)
    val screenPaddingHorizontal: Dp = 16.dp,
    val screenPaddingVertical: Dp = 16.dp,
    val sectionSpacing: Dp = 24.dp,
    val cardRadius: Dp = 20.dp,
    val cardRadiusSmall: Dp = 16.dp,
    val cardRadiusPill: Dp = 27.dp,
    val cardPadding: Dp = 16.dp,
    val cardBorderWidth: Dp = 1.dp,

    // Button & Touch Target Ergonomics (Fitts's Law >= 48dp)
    val buttonHeight: Dp = 54.dp,
    val buttonHeightSmall: Dp = 44.dp,
    val minTouchTarget: Dp = 48.dp,
    val iconSizeSmall: Dp = 18.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,

    // Mascot Scales (Section 15)
    val mascotSplash: Dp = 240.dp,
    val mascotHome: Dp = 160.dp,
    val mascotDetail: Dp = 140.dp,
    val mascotSuccess: Dp = 180.dp,
    val mascotWarning: Dp = 140.dp,
    val mascotAdmin: Dp = 90.dp,

    // Backward-compatible scales
    val mascotSmall: Dp = 90.dp,
    val mascotMedium: Dp = 140.dp,
    val mascotLarge: Dp = 160.dp,
    val bottomBarHeight: Dp = 64.dp
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
