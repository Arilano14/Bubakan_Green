package id.bubakangreen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryForest,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainerMint,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondarySage,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = SecondaryContainer,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineGrey,
    error = ErrorRed
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
