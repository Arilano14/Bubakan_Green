package id.bubakangreen.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Image compression and storage path utility.
 * Compresses images off the main thread to target long edge ~1600px, quality 80%.
 * Complies with Section 10 & 11 of Bubakan Green architecture.
 */
object ImageCompressor {

    fun getLocationCoverPath(locationId: String): String =
        "locations/$locationId/cover.jpg"

    fun getLocationConditionPath(locationId: String, timestamp: Long = System.currentTimeMillis()): String =
        "locations/$locationId/condition/$timestamp.jpg"

    fun getLocationPlantPath(locationPlantId: String, timestamp: Long = System.currentTimeMillis()): String =
        "location-plants/$locationPlantId/$timestamp.jpg"

    suspend fun compressBitmap(
        bitmap: Bitmap,
        maxDimension: Int = 1600,
        quality: Int = 80
    ): ByteArray = withContext(Dispatchers.IO) {
        val scaled = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val scale = maxDimension.toFloat() / max(bitmap.width, bitmap.height)
            val newW = (bitmap.width * scale).toInt()
            val newH = (bitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        } else {
            bitmap
        }
        val outStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
        outStream.toByteArray()
    }

    suspend fun saveCompressedToAppCache(
        context: Context,
        bytes: ByteArray,
        prefix: String = "photo"
    ): String = withContext(Dispatchers.IO) {
        val photosDir = File(context.filesDir, "photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val file = File(photosDir, "${prefix}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { it.write(bytes) }
        file.absolutePath
    }

    suspend fun compressAndResizeImage(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1600,
        quality: Int = 80
    ): ByteArray = withContext(Dispatchers.IO) {
        // Read EXIF orientation
        val orientation = runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL

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

        // Correct EXIF orientation
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(safeBitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(safeBitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(safeBitmap, 270f)
            else -> safeBitmap
        }

        // Final scale if needed
        val scaledBitmap = if (rotatedBitmap.width > maxDimension || rotatedBitmap.height > maxDimension) {
            val scale = maxDimension.toFloat() / max(rotatedBitmap.width, rotatedBitmap.height)
            val newW = (rotatedBitmap.width * scale).toInt()
            val newH = (rotatedBitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(rotatedBitmap, newW, newH, true)
        } else {
            rotatedBitmap
        }

        val outStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
        outStream.toByteArray()
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
