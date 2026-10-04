package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.PlantVoice
import id.bubakangreen.app.domain.repository.VoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreVoiceRepository(
    private val firestore: FirebaseFirestore
) : VoiceRepository {

    private val voiceCollection by lazy {
        firestore.collection("plant_voices")
    }

    override fun getVoiceByPlant(plantId: String): Flow<Result<PlantVoice?>> {
        return voiceCollection
            .document(plantId)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.toPlantVoice()) as Result<PlantVoice?>
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override suspend fun setPlantVoice(voice: PlantVoice): Result<Unit> {
        return try {
            voiceCollection.document(voice.plantId).set(voice.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    companion object {
        fun PlantVoice.toMap(): Map<String, Any?> = mapOf(
            "plantId" to plantId,
            "language" to language,
            "audioUrl" to audioUrl,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toPlantVoice(): PlantVoice? {
            if (!exists()) return null
            val pId = getString("plantId") ?: id
            val lang = getString("language") ?: "zh-CN"
            val url = getString("audioUrl")
            val active = getBoolean("isActive") ?: true
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return PlantVoice(
                plantId = pId,
                language = lang,
                audioUrl = url,
                isActive = active,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
