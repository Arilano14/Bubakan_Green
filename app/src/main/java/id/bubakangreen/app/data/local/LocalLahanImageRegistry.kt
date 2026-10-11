package id.bubakangreen.app.data.local

import androidx.annotation.DrawableRes
import id.bubakangreen.app.R
import id.bubakangreen.app.domain.model.Location
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Tipe sumber foto lahan untuk membedakan mode lokal dan remote cloud.
 */
enum class LandImageSourceType {
    /** Foto statis dari aset web/repo (/assets/Lahan/...) */
    LOCAL_ASSET,
    /** Tag drawable lokal APK (R.drawable.lahan_*) */
    LOCAL_TAG,
    /** Remote HTTPS URL umum */
    REMOTE_URL,
    /** Remote URL dari Google Firebase Storage */
    FIREBASE_STORAGE
}

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
    val locationIdHint: String? = null
)

object LocalLahanImageRegistry {

    // ==========================================
    // RW 01: Urban Farming & Taman TOGA
    // ==========================================
    val TAG_RW1_1 = LocalLahanTag(
        tag = "lahan_rw1_1",
        title = "RW 01 — Gerbang & Pintu Masuk",
        description = "Papan nama dan gerbang masuk Urban Farming & Taman Toga RW 01",
        drawableRes = R.drawable.lahan_rw1_1,
        rw = "01",
        locationIdHint = "loc_urban_farming_toga_rw01"
    )

    val TAG_RW1_2 = LocalLahanTag(
        tag = "lahan_rw1_2",
        title = "RW 01 — Bedeng Pembibitan & Sayur",
        description = "Area bedeng sayuran pekarangan dan instalasi pembibitan RW 01",
        drawableRes = R.drawable.lahan_rw1_2,
        rw = "01",
        locationIdHint = "loc_urban_farming_toga_rw01"
    )

    val TAG_RW1_3 = LocalLahanTag(
        tag = "lahan_rw1_3",
        title = "RW 01 — Kebun TOGA & Konservasi Hijau",
        description = "Koleksi tanaman obat keluarga (TOGA) dan konservasi hijau RW 01",
        drawableRes = R.drawable.lahan_rw1_3,
        rw = "01",
        locationIdHint = "loc_urban_farming_toga_rw01"
    )

    // ==========================================
    // RW 02: Taman TOGA
    // ==========================================
    val TAG_RW2_TOGA_1 = LocalLahanTag(
        tag = "lahan_rw2_toga_1",
        title = "RW 02 — Taman Toga Utama",
        description = "Area utama kebun Tanaman Obat Keluarga (TOGA) RW 02",
        drawableRes = R.drawable.lahan_rw2_toga_1,
        rw = "02",
        locationIdHint = "loc_taman_toga_rw02"
    )

    val TAG_RW2_TOGA_2 = LocalLahanTag(
        tag = "lahan_rw2_toga_2",
        title = "RW 02 — Koleksi Herbal & Rimpang",
        description = "Bedeng tanaman herbal dan rimpang obat RW 02",
        drawableRes = R.drawable.lahan_rw2_toga_2,
        rw = "02",
        locationIdHint = "loc_taman_toga_rw02"
    )

    val TAG_RW2_TOGA_3 = LocalLahanTag(
        tag = "lahan_rw2_toga_3",
        title = "RW 02 — Penataan Pot & Konservasi",
        description = "Penataan tanaman obat dalam pot dan konservasi pekarangan RW 02",
        drawableRes = R.drawable.lahan_rw2_toga_3,
        rw = "02",
        locationIdHint = "loc_taman_toga_rw02"
    )

    // ==========================================
    // RW 03: Taman TOGA (Kolaborasi KKN)
    // ==========================================
    val TAG_RW3_TOGA_1 = LocalLahanTag(
        tag = "lahan_rw3_toga_1",
        title = "RW 03 — Taman TOGA KKN",
        description = "Koleksi tanaman obat keluarga kolaborasi warga dan Mahasiswa KKN",
        drawableRes = R.drawable.lahan_rw3_toga_1,
        rw = "03",
        locationIdHint = "loc_taman_toga_rw03"
    )

    val TAG_RW3_TOGA_2 = LocalLahanTag(
        tag = "lahan_rw3_toga_2",
        title = "RW 03 — Bedeng TOGA Percontohan",
        description = "Bedeng percontohan tanaman rimpang dan herba TOGA RW 03",
        drawableRes = R.drawable.lahan_rw3_toga_2,
        rw = "03",
        locationIdHint = "loc_taman_toga_rw03"
    )

    val TAG_RW3_TOGA_3 = LocalLahanTag(
        tag = "lahan_rw3_toga_3",
        title = "RW 03 — Area Konservasi Herbal",
        description = "Area konservasi tanaman herba dan pekarangan hijau RW 03",
        drawableRes = R.drawable.lahan_rw3_toga_3,
        rw = "03",
        locationIdHint = "loc_taman_toga_rw03"
    )

    // ==========================================
    // RW 03: Urban Farming
    // ==========================================
    val TAG_RW3_URBAN_1 = LocalLahanTag(
        tag = "lahan_rw3_urban_1",
        title = "RW 03 — Urban Farming Utama",
        description = "Lahan produktif pertanian perkotaan dan tanaman pangan RW 03",
        drawableRes = R.drawable.lahan_rw3_urban_1,
        rw = "03",
        locationIdHint = "loc_urban_farming_rw03"
    )

    val TAG_RW3_URBAN_2 = LocalLahanTag(
        tag = "lahan_rw3_urban_2",
        title = "RW 03 — Jalur Tanam Pangan",
        description = "Jalur budidaya tanaman pangan mandiri warga RW 03",
        drawableRes = R.drawable.lahan_rw3_urban_2,
        rw = "03",
        locationIdHint = "loc_urban_farming_rw03"
    )

    val TAG_RW3_URBAN_3 = LocalLahanTag(
        tag = "lahan_rw3_urban_3",
        title = "RW 03 — Area Pohon Buah & Durian",
        description = "Kawasan budidaya pohon buah dan durian produktif RW 03",
        drawableRes = R.drawable.lahan_rw3_urban_3,
        rw = "03",
        locationIdHint = "loc_urban_farming_rw03"
    )

    val ALL_TAGS: List<LocalLahanTag> = listOf(
        TAG_RW1_1, TAG_RW1_2, TAG_RW1_3,
        TAG_RW2_TOGA_1, TAG_RW2_TOGA_2, TAG_RW2_TOGA_3,
        TAG_RW3_TOGA_1, TAG_RW3_TOGA_2, TAG_RW3_TOGA_3,
        TAG_RW3_URBAN_1, TAG_RW3_URBAN_2, TAG_RW3_URBAN_3
    )

    /**
     * Mendeteksi jenis sumber foto (LOCAL_ASSET, LOCAL_TAG, REMOTE_URL, atau FIREBASE_STORAGE).
     */
    fun detectSourceType(raw: String?): LandImageSourceType {
        if (raw.isNullOrBlank()) return LandImageSourceType.LOCAL_TAG
        val clean = raw.trim().lowercase()
        return when {
            clean.contains("firebasestorage.googleapis.com") -> LandImageSourceType.FIREBASE_STORAGE
            clean.startsWith("http://") || clean.startsWith("https://") -> LandImageSourceType.REMOTE_URL
            clean.startsWith("/assets/lahan/") || clean.startsWith("assets/lahan/") -> LandImageSourceType.LOCAL_ASSET
            else -> LandImageSourceType.LOCAL_TAG
        }
    }

    /**
     * Mengonversi nama file foto menjadi URL relatif web yang ter-encode dengan benar.
     * Contoh: "RW 1_1.jpeg" -> "/assets/Lahan/RW%201_1.jpeg"
     */
    fun formatWebAssetUrl(filename: String): String {
        val clean = filename.trim().removePrefix("/").removePrefix("assets/Lahan/").removePrefix("assets/lahan/")
        val encoded = try {
            URLEncoder.encode(clean, "UTF-8").replace("+", "%20")
        } catch (e: Exception) {
            clean.replace(" ", "%20")
        }
        return "/assets/Lahan/$encoded"
    }

    /**
     * Mencocokkan input tag atau path string ke objek [LocalLahanTag].
     * Mendukung format tag langsung, format tag ber-prefix, path Firestore eksisting,
     * serta format URL-encoded (%20).
     */
    fun findTag(raw: String?): LocalLahanTag? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim().lowercase()
        val decoded = try {
            URLDecoder.decode(clean, "UTF-8").lowercase()
        } catch (e: Exception) {
            clean.replace("%20", " ")
        }

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

        // 2. Substring matching untuk nama file aktual di Firestore / web assets (mendukung variasi spasi dan %20)
        fun matches(pattern: String): Boolean = clean.contains(pattern) || decoded.contains(pattern)

        return when {
            // RW 1
            matches("rw 1_1") || matches("rw1_1") || matches("rw_1_1") -> TAG_RW1_1
            matches("rw 1_2") || matches("rw1_2") || matches("rw_1_2") -> TAG_RW1_2
            matches("rw 1_3") || matches("rw1_3") || matches("rw_1_3") -> TAG_RW1_3

            // RW 2
            matches("rw 2_toga 1") || matches("rw2_toga 1") || matches("rw2_toga_1") -> TAG_RW2_TOGA_2
            matches("rw 2_toga 2") || matches("rw2_toga 2") || matches("rw2_toga_2") -> TAG_RW2_TOGA_3
            matches("rw 2_toga") || matches("rw2_toga") -> TAG_RW2_TOGA_1

            // RW 3 Toga
            matches("rw 3_toga 2") || matches("rw3_toga 2") || matches("rw3_toga_2") -> TAG_RW3_TOGA_2
            matches("rw 3_toga 3") || matches("rw3_toga 3") || matches("rw3_toga_3") -> TAG_RW3_TOGA_3
            matches("rw 3_toga") || matches("rw3_toga") -> TAG_RW3_TOGA_1

            // RW 3 Urban
            matches("rw 3_urban 2") || matches("rw3_urban 2") || matches("rw3_urban_2") -> TAG_RW3_URBAN_2
            matches("rw 3_urban 3") || matches("rw3_urban 3") || matches("rw3_urban_3") -> TAG_RW3_URBAN_3
            matches("rw 3_urban") || matches("rw3_urban") -> TAG_RW3_URBAN_1

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
            val locId = location.id.lowercase()
            val name = location.name.lowercase()
            val rw = location.rw.trim()

            return when {
                // RW 1
                locId.contains("rw01") || locId.contains("rw1") || rw in listOf("01", "1", "RW 01", "RW 1") || name.contains("rw 1") || name.contains("rw 01") -> {
                    listOf(TAG_RW1_1.drawableRes, TAG_RW1_2.drawableRes, TAG_RW1_3.drawableRes)
                }
                // RW 3 Toga
                locId == "loc_taman_toga_rw03" || (name.contains("toga") && (name.contains("rw 3") || name.contains("rw 03") || rw in listOf("03", "3", "RW 03", "RW 3"))) -> {
                    listOf(TAG_RW3_TOGA_1.drawableRes, TAG_RW3_TOGA_2.drawableRes, TAG_RW3_TOGA_3.drawableRes)
                }
                // RW 3 Urban Farming
                locId == "loc_urban_farming_rw03" || (name.contains("urban") && (name.contains("rw 3") || name.contains("rw 03") || rw in listOf("03", "3", "RW 03", "RW 3"))) -> {
                    listOf(TAG_RW3_URBAN_1.drawableRes, TAG_RW3_URBAN_2.drawableRes, TAG_RW3_URBAN_3.drawableRes)
                }
                // RW 2
                locId.contains("rw02") || locId.contains("rw2") || rw in listOf("02", "2", "RW 02", "RW 2") || name.contains("rw 2") || name.contains("rw 02") -> {
                    listOf(TAG_RW2_TOGA_1.drawableRes, TAG_RW2_TOGA_2.drawableRes, TAG_RW2_TOGA_3.drawableRes)
                }
                else -> emptyList()
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
