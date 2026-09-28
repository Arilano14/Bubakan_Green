package id.bubakangreen.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.OnAccentGoldDark
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen

enum class TactileButtonStyle {
    PRIMARY,   // Energetic chlorophyll green with dark forest bottom rim
    SUNNY,     // Vibrant golden blossom with warm bronze bottom rim
    SECONDARY  // Soft mint container with gentle border
}

/**
 * A tactile 3D-rim button inspired by playful educational design.
 * Depresses vertically on tap for a satisfying tactile sensation.
 */
@Composable
fun TactileButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    style: TactileButtonStyle = TactileButtonStyle.PRIMARY,
    height: Dp = 50.dp,
    shapeRadius: Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rimDepth = 3.dp
    val verticalOffset by animateDpAsState(
        targetValue = if (isPressed && enabled && !loading) rimDepth else 0.dp,
        label = "TactileDepression"
    )

    val (faceColor, rimColor, textColor) = when (style) {
        TactileButtonStyle.PRIMARY -> Triple(
            if (enabled) PrimarySeedlingGreen else Color(0xFFA5D6A7),
            if (enabled) PrimaryForestDark else Color(0xFF81C784),
            OnPrimaryWhite
        )
        TactileButtonStyle.SUNNY -> Triple(
            if (enabled) AccentSunnyGold else Color(0xFFFFE082),
            if (enabled) OnAccentGoldDark else Color(0xFFFFB300),
            Color(0xFF2E1C00)
        )
        TactileButtonStyle.SECONDARY -> Triple(
            if (enabled) Color(0xFFE2F7EC) else Color(0xFFEEEEEE),
            if (enabled) Color(0xFFB7E4C7) else Color(0xFFE0E0E0),
            if (enabled) PrimaryForestDark else Color(0xFF9E9E9E)
        )
    }

    val shape = RoundedCornerShape(shapeRadius)

    Box(
        modifier = modifier
            .height(height + rimDepth)
            .clip(shape)
            .background(rimColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Top Face of Button that depresses
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = verticalOffset)
                .clip(shape)
                .background(faceColor)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp).width(22.dp),
                    color = textColor,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.height(20.dp).width(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}
