package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
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

    override fun getPublishedLocations(): Flow<Result<List<Location>>> {
        return collection
            .whereEqualTo("status", LocationStatus.PUBLISHED.name)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getFeaturedLocations(): Flow<Result<List<Location>>> {
        return collection
            .whereEqualTo("status", LocationStatus.PUBLISHED.name)
            .whereEqualTo("featured", true)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> {
        return collection
            .whereEqualTo("status", LocationStatus.PUBLISHED.name)
            .whereEqualTo("type", type.name)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    override fun getLocationById(locationId: String): Flow<Result<Location?>> {
        return collection.document(locationId).snapshots().map { snapshot ->
            Result.Success(snapshot.toLocation())
        }.catch { emit(Result.Error(it, it.localizedMessage)) }
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

    override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> {
        return try {
            val snapshot = collection.whereEqualTo("picUid", picUid).get().await()
            Result.Success(snapshot.documents.mapNotNull { it.toLocation() })
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    companion object {
        fun Location.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "name" to name,
            "type" to type.name,
            "rw" to rw,
            "address" to address,
            "description" to description,
            "latitude" to latitude,
            "longitude" to longitude,
            "coordinatesStatus" to coordinatesStatus.name,
            "featured" to featured,
            "photoUrl" to photoUrl,
            "picUid" to picUid,
            "status" to status.name,
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
            val address = getString("address") ?: ""
            val description = getString("description") ?: ""
            val latitude = getDouble("latitude") ?: 0.0
            val longitude = getDouble("longitude") ?: 0.0
            val coordStatusStr = getString("coordinatesStatus") ?: CoordinatesStatus.PENDING.name
            val coordinatesStatus = runCatching { CoordinatesStatus.valueOf(coordStatusStr) }.getOrDefault(CoordinatesStatus.PENDING)
            val featured = getBoolean("featured") ?: false
            val photoUrl = getString("photoUrl")
            val picUid = getString("picUid") ?: ""
            val statusStr = getString("status") ?: LocationStatus.DRAFT.name
            val status = runCatching { LocationStatus.valueOf(statusStr) }.getOrDefault(LocationStatus.DRAFT)
            val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()

            return Location(
                id = id,
                name = name,
                type = type,
                rw = rw,
                address = address,
                description = description,
                latitude = latitude,
                longitude = longitude,
                coordinatesStatus = coordinatesStatus,
                featured = featured,
                photoUrl = photoUrl,
                picUid = picUid,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
