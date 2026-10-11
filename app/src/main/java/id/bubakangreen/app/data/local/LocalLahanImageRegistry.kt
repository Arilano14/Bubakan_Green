package id.bubakangreen.app.data.local

import androidx.annotation.DrawableRes
import id.bubakangreen.app.R
import id.bubakangreen.app.domain.model.Location

/**
 * Metadata untuk tag gambar lahan lokal.
 *
 * FITUR SEMENTARA:
 * Digunakan sebagai jembatan tampilan visual lahan di APK selagi Firebase Storage
 * belum aktif/dikonfigurasi (Pre-Blaze plan).
 *
 * ARSITEKTUR TERPISAH:
 * - Modul ini tidak menghapus, memodifikasi, atau bergantung pada skema Firebase Firestore.
 * - Resolusi gambar dilakukan sepenuhnya di sisi klien APK (client-side resolution).
 * - Ketika Firebase Storage nanti diaktifkan dengan URL remote (https://firebasestorage...),
 *   komponen UI akan otomatis memuat gambar remote tanpa merusak alur data.
 */
data class LocalLahanTag(
    val tag: String,
    val title: String,
    val description: String,
    @DrawableRes val drawableRes: Int,
    val rw: String = "01",
    val locationIdHint: String = "loc_urban_farming_toga_rw01"
)

object LocalLahanImageRegistry {

    val TAG_RW1_1 = LocalLahanTag(
        tag = "lahan_rw1_1",
        title = "RW 01 — Gerbang & Pintu Masuk",
        description = "Papan nama dan gerbang masuk Urban Farming & Taman Toga RW 01",
        drawableRes = R.drawable.lahan_rw1_1,
        rw = "01"
    )

    val TAG_RW1_2 = LocalLahanTag(
        tag = "lahan_rw1_2",
        title = "RW 01 — Bedeng Pembibitan & Sayur",
        description = "Area bedeng sayuran pekarangan dan instalasi pembibitan RW 01",
        drawableRes = R.drawable.lahan_rw1_2,
        rw = "01"
    )

    val TAG_RW1_3 = LocalLahanTag(
        tag = "lahan_rw1_3",
        title = "RW 01 — Kebun TOGA & Konservasi Hijau",
        description = "Koleksi tanaman obat keluarga (TOGA) dan konservasi hijau RW 01",
        drawableRes = R.drawable.lahan_rw1_3,
        rw = "01"
    )

    val ALL_TAGS: List<LocalLahanTag> = listOf(TAG_RW1_1, TAG_RW1_2, TAG_RW1_3)

    /**
     * Mencocokkan input tag atau path string ke objek [LocalLahanTag].
     * Mendukung format tag langsung, format tag ber-prefix, dan path Firestore eksisting.
     */
    fun findTag(raw: String?): LocalLahanTag? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim().lowercase()

        // 1. Direct or prefixed tag match
        val directMatch = ALL_TAGS.firstOrNull {
            clean == it.tag.lowercase() ||
            clean == "tag:${it.tag.lowercase()}" ||
            clean == "local:${it.tag.lowercase()}" ||
            clean == "${it.tag.lowercase()}.webp" ||
            clean == "${it.tag.lowercase()}.jpg" ||
            clean == "${it.tag.lowercase()}.jpeg"
        }
        if (directMatch != null) return directMatch

        // 2. Substring matching untuk nama file aktual (misal: /assets/Lahan/RW 1_1.jpeg)
        return when {
            clean.contains("rw 1_1") || clean.contains("rw1_1") || clean.contains("rw_1_1") || clean.contains("rw 1 1") -> TAG_RW1_1
            clean.contains("rw 1_2") || clean.contains("rw1_2") || clean.contains("rw_1_2") || clean.contains("rw 1 2") -> TAG_RW1_2
            clean.contains("rw 1_3") || clean.contains("rw1_3") || clean.contains("rw_1_3") || clean.contains("rw 1 3") -> TAG_RW1_3
            else -> null
        }
    }

    /**
     * Mengembalikan Resource ID drawable jika terdaftar sebagai tag/foto lahan lokal,
     * atau null jika berupa URL remote atau tidak dikenali.
     */
    fun resolveDrawable(raw: String?): Int? = findTag(raw)?.drawableRes

    /**
     * Mengambil seluruh daftar foto yang tersedia untuk suatu [Location].
     * Setiap item pada list dapat bertipe Int (Resource ID drawable) atau String (Remote HTTPS URL).
     */
    fun resolvePhotosForLocation(location: Location): List<Any> {
        val candidates = mutableListOf<String>()
        candidates.addAll(location.photos)
        location.coverPhotoUrl?.let { if (!candidates.contains(it)) candidates.add(it) }
        location.photoUrl?.let { if (!candidates.contains(it)) candidates.add(it) }

        val resolved = mutableListOf<Any>()
        for (item in candidates) {
            val trimmed = item.trim()
            if (trimmed.isBlank()) continue
            val drawableId = resolveDrawable(trimmed)
            if (drawableId != null) {
                if (!resolved.contains(drawableId)) {
                    resolved.add(drawableId)
                }
            } else if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
                if (!resolved.contains(trimmed)) {
                    resolved.add(trimmed)
                }
            }
        }

        // Fallback otomatis berbasis identitas wilayah jika belum ada foto terdaftar
        if (resolved.isEmpty()) {
            val isRw01 = location.id == "loc_urban_farming_toga_rw01" ||
                location.rw.trim() in listOf("01", "1", "RW 01", "RW 1") ||
                location.name.contains("RW 01", ignoreCase = true) ||
                location.name.contains("RW 1", ignoreCase = true)

            if (isRw01) {
                return ALL_TAGS.map { it.drawableRes }
            }
        }

        return resolved
    }

    /**
     * Mengembalikan foto utama (primary/cover) untuk suatu [Location].
     */
    fun resolvePrimaryPhoto(location: Location): Any? {
        return resolvePhotosForLocation(location).firstOrNull()
    }
}
