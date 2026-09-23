package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.catalog.CatalogViewModel
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakePlants = listOf(
        MasterPlant(
            id = "P1",
            nameId = "Jahe Merah",
            nameLatin = "Zingiber officinale",
            nameMandarin = "红姜",
            pinyin = "hóng jiāng",
            description = "Menghangatkan badan dan radang sendi"
        ),
        MasterPlant(
            id = "P2",
            nameId = "Kencur",
            nameLatin = "Kaempferia galanga",
            nameMandarin = "山柰",
            pinyin = "shān nài",
            description = "Meredakan batuk dan radang tenggorokan"
        ),
        MasterPlant(
            id = "P3",
            nameId = "Kunyit Putih",
            nameLatin = "Curcuma zedoaria",
            nameMandarin = "郁金",
            pinyin = "yù jīn",
            description = "Mengobati gangguan pencernaan dan lambung"
        )
    )

    private val fakePlantRepo = object : PlantRepository {
        override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
            flowOf(Result.Success(fakePlants))

        override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
            flowOf(Result.Success(fakePlants.find { it.id == plantId }))

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
    fun loadAllPlants_exposesSuccessStateWithAllPlants() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val successData = (state as UiState.Success).data
        assertThat(successData).hasSize(3)
        assertThat(successData.map { it.nameId }).containsExactly("Jahe Merah", "Kencur", "Kunyit Putih")
    }

    @Test
    fun searchByIndonesianName_filtersAccuratelyAfterDebounce() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Jahe")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val filtered = (state as UiState.Success).data
        assertThat(filtered).hasSize(1)
        assertThat(filtered.first().nameId).isEqualTo("Jahe Merah")
    }

    @Test
    fun searchByLatinName_filtersAccurately() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Curcuma")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val filtered = (state as UiState.Success).data
        assertThat(filtered).hasSize(1)
        assertThat(filtered.first().nameLatin).isEqualTo("Curcuma zedoaria")
    }

    @Test
    fun searchByBenefitDescription_filtersAccurately() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("batuk")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        val filtered = (state as UiState.Success).data
        assertThat(filtered).hasSize(1)
        assertThat(filtered.first().nameId).isEqualTo("Kencur")
    }

    @Test
    fun searchWithNonExistentTerm_exposesEmptyState() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("stroberi")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Empty::class.java)
    }

    @Test
    fun resetSearch_restoresAllPlants() = runTest(testDispatcher) {
        val viewModel = CatalogViewModel(plantRepository = fakePlantRepo)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("Jahe")
        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.resetSearch()
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value.plants
        assertThat(state).isInstanceOf(UiState.Success::class.java)
        assertThat((state as UiState.Success).data).hasSize(3)
    }
}
