package id.bubakangreen.app.domain.model

enum class LocationType {
    URBAN_FARMING,
    TAMAN_TOGA
}

enum class LocationStatus {
    ACTIVE,
    NEEDS_MAINTENANCE,
    TEMPORARILY_INACTIVE,
    INACTIVE,
    DRAFT,
    PENDING_APPROVAL,
    PUBLISHED,
    ARCHIVED
}

enum class CoordinatesStatus {
    PENDING,
    VERIFIED
}

enum class RegionTag(val code: String, val label: String) {
    KELURAHAN("KELURAHAN", "Kelurahan"),
    RW_01("RW_01", "RW 01"),
    RW_02("RW_02", "RW 02"),
    RW_03("RW_03", "RW 03"),
    RW_04("RW_04", "RW 04"),
    RW_05("RW_05", "RW 05");

    companion object {
        fun fromCodeOrRw(input: String): RegionTag? {
            val normalized = input.trim().uppercase()
            return entries.find { it.code == normalized }
                ?: when (input.trim()) {
                    "01", "1", "RW 01", "RW 1" -> RW_01
                    "02", "2", "RW 02", "RW 2" -> RW_02
                    "03", "3", "RW 03", "RW 3" -> RW_03
                    "04", "4", "RW 04", "RW 4" -> RW_04
                    "05", "5", "RW 05", "RW 5" -> RW_05
                    "Kelurahan", "KELURAHAN" -> KELURAHAN
                    else -> null
                }
        }
    }
}

/**
 * First-class Location domain entity.
 * Represents an Urban Farming or Taman Toga site in Kelurahan Bubakan.
 * Complies with Section 7 & 8 of Bubakan Green architecture.
 */
data class Location(
    val id: String,
    val name: String,
    val type: LocationType,
    val rw: String = "01",
    val regionTag: String = "",
    val address: String = "",
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val coordinatesStatus: CoordinatesStatus = CoordinatesStatus.PENDING,
    val featured: Boolean = false,
    val photos: List<String> = emptyList(),
    val coverPhotoUrl: String? = photos.firstOrNull(),
    val photoUrl: String? = coverPhotoUrl ?: photos.firstOrNull(),
    val conditionNote: String = "",
    val conditionUpdatedAt: Long? = null,
    val conditionUpdatedBy: String? = null,
    val isPublished: Boolean = true,
    val createdBy: String = "",
    val picUid: String = createdBy,
    val status: LocationStatus = LocationStatus.ACTIVE,
    val accuracyMeters: Float? = null,
    val capturedAt: Long? = null,
    val rejectionNote: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayRegionTag: String
        get() = when {
            regionTag.isNotBlank() -> when (regionTag) {
                "KELURAHAN" -> "Kelurahan"
                "RW_01" -> "RW 01"
                "RW_02" -> "RW 02"
                "RW_03" -> "RW 03"
                "RW_04" -> "RW 04"
                "RW_05" -> "RW 05"
                else -> regionTag
            }
            rw.isNotBlank() -> if (rw.startsWith("RW", ignoreCase = true) || rw.equals("Kelurahan", ignoreCase = true)) rw else "RW $rw"
            else -> "Kelurahan Bubakan"
        }
}


