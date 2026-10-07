package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.PlantCondition
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
                Result.Success(snapshot.documents.mapNotNull { it.toMasterPlant() }) as Result<List<MasterPlant>>
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> {
        return masterPlantsCollection.document(plantId).snapshots().map { snapshot ->
            Result.Success(snapshot.toMasterPlant()) as Result<MasterPlant?>
        }.catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> {
        return locationPlantsCollection
            .whereEqualTo("locationId", locationId)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toLocationPlant() }.filter { it.isPresent && it.status == PlantStatus.ACTIVE }
                Result.Success(list) as Result<List<LocationPlant>>
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

    override suspend fun addPlantsToLocation(locationId: String, plantIds: List<String>): Result<Int> {
        return try {
            val uniqueIds = plantIds.distinct().filter { it.isNotBlank() }
            if (uniqueIds.isEmpty()) return Result.Success(0)

            val batch = firestore.batch()
            val now = System.currentTimeMillis()
            var count = 0

            uniqueIds.forEach { plantId ->
                val relationId = "${locationId}_${plantId}"
                val docRef = locationPlantsCollection.document(relationId)
                val relation = LocationPlant(
                    id = relationId,
                    locationId = locationId,
                    plantId = plantId,
                    masterPlantId = plantId,
                    condition = PlantCondition.GOOD,
                    quantity = 1,
                    isPresent = true,
                    status = PlantStatus.ACTIVE,
                    createdAt = now,
                    updatedAt = now
                )
                batch.set(docRef, relation.toMap())
                count++
            }
            batch.commit().await()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun createMasterPlantWithLocation(plant: MasterPlant, locationId: String): Result<String> {
        return try {
            val plantDocRef = if (plant.id.isNotBlank()) {
                masterPlantsCollection.document(plant.id)
            } else {
                masterPlantsCollection.document()
            }
            val finalPlant = plant.copy(id = plantDocRef.id)
            val relationId = "${locationId}_${finalPlant.id}"
            val relDocRef = locationPlantsCollection.document(relationId)
            val now = System.currentTimeMillis()
            val relation = LocationPlant(
                id = relationId,
                locationId = locationId,
                plantId = finalPlant.id,
                masterPlantId = finalPlant.id,
                condition = PlantCondition.GOOD,
                quantity = 1,
                isPresent = true,
                status = PlantStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )

            val batch = firestore.batch()
            batch.set(plantDocRef, finalPlant.toMap())
            batch.set(relDocRef, relation.toMap())
            batch.commit().await()

            Result.Success(finalPlant.id)
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

    override suspend fun updateMasterPlant(plant: MasterPlant): Result<Unit> {
        return try {
            masterPlantsCollection.document(plant.id).set(plant.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun removePlantFromLocation(locationPlantId: String): Result<Unit> {
        return try {
            // Delete junction document only; master_plants document remains 100% untouched
            locationPlantsCollection.document(locationPlantId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }


    companion object {
        fun MasterPlant.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "name" to (name.ifBlank { nameId }),
            "nameId" to (nameId.ifBlank { name }),
            "scientificName" to (scientificName.ifBlank { nameLatin }),
            "nameLatin" to (nameLatin.ifBlank { scientificName }),
            "mandarinName" to (mandarinName ?: nameMandarin),
            "nameMandarin" to (nameMandarin ?: mandarinName),
            "mandarinPinyin" to (mandarinPinyin ?: pinyin),
            "pinyin" to (pinyin ?: mandarinPinyin),
            "description" to description,
            "characteristics" to characteristics,
            "commonUses" to (commonUses.ifBlank { benefits }),
            "cultivationNotes" to (cultivationNotes.ifBlank { plantingGuide }),
            "benefits" to (benefits.ifBlank { commonUses }),
            "plantingGuide" to (plantingGuide.ifBlank { cultivationNotes }),
            "defaultPhotoUrl" to (defaultPhotoUrl ?: primaryPhotoUrl),
            "primaryPhotoUrl" to (primaryPhotoUrl ?: defaultPhotoUrl),
            "imageSourceType" to imageSourceType,
            "imageAssetName" to imageAssetName,
            "imageSource" to imageSource,
            "imageLicense" to imageLicense,
            "imageAuthor" to imageAuthor,
            "sourceReferences" to sourceReferences,
            "profileCompleteness" to profileCompleteness,
            "createdFrom" to createdFrom,
            "mandarinAudioUrl" to mandarinAudioUrl,
            "isPublished" to isPublished,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toMasterPlant(): MasterPlant? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val name = getString("name") ?: getString("nameId") ?: return null
            val scientificName = getString("scientificName") ?: getString("nameLatin") ?: ""
            val nameMandarin = getString("nameMandarin") ?: getString("mandarinName")
            val pinyin = getString("mandarinPinyin") ?: getString("pinyin")
            val description = getString("description") ?: ""
            val characteristics = getString("characteristics") ?: ""
            val commonUses = getString("commonUses") ?: getString("benefits") ?: ""
            val cultivationNotes = getString("cultivationNotes") ?: getString("plantingGuide") ?: ""
            val benefits = getString("benefits") ?: commonUses
            val plantingGuide = getString("plantingGuide") ?: cultivationNotes
            val photoUrl = getString("defaultPhotoUrl") ?: getString("primaryPhotoUrl")
            val imageSourceType = getString("imageSourceType")
                ?: if (photoUrl?.startsWith("http") == true) "REMOTE_URL" else "LOCAL"
            val imageAssetName = getString("imageAssetName")
                ?: if (photoUrl != null && !photoUrl.startsWith("http")) photoUrl else null
            val imageSource = getString("imageSource")
            val imageLicense = getString("imageLicense")
            val imageAuthor = getString("imageAuthor")
            val sourceReferences = getString("sourceReferences") ?: ""
            val profileCompleteness = getString("profileCompleteness") ?: "COMPLETE"
            val createdFrom = getString("createdFrom")
            val mandarinAudioUrl = getString("mandarinAudioUrl")
            val isPublished = runCatching { getBoolean("isPublished") }.getOrNull() ?: true
            val createdAt = runCatching { getLong("createdAt") }.getOrNull()
                ?: runCatching { getString("createdAt")?.toLongOrNull() }.getOrNull()
                ?: System.currentTimeMillis()
            val updatedAt = runCatching { getLong("updatedAt") }.getOrNull()
                ?: runCatching { getString("updatedAt")?.toLongOrNull() }.getOrNull()
                ?: System.currentTimeMillis()

            return MasterPlant(
                id = id,
                name = name,
                nameId = name,
                scientificName = scientificName,
                nameLatin = scientificName,
                mandarinName = nameMandarin,
                nameMandarin = nameMandarin,
                mandarinPinyin = pinyin,
                pinyin = pinyin,
                description = description,
                characteristics = characteristics,
                commonUses = commonUses,
                cultivationNotes = cultivationNotes,
                benefits = benefits,
                plantingGuide = plantingGuide,
                defaultPhotoUrl = photoUrl,
                primaryPhotoUrl = photoUrl,
                imageSourceType = imageSourceType,
                imageAssetName = imageAssetName,
                imageSource = imageSource,
                imageLicense = imageLicense,
                imageAuthor = imageAuthor,
                sourceReferences = sourceReferences,
                profileCompleteness = profileCompleteness,
                createdFrom = createdFrom,
                mandarinAudioUrl = mandarinAudioUrl,
                isPublished = isPublished,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }


        fun LocationPlant.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "locationId" to locationId,
            "plantId" to (plantId.ifBlank { masterPlantId }),
            "masterPlantId" to (masterPlantId.ifBlank { plantId }),
            "photoUrl" to (photoUrl ?: localPhotoUrl),
            "localPhotoUrl" to (localPhotoUrl ?: photoUrl),
            "condition" to condition.name,
            "quantity" to quantity,
            "quantityNote" to (quantityNote ?: "$quantity polybag"),
            "notes" to notes,
            "isPresent" to isPresent,
            "featuredForQr" to featuredForQr,
            "status" to status.name,
            "createdBy" to createdBy,
            "updatedBy" to updatedBy,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toLocationPlant(): LocationPlant? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val locationId = getString("locationId") ?: return null
            val plantId = getString("plantId") ?: getString("masterPlantId") ?: return null
            val photoUrl = getString("photoUrl") ?: getString("localPhotoUrl")
            val conditionStr = getString("condition") ?: PlantCondition.GOOD.name
            val condition = runCatching { PlantCondition.valueOf(conditionStr) }.getOrDefault(PlantCondition.GOOD)
            val quantity = getLong("quantity")?.toInt() ?: 1
            val quantityNote = getString("quantityNote")
            val notes = getString("notes")
            val isPresent = getBoolean("isPresent") ?: true
            val featuredForQr = getBoolean("featuredForQr") ?: false
            val statusStr = getString("status") ?: PlantStatus.ACTIVE.name
            val status = runCatching { PlantStatus.valueOf(statusStr) }.getOrDefault(PlantStatus.ACTIVE)
            val createdBy = getString("createdBy")
            val updatedBy = getString("updatedBy") ?: createdBy
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return LocationPlant(
                id = id,
                locationId = locationId,
                plantId = plantId,
                masterPlantId = plantId,
                photoUrl = photoUrl,
                localPhotoUrl = photoUrl,
                condition = condition,
                quantity = quantity,
                quantityNote = quantityNote,
                notes = notes,
                isPresent = isPresent,
                featuredForQr = featuredForQr,
                status = status,
                createdBy = createdBy,
                updatedBy = updatedBy,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

