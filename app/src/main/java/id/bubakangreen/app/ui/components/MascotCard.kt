package id.bubakangreen.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * MascotCard: Reusable educational card guided by Bubakan Green Mascot.
 * Follows Section 6 (soft rounded surface 20-24dp, 16dp padding, no harsh clipping).
 */
@Composable
fun MascotCard(
    title: String,
    body: String,
    mascotType: MascotType = MascotType.THINKING,
    mascotSize: Dp = 140.dp,
    modifier: Modifier = Modifier,
    containerColor: Color = Surface,
    borderColor: Color = BorderDivider,
    action: (@Composable () -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, borderColor),
        color = containerColor,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Mascot(
                type = mascotType,
                size = mascotSize
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = body,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            if (action != null) {
                Spacer(modifier = Modifier.height(16.dp))
                action()
            }
        }
    }
}
