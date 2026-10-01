package id.bubakangreen.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BorderCard
import id.bubakangreen.app.ui.theme.SurfaceCard
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

enum class MascotPosition {
    START,
    END,
    CENTER
}

enum class MascotCardSize(val dpSize: Dp) {
    SMALL(80.dp),
    MEDIUM(120.dp),
    LARGE(160.dp)
}

/**
 * Reusable Duolingo-inspired MascotCard component.
 * Features:
 * - START / END / CENTER positioning
 * - SMALL / MEDIUM / LARGE scales
 * - 60 FPS native Compose graphicsLayer micro-motion (rotation -3° to 3°, floating Y, scale breathing)
 * - Zero heavy recomposition
 */
@Composable
fun MascotCard(
    image: MascotType = MascotType.DEFAULT,
    message: String,
    title: String? = null,
    position: MascotPosition = MascotPosition.CENTER,
    size: MascotCardSize = MascotCardSize.MEDIUM,
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceCard,
    borderColor: Color = BorderCard,
    action: (@Composable () -> Unit)? = null
) {
    // Subtle Compose motion in graphicsLayer (no layout shifts, 60fps)
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_card_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_card_rot"
    )
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_card_float"
    )
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_card_scale"
    )

    val animatedMascotModifier = Modifier.graphicsLayer {
        rotationZ = rotation
        translationY = floatOffset.dp.toPx()
        scaleX = breathingScale
        scaleY = breathingScale
    }

    when (position) {
        MascotPosition.CENTER -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    color = containerColor,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = size.dpSize * 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            top = (size.dpSize * 0.62f) + 8.dp,
                            end = 20.dp,
                            bottom = 20.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!title.isNullOrBlank()) {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Text(
                            text = message,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 21.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (action != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            action()
                        }
                    }
                }

                Box(modifier = animatedMascotModifier) {
                    Mascot(
                        type = image,
                        size = size.dpSize,
                        animateIdle = false
                    )
                }
            }
        }

        MascotPosition.START -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = animatedMascotModifier) {
                    Mascot(
                        type = image,
                        size = size.dpSize,
                        animateIdle = false
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    color = containerColor,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!title.isNullOrBlank()) {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = TextSecondary
                        )
                        if (action != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            action()
                        }
                    }
                }
            }
        }

        MascotPosition.END -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    color = containerColor,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!title.isNullOrBlank()) {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = TextSecondary
                        )
                        if (action != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            action()
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(modifier = animatedMascotModifier) {
                    Mascot(
                        type = image,
                        size = size.dpSize,
                        animateIdle = false
                    )
                }
            }
        }
    }
}

/**
 * Backward-compatible MascotCard overload matching legacy invocations.
 */
@Composable
fun MascotCard(
    title: String,
    body: String,
    mascotType: MascotType = MascotType.THINKING,
    mascotSize: Dp = 140.dp,
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceCard,
    borderColor: Color = BorderCard,
    action: (@Composable () -> Unit)? = null
) {
    val sizeEnum = when {
        mascotSize <= 90.dp -> MascotCardSize.SMALL
        mascotSize <= 130.dp -> MascotCardSize.MEDIUM
        else -> MascotCardSize.LARGE
    }
    MascotCard(
        image = mascotType,
        title = title,
        message = body,
        position = MascotPosition.CENTER,
        size = sizeEnum,
        modifier = modifier,
        containerColor = containerColor,
        borderColor = borderColor,
        action = action
    )
}
