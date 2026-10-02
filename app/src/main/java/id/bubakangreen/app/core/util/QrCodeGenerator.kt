package id.bubakangreen.app.core.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

/**
 * QrCodeGenerator: Deterministic visual QR Code bitmap generator.
 * Encodes solely the verified canonical HTTPS URL per Phase 5 contract.
 */
object QrCodeGenerator {

    /**
     * Determines whether a resource meets the production eligibility criteria for generating a QR code.
     * Enforces Section D: only existing, published, and active resources are eligible.
     */
    fun isEligibleForQr(isPublished: Boolean, status: String): Boolean {
        if (!isPublished) return false
        val s = status.uppercase().trim()
        return s == "ACTIVE" || s == "PUBLISHED"
    }

    /**
     * Generates an Android Bitmap representing the QR code for a given canonical URL.
     *
     * @param content Canonical HTTPS URL to encode.
     * @param sizePx Pixel width and height of the resulting square bitmap (default: 512px).
     * @param foregroundColor ARGB color for the QR modules (default: dark botanical green 0xFF1B4D3E).
     * @param backgroundColor ARGB color for the background (default: pure white 0xFFFFFFFF).
     * @return Generated Bitmap, or null if encoding fails.
     */
    /**
     * Encodes a string into a ZXing BitMatrix.
     * Accessible for testing and headless QR verification without Android graphics dependencies.
     */
    fun generateQrBitMatrix(
        content: String,
        sizePx: Int = 512,
        margin: Int = 1
    ): com.google.zxing.common.BitMatrix? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
                put(EncodeHintType.MARGIN, margin)
            }
            val writer = QRCodeWriter()
            writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Generates an Android Bitmap representing the QR code for a given canonical URL.
     *
     * @param content Canonical HTTPS URL to encode.
     * @param sizePx Pixel width and height of the resulting square bitmap (default: 512px).
     * @param foregroundColor ARGB color for the QR modules (default: dark botanical green 0xFF1B4D3E).
     * @param backgroundColor ARGB color for the background (default: pure white 0xFFFFFFFF).
     * @return Generated Bitmap, or null if encoding fails.
     */
    fun generateQrBitmap(
        content: String,
        sizePx: Int = 512,
        foregroundColor: Int = Color.parseColor("#1B4D3E"),
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        val bitMatrix = generateQrBitMatrix(content, sizePx) ?: return null
        return try {
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (_: Exception) {
            null
        }
    }
}
