package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.pic.PicDashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class PicDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleGarden = Location(
        id = "LOC_PIC_01",
        name = "Kebun Hidroponik RW 01",
        type = LocationType.URBAN_FARMING,
        rw = "01",
        address = "Jl. Melati",
        description = "Pekarangan warga",
        latitude = -7.0680,
        longitude = 110.3290,
        accuracyMeters = 8f,
        coordinatesStatus = CoordinatesStatus.VERIFIED,
        picUid = "pic_user_1",
        status = LocationStatus.PUBLISHED
    )

    private class FakeLocationRepo(var locationsToReturn: List<Location>) : LocationRepository {
        override fun getPublishedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getFeaturedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationById(locationId: String): Flow<Result<Location?>> = flowOf(Result.Success(null))
        override suspend fun createLocation(location: Location): Result<String> = Result.Success(location.id)
        override suspend fun updateLocation(location: Location): Result<Unit> = Result.Success(Unit)
        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> =
            Result.Success(locationsToReturn)
        override suspend fun getPendingLocations(): Result<List<Location>> = Result.Success(emptyList())
        override suspend fun deleteLocation(locationId: String): Result<Unit> = Result.Success(Unit)
    }

    private class FakeAuthRepo : AuthRepository {
        val sessionState = MutableStateFlow(
            UserSession(
                uid = "pic_user_1",
                email = "pic@bubakan.id",
                displayName = "Petugas RW 01",
                role = UserRole.PIC
            )
        )
        override val currentUserSession: Flow<UserSession> = sessionState
        override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> =
            Result.Success(sessionState.value)
        override suspend fun signOut(): Result<Unit> {
            sessionState.value = UserSession("", "", "", UserRole.PUBLIC)
            return Result.Success(Unit)
        }
        override fun isUserSignedIn(): Boolean = true
    }

    private lateinit var fakeLocationRepo: FakeLocationRepo
    private lateinit var fakeAuthRepo: FakeAuthRepo

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationRepo = FakeLocationRepo(listOf(sampleGarden))
        fakeAuthRepo = FakeAuthRepo()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_withAssignedGardens_setsSuccessState() = runTest(testDispatcher) {
        val viewModel = PicDashboardViewModel(fakeLocationRepo, fakeAuthRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val list = (state as UiState.Success).data
        assertThat(list).hasSize(1)
        assertThat(list.first().name).isEqualTo("Kebun Hidroponik RW 01")
    }

    @Test
    fun loadData_withNoAssignedGardens_setsEmptyState() = runTest(testDispatcher) {
        fakeLocationRepo.locationsToReturn = emptyList()
        val viewModel = PicDashboardViewModel(fakeLocationRepo, fakeAuthRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Empty::class.java)
    }

    @Test
    fun signOut_resetsSession() = runTest(testDispatcher) {
        val viewModel = PicDashboardViewModel(fakeLocationRepo, fakeAuthRepo)
        advanceUntilIdle()

        var signedOutCallbackCalled = false
        viewModel.signOut { signedOutCallbackCalled = true }
        advanceUntilIdle()

        assertThat(signedOutCallbackCalled).isTrue()
        assertThat(fakeAuthRepo.sessionState.value.role).isEqualTo(UserRole.PUBLIC)
    }
}
