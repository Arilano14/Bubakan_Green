package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val featuredLocation = Location(
        id = "L_FEATURED",
        name = "Urban Farming Kelurahan",
        type = LocationType.URBAN_FARMING,
        rw = "01",
        address = "Jl. Raya Bubakan",
        description = "Kebun unggulan",
        latitude = -7.0681,
        longitude = 110.3289,
        coordinatesStatus = CoordinatesStatus.VERIFIED,
        featured = true,
        picUid = "pic1",
        status = LocationStatus.PUBLISHED
    )

    private val samplePlant = MasterPlant(
        id = "P1",
        nameId = "Jahe Merah",
        nameLatin = "Zingiber officinale",
        description = "Herbal hangat"
    )

    private val fakeLocationRepo = object : LocationRepository {
        override fun getPublishedLocations(): Flow<Result<List<Location>>> =
            flowOf(Result.Success(listOf(featuredLocation)))

        override fun getFeaturedLocations(): Flow<Result<List<Location>>> =
            flowOf(Result.Success(listOf(featuredLocation)))

        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> =
            flowOf(Result.Success(listOf(featuredLocation)))

        override fun getLocationById(locationId: String): Flow<Result<Location?>> =
            flowOf(Result.Success(featuredLocation))

        override suspend fun createLocation(location: Location): Result<String> = Result.Success(location.id)
        override suspend fun updateLocation(location: Location): Result<Unit> = Result.Success(Unit)
        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> = Result.Success(listOf(featuredLocation))
    }

    private val fakePlantRepo = object : PlantRepository {
        override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
            flowOf(Result.Success(listOf(samplePlant)))

        override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
            flowOf(Result.Success(samplePlant))

        override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> =
            flowOf(Result.Success(emptyList()))

        override suspend fun createMasterPlant(plant: MasterPlant): Result<String> = Result.Success(plant.id)
        override suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String> = Result.Success(locationPlant.id)
        override suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit> = Result.Success(Unit)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_loadsFeaturedLocationsAndPopularPlants() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(
            locationRepository = fakeLocationRepo,
            plantRepository = fakePlantRepo
        )
        advanceUntilIdle()

        val featuredState = viewModel.uiState.value.featuredLocations
        assertThat(featuredState).isInstanceOf(UiState.Success::class.java)
        val locations = (featuredState as UiState.Success).data
        assertThat(locations).hasSize(1)
        assertThat(locations.first().name).isEqualTo("Urban Farming Kelurahan")

        val plantsState = viewModel.uiState.value.popularPlants
        assertThat(plantsState).isInstanceOf(UiState.Success::class.java)
        val plants = (plantsState as UiState.Success).data
        assertThat(plants).hasSize(1)
        assertThat(plants.first().nameId).isEqualTo("Jahe Merah")
    }
}
