package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.locations.LocationsViewModel
import id.bubakangreen.app.ui.locations.ViewMode
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
class LocationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeLocations = listOf(
        Location(
            id = "L1",
            name = "Urban Farming Kelurahan",
            type = LocationType.URBAN_FARMING,
            rw = "01",
            address = "Jl. Raya Bubakan",
            description = "Kebun sayur",
            latitude = -7.0681,
            longitude = 110.3289,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            picUid = "pic1",
            status = LocationStatus.PUBLISHED
        ),
        Location(
            id = "L2",
            name = "Taman Toga RW 03",
            type = LocationType.TAMAN_TOGA,
            rw = "03",
            address = "RW 03",
            description = "Kebun obat",
            latitude = -7.0695,
            longitude = 110.3310,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            picUid = "pic2",
            status = LocationStatus.PUBLISHED
        )
    )

    private val fakeLocationRepo = object : LocationRepository {
        override fun getPublishedLocations(): Flow<Result<List<Location>>> =
            flowOf(Result.Success(fakeLocations))

        override fun getFeaturedLocations(): Flow<Result<List<Location>>> =
            flowOf(Result.Success(fakeLocations.filter { it.featured }))

        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> =
            flowOf(Result.Success(fakeLocations.filter { it.type == type }))

        override fun getLocationById(locationId: String): Flow<Result<Location?>> =
            flowOf(Result.Success(fakeLocations.find { it.id == locationId }))

        override suspend fun createLocation(location: Location): Result<String> = Result.Success(location.id)
        override suspend fun updateLocation(location: Location): Result<Unit> = Result.Success(Unit)
        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> = Result.Success(fakeLocations)
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
    fun loadLocations_exposesAllLocationsByDefault() = runTest(testDispatcher) {
        val viewModel = LocationsViewModel(locationRepository = fakeLocationRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value.locations
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val data = (state as UiState.Success).data
        assertThat(data).hasSize(2)
    }

    @Test
    fun filterByCategory_urbanFarming_returnsOnlyUrbanFarming() = runTest(testDispatcher) {
        val viewModel = LocationsViewModel(locationRepository = fakeLocationRepo)
        advanceUntilIdle()

        viewModel.setCategoryFilter(LocationType.URBAN_FARMING)
        advanceUntilIdle()

        val state = viewModel.uiState.value.locations
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val data = (state as UiState.Success).data
        assertThat(data).hasSize(1)
        assertThat(data.first().type).isEqualTo(LocationType.URBAN_FARMING)
        assertThat(data.first().name).isEqualTo("Urban Farming Kelurahan")
    }

    @Test
    fun filterByCategory_tamanToga_returnsOnlyTamanToga() = runTest(testDispatcher) {
        val viewModel = LocationsViewModel(locationRepository = fakeLocationRepo)
        advanceUntilIdle()

        viewModel.setCategoryFilter(LocationType.TAMAN_TOGA)
        advanceUntilIdle()

        val state = viewModel.uiState.value.locations
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val data = (state as UiState.Success).data
        assertThat(data).hasSize(1)
        assertThat(data.first().type).isEqualTo(LocationType.TAMAN_TOGA)
        assertThat(data.first().name).isEqualTo("Taman Toga RW 03")
    }

    @Test
    fun switchViewMode_updatesViewModeInState() = runTest(testDispatcher) {
        val viewModel = LocationsViewModel(locationRepository = fakeLocationRepo)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.viewMode).isEqualTo(ViewMode.LIST)
        viewModel.setViewMode(ViewMode.MAP)
        assertThat(viewModel.uiState.value.viewMode).isEqualTo(ViewMode.MAP)
    }

    @Test
    fun selectMapLocation_updatesSelectedLocation() = runTest(testDispatcher) {
        val viewModel = LocationsViewModel(locationRepository = fakeLocationRepo)
        advanceUntilIdle()

        val target = fakeLocations[1]
        viewModel.selectMapLocation(target)
        assertThat(viewModel.uiState.value.selectedMapLocation).isEqualTo(target)
    }
}
