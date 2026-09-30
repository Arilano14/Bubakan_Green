package id.bubakangreen.app.domain.model

enum class PlantStatus {
    ACTIVE,
    ARCHIVED
}

enum class PlantCondition {
    GOOD,
    NEEDS_ATTENTION,
    NOT_AVAILABLE
}

/**
 * Junction entity linking a MasterPlant species to a specific Location garden plot.
 * Contains site-specific details such as condition, quantities, and presence.
 * Complies with Section 12 & 13 of Bubakan Green architecture.
 */
data class LocationPlant(
    val id: String,
    val locationId: String,
    val plantId: String = "",
    val masterPlantId: String = plantId,
    val photoUrl: String? = null,
    val localPhotoUrl: String? = photoUrl,
    val condition: PlantCondition = PlantCondition.GOOD,
    val quantity: Int = 1,
    val quantityNote: String? = null,
    val notes: String? = null,
    val isPresent: Boolean = true,
    val featuredForQr: Boolean = false,
    val status: PlantStatus = PlantStatus.ACTIVE,
    val createdBy: String? = null,
    val updatedBy: String? = createdBy,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

