package id.bubakangreen.app.core.util

/**
 * Deterministic URL builder for QR codes and deep links.
 * Enforces the approved contract: QR encodes solely an HTTPS URL with an entity ID.
 */
object QrUrlBuilder {
    const val DEFAULT_DOMAIN = "bubakangreen.web.app"

    fun buildPlantUrl(masterPlantId: String, domain: String = DEFAULT_DOMAIN): String {
        require(masterPlantId.isNotBlank()) { "masterPlantId cannot be blank" }
        return "https://$domain/plant/${masterPlantId.trim()}"
    }

    fun buildLocationUrl(locationId: String, domain: String = DEFAULT_DOMAIN): String {
        require(locationId.isNotBlank()) { "locationId cannot be blank" }
        return "https://$domain/location/${locationId.trim()}"
    }
}
