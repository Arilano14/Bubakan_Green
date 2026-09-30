package id.bubakangreen.app.domain.model

/**
 * Lightweight condition log record.
 * Tracks who changed garden condition, when, and what was noted.
 * Complies with Section 16 of Bubakan Green architecture.
 */
data class LocationConditionLog(
    val id: String = "",
    val locationId: String,
    val status: String,
    val note: String = "",
    val photoUrl: String? = null,
    val updatedBy: String,
    val updatedAt: Long = System.currentTimeMillis()
)
