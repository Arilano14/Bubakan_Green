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
    val candidateRaw = imageAssetName?.ifBlank { null }
        ?: primaryPhotoUrl?.takeIf { !it.startsWith("http://") && !it.startsWith("https://") }
        ?: primaryPhotoUrl

    val effectiveLocalAsset = remember(candidateRaw) {
        PlantImageResolver.resolveDrawableName(candidateRaw)
    }

    val localResId = remember(effectiveLocalAsset) {
        if (!effectiveLocalAsset.isNullOrBlank()) {
            val directId = context.resources.getIdentifier(effectiveLocalAsset, "drawable", context.packageName)
            if (directId != 0) directId else null
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

/**
 * Authoritative pattern matching resolver for all 32 botanical plants in Bubakan Green.
 * Maps plant IDs, Indonesian common names, local synonyms, and scientific names to drawable tags.
 */
object PlantImageResolver {
    fun resolveDrawableName(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim()
            .substringAfterLast("/")
            .substringAfterLast("\\")
            .removeSuffix(".webp")
            .removeSuffix(".png")
            .removeSuffix(".jpg")
            .removeSuffix(".jpeg")
            .lowercase()

        val tag = if (clean.startsWith("plant_")) clean else "plant_$clean"

        return when {
            tag == "plant_bayam" || clean.contains("bayam") || clean.contains("amaranth") -> "plant_bayam"
            tag == "plant_bunga_bugang" || clean.contains("bugang") || clean.contains("calamitosum") -> "plant_bunga_bugang"
            tag == "plant_bunga_kenanga" || clean.contains("kenanga") || clean.contains("cananga") -> "plant_bunga_kenanga"
            tag == "plant_cabai" || clean.contains("cabai") || clean.contains("cabe") || clean.contains("capsicum") -> "plant_cabai"
            tag == "plant_caisim" || clean.contains("caisim") || clean.contains("choy sum") -> "plant_caisim"
            tag == "plant_cepokak" || clean.contains("cepokak") || clean.contains("takokak") || clean.contains("torvum") -> "plant_cepokak"
            tag == "plant_ciplukan" || clean.contains("ciplukan") || clean.contains("physalis") -> "plant_ciplukan"
            tag == "plant_daun_bawang" || clean.contains("daun_bawang") || clean.contains("bawang daun") || clean.contains("fistulosum") -> "plant_daun_bawang"
            tag == "plant_daun_sintrong" || clean.contains("sintrong") || clean.contains("crassocephalum") -> "plant_daun_sintrong"
            tag == "plant_durian" || clean.contains("durian") || clean.contains("durio") -> "plant_durian"
            tag == "plant_jahe" || clean.contains("jahe") || clean.contains("zingiber") -> "plant_jahe"
            tag == "plant_kacang_panjang" || clean.contains("kacang_panjang") || clean.contains("sesquipedalis") -> "plant_kacang_panjang"
            tag == "plant_kangkung" || clean.contains("kangkung") || clean.contains("ipomoea") -> "plant_kangkung"
            tag == "plant_kemangi" || clean.contains("kemangi") || clean.contains("basilicum") -> "plant_kemangi"
            tag == "plant_kencur" || clean.contains("kencur") || clean.contains("kaempferia") -> "plant_kencur"
            tag == "plant_kitolot" || clean.contains("kitolot") || clean.contains("korejat") || clean.contains("hippobroma") -> "plant_kitolot"
            tag == "plant_kucai" || clean.contains("kucai") || clean.contains("tuberosum") -> "plant_kucai"
            tag == "plant_kumis_kucing" || clean.contains("kumis_kucing") || clean.contains("orthosiphon") -> "plant_kumis_kucing"
            tag == "plant_kunyit" || clean.contains("kunyit") || clean.contains("curcuma") -> "plant_kunyit"
            tag == "plant_lengkuas" || clean.contains("lengkuas") || clean.contains("laos") || clean.contains("alpinia") -> "plant_lengkuas"
            tag == "plant_lidah_buaya" || clean.contains("lidah") || clean.contains("buaya") || clean.contains("aloe") -> "plant_lidah_buaya"
            tag == "plant_mahoni" || clean.contains("mahoni") || clean.contains("swietenia") -> "plant_mahoni"
            tag == "plant_pakcoy" || clean.contains("pakcoy") || clean.contains("pokchoy") || clean.contains("bok choy") -> "plant_pakcoy"
            tag == "plant_pandan" || clean.contains("pandan") -> "plant_pandan"
            tag == "plant_pegagan" || clean.contains("pegagan") || clean.contains("centella") -> "plant_pegagan"
            tag == "plant_pepaya" || clean.contains("pepaya") || clean.contains("papaya") || clean.contains("carica") -> "plant_pepaya"
            tag == "plant_seledri" || clean.contains("seledri") || clean.contains("celery") || clean.contains("apium") -> "plant_seledri"
            tag == "plant_sereh" || clean.contains("sereh") || clean.contains("serai") || clean.contains("cymbopogon") -> "plant_sereh"
            tag == "plant_sirih" || clean.contains("sirih") || clean.contains("piper_betle") || clean.contains("betle") -> "plant_sirih"
            tag == "plant_talas" || clean.contains("talas") || clean.contains("taro") || clean.contains("colocasia") -> "plant_talas"
            tag == "plant_terong" || clean.contains("terong") || clean.contains("melongena") -> "plant_terong"
            tag == "plant_tomat" || clean.contains("tomat") || clean.contains("lycopersicum") -> "plant_tomat"
            else -> tag
        }
    }
}
