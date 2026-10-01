package id.bubakangreen.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BorderCard
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.LeafGreen
import id.bubakangreen.app.ui.theme.LeafGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary

/**
 * Duolingo-inspired outlined secondary action button.
 * - Height: 52-56dp (default 54dp)
 * - Rounded pill geometry (27dp radius)
 * - Outlined with less visual weight
 * - Meets Fitts's Law touch target ergonomics (>= 48dp)
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    height: Dp = 54.dp,
    shapeRadius: Dp = 27.dp,
    borderColor: Color = BorderCard,
    textColor: Color = TextPrimary
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = RoundedCornerShape(shapeRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = LeafGreenLight.copy(alpha = 0.35f),
            contentColor = textColor,
            disabledContainerColor = LeafGreenLight.copy(alpha = 0.15f),
            disabledContentColor = textColor.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.5.dp, if (enabled) borderColor else borderColor.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = LeafGreen,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LeafGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}
