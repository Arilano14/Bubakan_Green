package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationConditionLog
import id.bubakangreen.app.domain.model.LocationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface LocationRepository {
    fun getPublishedLocations(): Flow<Result<List<Location>>>
    fun getFeaturedLocations(): Flow<Result<List<Location>>>
    fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>>
    fun getLocationById(locationId: String): Flow<Result<Location?>>
    fun getAllLocations(): Flow<Result<List<Location>>> = getPublishedLocations()
    suspend fun createLocation(location: Location): Result<String>
    suspend fun updateLocation(location: Location): Result<Unit>
    suspend fun updateLocationCondition(
        locationId: String,
        status: String,
        note: String,
        photoUrl: String?,
        updatedBy: String
    ): Result<Unit> = Result.Success(Unit)
    fun getLocationConditionLogs(locationId: String): Flow<Result<List<LocationConditionLog>>> =
        flowOf(Result.Success(emptyList()))
    suspend fun deactivateLocation(locationId: String): Result<Unit> = Result.Success(Unit)
    suspend fun getAssignedLocations(picUid: String): Result<List<Location>>
    suspend fun getPendingLocations(): Result<List<Location>>
    suspend fun deleteLocation(locationId: String): Result<Unit>
}


