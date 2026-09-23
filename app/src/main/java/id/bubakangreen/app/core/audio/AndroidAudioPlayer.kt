package id.bubakangreen.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android MediaPlayer implementation of [AudioPlayer].
 * Enforces single-play, non-looping audio for botanical Mandarin pronunciation.
 */
class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private val _state = MutableStateFlow<AudioState>(AudioState.Idle)
    override val state: StateFlow<AudioState> = _state.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    override fun play(url: String) {
        if (url.isBlank()) {
            _state.value = AudioState.Error("Tautan audio tidak valid.")
            return
        }

        stop()

        try {
            _state.value = AudioState.Loading
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                isLooping = false

                setOnPreparedListener { player ->
                    _state.value = AudioState.Playing
                    player.start()
                }

                setOnCompletionListener {
                    _state.value = AudioState.Idle
                    cleanUp()
                }

                setOnErrorListener { _, _, _ ->
                    _state.value = AudioState.Error("Gagal memutar audio pelafalan.")
                    cleanUp()
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            _state.value = AudioState.Error(e.message ?: "Gagal memutar audio.")
            cleanUp()
        }
    }

    override fun stop() {
        cleanUp()
        _state.value = AudioState.Idle
    }

    override fun release() {
        cleanUp()
        _state.value = AudioState.Idle
    }

    private fun cleanUp() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            }
        } catch (_: Exception) {
            // Safe cleanup ignore
        } finally {
            mediaPlayer = null
        }
    }
}
