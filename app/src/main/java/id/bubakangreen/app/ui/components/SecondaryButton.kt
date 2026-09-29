package id.bubakangreen.app.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary

/**
 * Standard reusable secondary action button complying with Fitts's law (>=48dp touch target)
 * and Eco-Green design system.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    height: Dp = 50.dp,
    shapeRadius: Dp = 18.dp
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = RoundedCornerShape(shapeRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = PrimaryGreenLight.copy(alpha = 0.5f),
            contentColor = TextPrimary,
            disabledContainerColor = PrimaryGreenLight.copy(alpha = 0.2f),
            disabledContentColor = TextPrimary.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.5.dp, if (enabled) BorderDivider else BorderDivider.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = PrimaryGreen,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }
        }
    }
}
