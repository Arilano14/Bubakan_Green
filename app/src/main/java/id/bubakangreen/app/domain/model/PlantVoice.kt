package id.bubakangreen.app.domain.model

/**
 * Optional botanical Mandarin pronunciation audio entity.
 * Stored in /plant_voices/{plantId} with stable plantId matching MasterPlant.
 * Does not duplicate nomenclature (Hanzi, Pinyin) which lives in MasterPlant.
 */
data class PlantVoice(
    val plantId: String,
    val language: String = "zh-CN",
    val audioUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
