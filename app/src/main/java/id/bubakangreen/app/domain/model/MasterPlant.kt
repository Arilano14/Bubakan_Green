package id.bubakangreen.app.domain.model

/**
 * Botanical Master Plant entity.
 * Serves as the authoritative botanical encyclopedia entry for a species,
 * independent of specific physical garden plots.
 */
data class MasterPlant(
    val id: String,
    val nameId: String,
    val nameLatin: String,
    val nameMandarin: String? = null,
    val pinyin: String? = null,
    val description: String,
    val primaryPhotoUrl: String? = null,
    val mandarinAudioUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
