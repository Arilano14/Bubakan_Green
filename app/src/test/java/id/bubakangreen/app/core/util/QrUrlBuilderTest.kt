package id.bubakangreen.app.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class QrUrlBuilderTest {

    @Test
    fun buildPlantUrl_producesDeterministicContractUrl() {
        val url = QrUrlBuilder.buildPlantUrl("TEST_PLANT_123")
        assertThat(url).isEqualTo("https://bubakangreen.web.app/plant/TEST_PLANT_123")
    }

    @Test
    fun buildLocationUrl_producesDeterministicContractUrl() {
        val url = QrUrlBuilder.buildLocationUrl("TEST_LOC_456")
        assertThat(url).isEqualTo("https://bubakangreen.web.app/location/TEST_LOC_456")
    }

    @Test
    fun buildPlantUrl_withBlankId_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            QrUrlBuilder.buildPlantUrl("   ")
        }
    }

    @Test
    fun buildPlantUrl_withInvalidCharacters_throwsException() {
        assertThrows(IllegalArgumentException::class.java) {
            QrUrlBuilder.buildPlantUrl("pl-test space!#")
        }
    }

    @Test
    fun parseCanonicalUrl_validPlantUrl_returnsPlant() {
        val result = QrUrlBuilder.parseCanonicalUrl("https://bubakangreen.web.app/plant/pl-a84f21c9")
        assertThat(result).isInstanceOf(ParsedQrResult.Plant::class.java)
        assertThat((result as ParsedQrResult.Plant).stableId).isEqualTo("pl-a84f21c9")
    }

    @Test
    fun parseCanonicalUrl_validLocationUrl_returnsLocation() {
        val result = QrUrlBuilder.parseCanonicalUrl("https://bubakangreen.web.app/location/loc-91af7b23")
        assertThat(result).isInstanceOf(ParsedQrResult.Location::class.java)
        assertThat((result as ParsedQrResult.Location).stableId).isEqualTo("loc-91af7b23")
    }

    @Test
    fun parseCanonicalUrl_insecureHttpScheme_returnsInvalid() {
        val result = QrUrlBuilder.parseCanonicalUrl("http://bubakangreen.web.app/plant/pl-123")
        assertThat(result).isInstanceOf(ParsedQrResult.Invalid::class.java)
        assertThat((result as ParsedQrResult.Invalid).reason).contains("Insecure")
    }

    @Test
    fun parseCanonicalUrl_foreignHost_returnsInvalid() {
        val result = QrUrlBuilder.parseCanonicalUrl("https://evil-site.com/plant/pl-123")
        assertThat(result).isInstanceOf(ParsedQrResult.Invalid::class.java)
        assertThat((result as ParsedQrResult.Invalid).reason).contains("Unsupported domain")
    }

    @Test
    fun parseCanonicalUrl_malformedPath_returnsInvalid() {
        val result = QrUrlBuilder.parseCanonicalUrl("https://bubakangreen.web.app/unknown_route/pl-123")
        assertThat(result).isInstanceOf(ParsedQrResult.Invalid::class.java)
    }

    @Test
    fun parseCanonicalUrl_blankOrMalformed_returnsInvalid() {
        val blankResult = QrUrlBuilder.parseCanonicalUrl("   ")
        assertThat(blankResult).isInstanceOf(ParsedQrResult.Invalid::class.java)

        val malformedResult = QrUrlBuilder.parseCanonicalUrl("not_a_valid_url_at_all")
        assertThat(malformedResult).isInstanceOf(ParsedQrResult.Invalid::class.java)
    }
}
