package id.bubakangreen.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.ui.theme.AccentDewTeal
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen

/**
 * Expression states for Si Buba, the friendly Bubakan botanical companion.
 */
enum class BubaState {
    GREETING,     // Waving happily (Home Header / Welcome)
    LISTENING,    // Perked leaf-ears / audio feedback (Mandarin pronunciation)
    THINKING,     // Tilted head, curious look (Trivia / Learning)
    CELEBRATING,  // Joyful sparkles / high excitement (QR discovery / Success)
    SEARCHING,    // Inquisitive look, peeking around (Empty search results)
    RESTING       // Patient, calm eyes (Offline / Error state)
}

/**
 * Si Buba — The Botanical Sprout Mascot of Kelurahan Bubakan.
 *
 * Implemented completely with Jetpack Compose Canvas vector primitives for:
 * - 0 external asset overhead (<15KB code, 0 raster bitmaps).
 * - Full scalability across any device DPI.
 * - Gentle, delightful micro-animations for educational engagement.
 */
@Composable
fun BubaMascot(
    state: BubaState = BubaState.GREETING,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BubaAnimation")
    
    // Gentle floating or sway animation
    val swayProgress by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Sway"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val stateDescription = when (state) {
        BubaState.GREETING -> "Maskot Si Buba sedang tersenyum ramah dan menyapa"
        BubaState.LISTENING -> "Maskot Si Buba sedang mendengarkan pelafalan audio"
        BubaState.THINKING -> "Maskot Si Buba sedang berpikir penuh rasa ingin tahu"
        BubaState.CELEBRATING -> "Maskot Si Buba merayakan penemuan tanaman baru"
        BubaState.SEARCHING -> "Maskot Si Buba sedang mencari tanaman di kebun"
        BubaState.RESTING -> "Maskot Si Buba sedang istirahat dengan tenang"
    }

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = stateDescription },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val scale = canvasWidth / 100f // Reference dimension is 100x100

            // 1. Draw Terracotta Garden Pot Base
            drawPotBase(scale, canvasWidth, canvasHeight)

            // 2. Draw Main Sprout Body (Plump, friendly teardrop)
            drawSproutBody(scale, canvasWidth, canvasHeight, swayProgress * 1.5f * scale)

            // 3. Draw Leaf Ears on Head
            drawLeafEars(scale, canvasWidth, canvasHeight, state, swayProgress)

            // 4. Draw Cute Expressive Face (Eyes, Cheeks, Mouth)
            drawFace(scale, canvasWidth, canvasHeight, state)

            // 5. Draw State-Specific Props / Emotes
            drawStateAccents(scale, canvasWidth, canvasHeight, state, swayProgress)
        }
    }
}

/**
 * Draws the earthen terracotta pot base where Si Buba grows.
 */
private fun DrawScope.drawPotBase(scale: Float, width: Float, height: Float) {
    val potTopY = height * 0.72f
    val potBottomY = height * 0.94f
    val potTopWidth = width * 0.54f
    val potBottomWidth = width * 0.40f

    val potPath = Path().apply {
        moveTo((width - potTopWidth) / 2f, potTopY)
        lineTo((width + potTopWidth) / 2f, potTopY)
        lineTo((width + potBottomWidth) / 2f, potBottomY)
        lineTo((width - potBottomWidth) / 2f, potBottomY)
        close()
    }

    // Warm terracotta gradient
    drawPath(
        path = potPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE27D60), Color(0xFFC3583C)),
            startY = potTopY,
            endY = potBottomY
        )
    )

    // Pot Rim
    val rimRect = Rect(
        left = (width - potTopWidth) / 2f - 2f * scale,
        top = potTopY - 4f * scale,
        right = (width + potTopWidth) / 2f + 2f * scale,
        bottom = potTopY + 3f * scale
    )
    drawRoundRect(
        color = Color(0xFFE88A6E),
        topLeft = Offset(rimRect.left, rimRect.top),
        size = Size(rimRect.width, rimRect.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * scale, 3f * scale)
    )
}

/**
 * Draws the soft seedling body.
 */
private fun DrawScope.drawSproutBody(scale: Float, width: Float, height: Float, swayOffset: Float) {
    val centerX = width * 0.5f + swayOffset
    val bodyCenterY = height * 0.50f
    val bodyRadius = 24f * scale

    // Emerald seedling gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF38B000), PrimarySeedlingGreen, PrimaryForestDark),
            center = Offset(centerX - 4f * scale, bodyCenterY - 4f * scale),
            radius = bodyRadius * 1.2f
        ),
        radius = bodyRadius,
        center = Offset(centerX, bodyCenterY)
    )

    // Gentle belly highlight
    drawOval(
        color = Color(0x33FFFFFF),
        topLeft = Offset(centerX - 10f * scale, bodyCenterY - 12f * scale),
        size = Size(14f * scale, 18f * scale)
    )
}

/**
 * Draws the expressive sprout leaf-ears atop the head.
 */
private fun DrawScope.drawLeafEars(
    scale: Float,
    width: Float,
    height: Float,
    state: BubaState,
    sway: Float
) {
    val headTopX = width * 0.5f
    val headTopY = height * 0.28f

    val leafTilt = when (state) {
        BubaState.LISTENING -> 8f // Tilted up attentively
        BubaState.THINKING -> -12f // Tilted curious
        BubaState.CELEBRATING -> 15f // Peppy and raised
        else -> sway * 5f
    }

    // Left Leaf
    val leftLeafPath = Path().apply {
        moveTo(headTopX - 2f * scale, headTopY)
        cubicTo(
            headTopX - 16f * scale, headTopY - 12f * scale + leafTilt,
            headTopX - 14f * scale, headTopY - 24f * scale + leafTilt,
            headTopX - 4f * scale, headTopY - 18f * scale
        )
        cubicTo(
            headTopX - 2f * scale, headTopY - 12f * scale,
            headTopX - 1f * scale, headTopY - 6f * scale,
            headTopX - 2f * scale, headTopY
        )
        close()
    }

    drawPath(
        path = leftLeafPath,
        color = Color(0xFF70E000)
    )

    // Right Leaf
    val rightLeafPath = Path().apply {
        moveTo(headTopX + 2f * scale, headTopY)
        cubicTo(
            headTopX + 16f * scale, headTopY - 12f * scale - leafTilt,
            headTopX + 14f * scale, headTopY - 24f * scale - leafTilt,
            headTopX + 4f * scale, headTopY - 18f * scale
        )
        cubicTo(
            headTopX + 2f * scale, headTopY - 12f * scale,
            headTopX + 1f * scale, headTopY - 6f * scale,
            headTopX + 2f * scale, headTopY
        )
        close()
    }

    drawPath(
        path = rightLeafPath,
        color = Color(0xFF38B000)
    )
}

/**
 * Draws expressive face features (eyes, rosy cheeks, mouth).
 */
private fun DrawScope.drawFace(
    scale: Float,
    width: Float,
    height: Float,
    state: BubaState
) {
    val centerX = width * 0.5f
    val faceY = height * 0.48f

    // 1. Rosy Cheeks
    val cheekRadius = 3.5f * scale
    drawCircle(
        color = AccentSunnyGold.copy(alpha = 0.55f),
        radius = cheekRadius,
        center = Offset(centerX - 14f * scale, faceY + 5f * scale)
    )
    drawCircle(
        color = AccentSunnyGold.copy(alpha = 0.55f),
        radius = cheekRadius,
        center = Offset(centerX + 14f * scale, faceY + 5f * scale)
    )

    // 2. Eyes
    val eyeY = faceY - 1f * scale
    val eyeSpacing = 8.5f * scale

    when (state) {
        BubaState.RESTING -> {
            // Calm sleeping/peaceful curved arches: ^^
            val leftArch = Path().apply {
                moveTo(centerX - eyeSpacing - 4f * scale, eyeY + 1f * scale)
                quadraticBezierTo(centerX - eyeSpacing, eyeY - 3f * scale, centerX - eyeSpacing + 4f * scale, eyeY + 1f * scale)
            }
            val rightArch = Path().apply {
                moveTo(centerX + eyeSpacing - 4f * scale, eyeY + 1f * scale)
                quadraticBezierTo(centerX + eyeSpacing, eyeY - 3f * scale, centerX + eyeSpacing + 4f * scale, eyeY + 1f * scale)
            }
            drawPath(leftArch, Color(0xFF0F3B20), style = Stroke(width = 2f * scale, cap = StrokeCap.Round))
            drawPath(rightArch, Color(0xFF0F3B20), style = Stroke(width = 2f * scale, cap = StrokeCap.Round))
        }
        BubaState.CELEBRATING -> {
            // Joyful starburst happy eyes: ><
            drawEyeArc(centerX - eyeSpacing, eyeY, scale)
            drawEyeArc(centerX + eyeSpacing, eyeY, scale)
        }
        else -> {
            // Round sparkling cartoon eyes
            drawCircle(color = Color(0xFF0D2818), radius = 3.5f * scale, center = Offset(centerX - eyeSpacing, eyeY))
            drawCircle(color = Color(0xFF0D2818), radius = 3.5f * scale, center = Offset(centerX + eyeSpacing, eyeY))

            // Specular highlights
            drawCircle(color = Color.White, radius = 1.3f * scale, center = Offset(centerX - eyeSpacing - 1f * scale, eyeY - 1f * scale))
            drawCircle(color = Color.White, radius = 1.3f * scale, center = Offset(centerX + eyeSpacing - 1f * scale, eyeY - 1f * scale))
        }
    }

    // 3. Mouth
    when (state) {
        BubaState.CELEBRATING, BubaState.GREETING -> {
            // Wide happy smile
            val mouthPath = Path().apply {
                moveTo(centerX - 4.5f * scale, faceY + 6f * scale)
                quadraticBezierTo(centerX, faceY + 11f * scale, centerX + 4.5f * scale, faceY + 6f * scale)
            }
            drawPath(mouthPath, Color(0xFF0F3B20), style = Stroke(width = 1.8f * scale, cap = StrokeCap.Round))
        }
        BubaState.THINKING, BubaState.SEARCHING -> {
            // Cute small "o" inquisitive mouth
            drawCircle(
                color = Color(0xFF0F3B20),
                radius = 1.8f * scale,
                center = Offset(centerX, faceY + 8f * scale),
                style = Stroke(width = 1.5f * scale)
            )
        }
        else -> {
            // Gentle curve smile
            val mouthPath = Path().apply {
                moveTo(centerX - 3.5f * scale, faceY + 7f * scale)
                quadraticBezierTo(centerX, faceY + 9.5f * scale, centerX + 3.5f * scale, faceY + 7f * scale)
            }
            drawPath(mouthPath, Color(0xFF0F3B20), style = Stroke(width = 1.6f * scale, cap = StrokeCap.Round))
        }
    }
}

private fun DrawScope.drawEyeArc(centerX: Float, centerY: Float, scale: Float) {
    val path = Path().apply {
        moveTo(centerX - 3f * scale, centerY + 2f * scale)
        quadraticBezierTo(centerX, centerY - 2.5f * scale, centerX + 3f * scale, centerY + 2f * scale)
    }
    drawPath(path, Color(0xFF0F3B20), style = Stroke(width = 2f * scale, cap = StrokeCap.Round))
}

/**
 * Draws state-specific celebratory sparkles, musical notes, or waving hands.
 */
private fun DrawScope.drawStateAccents(
    scale: Float,
    width: Float,
    height: Float,
    state: BubaState,
    sway: Float
) {
    val centerX = width * 0.5f

    when (state) {
        BubaState.GREETING -> {
            // Little sprout hand waving on the right
            val waveHand = Path().apply {
                moveTo(centerX + 20f * scale, height * 0.52f)
                quadraticBezierTo(
                    centerX + 30f * scale,
                    height * 0.44f + (sway * 3f * scale),
                    centerX + 26f * scale,
                    height * 0.38f + (sway * 4f * scale)
                )
            }
            drawPath(waveHand, Color(0xFF38B000), style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
        }
        BubaState.LISTENING -> {
            // Cute audio wave ripples in Dew Teal
            val rippleColor = AccentDewTeal.copy(alpha = 0.85f)
            val audioY = height * 0.30f
            // Left sound ripple
            val leftSound = Path().apply {
                moveTo(centerX - 24f * scale, audioY - 4f * scale)
                quadraticBezierTo(centerX - 28f * scale, audioY, centerX - 24f * scale, audioY + 4f * scale)
            }
            drawPath(leftSound, rippleColor, style = Stroke(width = 2f * scale, cap = StrokeCap.Round))

            // Right sound ripple
            val rightSound = Path().apply {
                moveTo(centerX + 24f * scale, audioY - 4f * scale)
                quadraticBezierTo(centerX + 28f * scale, audioY, centerX + 24f * scale, audioY + 4f * scale)
            }
            drawPath(rightSound, rippleColor, style = Stroke(width = 2f * scale, cap = StrokeCap.Round))
        }
        BubaState.CELEBRATING -> {
            // Golden star sparkles around head
            drawStarSparkle(centerX - 22f * scale, height * 0.22f, 4f * scale, AccentSunnyGold)
            drawStarSparkle(centerX + 22f * scale, height * 0.20f, 5f * scale, AccentSunnyGold)
            drawStarSparkle(centerX, height * 0.12f, 3.5f * scale, Color(0xFF70E000))
        }
        BubaState.SEARCHING -> {
            // Small curiosity sparkle
            drawStarSparkle(centerX + 24f * scale, height * 0.36f, 3.5f * scale, AccentSunnyGold)
        }
        BubaState.RESTING, BubaState.THINKING -> {
            // No extra clutter
        }
    }
}

private fun DrawScope.drawStarSparkle(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, cy - radius)
        lineTo(cx + radius * 0.28f, cy - radius * 0.28f)
        lineTo(cx + radius, cy)
        lineTo(cx + radius * 0.28f, cy + radius * 0.28f)
        lineTo(cx, cy + radius)
        lineTo(cx - radius * 0.28f, cy + radius * 0.28f)
        lineTo(cx - radius, cy)
        lineTo(cx - radius * 0.28f, cy - radius * 0.28f)
        close()
    }
    drawPath(path, color, style = Fill)
}
