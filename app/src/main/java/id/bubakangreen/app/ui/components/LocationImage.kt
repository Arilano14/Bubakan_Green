package id.bubakangreen.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import id.bubakangreen.app.data.local.LocalLahanImageRegistry
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.OnAccentGoldDark
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen

/**
 * Komponen rendering foto lahan Bubakan Green dengan dukungan ganda:
 * 1. Tag lokal / bundled asset APK (resolusi lokal cepat tanpa internet).
 * 2. Remote URL (kompatibel penuh jika kelak Firebase Storage diaktifkan).
 * 3. Graceful fallback placeholder tematik jika foto belum ada.
 */
@Composable
fun LocationImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isUrbanFarming: Boolean = true,
    placeholderContent: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val resolvedDrawableId = remember(model) {
        when (model) {
            is Int -> model
            is String -> LocalLahanImageRegistry.resolveDrawable(model)
            else -> null
        }
    }

    if (resolvedDrawableId != null) {
        Image(
            painter = painterResource(id = resolvedDrawableId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (model is String && (model.startsWith("http://", ignoreCase = true) || model.startsWith("https://", ignoreCase = true))) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(model)
                .setHeader("User-Agent", "BubakanGreen/1.0 (Android; id.bubakangreen.app)")
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        if (placeholderContent != null) {
            placeholderContent()
        } else {
            Box(
                modifier = modifier.background(if (isUrbanFarming) PrimaryContainerMint else AccentSunnyContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUrbanFarming) "🌱 Urban Farming" else "🌿 Taman Toga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUrbanFarming) PrimarySeedlingGreen else OnAccentGoldDark
                )
            }
        }
    }
}

/**
 * Overload yang menerima entity [Location] langsung dan menentukan foto terbaik secara otomatis.
 */
@Composable
fun LocationImage(
    location: Location,
    contentDescription: String? = "Foto ${location.name}",
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    photoIndex: Int = 0,
    placeholderContent: (@Composable () -> Unit)? = null
) {
    val photos = remember(location) { LocalLahanImageRegistry.resolvePhotosForLocation(location) }
    val targetPhoto = photos.getOrNull(photoIndex)

    LocationImage(
        model = targetPhoto,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        isUrbanFarming = location.type == LocationType.URBAN_FARMING,
        placeholderContent = placeholderContent
    )
}
