package id.bubakangreen.app.data.remote

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.repository.StorageRepository

/**
 * Production FirebaseStorageRepository implementation stub.
 * Ready for activation when Firebase Cloud Billing (Blaze Plan) is provisioned.
 * Seamless drop-in replacement for LocalStorageRepository.
 */
class FirebaseStorageRepository(
    private val bucketName: String = "bubakan-green.firebasestorage.app"
) : StorageRepository {

    override suspend fun uploadImage(
        bytes: ByteArray,
        filename: String,
        subfolder: String
    ): Result<String> {
        // Post-Blaze activation hook:
        // val storageRef = FirebaseStorage.getInstance("gs://$bucketName").reference.child("$subfolder/$filename")
        // storageRef.putBytes(bytes).await()
        // return Result.Success(storageRef.downloadUrl.await().toString())
        return Result.Error(
            IllegalStateException("Firebase Storage is awaiting Blaze plan activation."),
            "Firebase Storage belum aktif (Pre-Blaze Phase). Gunakan LocalStorageRepository."
        )
    }

    override suspend fun deleteImage(pathOrUrl: String): Result<Unit> {
        // Post-Blaze activation hook:
        // FirebaseStorage.getInstance().getReferenceFromUrl(pathOrUrl).delete().await()
        return Result.Success(Unit)
    }

    override fun getImageUrl(pathOrUrl: String): String {
        return pathOrUrl
    }
}
