package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result

/**
 * Storage abstraction allowing seamless operation between Local/Test storage
 * and production Cloud Firebase Storage (pre-Blaze vs post-Blaze).
 */
interface StorageRepository {
    /**
     * Uploads an image byte array to storage and returns the access URL or file path.
     */
    suspend fun uploadImage(bytes: ByteArray, filename: String, subfolder: String = "photos"): Result<String>

    /**
     * Deletes an image from storage given its URL or storage path.
     */
    suspend fun deleteImage(pathOrUrl: String): Result<Unit>

    /**
     * Resolves an image URL or local storage path for presentation.
     */
    fun getImageUrl(pathOrUrl: String): String = pathOrUrl
}
