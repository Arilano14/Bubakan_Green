package id.bubakangreen.app.ui.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.R
import kotlinx.coroutines.delay

/**
 * Custom 6-Second Branded Splash Screen for Bubakan Green.
 *
 * Requirements:
 * 1. Background: bg_splash_onboarding rendered full-bleed with ContentScale.Crop.
 * 2. Center: new_logo with strictly preserved 1:1 aspect ratio and crisp rendering.
 * 3. Welcoming text timeline:
 *    - 0-3 sec: "Selamat datang di Bubakan Green."
 *    - 3-6 sec: "Kenali tanaman, rawat kebun, dan tumbuhkan manfaat bersama."
 * 4. Visual-only loading indicator decoupled from backend/network/storage services.
 * 5. Lifecycle-safe monotonic timing mechanism (no recomposition reset, no duplicate navigation).
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textIndex by rememberSaveable { mutableIntStateOf(0) }
    var hasNavigated by rememberSaveable { mutableStateOf(false) }

    // Lifecycle-safe 6-second branding timer
    LaunchedEffect(Unit) {
        val t0 = android.os.SystemClock.elapsedRealtime()
        android.util.Log.d("BubakanSplash", "Splash started at t=0ms")

        // Phase 1: 0.0s - 3.0s
        delay(3000L)
        textIndex = 1
        val t1 = android.os.SystemClock.elapsedRealtime() - t0
        android.util.Log.d("BubakanSplash", "Splash transitioned to Phase 2 at t=${t1}ms")

        // Phase 2: 3.0s - 6.0s (Target: 6.0s, acceptable range 5.8s - 6.3s)
        delay(3000L)
        val t2 = android.os.SystemClock.elapsedRealtime() - t0
        android.util.Log.d("BubakanSplash", "Splash completed at t=${t2}ms, triggering navigation")

        if (!hasNavigated) {
            hasNavigated = true
            onSplashComplete()
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isLandscape = maxHeight < 500.dp
        val logoSize = if (isLandscape) 110.dp else 160.dp
        val verticalSpacing = if (isLandscape) 12.dp else 24.dp

        // Full-bleed responsive background
        Image(
            painter = painterResource(id = R.drawable.bg_splash_onboarding),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Centered branding content with system bars insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered sharp new logo
                Image(
                    painter = painterResource(id = R.drawable.new_logo),
                    contentDescription = "Logo Bubakan Green",
                    modifier = Modifier.size(logoSize),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(verticalSpacing))

                // Welcoming text with smooth crossfade
                AnimatedContent(
                    targetState = textIndex,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "splash_welcome_text"
                ) { index ->
                    val text = when (index) {
                        0 -> "Selamat datang di Bubakan Green."
                        else -> "Kenali tanaman, rawat kebun, dan tumbuhkan manfaat bersama."
                    }

                    Text(
                        text = text,
                        fontSize = if (isLandscape) 15.sp else 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1B4332), // Deep Forest Green with optimal contrast on warm earth backdrop
                        textAlign = TextAlign.Center,
                        lineHeight = if (isLandscape) 20.sp else 24.sp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 28.dp))

                // Visual-only lightweight loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF2D6A4F),
                    strokeWidth = 2.5.dp,
                    trackColor = Color(0x332D6A4F)
                )
            }
        }
    }
}
