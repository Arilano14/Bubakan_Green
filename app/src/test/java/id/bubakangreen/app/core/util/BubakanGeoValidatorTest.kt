package id.bubakangreen.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BubakanGeoValidatorTest {

    @Test
    fun isInsideBubakan_returnsTrueForKnownInternalCoordinates() {
        // Kantor Kelurahan Bubakan (Official Municipal Site)
        assertTrue(
            "Kantor Kelurahan Bubakan should be inside",
            BubakanGeoValidator.isInsideBubakan(-7.09236996, 110.3203602)
        )

        // RW 01 Bubakan
        assertTrue(
            "RW 01 coordinate should be inside",
            BubakanGeoValidator.isInsideBubakan(-7.09350, 110.32150)
        )

        // RW 03 Bubakan
        assertTrue(
            "RW 03 coordinate should be inside",
            BubakanGeoValidator.isInsideBubakan(-7.09050, 110.32300)
        )

        // RW 05 Bubakan
        assertTrue(
            "RW 05 coordinate should be inside",
            BubakanGeoValidator.isInsideBubakan(-7.09600, 110.31850)
        )
    }

    @Test
    fun isInsideBubakan_returnsFalseForNeighboringKelurahanAndDistantLocations() {
        // Kelurahan Mijen (North of Bubakan)
        assertFalse(
            "Kelurahan Mijen should be rejected",
            BubakanGeoValidator.isInsideBubakan(-7.06810, 110.32890)
        )

        // Kelurahan Tambangan (North)
        assertFalse(
            "Tambangan should be rejected",
            BubakanGeoValidator.isInsideBubakan(-7.07949, 110.31806)
        )

        // Kelurahan Cangkiran (West)
        assertFalse(
            "Cangkiran should be rejected",
            BubakanGeoValidator.isInsideBubakan(-7.09316, 110.30898)
        )

        // Kecamatan Boja, Kendal (South)
        assertFalse(
            "Boja should be rejected",
            BubakanGeoValidator.isInsideBubakan(-7.11200, 110.32000)
        )

        // Simpang Lima Semarang (Downtown)
        assertFalse(
            "Downtown Semarang should be rejected",
            BubakanGeoValidator.isInsideBubakan(-6.99040, 110.42290)
        )
    }

    @Test
    fun isInsideBubakan_handlesNullAndInvalidCoordinatesSafely() {
        assertFalse("Null lat should return false", BubakanGeoValidator.isInsideBubakan(null, 110.320))
        assertFalse("Null lng should return false", BubakanGeoValidator.isInsideBubakan(-7.092, null))
        assertFalse("Null both should return false", BubakanGeoValidator.isInsideBubakan(null, null))
        assertFalse("Zero coordinates should return false", BubakanGeoValidator.isInsideBubakan(0.0, 0.0))
        assertFalse("NaN should return false", BubakanGeoValidator.isInsideBubakan(Double.NaN, 110.320))
    }

    @Test
    fun validateCoordinates_returnsErrorMessageWhenOutsideBoundary() {
        val error = BubakanGeoValidator.validateCoordinates(-7.06810, 110.32890)
        assertNotNull(error)
        assertTrue(error!!.contains("di luar wilayah administratif Kelurahan Bubakan"))

        val valid = BubakanGeoValidator.validateCoordinates(-7.09237, 110.32036)
        assertNull("Valid Bubakan coordinates should produce null error", valid)
    }
}
