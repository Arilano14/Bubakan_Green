package id.bubakangreen.app.core.di

import com.google.firebase.firestore.FirebaseFirestore
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.remote.FirestoreLocationRepository
import id.bubakangreen.app.data.remote.FirestorePlantRepository
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Service locator providing repositories to ViewModels.
 * Safely provides Firestore repositories when Firebase is active,
 * or UI preview fixtures if Firebase is not yet configured.
 */
object RepositoryProvider {

    private var locationRepo: LocationRepository? = null
    private var plantRepo: PlantRepository? = null

    fun getLocationRepository(): LocationRepository {
        return locationRepo ?: synchronized(this) {
            locationRepo ?: createLocationRepository().also { locationRepo = it }
        }
    }

    fun getPlantRepository(): PlantRepository {
        return plantRepo ?: synchronized(this) {
            plantRepo ?: createPlantRepository().also { plantRepo = it }
        }
    }

    private fun createLocationRepository(): LocationRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestoreLocationRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyLocationRepository
        }
    }

    private fun createPlantRepository(): PlantRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestorePlantRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyPlantRepository
        }
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyLocationRepository : LocationRepository {
    private val previewLocations = listOf(
        Location(
            id = "LOC_PREVIEW_01",
            name = "Urban Farming Kelurahan",
            type = LocationType.URBAN_FARMING,
            rw = "01",
            address = "Jl. Raya Bubakan No. 1, Kel. Bubakan",
            description = "Kebun percontohan budidaya sayuran dan pangan mandiri binaan Kelurahan Bubakan.",
            latitude = -7.0681,
            longitude = 110.3289,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            photoUrl = null,
            picUid = "system_preview",
            status = LocationStatus.PUBLISHED
        ),
        Location(
            id = "LOC_PREVIEW_02",
            name = "Taman Toga RW 03",
            type = LocationType.TAMAN_TOGA,
            rw = "03",
            address = "Lingkungan RW 03, Kelurahan Bubakan",
            description = "Taman tanaman obat keluarga warga RW 03 yang membudidayakan ragam tanaman herbal tradisional.",
            latitude = -7.0695,
            longitude = 110.3310,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            photoUrl = null,
            picUid = "system_preview",
            status = LocationStatus.PUBLISHED
        )
    )

    override fun getPublishedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations))

    override fun getFeaturedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.featured }))

    override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.type == type }))

    override fun getLocationById(locationId: String): Flow<Result<Location?>> =
        flowOf(Result.Success(previewLocations.find { it.id == locationId }))

    override suspend fun createLocation(location: Location): Result<String> =
        Result.Success(location.id)

    override suspend fun updateLocation(location: Location): Result<Unit> =
        Result.Success(Unit)

    override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> =
        Result.Success(previewLocations)
}

/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyPlantRepository : PlantRepository {
    private val previewPlants = listOf(
        MasterPlant(
            id = "PLANT_PREVIEW_01",
            nameId = "Jahe Merah",
            nameLatin = "Zingiber officinale var. rubrum",
            nameMandarin = "红姜",
            pinyin = "hóng jiāng",
            description = "Rimpang berkhasiat menghangatkan tubuh, meredakan peradangan, dan meningkatkan imunitas.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        ),
        MasterPlant(
            id = "PLANT_PREVIEW_02",
            nameId = "Kencur",
            nameLatin = "Kaempferia galanga",
            nameMandarin = "山柰",
            pinyin = "shān nài",
            description = "Tanaman obat keluarga dengan aroma khas, berkhasiat meredakan batuk dan melegakan tenggorokan.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        ),
        MasterPlant(
            id = "PLANT_PREVIEW_03",
            nameId = "Kunyit",
            nameLatin = "Curcuma longa",
            nameMandarin = "姜黄",
            pinyin = "jiāng huáng",
            description = "Herba rimpang kuning kaya kurkuminoid, berfungsi sebagai antioksidan alami dan kesehatan lambung.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        )
    )

    override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
        flowOf(Result.Success(previewPlants))

    override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
        flowOf(Result.Success(previewPlants.find { it.id == plantId }))

    override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> {
        val junctionList = previewPlants.mapIndexed { index, plant ->
            LocationPlant(
                id = "LOC_PLANT_$index",
                locationId = locationId,
                masterPlantId = plant.id,
                localPhotoUrl = null,
                quantityNote = "Bedengan B-$index",
                featuredForQr = true
            )
        }
        return flowOf(Result.Success(junctionList))
    }

    override suspend fun createMasterPlant(plant: MasterPlant): Result<String> =
        Result.Success(plant.id)

    override suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String> =
        Result.Success(locationPlant.id)

    override suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit> =
        Result.Success(Unit)
}
