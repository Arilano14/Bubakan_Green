package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.PlantStatus
import id.bubakangreen.app.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestorePlantRepository(
    private val firestore: FirebaseFirestore
) : PlantRepository {

    private val masterPlantsCollection by lazy {
        firestore.collection("master_plants")
    }

    private val locationPlantsCollection by lazy {
        firestore.collection("location_plants")
    }

    override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> {
        return masterPlantsCollection
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toMasterPlant() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> {
        return masterPlantsCollection.document(plantId).snapshots().map { snapshot ->
            Result.Success(snapshot.toMasterPlant())
        }.catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> {
        return locationPlantsCollection
            .whereEqualTo("locationId", locationId)
            .whereEqualTo("status", PlantStatus.ACTIVE.name)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toLocationPlant() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override suspend fun createMasterPlant(plant: MasterPlant): Result<String> {
        return try {
            val docRef = if (plant.id.isNotBlank()) {
                masterPlantsCollection.document(plant.id)
            } else {
                masterPlantsCollection.document()
            }
            val finalPlant = plant.copy(id = docRef.id)
            docRef.set(finalPlant.toMap()).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String> {
        return try {
            val docRef = if (locationPlant.id.isNotBlank()) {
                locationPlantsCollection.document(locationPlant.id)
            } else {
                locationPlantsCollection.document()
            }
            val finalPlant = locationPlant.copy(id = docRef.id)
            docRef.set(finalPlant.toMap()).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit> {
        return try {
            locationPlantsCollection.document(locationPlant.id).set(locationPlant.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    companion object {
        fun MasterPlant.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "nameId" to nameId,
            "nameLatin" to nameLatin,
            "nameMandarin" to nameMandarin,
            "pinyin" to pinyin,
            "description" to description,
            "primaryPhotoUrl" to primaryPhotoUrl,
            "mandarinAudioUrl" to mandarinAudioUrl,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toMasterPlant(): MasterPlant? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val nameId = getString("nameId") ?: return null
            val nameLatin = getString("nameLatin") ?: ""
            val nameMandarin = getString("nameMandarin")
            val pinyin = getString("pinyin")
            val description = getString("description") ?: ""
            val primaryPhotoUrl = getString("primaryPhotoUrl")
            val mandarinAudioUrl = getString("mandarinAudioUrl")
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return MasterPlant(
                id = id,
                nameId = nameId,
                nameLatin = nameLatin,
                nameMandarin = nameMandarin,
                pinyin = pinyin,
                description = description,
                primaryPhotoUrl = primaryPhotoUrl,
                mandarinAudioUrl = mandarinAudioUrl,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        fun LocationPlant.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "locationId" to locationId,
            "masterPlantId" to masterPlantId,
            "localPhotoUrl" to localPhotoUrl,
            "quantityNote" to quantityNote,
            "notes" to notes,
            "featuredForQr" to featuredForQr,
            "status" to status.name,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toLocationPlant(): LocationPlant? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val locationId = getString("locationId") ?: return null
            val masterPlantId = getString("masterPlantId") ?: return null
            val localPhotoUrl = getString("localPhotoUrl")
            val quantityNote = getString("quantityNote")
            val notes = getString("notes")
            val featuredForQr = getBoolean("featuredForQr") ?: false
            val statusStr = getString("status") ?: PlantStatus.ACTIVE.name
            val status = runCatching { PlantStatus.valueOf(statusStr) }.getOrDefault(PlantStatus.ACTIVE)
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return LocationPlant(
                id = id,
                locationId = locationId,
                masterPlantId = masterPlantId,
                localPhotoUrl = localPhotoUrl,
                quantityNote = quantityNote,
                notes = notes,
                featuredForQr = featuredForQr,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
