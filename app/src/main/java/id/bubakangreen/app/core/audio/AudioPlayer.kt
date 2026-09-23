package id.bubakangreen.app.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * State machine for botanical Mandarin pronunciation audio.
 */
sealed interface AudioState {
    data object Idle : AudioState
    data object Loading : AudioState
    data object Playing : AudioState
    data class Error(val message: String) : AudioState
}

/**
 * Audio player interface decoupling presentation from Android MediaPlayer.
 * Enforces single-play, user-triggered pronunciation audio contract.
 */
interface AudioPlayer {
    val state: StateFlow<AudioState>

    /**
     * Plays audio once from the given URL.
     * Must not loop or autoplay.
     */
    fun play(url: String)

    /**
     * Stops current playback and resets state to Idle.
     */
    fun stop()

    /**
     * Releases underlying media resources when lifecycle ends.
     */
    fun release()
}
