package id.bubakangreen.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import id.bubakangreen.app.R
import id.bubakangreen.app.domain.model.MasterPlant

/**
 * Robust dual-source image renderer for Bubakan Green.
 * - LOCAL: resolves bundled WebP drawable asset in drawable-nodpi/ (e.g. plant_sereh).
 * - REMOTE_URL: loads via Coil with HTTPS with fallback to mascot_default.
 * - Never crashes, never displays broken icons.
 */
@Composable
fun PlantImage(
    plant: MasterPlant,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackResId: Int = R.drawable.mascot_default
) {
    PlantImage(
        primaryPhotoUrl = plant.primaryPhotoUrl,
        imageSourceType = plant.imageSourceType,
        imageAssetName = plant.imageAssetName,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        fallbackResId = fallbackResId
    )
}

@Composable
fun PlantImage(
    primaryPhotoUrl: String?,
    imageSourceType: String? = null,
    imageAssetName: String? = null,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackResId: Int = R.drawable.mascot_default
) {
    val context = LocalContext.current
    val effectiveLocalAsset = (imageAssetName?.ifBlank { null }
        ?: primaryPhotoUrl?.takeIf { !it.startsWith("http://") && !it.startsWith("https://") }
        ?: run {
            val lower = primaryPhotoUrl?.lowercase() ?: ""
            when {
                lower.contains("kemangi") -> "plant_kemangi"
                lower.contains("pegagan") || lower.contains("centella") -> "plant_pegagan"
                lower.contains("sirih") || lower.contains("piper_betle") -> "plant_sirih"
                lower.contains("cabai") || lower.contains("cabe") -> "plant_cabai"
                lower.contains("jahe") -> "plant_jahe"
                lower.contains("kangkung") -> "plant_kangkung"
                lower.contains("kencur") -> "plant_kencur"
                lower.contains("kunyit") -> "plant_kunyit"
                lower.contains("lidah") || lower.contains("aloe") -> "plant_lidah_buaya"
                lower.contains("sereh") || lower.contains("serai") -> "plant_sereh"
                lower.contains("terong") -> "plant_terong"
                lower.contains("tomat") -> "plant_tomat"
                else -> null
            }
        })?.trim()

    val localResId = remember(effectiveLocalAsset) {
        if (!effectiveLocalAsset.isNullOrBlank()) {
            val id = context.resources.getIdentifier(effectiveLocalAsset, "drawable", context.packageName)
            if (id != 0) id else null
        } else {
            null
        }
    }

    if (localResId != null) {
        Image(
            painter = painterResource(id = localResId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (!primaryPhotoUrl.isNullOrBlank()) {
        val modelData = if (primaryPhotoUrl.startsWith("/")) java.io.File(primaryPhotoUrl) else primaryPhotoUrl
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(modelData)
                .setHeader("User-Agent", "BubakanGreen/1.0 (Android; id.bubakangreen.app)")
                .crossfade(true)
                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                .error(fallbackResId)
                .placeholder(fallbackResId)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = fallbackResId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .padding(8.dp)
            )
        }
    }
}
