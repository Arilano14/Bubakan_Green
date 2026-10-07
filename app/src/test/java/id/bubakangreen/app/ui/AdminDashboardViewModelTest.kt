package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.admin.AdminDashboardViewModel
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
class AdminDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val initialLocation1 = Location(
        id = "loc_1",
        name = "Kebun Hidroponik RW 01",
        type = LocationType.URBAN_FARMING,
        rw = "01",
        regionTag = "RW 01",
        latitude = -7.0690,
        longitude = 110.3320,
        status = LocationStatus.PUBLISHED,
        coordinatesStatus = CoordinatesStatus.VERIFIED
    )

    private val initialLocation2 = Location(
        id = "loc_2",
        name = "Taman Toga Kelurahan",
        type = LocationType.TAMAN_TOGA,
        rw = "Kelurahan",
        regionTag = "Kelurahan",
        latitude = -7.0700,
        longitude = 110.3330,
        status = LocationStatus.PUBLISHED,
        coordinatesStatus = CoordinatesStatus.VERIFIED
    )

    private val initialPlant = MasterPlant(
        id = "plant_1",
        name = "Jahe Merah",
        nameId = "Jahe Merah",
        scientificName = "Zingiber officinale var. rubrum",
        nameLatin = "Zingiber officinale var. rubrum",
        primaryPhotoUrl = "plant_jahe"
    )

    private class ReactiveLocationRepo(
        private val locationFlow: Flow<Result<List<Location>>>
    ) : LocationRepository {
        override fun getAllLocations(): Flow<Result<List<Location>>> = locationFlow
        override fun getPublishedLocations(): Flow<Result<List<Location>>> = locationFlow
        override fun getFeaturedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationById(locationId: String): Flow<Result<Location?>> = flowOf(Result.Success(null))
        override suspend fun createLocation(location: Location): Result<String> = Result.Success(location.id)
        override suspend fun updateLocation(location: Location): Result<Unit> = Result.Success(Unit)
        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> = Result.Success(emptyList())
        override suspend fun getPendingLocations(): Result<List<Location>> = Result.Success(emptyList())
        override suspend fun deleteLocation(locationId: String): Result<Unit> = Result.Success(Unit)
    }

    private class ReactivePlantRepo(
        private val plantFlow: Flow<Result<List<MasterPlant>>>
    ) : PlantRepository {
        override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> = plantFlow
        override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> = flowOf(Result.Success(null))
        override fun getPlantsAtLocation(locationId: String): Flow<Result<List<id.bubakangreen.app.domain.model.LocationPlant>>> = flowOf(Result.Success(emptyList()))
        override suspend fun createMasterPlant(plant: MasterPlant): Result<String> = Result.Success(plant.id)
        override suspend fun updateMasterPlant(plant: MasterPlant): Result<Unit> = Result.Success(Unit)
        override suspend fun addPlantToLocation(locationPlant: id.bubakangreen.app.domain.model.LocationPlant): Result<String> = Result.Success("lp_1")
        override suspend fun updateLocationPlant(locationPlant: id.bubakangreen.app.domain.model.LocationPlant): Result<Unit> = Result.Success(Unit)
        override suspend fun removePlantFromLocation(locationPlantId: String): Result<Unit> = Result.Success(Unit)
    }

    private class FakeAuthRepo(
        private val session: UserSession
    ) : AuthRepository {
        override val currentUserSession: Flow<UserSession> = flowOf(session)
        override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> = Result.Success(session)
        override suspend fun signOut(): Result<Unit> = Result.Success(Unit)
        override fun isUserSignedIn(): Boolean = true
    }

    private val adminSession = UserSession(
        uid = "admin_01",
        email = "admin@bubakangreen.id",
        displayName = "Admin Bubakan",
        role = UserRole.ADMIN
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_initializesDashboardWithAccurateMetricsAndNoApprovalQueue() = runTest(testDispatcher) {
        val locationFlow = flowOf(Result.Success(listOf(initialLocation1, initialLocation2)))
        val plantFlow = flowOf(Result.Success(listOf(initialPlant)))

        val repo = ReactiveLocationRepo(locationFlow)
        val plantRepo = ReactivePlantRepo(plantFlow)
        val authRepo = FakeAuthRepo(adminSession)

        val viewModel = AdminDashboardViewModel(repo, plantRepo, authRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val data = (state as UiState.Success).data

        assertThat(data.totalLocations).isEqualTo(2)
        assertThat(data.activeLocations).isEqualTo(2)
        assertThat(data.totalUrbanFarming).isEqualTo(1)
        assertThat(data.totalTamanToga).isEqualTo(1)
        assertThat(data.totalMasterPlants).isEqualTo(1)
        // Strictly verify approval queue is empty / removed
        assertThat(data.pendingLocations).isEmpty()
    }

    @Test
    fun liveSync_whenNewLocationEmitted_dashboardUpdatesCountersReactively() = runTest(testDispatcher) {
        val locationFlow = MutableSharedFlow<Result<List<Location>>>(replay = 1)
        val plantFlow = MutableSharedFlow<Result<List<MasterPlant>>>(replay = 1)

        val repo = ReactiveLocationRepo(locationFlow)
        val plantRepo = ReactivePlantRepo(plantFlow)
        val authRepo = FakeAuthRepo(adminSession)

        val viewModel = AdminDashboardViewModel(repo, plantRepo, authRepo)

        // Initial emission: 1 location, 1 plant
        locationFlow.emit(Result.Success(listOf(initialLocation1)))
        plantFlow.emit(Result.Success(listOf(initialPlant)))
        advanceUntilIdle()

        val firstState = (viewModel.uiState.value as UiState.Success).data
        assertThat(firstState.totalLocations).isEqualTo(1)
        assertThat(firstState.totalUrbanFarming).isEqualTo(1)
        assertThat(firstState.totalTamanToga).isEqualTo(0)

        // Reactive Firestore Snapshot Simulation: A new Taman Toga location is added in the background
        locationFlow.emit(Result.Success(listOf(initialLocation1, initialLocation2)))
        advanceUntilIdle()

        val updatedState = (viewModel.uiState.value as UiState.Success).data
        // Proves that combine() keeps the stream alive and doesn't get stuck with stale firstOrNull()
        assertThat(updatedState.totalLocations).isEqualTo(2)
        assertThat(updatedState.totalUrbanFarming).isEqualTo(1)
        assertThat(updatedState.totalTamanToga).isEqualTo(1)
    }

    @Test
    fun liveSync_whenNewPlantEmitted_dashboardUpdatesPlantCountReactively() = runTest(testDispatcher) {
        val locationFlow = MutableSharedFlow<Result<List<Location>>>(replay = 1)
        val plantFlow = MutableSharedFlow<Result<List<MasterPlant>>>(replay = 1)

        val repo = ReactiveLocationRepo(locationFlow)
        val plantRepo = ReactivePlantRepo(plantFlow)
        val authRepo = FakeAuthRepo(adminSession)

        val viewModel = AdminDashboardViewModel(repo, plantRepo, authRepo)

        locationFlow.emit(Result.Success(listOf(initialLocation1)))
        plantFlow.emit(Result.Success(listOf(initialPlant)))
        advanceUntilIdle()

        val plant2 = MasterPlant(
            id = "plant_2",
            name = "Kencur",
            nameId = "Kencur",
            primaryPhotoUrl = "plant_kencur"
        )

        // Reactive Firestore Snapshot: plant added
        plantFlow.emit(Result.Success(listOf(initialPlant, plant2)))
        advanceUntilIdle()

        val updatedState = (viewModel.uiState.value as UiState.Success).data
        assertThat(updatedState.totalMasterPlants).isEqualTo(2)
    }
}
