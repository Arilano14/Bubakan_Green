package id.bubakangreen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen,
    onPrimary = TextOnColor,
    primaryContainer = LeafGreenLight,
    onPrimaryContainer = ForestGreen,
    secondary = ForestGreen,
    onSecondary = TextOnColor,
    secondaryContainer = EcoMint,
    tertiary = WarmYellow,
    onTertiary = TextPrimary,
    tertiaryContainer = WarmYellowLight,
    background = BotanicalPaper,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderCard,
    outlineVariant = DividerSoft,
    error = FriendlyRed,
    onError = TextOnColor
)

@Composable
fun BubakanGreenTheme(
    dimensions: Dimensions = Dimensions(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = BubakanTypography,
            content = content
        )
    }
}
