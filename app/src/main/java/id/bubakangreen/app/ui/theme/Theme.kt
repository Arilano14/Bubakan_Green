package id.bubakangreen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = PrimaryGreenDark,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = EcoMint,
    tertiary = MascotYellow,
    onTertiary = OnAccentGoldDark,
    tertiaryContainer = AccentSunnyContainer,
    background = BackgroundWarm,
    surface = Surface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderDivider,
    error = ErrorMaterialRed
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
