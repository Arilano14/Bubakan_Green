package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.admin.MasterPlantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MasterPlantViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val defaultSereh = MasterPlant(
        id = "sereh",
        name = "Sereh",
        nameId = "Sereh",
        scientificName = "Cymbopogon citratus",
        nameLatin = "Cymbopogon citratus",
        mandarinName = "柠檬草",
        nameMandarin = "柠檬草",
        mandarinPinyin = "níng méng cǎo",
        pinyin = "níng méng cǎo",
        description = "Tanaman rumput aromatik beraroma sitrun segar.",
        primaryPhotoUrl = "plant_sereh",
        imageSourceType = "LOCAL",
        imageAssetName = "plant_sereh"
    )

    private class FakePlantRepo(initialList: List<MasterPlant>) : PlantRepository {
        val masterPlants = initialList.toMutableList()

        override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
            flowOf(Result.Success(masterPlants.toList()))

        override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
            flowOf(Result.Success(masterPlants.find { it.id == plantId }))

        override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> =
            flowOf(Result.Success(emptyList()))

        override suspend fun createMasterPlant(plant: MasterPlant): Result<String> {
            masterPlants.add(plant)
            return Result.Success(plant.id)
        }

        override suspend fun updateMasterPlant(plant: MasterPlant): Result<Unit> {
            val idx = masterPlants.indexOfFirst { it.id == plant.id }
            if (idx != -1) {
                masterPlants[idx] = plant
            }
            return Result.Success(Unit)
        }

        override suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String> =
            Result.Success("LP_01")

        override suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit> =
            Result.Success(Unit)

        override suspend fun removePlantFromLocation(locationPlantId: String): Result<Unit> =
            Result.Success(Unit)
    }

    private class FakeAuditRepo : AuditRepository {
        val logs = mutableListOf<AuditLog>()
        override suspend fun recordAction(auditLog: AuditLog): Result<Unit> {
            logs.add(auditLog)
            return Result.Success(Unit)
        }
        override fun getAuditLogs(): Flow<Result<List<AuditLog>>> = flowOf(Result.Success(logs.toList()))
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
    fun `savePlant with duplicate Latin name is rejected with botanical duplicate error`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("Serai Dapur Baru")
        // Canonical match: "Cymbopogon citratus" with different casing and whitespace
        viewModel.onNameLatinChange("  cymbopogon   citratus  ")
        viewModel.onDescriptionChange("Deskripsi sereh dapur uji.")
        viewModel.onPhotoUrlChange("plant_sereh")

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isSaving).isFalse()
        assertThat(state.validationError).isNotNull()
        assertThat(state.validationError).contains("sudah terdaftar")
        assertThat(state.validationError).contains("Sereh")
        assertThat(fakePlantRepo.masterPlants).hasSize(1)
    }

    @Test
    fun `savePlant with duplicate Indonesian common name is rejected`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("sereh") // duplicate case-insensitive common name
        viewModel.onNameLatinChange("Cymbopogon nardus") // distinct Latin name
        viewModel.onDescriptionChange("Minyak atsiri sereh wangi.")
        viewModel.onPhotoUrlChange("plant_sereh")

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.validationError).isNotNull()
        assertThat(state.validationError).contains("sudah terdaftar")
        assertThat(fakePlantRepo.masterPlants).hasSize(1)
    }

    @Test
    fun `savePlant without photo is rejected with validation error`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("Kunyit Putih")
        viewModel.onPhotoUrlChange("") // missing photo

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.validationError).contains("Foto tanaman wajib disertakan")
        assertThat(fakePlantRepo.masterPlants).hasSize(1)
    }

    @Test
    fun `savePlant with only required fields (nameId and photoUrl) succeeds`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        // Only fill required fields
        viewModel.onNameIdChange("Brotowali")
        viewModel.onPhotoUrlChange("plant_brotowali")

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        assertThat(fakePlantRepo.masterPlants).hasSize(2)
        val created = fakePlantRepo.masterPlants.find { it.nameId == "Brotowali" }
        assertThat(created).isNotNull()
        assertThat(created!!.primaryPhotoUrl).isEqualTo("plant_brotowali")
        assertThat(created.nameLatin).isEmpty()
        assertThat(created.description).isEmpty()
    }

    @Test
    fun `updatePlant preserves stable plantId`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.loadPlant("sereh")
        advanceUntilIdle()

        // Edit attributes
        viewModel.onNameIdChange("Sereh Wangi Super")
        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        assertThat(fakePlantRepo.masterPlants).hasSize(1)
        val updated = fakePlantRepo.masterPlants.first()
        assertThat(updated.id).isEqualTo("sereh") // Preserved stable ID!
        assertThat(updated.nameId).isEqualTo("Sereh Wangi Super")
    }

    @Test
    fun `savePlant with insecure HTTP URL is rejected`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("Rosela")
        viewModel.onNameLatinChange("Hibiscus sabdariffa")
        viewModel.onDescriptionChange("Tanaman bunga rosela kaya antioksidan.")
        viewModel.onPhotoUrlChange("http://example.com/rosela.jpg") // insecure HTTP

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.validationError).contains("HTTPS")
        assertThat(fakePlantRepo.masterPlants).hasSize(1)
    }

    @Test
    fun `savePlant with valid HTTPS remote URL saves with REMOTE_URL source type`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("Pegagan")
        viewModel.onNameLatinChange("Centella asiatica")
        viewModel.onNameMandarinChange("积雪草")
        viewModel.onPinyinChange("jī xuě cǎo")
        viewModel.onDescriptionChange("Herbal obat perangsang daya ingat dan sirkulasi darah.")
        viewModel.onCharacteristicsChange("Tanaman merayap tidak berbatang kayu.")
        viewModel.onCommonUsesChange("Teh herbal, lalapan.")
        viewModel.onCultivationNotesChange("Menyukai tanah lembap dan sinar matahari cukup.")
        viewModel.onPhotoUrlChange("https://upload.wikimedia.org/wikipedia/commons/pegagan.jpg")
        viewModel.onImageAuthorChange("Budi Botanist")
        viewModel.onImageLicenseChange("CC BY-SA 4.0")
        viewModel.onSourceReferencesChange("Royal Botanic Gardens, Kew (POWO)")

        var savedEventReceived = false
        val navCollector = launch(StandardTestDispatcher(testScheduler)) {
            viewModel.successEvent.collect {
                savedEventReceived = true
            }
        }

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()
        navCollector.cancel()

        assertThat(savedEventReceived).isTrue()
        assertThat(fakePlantRepo.masterPlants).hasSize(2)
        val savedPlant = fakePlantRepo.masterPlants.find { it.nameId == "Pegagan" }
        assertThat(savedPlant).isNotNull()
        assertThat(savedPlant!!.imageSourceType).isEqualTo("REMOTE_URL")
        assertThat(savedPlant.primaryPhotoUrl).isEqualTo("https://upload.wikimedia.org/wikipedia/commons/pegagan.jpg")
        assertThat(savedPlant.nameMandarin).isEqualTo("积雪草")
        assertThat(savedPlant.pinyin).isEqualTo("jī xuě cǎo")
        assertThat(savedPlant.characteristics).isEqualTo("Tanaman merayap tidak berbatang kayu.")
        assertThat(fakeAuditRepo.logs).hasSize(1)
        assertThat(fakeAuditRepo.logs[0].action).isEqualTo("MASTER_PLANT_CREATED")
    }

    @Test
    fun `savePlant with local asset name saves with LOCAL source type`() = runTest {
        val fakePlantRepo = FakePlantRepo(listOf(defaultSereh))
        val fakeAuditRepo = FakeAuditRepo()
        val viewModel = MasterPlantViewModel(fakePlantRepo, fakeAuditRepo)

        viewModel.onNameIdChange("Temulawak")
        viewModel.onNameLatinChange("Curcuma zanthorrhiza")
        viewModel.onDescriptionChange("Rimpang herbal khas nusantara.")
        viewModel.onPhotoUrlChange("plant_temulawak")

        viewModel.savePlant("admin_tester")
        advanceUntilIdle()

        val savedPlant = fakePlantRepo.masterPlants.find { it.nameId == "Temulawak" }
        assertThat(savedPlant).isNotNull()
        assertThat(savedPlant!!.imageSourceType).isEqualTo("LOCAL")
        assertThat(savedPlant.imageAssetName).isEqualTo("plant_temulawak")
    }
}
