package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.location.LocationClient
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.Coordinates
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.pic.LocationFormViewModel
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
class LocationFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeLocationRepo : LocationRepository {
        val createdLocations = mutableListOf<Location>()
        override fun getPublishedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getFeaturedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationById(locationId: String): Flow<Result<Location?>> = flowOf(Result.Success(null))
        override suspend fun createLocation(location: Location): Result<String> {
            createdLocations.add(location)
            return Result.Success(location.id)
        }
        override suspend fun updateLocation(location: Location): Result<Unit> = Result.Success(Unit)
        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> = Result.Success(emptyList())
        override suspend fun getPendingLocations(): Result<List<Location>> = Result.Success(emptyList())
        override suspend fun deleteLocation(locationId: String): Result<Unit> = Result.Success(Unit)
    }

    private class FakeAuditRepo : AuditRepository {
        val recordedLogs = mutableListOf<AuditLog>()
        override suspend fun recordAction(auditLog: AuditLog): Result<Unit> {
            recordedLogs.add(auditLog)
            return Result.Success(Unit)
        }
        override fun getAuditLogs(): Flow<Result<List<AuditLog>>> = flowOf(Result.Success(emptyList()))
    }

    private class FakeLocationClient(
        var simulatedAccuracy: Float = 12f
    ) : LocationClient {
        override suspend fun getCurrentLocation(): Result<Coordinates> {
            return Result.Success(
                Coordinates(
                    latitude = -7.0681,
                    longitude = 110.3289,
                    accuracyMeters = simulatedAccuracy
                )
            )
        }
    }

    private lateinit var fakeLocationRepo: FakeLocationRepo
    private lateinit var fakeAuditRepo: FakeAuditRepo

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationRepo = FakeLocationRepo()
        fakeAuditRepo = FakeAuditRepo()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveLocation_withShortName_setsValidationError() = runTest(testDispatcher) {
        val viewModel = LocationFormViewModel(fakeLocationRepo, fakeAuditRepo)
        viewModel.onNameChange("A")
        viewModel.saveLocation("pic_1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.validationError).contains("minimal 3 karakter")
        assertThat(fakeLocationRepo.createdLocations).isEmpty()
    }

    @Test
    fun saveLocation_withoutGps_setsValidationError() = runTest(testDispatcher) {
        val viewModel = LocationFormViewModel(fakeLocationRepo, fakeAuditRepo)
        viewModel.onNameChange("Kebun Toga RW 03")
        viewModel.onAddressChange("Jl. Mawar")
        viewModel.onDescriptionChange("Kebun tanaman obat")
        viewModel.saveLocation("pic_1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.validationError).contains("GPS wajib dikunci")
        assertThat(fakeLocationRepo.createdLocations).isEmpty()
    }

    @Test
    fun captureGps_withHighAccuracy_setsCoordinatesAndNoWarning() = runTest(testDispatcher) {
        val fakeClient = FakeLocationClient(simulatedAccuracy = 12f)
        val viewModel = LocationFormViewModel(fakeLocationRepo, fakeAuditRepo, fakeClient)
        viewModel.captureGps()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.latitude).isEqualTo(-7.0681)
        assertThat(state.longitude).isEqualTo(110.3289)
        assertThat(state.accuracyMeters).isEqualTo(12f)
        assertThat(state.gpsWarning).isNull()
    }

    @Test
    fun captureGps_withLowAccuracy_setsWarningPromptingRetry() = runTest(testDispatcher) {
        val fakeClient = FakeLocationClient(simulatedAccuracy = 35f)
        val viewModel = LocationFormViewModel(fakeLocationRepo, fakeAuditRepo, fakeClient)
        viewModel.captureGps()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.accuracyMeters).isEqualTo(35f)
        assertThat(state.gpsWarning).contains(">25m")
    }

    @Test
    fun saveLocation_success_createsPendingLocationAndAuditLog() = runTest(testDispatcher) {
        val fakeClient = FakeLocationClient(simulatedAccuracy = 10f)
        val viewModel = LocationFormViewModel(fakeLocationRepo, fakeAuditRepo, fakeClient)
        viewModel.onNameChange("Kebun Toga Herbal RW 02")
        viewModel.onAddressChange("Jl. Melati RW 02")
        viewModel.onDescriptionChange("Koleksi tanaman obat keluarga warga RW 02")
        viewModel.captureGps()
        advanceUntilIdle()

        viewModel.saveLocation("pic_rw02")
        advanceUntilIdle()

        assertThat(fakeLocationRepo.createdLocations).hasSize(1)
        val created = fakeLocationRepo.createdLocations.first()
        assertThat(created.name).isEqualTo("Kebun Toga Herbal RW 02")
        assertThat(created.status).isEqualTo(LocationStatus.PENDING_APPROVAL)
        assertThat(created.coordinatesStatus).isEqualTo(CoordinatesStatus.PENDING)
        assertThat(created.picUid).isEqualTo("pic_rw02")

        assertThat(fakeAuditRepo.recordedLogs).hasSize(1)
        val audit = fakeAuditRepo.recordedLogs.first()
        assertThat(audit.action).isEqualTo("LOCATION_CREATED")
        assertThat(audit.actorRole).isEqualTo("PIC")
    }
}
