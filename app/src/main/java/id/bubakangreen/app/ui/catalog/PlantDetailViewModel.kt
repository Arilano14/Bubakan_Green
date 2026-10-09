package id.bubakangreen.app.ui.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.R
import id.bubakangreen.app.core.audio.AndroidAudioPlayer
import id.bubakangreen.app.core.audio.AudioPlayer
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.core.audio.QuizAudioManager
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
import kotlinx.coroutines.delay
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
    val isOffline: Boolean = false,
    val hasChineseVoice: Boolean = false
) {
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

    private val quizAudioManager: QuizAudioManager =
        QuizAudioManager(application.applicationContext, viewModelScope)

    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState.asStateFlow()

    companion object {
        fun getLocalPlantRawRes(plantId: String, nameId: String): Int? {
            val idNorm = plantId.lowercase().removePrefix("pl-").replace("-", "_")
            val nameNorm = nameId.lowercase().trim().replace(" ", "_").replace("daun_", "")
            return when {
                idNorm == "cabai" || nameNorm == "cabai" -> R.raw.audio_plant_cabai
                idNorm == "jahe" || nameNorm == "jahe" -> R.raw.audio_plant_jahe
                idNorm == "kangkung" || nameNorm == "kangkung" -> R.raw.audio_plant_kangkung
                idNorm == "kemangi" || nameNorm == "kemangi" -> R.raw.audio_plant_kemangi
                idNorm == "kencur" || nameNorm == "kencur" -> R.raw.audio_plant_kencur
                idNorm == "kunyit" || nameNorm == "kunyit" -> R.raw.audio_plant_kunyit
                idNorm.contains("lidah") || nameNorm.contains("lidah") -> R.raw.audio_plant_lidah_buaya
                idNorm.contains("pegag") || nameNorm.contains("pegag") -> R.raw.audio_plant_pegagan
                idNorm == "sereh" || nameNorm == "sereh" || idNorm == "serai" || nameNorm == "serai" -> R.raw.audio_plant_sereh
                idNorm.contains("sirih") || nameNorm.contains("sirih") -> R.raw.audio_plant_sirih
                idNorm == "terong" || nameNorm == "terong" -> R.raw.audio_plant_terong
                idNorm == "tomat" || nameNorm == "tomat" -> R.raw.audio_plant_tomat
                else -> null
            }
        }

        fun getFeedbackRawRes(score: Int): Int {
            return when {
                score >= 100 -> R.raw.audio_feedback_perfect       // hebat semuanya benar.aac
                score >= 80 -> R.raw.audio_feedback_excellent     // bagus sekalii.aac
                score >= 60 -> R.raw.audio_feedback_good          // lumayan bagus terus berusaha.aac
                score >= 40 -> R.raw.audio_feedback_low           // berusaha sedikit lagi kamu akan lebih baik.aac
                score >= 20 -> R.raw.audio_feedback_encouragement // terus semangat kamu pasti bisa.aac
                else -> R.raw.audio_feedback_retry                // jgn berkecil hati coba lagi.aac
            }
        }
    }

    private fun computeHasChineseVoice(plant: MasterPlant?, voice: PlantVoice?): Boolean {
        val audioUrl = voice?.audioUrl?.ifBlank { null } ?: plant?.mandarinAudioUrl?.ifBlank { null }
        if (!audioUrl.isNullOrBlank()) return true
        if (plant == null) return false
        return getLocalPlantRawRes(plant.id, plant.nameId) != null
    }

    init {
        viewModelScope.launch {
            audioPlayer.state.collect { audioState ->
                _uiState.update { it.copy(audioState = audioState) }
                // Duck background music during botanical pronunciation or feedback speech
                if (audioState is AudioState.Playing) {
                    quizAudioManager.duckMusic()
                } else {
                    quizAudioManager.restoreMusic()
                }
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
                                _uiState.update {
                                    it.copy(
                                        plant = UiState.Success(plant),
                                        hasChineseVoice = computeHasChineseVoice(plant, it.voice)
                                    )
                                }
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
                        val v = result.data
                        _uiState.update {
                            val currentPlant = (it.plant as? UiState.Success)?.data
                            it.copy(
                                voice = v,
                                hasChineseVoice = computeHasChineseVoice(currentPlant, v)
                            )
                        }
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

        android.util.Log.i("PlantDetailViewModel", "playMandarinAudio invoked for plant: id=${currentPlant.id}, nameId=${currentPlant.nameId}, audioUrl=$audioUrl")

        if (!audioUrl.isNullOrBlank() && audioUrl.startsWith("http", ignoreCase = true)) {
            audioPlayer.play(audioUrl)
        } else {
            val resId = getLocalPlantRawRes(currentPlant.id, currentPlant.nameId)
            android.util.Log.i("PlantDetailViewModel", "getLocalPlantRawRes returned resId=$resId")
            if (resId != null) {
                audioPlayer.playRaw(resId)
            } else if (!audioUrl.isNullOrBlank()) {
                audioPlayer.play(audioUrl)
            } else {
                _uiState.update {
                    it.copy(audioState = AudioState.Error("Audio pelafalan belum tersedia untuk tanaman ini."))
                }
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
        audioPlayer.stop()
        quizAudioManager.startMusic()
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

        val currentQ = session.currentQuestion
        val isCorrect = currentQ != null && selected == currentQ.correctAnswer
        if (isCorrect) {
            quizAudioManager.playCorrectSound()
        } else {
            quizAudioManager.playWrongSound()
        }

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
            // 1. Soft fade-out background music (~240ms)
            quizAudioManager.stopMusic(immediate = false)

            // 2. Play Mandarin feedback after brief pause while score popup animates
            viewModelScope.launch {
                delay(300)
                handleQuizResultAudio(finishedSession.score)
            }
        }
    }

    fun retryQuiz() {
        val questions = _uiState.value.activeQuestions
        if (questions.size < 5) return
        val selected = questions.shuffled().take(5)
        val currentPlantId = (_uiState.value.plant as? UiState.Success)?.data?.id ?: ""
        audioPlayer.stop()
        quizAudioManager.startMusic()
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
        audioPlayer.stop()
        quizAudioManager.stopMusic(immediate = true)
        _uiState.update { it.copy(quizSession = null) }
    }

    private fun handleQuizResultAudio(score: Int) {
        val resId = getFeedbackRawRes(score)
        audioPlayer.playRaw(resId)
    }

    fun stopAudio() {
        audioPlayer.stop()
        quizAudioManager.stopMusic(immediate = true)
    }

    override fun onCleared() {
        super.onCleared()
        quizAudioManager.release()
        audioPlayer.release()
    }
}
