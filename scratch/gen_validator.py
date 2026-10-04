import json

with open('app/src/main/assets/map/bubakan_boundary.geojson') as f:
    data = json.load(f)

coords = data['features'][0]['geometry']['coordinates'][0]
print(f'Total coords: {len(coords)}')

kt_points = ',\n'.join(f'        doubleArrayOf({lng}, {lat})' for lng, lat in coords)

kt_content = '''package id.bubakangreen.app.core.util

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
''' + kt_points + '''
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
'''

with open('app/src/main/java/id/bubakangreen/app/core/util/BubakanGeoValidator.kt', 'w', encoding='utf-8') as f:
    f.write(kt_content)

print('Generated BubakanGeoValidator.kt successfully!')
