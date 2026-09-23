package id.bubakangreen.app.domain.model

enum class LocationType {
    URBAN_FARMING,
    TAMAN_TOGA
}

enum class LocationStatus {
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
 */
data class Location(
    val id: String,
    val name: String,
    val type: LocationType,
    val rw: String,
    val address: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesStatus: CoordinatesStatus = CoordinatesStatus.PENDING,
    val featured: Boolean = false,
    val photoUrl: String? = null,
    val picUid: String,
    val status: LocationStatus = LocationStatus.PENDING_APPROVAL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
