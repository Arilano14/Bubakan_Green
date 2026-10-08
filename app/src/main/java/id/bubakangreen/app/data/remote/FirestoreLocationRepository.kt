package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.fixture.DefaultBotanicalData
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationConditionLog
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.RegionTag
import id.bubakangreen.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreLocationRepository(
    private val firestore: FirebaseFirestore
) : LocationRepository {

    private val collection by lazy {
        firestore.collection("locations")
    }

    private val conditionLogsCollection by lazy {
        firestore.collection("location_condition_logs")
    }

    override fun getPublishedLocations(): Flow<Result<List<Location>>> {
        return collection
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toLocation() }.filter { loc ->
                    loc.isPublished && loc.status != LocationStatus.INACTIVE && loc.status != LocationStatus.ARCHIVED && loc.status != LocationStatus.DRAFT
                }
                val finalList = if (list.isEmpty()) {
                    DefaultBotanicalData.defaultLocations.filter { it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }
                } else {
                    list
                }
                Result.Success(finalList) as Result<List<Location>>
            }
            .catch {
                emit(Result.Success(DefaultBotanicalData.defaultLocations.filter { it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }))
            }
    }

    override fun getFeaturedLocations(): Flow<Result<List<Location>>> {
        return collection
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toLocation() }.filter { loc ->
                    loc.featured && loc.isPublished && loc.status != LocationStatus.INACTIVE && loc.status != LocationStatus.ARCHIVED
                }
                val finalList = if (list.isEmpty()) {
                    DefaultBotanicalData.defaultLocations.filter { it.featured && it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }
                } else {
                    list
                }
                Result.Success(finalList) as Result<List<Location>>
            }
            .catch {
                emit(Result.Success(DefaultBotanicalData.defaultLocations.filter { it.featured && it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }))
            }
    }

    override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> {
        return collection
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toLocation() }.filter { loc ->
                    loc.type == type && loc.isPublished && loc.status != LocationStatus.INACTIVE && loc.status != LocationStatus.ARCHIVED
                }
                val finalList = if (list.isEmpty()) {
                    DefaultBotanicalData.defaultLocations.filter { it.type == type && it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }
                } else {
                    list
                }
                Result.Success(finalList) as Result<List<Location>>
            }
            .catch {
                emit(Result.Success(DefaultBotanicalData.defaultLocations.filter { it.type == type && it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }))
            }
    }

    override fun getLocationById(locationId: String): Flow<Result<Location?>> {
        return collection.document(locationId).snapshots().map { snapshot ->
            val loc = snapshot.toLocation() ?: DefaultBotanicalData.defaultLocations.find {
                it.id == locationId ||
                (locationId == "loc_urban_farming_bubakan" && it.id == "LOC_PREVIEW_01") ||
                (locationId == "loc_taman_toga_rw03" && it.id == "LOC_PREVIEW_02") ||
                (locationId == "LOC_PREVIEW_01" && it.id == "loc_urban_farming_bubakan") ||
                (locationId == "LOC_PREVIEW_02" && it.id == "loc_taman_toga_rw03")
            }
            Result.Success(loc) as Result<Location?>
        }.catch {
            val fallback = DefaultBotanicalData.defaultLocations.find {
                it.id == locationId ||
                (locationId == "loc_urban_farming_bubakan" && it.id == "LOC_PREVIEW_01") ||
                (locationId == "loc_taman_toga_rw03" && it.id == "LOC_PREVIEW_02") ||
                (locationId == "LOC_PREVIEW_01" && it.id == "loc_urban_farming_bubakan") ||
                (locationId == "LOC_PREVIEW_02" && it.id == "loc_taman_toga_rw03")
            }
            emit(Result.Success(fallback))
        }
    }

    override fun getAllLocations(): Flow<Result<List<Location>>> {
        return collection.snapshots().map { snapshot ->
            val list = snapshot.documents.mapNotNull { it.toLocation() }
            val finalList = if (list.isEmpty()) DefaultBotanicalData.defaultLocations else list
            Result.Success(finalList) as Result<List<Location>>
        }.catch {
            emit(Result.Success(DefaultBotanicalData.defaultLocations))
        }
    }

    override suspend fun createLocation(location: Location): Result<String> {
        return try {
            val docRef = if (location.id.isNotBlank()) {
                collection.document(location.id)
            } else {
                collection.document()
            }
            val finalLocation = location.copy(id = docRef.id)
            docRef.set(finalLocation.toMap()).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun updateLocation(location: Location): Result<Unit> {
        return try {
            collection.document(location.id).set(location.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun updateLocationCondition(
        locationId: String,
        status: String,
        note: String,
        photoUrl: String?,
        updatedBy: String
    ): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val parsedStatus = runCatching { LocationStatus.valueOf(status) }.getOrDefault(LocationStatus.ACTIVE)
            val updates = mutableMapOf<String, Any?>(
                "status" to parsedStatus.name,
                "conditionNote" to note,
                "conditionUpdatedAt" to now,
                "conditionUpdatedBy" to updatedBy,
                "updatedAt" to now
            )
            if (!photoUrl.isNullOrBlank()) {
                updates["coverPhotoUrl"] = photoUrl
                updates["photoUrl"] = photoUrl
            }
            collection.document(locationId).update(updates).await()

            // Record lightweight condition log (Section 16)
            val logRef = conditionLogsCollection.document()
            val log = LocationConditionLog(
                id = logRef.id,
                locationId = locationId,
                status = parsedStatus.name,
                note = note,
                photoUrl = photoUrl,
                updatedBy = updatedBy,
                updatedAt = now
            )
            val logMap = mapOf(
                "id" to log.id,
                "locationId" to log.locationId,
                "status" to log.status,
                "note" to log.note,
                "photoUrl" to log.photoUrl,
                "updatedBy" to log.updatedBy,
                "updatedAt" to log.updatedAt
            )
            logRef.set(logMap).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override fun getLocationConditionLogs(locationId: String): Flow<Result<List<LocationConditionLog>>> {
        return conditionLogsCollection
            .whereEqualTo("locationId", locationId)
            .snapshots()
            .map { snapshot ->
                val logs = snapshot.documents.mapNotNull { doc ->
                    if (!doc.exists()) return@mapNotNull null
                    LocationConditionLog(
                        id = doc.getString("id") ?: doc.id,
                        locationId = doc.getString("locationId") ?: "",
                        status = doc.getString("status") ?: LocationStatus.ACTIVE.name,
                        note = doc.getString("note") ?: "",
                        photoUrl = doc.getString("photoUrl"),
                        updatedBy = doc.getString("updatedBy") ?: "Admin",
                        updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                    )
                }.sortedByDescending { it.updatedAt }
                Result.Success(logs) as Result<List<LocationConditionLog>>
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override suspend fun deactivateLocation(locationId: String): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            collection.document(locationId).update(
                mapOf(
                    "isPublished" to false,
                    "status" to LocationStatus.INACTIVE.name,
                    "updatedAt" to now
                )
            ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> {
        return try {
            val snapshot = collection.whereEqualTo("picUid", picUid).get().await()
            Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun getPendingLocations(): Result<List<Location>> {
        return try {
            val snapshot = collection.whereEqualTo("status", LocationStatus.PENDING_APPROVAL.name).get().await()
            Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun deleteLocation(locationId: String): Result<Unit> {
        // Preferred soft delete per Section 8
        return deactivateLocation(locationId)
    }

    companion object {
        fun Location.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "name" to name,
            "type" to type.name,
            "rw" to rw,
            "regionTag" to regionTag,
            "address" to address,
            "description" to description,
            "latitude" to latitude,
            "longitude" to longitude,
            "coordinatesStatus" to coordinatesStatus.name,
            "featured" to featured,
            "photos" to photos,
            "coverPhotoUrl" to (photos.firstOrNull() ?: coverPhotoUrl),
            "photoUrl" to (photos.firstOrNull() ?: photoUrl ?: coverPhotoUrl),
            "conditionNote" to conditionNote,
            "conditionUpdatedAt" to conditionUpdatedAt,
            "conditionUpdatedBy" to conditionUpdatedBy,
            "isPublished" to isPublished,
            "createdBy" to createdBy,
            "picUid" to (picUid.ifBlank { createdBy }),
            "status" to status.name,
            "accuracyMeters" to accuracyMeters,
            "capturedAt" to capturedAt,
            "rejectionNote" to rejectionNote,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )

        fun DocumentSnapshot.toLocation(): Location? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val name = getString("name") ?: return null
            val typeStr = getString("type") ?: LocationType.URBAN_FARMING.name
            val type = runCatching { LocationType.valueOf(typeStr) }.getOrDefault(LocationType.URBAN_FARMING)
            val rw = getString("rw") ?: ""
            val regionTag = getString("regionTag") ?: (RegionTag.fromCodeOrRw(rw)?.code ?: rw)
            val address = getString("address") ?: ""
            val description = getString("description") ?: ""
            val latitude = getDouble("latitude") ?: 0.0
            val longitude = getDouble("longitude") ?: 0.0
            val coordStatusStr = getString("coordinatesStatus") ?: CoordinatesStatus.PENDING.name
            val coordinatesStatus = runCatching { CoordinatesStatus.valueOf(coordStatusStr) }.getOrDefault(CoordinatesStatus.PENDING)
            val featured = getBoolean("featured") ?: false
            val photosList = (get("photos") as? List<*>)?.mapNotNull { it?.toString() }
                ?: listOfNotNull(getString("coverPhotoUrl") ?: getString("photoUrl"))
            val coverPhotoUrl = photosList.firstOrNull() ?: getString("coverPhotoUrl") ?: getString("photoUrl")
            val photoUrl = coverPhotoUrl
            val conditionNote = getString("conditionNote") ?: ""
            val conditionUpdatedAt = getLong("conditionUpdatedAt")
            val conditionUpdatedBy = getString("conditionUpdatedBy")
            val statusStr = getString("status") ?: LocationStatus.ACTIVE.name
            val status = runCatching { LocationStatus.valueOf(statusStr) }.getOrDefault(LocationStatus.ACTIVE)
            val isPublished = getBoolean("isPublished") ?: (status == LocationStatus.PUBLISHED || status == LocationStatus.ACTIVE || status == LocationStatus.NEEDS_MAINTENANCE)
            val createdBy = getString("createdBy") ?: getString("picUid") ?: ""
            val picUid = createdBy
            val accuracyMeters = getDouble("accuracyMeters")?.toFloat()
            val capturedAt = getLong("capturedAt")
            val rejectionNote = getString("rejectionNote")
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return Location(
                id = id,
                name = name,
                type = type,
                rw = rw,
                regionTag = regionTag,
                address = address,
                description = description,
                latitude = latitude,
                longitude = longitude,
                coordinatesStatus = coordinatesStatus,
                featured = featured,
                photos = photosList,
                coverPhotoUrl = coverPhotoUrl,
                photoUrl = photoUrl,
                conditionNote = conditionNote,
                conditionUpdatedAt = conditionUpdatedAt,
                conditionUpdatedBy = conditionUpdatedBy,
                isPublished = isPublished,
                createdBy = createdBy,
                picUid = picUid,
                status = status,
                accuracyMeters = accuracyMeters,
                capturedAt = capturedAt,
                rejectionNote = rejectionNote,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

