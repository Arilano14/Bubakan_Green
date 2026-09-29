package id.bubakangreen.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * MascotCard: Educational speech card where the mascot breaks out of the frame.
 * Complies with Section 6:
 * - Mascot visually floats above surface, tilted -2°
 * - Subtle native breathing micro-motion
 * - Card corner radius: 18dp (not over-rounded 24-32dp)
 * - Zero nested card clutter
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
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Friendly learning speech surface
        Surface(
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, borderColor),
            color = containerColor,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = mascotSize * 0.4f)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 18.dp,
                    top = (mascotSize * 0.65f) + 6.dp,
                    end = 18.dp,
                    bottom = 18.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = body,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 21.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (action != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    action()
                }
            }
        }

        // Mascot breaking out of the card composition
        Mascot(
            type = mascotType,
            size = mascotSize,
            baseRotation = -2f,
            animateIdle = true
        )
    }
}
