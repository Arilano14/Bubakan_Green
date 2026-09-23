package id.bubakangreen.app.ui.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.audio.AndroidAudioPlayer
import id.bubakangreen.app.core.audio.AudioPlayer
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlantDetailUiState(
    val plant: UiState<MasterPlant> = UiState.Loading,
    val locationsGrowing: List<Location> = emptyList(),
    val audioState: AudioState = AudioState.Idle,
    val isOffline: Boolean = false
)

class PlantDetailViewModel(
    application: Application,
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository(),
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository(),
    private val audioPlayer: AudioPlayer = AndroidAudioPlayer(application.applicationContext)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            audioPlayer.state.collect { audioState ->
                _uiState.update { it.copy(audioState = audioState) }
            }
        }
    }

    fun loadPlantDetail(plantId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(plant = UiState.Loading) }
            plantRepository.getMasterPlantById(plantId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(plant = UiState.Loading) }
                    }
                    is Result.Success -> {
                        val plant = result.data
                        if (plant == null) {
                            _uiState.update {
                                it.copy(plant = UiState.Empty("Informasi tanaman tidak ditemukan."))
                            }
                        } else {
                            _uiState.update { it.copy(plant = UiState.Success(plant)) }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                plant = UiState.Error(
                                    result.message ?: "Gagal memuat detail tanaman."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Strictly user-triggered audio pronunciation playback.
     */
    fun playMandarinAudio() {
        val currentPlant = (_uiState.value.plant as? UiState.Success)?.data ?: return
        val audioUrl = currentPlant.mandarinAudioUrl
        if (!audioUrl.isNullOrBlank()) {
            audioPlayer.play(audioUrl)
        } else {
            _uiState.update {
                it.copy(audioState = AudioState.Error("Audio pelafalan belum tersedia untuk tanaman ini."))
            }
        }
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
