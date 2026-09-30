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

/**
 * First-class Location domain entity.
 * Represents an Urban Farming or Taman Toga site in Kelurahan Bubakan.
 * Complies with Section 7 & 8 of Bubakan Green architecture.
 */
data class Location(
    val id: String,
    val name: String,
    val type: LocationType,
    val rw: String,
    val address: String = "",
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesStatus: CoordinatesStatus = CoordinatesStatus.PENDING,
    val featured: Boolean = false,
    val coverPhotoUrl: String? = null,
    val photoUrl: String? = coverPhotoUrl,
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
)

