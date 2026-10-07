package id.bubakangreen.app.data.local

import android.content.Context
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Local file-based StorageRepository implementation.
 * Used during Pre-Blaze development and testing. Stores compressed media
 * in the application's internal files storage.
 */
class LocalStorageRepository(
    private val context: Context
) : StorageRepository {

    override suspend fun uploadImage(
        bytes: ByteArray,
        filename: String,
        subfolder: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, subfolder).apply {
                if (!exists()) mkdirs()
            }
            val sanitizedName = filename.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val targetFile = File(dir, sanitizedName)
            FileOutputStream(targetFile).use { it.write(bytes) }
            Result.Success(targetFile.absolutePath)
        } catch (e: Exception) {
            Result.Error(e, "Gagal menyimpan gambar secara lokal: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteImage(pathOrUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (pathOrUrl.startsWith("/")) {
                val file = File(pathOrUrl)
                if (file.exists()) {
                    file.delete()
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Gagal menghapus gambar: ${e.localizedMessage}")
        }
    }

    override fun getImageUrl(pathOrUrl: String): String {
        return pathOrUrl
    }
}
