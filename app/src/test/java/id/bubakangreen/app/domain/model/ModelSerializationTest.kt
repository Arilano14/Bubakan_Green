package id.bubakangreen.app.domain.model

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.data.remote.FirestoreLocationRepository.Companion.toMap
import id.bubakangreen.app.data.remote.FirestorePlantRepository.Companion.toMap
import org.junit.Test

class ModelSerializationTest {

    @Test
    fun location_toMap_containsAllRequiredFields() {
        val testLocation = Location(
            id = "TEST_FIXTURE_LOC_001",
            name = "Test Garden Plot",
            type = LocationType.TAMAN_TOGA,
            rw = "03",
            address = "Test Street No. 1",
            description = "Test description for validation",
            latitude = -7.0501,
            longitude = 110.3201,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            photoUrl = "https://example.com/test.jpg",
            picUid = "TEST_PIC_USER_001",
            status = LocationStatus.PUBLISHED,
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L
        )

        val map = testLocation.toMap()

        assertThat(map["id"]).isEqualTo("TEST_FIXTURE_LOC_001")
        assertThat(map["name"]).isEqualTo("Test Garden Plot")
        assertThat(map["type"]).isEqualTo("TAMAN_TOGA")
        assertThat(map["rw"]).isEqualTo("03")
        assertThat(map["latitude"]).isEqualTo(-7.0501)
        assertThat(map["longitude"]).isEqualTo(110.3201)
        assertThat(map["coordinatesStatus"]).isEqualTo("VERIFIED")
        assertThat(map["featured"]).isEqualTo(true)
        assertThat(map["status"]).isEqualTo("PUBLISHED")
        assertThat(map["isPublished"]).isEqualTo(true)
    }

    @Test
    fun masterPlant_toMap_containsBotanicalFields() {
        val testMasterPlant = MasterPlant(
            id = "TEST_FIXTURE_PLANT_001",
            name = "Test Plant Name",
            scientificName = "Testus botanicus",
            mandarinName = "测试植物",
            mandarinPinyin = "cè shì zhí wù",
            description = "Test herbal and pharmacological characteristics",
            benefits = "Meredakan inflamasi dan batuk",
            plantingGuide = "Siram 2 kali sehari di tempat teduh",
            defaultPhotoUrl = "https://example.com/plant.jpg",
            mandarinAudioUrl = "https://example.com/audio.mp3"
        )

        val map = testMasterPlant.toMap()

        assertThat(map["id"]).isEqualTo("TEST_FIXTURE_PLANT_001")
        assertThat(map["name"]).isEqualTo("Test Plant Name")
        assertThat(map["nameId"]).isEqualTo("Test Plant Name")
        assertThat(map["scientificName"]).isEqualTo("Testus botanicus")
        assertThat(map["nameLatin"]).isEqualTo("Testus botanicus")
        assertThat(map["mandarinName"]).isEqualTo("测试植物")
        assertThat(map["mandarinPinyin"]).isEqualTo("cè shì zhí wù")
        assertThat(map["benefits"]).isEqualTo("Meredakan inflamasi dan batuk")
        assertThat(map["plantingGuide"]).isEqualTo("Siram 2 kali sehari di tempat teduh")
        assertThat(map["mandarinAudioUrl"]).isEqualTo("https://example.com/audio.mp3")
    }

    @Test
    fun locationPlant_toMap_containsJunctionFields() {
        val testLocationPlant = LocationPlant(
            id = "TEST_FIXTURE_LP_001",
            locationId = "TEST_FIXTURE_LOC_001",
            plantId = "TEST_FIXTURE_PLANT_001",
            condition = PlantCondition.GOOD,
            quantity = 10,
            quantityNote = "10 polybag",
            notes = "Bedeng herbal sisi utara",
            isPresent = true,
            featuredForQr = true,
            status = PlantStatus.ACTIVE
        )

        val map = testLocationPlant.toMap()

        assertThat(map["id"]).isEqualTo("TEST_FIXTURE_LP_001")
        assertThat(map["locationId"]).isEqualTo("TEST_FIXTURE_LOC_001")
        assertThat(map["plantId"]).isEqualTo("TEST_FIXTURE_PLANT_001")
        assertThat(map["masterPlantId"]).isEqualTo("TEST_FIXTURE_PLANT_001")
        assertThat(map["condition"]).isEqualTo("GOOD")
        assertThat(map["quantity"]).isEqualTo(10)
        assertThat(map["isPresent"]).isEqualTo(true)
        assertThat(map["quantityNote"]).isEqualTo("10 polybag")
        assertThat(map["featuredForQr"]).isEqualTo(true)
        assertThat(map["status"]).isEqualTo("ACTIVE")
    }
}

