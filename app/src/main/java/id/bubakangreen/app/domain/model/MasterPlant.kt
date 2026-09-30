package id.bubakangreen.app.domain.model

/**
 * Botanical Master Plant entity.
 * Serves as the authoritative botanical encyclopedia entry for a species,
 * independent of specific physical garden plots.
 * Complies with Section 11 of Bubakan Green architecture.
 */
data class MasterPlant(
    val id: String,
    val name: String = "",
    val nameId: String = name,
    val scientificName: String = "",
    val nameLatin: String = scientificName,
    val mandarinName: String? = null,
    val nameMandarin: String? = mandarinName,
    val mandarinPinyin: String? = null,
    val pinyin: String? = mandarinPinyin,
    val mandarinAudioUrl: String? = null,
    val description: String = "",
    val benefits: String = "",
    val plantingGuide: String = "",
    val defaultPhotoUrl: String? = null,
    val primaryPhotoUrl: String? = defaultPhotoUrl,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


