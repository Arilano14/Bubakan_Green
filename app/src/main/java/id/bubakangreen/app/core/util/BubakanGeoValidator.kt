package id.bubakangreen.app.core.util

/**
 * Official Administrative Boundary Geospatial Validator for Kelurahan Bubakan.
 *
 * Source: Pemerintah Kota Semarang (dataspasial.semarangkota.go.id - FID: 16)
 * Jurisdiction: Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang (Kode Wilayah: 33.74.14.1002)
 *
 * Enforces strict Point-in-Polygon (Jordan curve / ray casting) geospatial validation
 * for all garden location creations and public map marker eligibility.
 */
object BubakanGeoValidator {

    const val KODE_WILAYAH = "33.74.14.1002"
    const val KELURAHAN_NAME = "Bubakan"
    const val KECAMATAN_NAME = "Mijen"
    const val KOTA_NAME = "Semarang"

    // Bounding Box limits with safety margin
    const val MIN_LAT = -7.106384
    const val MAX_LAT = -7.083224
    const val MIN_LNG = 110.313648
    const val MAX_LNG = 110.330410

    // Official Kantor Kelurahan Bubakan Anchor Point
    const val DEFAULT_CENTER_LAT = -7.092370
    const val DEFAULT_CENTER_LNG = 110.320360

    // 182-point official boundary polygon coordinates: [longitude, latitude]
    private val BOUNDARY_POINTS = arrayOf(
        doubleArrayOf(110.329927, -7.083757),
        doubleArrayOf(110.329873, -7.083453),
        doubleArrayOf(110.329771, -7.083224),
        doubleArrayOf(110.329514, -7.083523),
        doubleArrayOf(110.329288, -7.083868),
        doubleArrayOf(110.329162, -7.083978),
        doubleArrayOf(110.329028, -7.084015),
        doubleArrayOf(110.328856, -7.083962),
        doubleArrayOf(110.328537, -7.084041),
        doubleArrayOf(110.328484, -7.084103),
        doubleArrayOf(110.328371, -7.084521),
        doubleArrayOf(110.32821, -7.08471),
        doubleArrayOf(110.328111, -7.08472),
        doubleArrayOf(110.327561, -7.08438),
        doubleArrayOf(110.327271, -7.084284),
        doubleArrayOf(110.32608, -7.084294),
        doubleArrayOf(110.325807, -7.084231),
        doubleArrayOf(110.325689, -7.084108),
        doubleArrayOf(110.324954, -7.084779),
        doubleArrayOf(110.324653, -7.084928),
        doubleArrayOf(110.324138, -7.084896),
        doubleArrayOf(110.323704, -7.085034),
        doubleArrayOf(110.320587, -7.085024),
        doubleArrayOf(110.318238, -7.084971),
        doubleArrayOf(110.317275, -7.084981),
        doubleArrayOf(110.316556, -7.085056),
        doubleArrayOf(110.316175, -7.08516),
        doubleArrayOf(110.315888, -7.085362),
        doubleArrayOf(110.315848, -7.08545),
        doubleArrayOf(110.315732, -7.086296),
        doubleArrayOf(110.314977, -7.08628),
        doubleArrayOf(110.31456, -7.086319),
        doubleArrayOf(110.31417, -7.086269),
        doubleArrayOf(110.314064, -7.086335),
        doubleArrayOf(110.313874, -7.086602),
        doubleArrayOf(110.313844, -7.086858),
        doubleArrayOf(110.313899, -7.087133),
        doubleArrayOf(110.314, -7.087576),
        doubleArrayOf(110.31426, -7.088332),
        doubleArrayOf(110.31444, -7.088753),
        doubleArrayOf(110.314552, -7.088936),
        doubleArrayOf(110.314788, -7.089136),
        doubleArrayOf(110.314894, -7.089259),
        doubleArrayOf(110.314953, -7.089394),
        doubleArrayOf(110.314926, -7.089541),
        doubleArrayOf(110.314757, -7.089841),
        doubleArrayOf(110.314195, -7.090446),
        doubleArrayOf(110.313957, -7.090749),
        doubleArrayOf(110.313753, -7.091087),
        doubleArrayOf(110.313664, -7.091279),
        doubleArrayOf(110.313659, -7.091574),
        doubleArrayOf(110.313718, -7.091726),
        doubleArrayOf(110.31419, -7.091931),
        doubleArrayOf(110.314271, -7.092016),
        doubleArrayOf(110.314252, -7.092168),
        doubleArrayOf(110.314488, -7.092567),
        doubleArrayOf(110.314558, -7.092966),
        doubleArrayOf(110.314735, -7.093525),
        doubleArrayOf(110.314954, -7.094047),
        doubleArrayOf(110.315276, -7.095633),
        doubleArrayOf(110.31504, -7.096283),
        doubleArrayOf(110.314772, -7.096581),
        doubleArrayOf(110.314633, -7.096985),
        doubleArrayOf(110.314676, -7.097283),
        doubleArrayOf(110.314858, -7.097635),
        doubleArrayOf(110.315534, -7.098306),
        doubleArrayOf(110.315652, -7.098817),
        doubleArrayOf(110.315588, -7.099498),
        doubleArrayOf(110.315609, -7.09986),
        doubleArrayOf(110.315909, -7.10019),
        doubleArrayOf(110.316338, -7.100552),
        doubleArrayOf(110.316521, -7.100882),
        doubleArrayOf(110.316811, -7.101627),
        doubleArrayOf(110.317197, -7.102128),
        doubleArrayOf(110.317175, -7.102564),
        doubleArrayOf(110.317079, -7.102841),
        doubleArrayOf(110.316929, -7.103054),
        doubleArrayOf(110.316888, -7.103339),
        doubleArrayOf(110.316811, -7.103443),
        doubleArrayOf(110.316824, -7.103522),
        doubleArrayOf(110.316958, -7.103637),
        doubleArrayOf(110.317063, -7.103794),
        doubleArrayOf(110.317004, -7.1039),
        doubleArrayOf(110.316623, -7.104124),
        doubleArrayOf(110.31651, -7.104433),
        doubleArrayOf(110.316789, -7.104731),
        doubleArrayOf(110.316896, -7.105114),
        doubleArrayOf(110.31688, -7.105625),
        doubleArrayOf(110.316921, -7.105747),
        doubleArrayOf(110.317178, -7.106109),
        doubleArrayOf(110.317208, -7.106384),
        doubleArrayOf(110.317489, -7.106269),
        doubleArrayOf(110.317556, -7.105857),
        doubleArrayOf(110.318184, -7.106024),
        doubleArrayOf(110.318399, -7.106157),
        doubleArrayOf(110.31887, -7.106227),
        doubleArrayOf(110.318999, -7.10603),
        doubleArrayOf(110.319702, -7.105545),
        doubleArrayOf(110.320265, -7.105364),
        doubleArrayOf(110.320405, -7.104699),
        doubleArrayOf(110.320598, -7.1043),
        doubleArrayOf(110.322178, -7.103099),
        doubleArrayOf(110.322513, -7.102466),
        doubleArrayOf(110.322862, -7.102053),
        doubleArrayOf(110.32299, -7.102005),
        doubleArrayOf(110.32313, -7.101867),
        doubleArrayOf(110.323006, -7.100988),
        doubleArrayOf(110.323065, -7.100754),
        doubleArrayOf(110.32321, -7.100706),
        doubleArrayOf(110.323463, -7.100211),
        doubleArrayOf(110.323682, -7.099913),
        doubleArrayOf(110.323736, -7.099658),
        doubleArrayOf(110.323436, -7.099328),
        doubleArrayOf(110.323307, -7.09895),
        doubleArrayOf(110.323345, -7.098774),
        doubleArrayOf(110.323629, -7.098391),
        doubleArrayOf(110.324037, -7.09814),
        doubleArrayOf(110.32424, -7.097768),
        doubleArrayOf(110.324374, -7.097252),
        doubleArrayOf(110.324353, -7.096879),
        doubleArrayOf(110.324535, -7.096474),
        doubleArrayOf(110.324723, -7.096288),
        doubleArrayOf(110.324884, -7.096049),
        doubleArrayOf(110.324881, -7.095705),
        doubleArrayOf(110.324927, -7.095479),
        doubleArrayOf(110.325013, -7.095346),
        doubleArrayOf(110.325434, -7.095109),
        doubleArrayOf(110.32548, -7.094962),
        doubleArrayOf(110.325715, -7.094664),
        doubleArrayOf(110.326056, -7.094348),
        doubleArrayOf(110.32618, -7.094271),
        doubleArrayOf(110.326445, -7.093722),
        doubleArrayOf(110.326708, -7.093485),
        doubleArrayOf(110.326839, -7.093411),
        doubleArrayOf(110.326998, -7.093376),
        doubleArrayOf(110.327083, -7.093294),
        doubleArrayOf(110.327239, -7.092783),
        doubleArrayOf(110.327218, -7.092594),
        doubleArrayOf(110.327293, -7.092407),
        doubleArrayOf(110.327395, -7.092258),
        doubleArrayOf(110.327405, -7.091912),
        doubleArrayOf(110.327481, -7.091737),
        doubleArrayOf(110.327534, -7.091391),
        doubleArrayOf(110.327459, -7.091172),
        doubleArrayOf(110.327175, -7.091007),
        doubleArrayOf(110.326885, -7.090954),
        doubleArrayOf(110.326595, -7.090816),
        doubleArrayOf(110.326472, -7.090571),
        doubleArrayOf(110.326456, -7.089996),
        doubleArrayOf(110.326279, -7.089442),
        doubleArrayOf(110.325919, -7.089336),
        doubleArrayOf(110.325831, -7.089269),
        doubleArrayOf(110.325809, -7.089184),
        doubleArrayOf(110.325952, -7.08903),
        doubleArrayOf(110.326, -7.088878),
        doubleArrayOf(110.326005, -7.088271),
        doubleArrayOf(110.326172, -7.088135),
        doubleArrayOf(110.326684, -7.087914),
        doubleArrayOf(110.327132, -7.087821),
        doubleArrayOf(110.327328, -7.08785),
        doubleArrayOf(110.32747, -7.087784),
        doubleArrayOf(110.327714, -7.087472),
        doubleArrayOf(110.328087, -7.087347),
        doubleArrayOf(110.328532, -7.087092),
        doubleArrayOf(110.328771, -7.087124),
        doubleArrayOf(110.32898, -7.087012),
        doubleArrayOf(110.329063, -7.0869),
        doubleArrayOf(110.329181, -7.086855),
        doubleArrayOf(110.329436, -7.086615),
        doubleArrayOf(110.32991, -7.0864),
        doubleArrayOf(110.330267, -7.086086),
        doubleArrayOf(110.330337, -7.08595),
        doubleArrayOf(110.330299, -7.085849),
        doubleArrayOf(110.330203, -7.08575),
        doubleArrayOf(110.329999, -7.085657),
        doubleArrayOf(110.329894, -7.085503),
        doubleArrayOf(110.330018, -7.085157),
        doubleArrayOf(110.330179, -7.08505),
        doubleArrayOf(110.330356, -7.084848),
        doubleArrayOf(110.330409, -7.084545),
        doubleArrayOf(110.329996, -7.084103),
        doubleArrayOf(110.329927, -7.083757)
    )

    /**
     * Evaluates whether a given GPS coordinate [latitude], [longitude]
     * falls strictly inside the official administrative boundary of Kelurahan Bubakan.
     *
     * Uses O(1) bounding-box pre-filtering followed by Ray-Casting Point-in-Polygon algorithm.
     */
    fun isInsideBubakan(latitude: Double?, longitude: Double?): Boolean {
        if (latitude == null || longitude == null) return false
        if (latitude.isNaN() || longitude.isNaN()) return false
        if (latitude == 0.0 && longitude == 0.0) return false

        // Quick bounding box check
        if (latitude < MIN_LAT || latitude > MAX_LAT || longitude < MIN_LNG || longitude > MAX_LNG) {
            return false
        }

        // Ray casting algorithm
        var inside = false
        val n = BOUNDARY_POINTS.size
        var j = n - 1

        for (i in 0 until n) {
            val xi = BOUNDARY_POINTS[i][0] // Longitude
            val yi = BOUNDARY_POINTS[i][1] // Latitude
            val xj = BOUNDARY_POINTS[j][0]
            val yj = BOUNDARY_POINTS[j][1]

            val intersect = ((yi > latitude) != (yj > latitude)) &&
                    (longitude < (xj - xi) * (latitude - yi) / (yj - yi) + xi)

            if (intersect) {
                inside = !inside
            }
            j = i
        }

        return inside
    }

    /**
     * Returns a human-friendly validation error message if the coordinates are outside Bubakan,
     * or null if the coordinates are strictly valid.
     */
    fun validateCoordinates(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) {
            return "Titik koordinat GPS wajib dikunci terlebih dahulu."
        }
        if (!isInsideBubakan(latitude, longitude)) {
            val formatted = String.format(java.util.Locale.US, "%.5f, %.5f", latitude, longitude)
            return "Titik koordinat ($formatted) berada di luar wilayah administratif Kelurahan Bubakan."
        }
        return null
    }
}
