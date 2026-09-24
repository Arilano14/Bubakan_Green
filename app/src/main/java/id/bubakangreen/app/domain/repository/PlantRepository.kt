package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>>
    fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>>
    fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>>
    suspend fun createMasterPlant(plant: MasterPlant): Result<String>
    suspend fun updateMasterPlant(plant: MasterPlant): Result<Unit>
    suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String>
    suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit>
    suspend fun removePlantFromLocation(locationPlantId: String): Result<Unit>
}
