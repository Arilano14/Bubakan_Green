package id.bubakangreen.app.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class QrCodeGeneratorTest {

    @Test
    fun isEligibleForQr_whenPublishedAndActive_returnsTrue() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "ACTIVE")
        assertThat(result).isTrue()
    }

    @Test
    fun isEligibleForQr_whenPublishedAndPublishedStatus_returnsTrue() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "PUBLISHED")
        assertThat(result).isTrue()
    }

    @Test
    fun isEligibleForQr_whenNotPublished_returnsFalse() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = false, status = "ACTIVE")
        assertThat(result).isFalse()
    }

    @Test
    fun isEligibleForQr_whenStatusPendingReview_returnsFalse() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "PENDING_REVIEW")
        assertThat(result).isFalse()
    }

    @Test
    fun isEligibleForQr_whenStatusTestOnly_returnsFalse() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "TEST_ONLY")
        assertThat(result).isFalse()
    }

    @Test
    fun isEligibleForQr_whenStatusDraftOrArchived_returnsFalse() {
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "DRAFT")).isFalse()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "ARCHIVED")).isFalse()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "REJECTED")).isFalse()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "INACTIVE")).isFalse()
    }

    @Test
    fun isEligibleForQr_caseInsensitiveHandling_returnsTrue() {
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "active")).isTrue()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "  Active  ")).isTrue()
    }

    @Test
    fun generateQrBitMatrix_andDecode_returnsExactCanonicalUrl() {
        val canonicalUrl = "https://bubakan-green.web.app/plant/pl-cabai-rawit"
        val bitMatrix = QrCodeGenerator.generateQrBitMatrix(canonicalUrl, sizePx = 256)
        assertThat(bitMatrix).isNotNull()

        // Decode the generated matrix using ZXing QRCodeReader
        val width = bitMatrix!!.width
        val height = bitMatrix.height
        val luminance = object : com.google.zxing.LuminanceSource(width, height) {
            override fun getRow(y: Int, row: ByteArray?): ByteArray {
                val r = row ?: ByteArray(width)
                for (x in 0 until width) {
                    r[x] = if (bitMatrix.get(x, y)) 0.toByte() else 255.toByte()
                }
                return r
            }
            override fun getMatrix(): ByteArray {
                val r = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        r[y * width + x] = if (bitMatrix.get(x, y)) 0.toByte() else 255.toByte()
                    }
                }
                return r
            }
        }

        val binaryBitmap = com.google.zxing.BinaryBitmap(com.google.zxing.common.HybridBinarizer(luminance))
        val reader = com.google.zxing.qrcode.QRCodeReader()
        val decodeResult = reader.decode(binaryBitmap)

        assertThat(decodeResult.text).isEqualTo(canonicalUrl)
    }

    @Test
    fun generateQrBitMatrix_withBlankContent_returnsNull() {
        val result = QrCodeGenerator.generateQrBitMatrix("   ")
        assertThat(result).isNull()
    }
}

