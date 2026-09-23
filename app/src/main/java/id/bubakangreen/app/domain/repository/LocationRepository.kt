package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getPublishedLocations(): Flow<Result<List<Location>>>
    fun getFeaturedLocations(): Flow<Result<List<Location>>>
    fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>>
    fun getLocationById(locationId: String): Flow<Result<Location?>>
    suspend fun createLocation(location: Location): Result<String>
    suspend fun updateLocation(location: Location): Result<Unit>
    suspend fun getAssignedLocations(picUid: String): Result<List<Location>>
}
