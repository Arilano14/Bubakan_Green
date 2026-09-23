package id.bubakangreen.app.domain.model

enum class PlantStatus {
    ACTIVE,
    ARCHIVED
}

/**
 * Junction entity linking a MasterPlant species to a specific Location garden plot.
 * Contains site-specific details such as bed location notes, quantities, and QR eligibility.
 */
data class LocationPlant(
    val id: String,
    val locationId: String,
    val masterPlantId: String,
    val localPhotoUrl: String? = null,
    val quantityNote: String? = null,
    val notes: String? = null,
    val featuredForQr: Boolean = false,
    val status: PlantStatus = PlantStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
