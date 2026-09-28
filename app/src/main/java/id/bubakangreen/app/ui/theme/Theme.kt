package id.bubakangreen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimarySeedlingGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainerMint,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondarySage,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = SecondaryContainer,
    tertiary = AccentSunnyGold,
    onTertiary = OnAccentGoldDark,
    tertiaryContainer = AccentSunnyContainer,
    background = BackgroundVanilla,
    surface = SurfaceCardWhite,
    onSurface = OnSurfaceForestDark,
    onSurfaceVariant = OnSurfaceSageMuted,
    outline = OutlineOrganic,
    error = ErrorRestrainedRed
)

@Composable
fun BubakanGreenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
