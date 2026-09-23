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
}
