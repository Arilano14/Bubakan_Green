package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.admin.LocationApprovalViewModel
import id.bubakangreen.app.ui.common.UiState
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
class LocationApprovalViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val pendingLocation = Location(
        id = "LOC_PENDING_01",
        name = "Kebun Toga RW 04",
        type = LocationType.TAMAN_TOGA,
        rw = "04",
        address = "Balai RW 04",
        description = "Tanaman obat warga",
        latitude = -7.0690,
        longitude = 110.3320,
        accuracyMeters = 14f,
        coordinatesStatus = CoordinatesStatus.PENDING,
        picUid = "pic_rw04",
        status = LocationStatus.PENDING_APPROVAL
    )

    private class FakeLocationRepo(initialList: List<Location>) : LocationRepository {
        val locations = initialList.toMutableList()

        override fun getPublishedLocations(): Flow<Result<List<Location>>> =
            flowOf(Result.Success(locations.filter { it.status == LocationStatus.PUBLISHED }))

        override fun getFeaturedLocations(): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> = flowOf(Result.Success(emptyList()))
        override fun getLocationById(locationId: String): Flow<Result<Location?>> =
            flowOf(Result.Success(locations.find { it.id == locationId }))

        override suspend fun createLocation(location: Location): Result<String> {
            locations.add(location)
            return Result.Success(location.id)
        }

        override suspend fun updateLocation(location: Location): Result<Unit> {
            val idx = locations.indexOfFirst { it.id == location.id }
            if (idx != -1) locations[idx] = location else locations.add(location)
            return Result.Success(Unit)
        }

        override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> =
            Result.Success(locations.filter { it.picUid == picUid })

        override suspend fun getPendingLocations(): Result<List<Location>> =
            Result.Success(locations.filter { it.status == LocationStatus.PENDING_APPROVAL })

        override suspend fun deleteLocation(locationId: String): Result<Unit> {
            locations.removeAll { it.id == locationId }
            return Result.Success(Unit)
        }
    }

    private class FakeAuditRepo : AuditRepository {
        val logs = mutableListOf<AuditLog>()
        override suspend fun recordAction(auditLog: AuditLog): Result<Unit> {
            logs.add(auditLog)
            return Result.Success(Unit)
        }
        override fun getAuditLogs(): Flow<Result<List<AuditLog>>> = flowOf(Result.Success(emptyList()))
    }

    private lateinit var fakeLocationRepo: FakeLocationRepo
    private lateinit var fakeAuditRepo: FakeAuditRepo

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationRepo = FakeLocationRepo(listOf(pendingLocation))
        fakeAuditRepo = FakeAuditRepo()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadPendingLocations_displaysPendingList() = runTest(testDispatcher) {
        val viewModel = LocationApprovalViewModel(fakeLocationRepo, fakeAuditRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val data = (state as UiState.Success).data
        assertThat(data).hasSize(1)
        assertThat(data.first().name).isEqualTo("Kebun Toga RW 04")
    }

    @Test
    fun approveLocation_transitionsToPublishedAndVerified_andRecordsAudit() = runTest(testDispatcher) {
        val viewModel = LocationApprovalViewModel(fakeLocationRepo, fakeAuditRepo)
        advanceUntilIdle()

        viewModel.approveLocation(pendingLocation, adminUid = "admin_super")
        advanceUntilIdle()

        val updated = fakeLocationRepo.locations.find { it.id == pendingLocation.id }
        assertThat(updated?.status).isEqualTo(LocationStatus.PUBLISHED)
        assertThat(updated?.coordinatesStatus).isEqualTo(CoordinatesStatus.VERIFIED)

        assertThat(fakeAuditRepo.logs).hasSize(1)
        val audit = fakeAuditRepo.logs.first()
        assertThat(audit.action).isEqualTo("LOCATION_APPROVED")
        assertThat(audit.actorRole).isEqualTo("ADMIN")

        // Pending queue should now be empty
        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Empty::class.java)
    }

    @Test
    fun rejectLocation_transitionsToDraftWithNote_andRecordsAudit() = runTest(testDispatcher) {
        val viewModel = LocationApprovalViewModel(fakeLocationRepo, fakeAuditRepo)
        advanceUntilIdle()

        viewModel.rejectLocation(
            location = pendingLocation,
            rejectionNote = "Mohon lengkapi foto gerbang kebun.",
            adminUid = "admin_super"
        )
        advanceUntilIdle()

        val updated = fakeLocationRepo.locations.find { it.id == pendingLocation.id }
        assertThat(updated?.status).isEqualTo(LocationStatus.DRAFT)
        assertThat(updated?.rejectionNote).isEqualTo("Mohon lengkapi foto gerbang kebun.")

        assertThat(fakeAuditRepo.logs).hasSize(1)
        val audit = fakeAuditRepo.logs.first()
        assertThat(audit.action).isEqualTo("LOCATION_REJECTED")
        assertThat(audit.actorRole).isEqualTo("ADMIN")

        // Pending queue should now be empty
        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Empty::class.java)
    }
}
