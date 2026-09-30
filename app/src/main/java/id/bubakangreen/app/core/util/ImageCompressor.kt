package id.bubakangreen.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * Image compression and storage path utility.
 * Compresses images off the main thread to max dimension ~1280px, quality 80%.
 * Complies with Section 14 & 15 of Bubakan Green architecture.
 */
object ImageCompressor {

    fun getLocationCoverPath(locationId: String): String =
        "locations/$locationId/cover.jpg"

    fun getLocationConditionPath(locationId: String, timestamp: Long = System.currentTimeMillis()): String =
        "locations/$locationId/condition/$timestamp.jpg"

    fun getLocationPlantPath(locationPlantId: String, timestamp: Long = System.currentTimeMillis()): String =
        "location-plants/$locationPlantId/$timestamp.jpg"

    suspend fun compressAndResizeImage(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1280,
        quality: Int = 80
    ): ByteArray = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open stream for Uri: $uri")

        // First decode bounds
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        val width = options.outWidth
        val height = options.outHeight

        var sampleSize = 1
        val maxSide = max(width, height)
        if (maxSide > maxDimension) {
            sampleSize = maxSide / maxDimension
        }

        // Decode sampled bitmap
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val sampledStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot re-open stream for Uri: $uri")
        val bitmap = BitmapFactory.decodeStream(sampledStream, null, decodeOptions)
        sampledStream.close()

        val safeBitmap = bitmap ?: throw IllegalStateException("Bitmap decode failed for Uri: $uri")

        // Final scale if needed
        val scaledBitmap = if (safeBitmap.width > maxDimension || safeBitmap.height > maxDimension) {
            val scale = maxDimension.toFloat() / max(safeBitmap.width, safeBitmap.height)
            val newW = (safeBitmap.width * scale).toInt()
            val newH = (safeBitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(safeBitmap, newW, newH, true)
        } else {
            safeBitmap
        }

        val outStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
        outStream.toByteArray()
    }
}
