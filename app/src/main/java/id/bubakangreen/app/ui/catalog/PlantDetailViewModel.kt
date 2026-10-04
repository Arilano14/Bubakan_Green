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
import id.bubakangreen.app.domain.model.PlantVoice
import id.bubakangreen.app.domain.model.QuizQuestion
import id.bubakangreen.app.domain.model.QuizSession
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.domain.repository.QuizRepository
import id.bubakangreen.app.domain.repository.VoiceRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlantDetailUiState(
    val plant: UiState<MasterPlant> = UiState.Loading,
    val voice: PlantVoice? = null,
    val activeQuestions: List<QuizQuestion> = emptyList(),
    val quizSession: QuizSession? = null,
    val locationsGrowing: List<Location> = emptyList(),
    val audioState: AudioState = AudioState.Idle,
    val isOffline: Boolean = false
) {
    val hasChineseVoice: Boolean
        get() {
            val audioUrl = voice?.audioUrl ?: (plant as? UiState.Success)?.data?.mandarinAudioUrl
            return !audioUrl.isNullOrBlank()
        }

    val canStartQuiz: Boolean
        get() = activeQuestions.size >= 5
}

class PlantDetailViewModel @JvmOverloads constructor(
    application: Application,
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository(),
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository(),
    private val quizRepository: QuizRepository = RepositoryProvider.getQuizRepository(),
    private val voiceRepository: VoiceRepository = RepositoryProvider.getVoiceRepository(),
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
            _uiState.update { it.copy(plant = UiState.Loading, quizSession = null) }

            // 1. Load botanical master encyclopedia
            launch {
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

            // 2. Load optional botanical Chinese voice metadata
            launch {
                voiceRepository.getVoiceByPlant(plantId).collect { result ->
                    if (result is Result.Success) {
                        _uiState.update { it.copy(voice = result.data) }
                    }
                }
            }

            // 3. Load optional quiz question bank for this plant
            launch {
                quizRepository.getQuestionsByPlant(plantId).collect { result ->
                    if (result is Result.Success) {
                        _uiState.update { it.copy(activeQuestions = result.data.filter { q -> q.isActive }) }
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
        val audioUrl = _uiState.value.voice?.audioUrl?.ifBlank { null }
            ?: currentPlant.mandarinAudioUrl?.ifBlank { null }

        if (!audioUrl.isNullOrBlank()) {
            audioPlayer.play(audioUrl)
        } else {
            _uiState.update {
                it.copy(audioState = AudioState.Error("Audio pelafalan belum tersedia untuk tanaman ini."))
            }
        }
    }

    /**
     * Starts a 5-question randomized quiz session from the question bank.
     */
    fun startQuiz() {
        val questions = _uiState.value.activeQuestions
        if (questions.size < 5) return
        val selected = questions.shuffled().take(5)
        val currentPlantId = (_uiState.value.plant as? UiState.Success)?.data?.id ?: ""
        _uiState.update {
            it.copy(
                quizSession = QuizSession(
                    plantId = currentPlantId,
                    questions = selected,
                    currentIndex = 0,
                    selectedOption = null,
                    isAnswerConfirmed = false,
                    userAnswers = emptyMap(),
                    isFinished = false
                )
            )
        }
    }

    fun selectOption(option: String) {
        val session = _uiState.value.quizSession ?: return
        if (session.isAnswerConfirmed || session.isFinished) return
        _uiState.update {
            it.copy(quizSession = session.copy(selectedOption = option))
        }
    }

    fun confirmAnswer() {
        val session = _uiState.value.quizSession ?: return
        val selected = session.selectedOption ?: return
        if (session.isAnswerConfirmed || session.isFinished) return
        val updatedAnswers = session.userAnswers.toMutableMap()
        updatedAnswers[session.currentIndex] = selected
        _uiState.update {
            it.copy(
                quizSession = session.copy(
                    isAnswerConfirmed = true,
                    userAnswers = updatedAnswers
                )
            )
        }
    }

    fun nextQuestion() {
        val session = _uiState.value.quizSession ?: return
        if (!session.isAnswerConfirmed) return
        val nextIdx = session.currentIndex + 1
        if (nextIdx < session.totalQuestions) {
            _uiState.update {
                it.copy(
                    quizSession = session.copy(
                        currentIndex = nextIdx,
                        selectedOption = null,
                        isAnswerConfirmed = false
                    )
                )
            }
        } else {
            val finishedSession = session.copy(isFinished = true)
            _uiState.update {
                it.copy(quizSession = finishedSession)
            }
            handleQuizResultAudio(finishedSession.score)
        }
    }

    fun retryQuiz() {
        val questions = _uiState.value.activeQuestions
        if (questions.size < 5) return
        val selected = questions.shuffled().take(5)
        val currentPlantId = (_uiState.value.plant as? UiState.Success)?.data?.id ?: ""
        _uiState.update {
            it.copy(
                quizSession = QuizSession(
                    plantId = currentPlantId,
                    questions = selected,
                    currentIndex = 0,
                    selectedOption = null,
                    isAnswerConfirmed = false,
                    userAnswers = emptyMap(),
                    isFinished = false
                )
            )
        }
    }

    fun exitQuiz() {
        _uiState.update { it.copy(quizSession = null) }
    }

    private fun handleQuizResultAudio(score: Int) {
        val context = getApplication<Application>().applicationContext
        val resName = when {
            score == 100 -> "quiz_perfect_zh"
            score <= 60 -> "quiz_encouragement_zh"
            else -> null
        } ?: return

        val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (resId != 0) {
            audioPlayer.playRaw(resId)
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
