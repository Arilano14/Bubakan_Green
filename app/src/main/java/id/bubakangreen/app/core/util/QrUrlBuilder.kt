package id.bubakangreen.app.core.util

import java.net.URI

sealed class ParsedQrResult {
    data class Plant(val stableId: String) : ParsedQrResult()
    data class Location(val stableId: String) : ParsedQrResult()
    data class Invalid(val reason: String) : ParsedQrResult()
}

/**
 * Deterministic URL builder and validator for QR codes and deep links.
 * Enforces the approved Phase 5 contract:
 * - QR encodes solely an HTTPS URL with an immutable stable ID.
 * - Allowed paths: /plant/<id> and /location/<id>.
 * - Canonical domain: bubakangreen.web.app.
 */
object QrUrlBuilder {
    const val DEFAULT_DOMAIN = "bubakangreen.web.app"
    private val VALID_DOMAINS = setOf("bubakangreen.web.app", "bubakangreen.app")
    private val STABLE_ID_REGEX = Regex("^[a-zA-Z0-9_-]+$")

    fun buildPlantUrl(masterPlantId: String, domain: String = DEFAULT_DOMAIN): String {
        require(masterPlantId.isNotBlank()) { "masterPlantId cannot be blank" }
        require(isValidStableId(masterPlantId)) { "masterPlantId contains invalid characters: $masterPlantId" }
        return "https://$domain/plant/${masterPlantId.trim()}"
    }

    fun buildLocationUrl(locationId: String, domain: String = DEFAULT_DOMAIN): String {
        require(locationId.isNotBlank()) { "locationId cannot be blank" }
        require(isValidStableId(locationId)) { "locationId contains invalid characters: $locationId" }
        return "https://$domain/location/${locationId.trim()}"
    }

    fun isValidStableId(id: String): Boolean {
        val trimmed = id.trim()
        return trimmed.isNotEmpty() && STABLE_ID_REGEX.matches(trimmed)
    }

    fun parseCanonicalUrl(rawUrl: String): ParsedQrResult {
        if (rawUrl.isBlank()) return ParsedQrResult.Invalid("URL is empty or blank.")

        val uri = try {
            URI(rawUrl.trim())
        } catch (_: Exception) {
            return ParsedQrResult.Invalid("Malformed URL.")
        }

        if (uri.scheme?.lowercase() != "https") {
            return ParsedQrResult.Invalid("Insecure or unsupported scheme: ${uri.scheme}. Expected https.")
        }

        val host = uri.host?.lowercase() ?: return ParsedQrResult.Invalid("Missing host in URL.")
        if (host !in VALID_DOMAINS) {
            return ParsedQrResult.Invalid("Unsupported domain: $host. Expected canonical domain.")
        }

        if (!uri.query.isNullOrBlank()) {
            return ParsedQrResult.Invalid("Canonical URL must not contain query parameters.")
        }

        if (!uri.fragment.isNullOrBlank()) {
            return ParsedQrResult.Invalid("Canonical URL must not contain fragments.")
        }

        val path = uri.path ?: return ParsedQrResult.Invalid("Empty path.")
        val segments = path.split("/").filter { it.isNotBlank() }

        if (segments.size != 2) {
            return ParsedQrResult.Invalid("Unsupported path format. Expected /plant/<id> or /location/<id>.")
        }

        val resourceType = segments[0].lowercase()
        val stableId = segments[1]

        if (!isValidStableId(stableId)) {
            return ParsedQrResult.Invalid("Invalid resource ID characters.")
        }

        return when (resourceType) {
            "plant" -> ParsedQrResult.Plant(stableId)
            "location" -> ParsedQrResult.Location(stableId)
            else -> ParsedQrResult.Invalid("Unknown resource type: $resourceType")
        }
    }
}
