package id.bubakangreen.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import id.bubakangreen.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Manages background music, ducking during voice playback, and sound effects for the plant quiz.
 * Guarantees single-instance playback, soft gain (~3%), smooth fade in/out, and zero recomposition leaks.
 */
class QuizAudioManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "QuizAudioManager"
        private const val TARGET_MUSIC_VOLUME = 0.03f // Soft ~3% gain
        private const val DUCK_MUSIC_VOLUME = 0.005f  // Ducked gain during voice playback
        private const val SFX_VOLUME = 0.35f          // Balanced feedback SFX volume (~35%)
    }

    private var musicPlayer: MediaPlayer? = null
    private var sfxPlayer: MediaPlayer? = null
    private var fadeJob: Job? = null

    private var isPlayingMusic = false
    private var isDucked = false

    /**
     * Starts the quiz background music with a gentle fade-in (~400 ms).
     * Continues looping seamlessly across questions until explicitly stopped.
     */
    fun startMusic() {
        stopMusic(immediate = true)

        try {
            val player = MediaPlayer.create(context, R.raw.quiz_background_music) ?: run {
                Log.w(TAG, "Failed to create MediaPlayer for quiz_background_music")
                return
            }

            musicPlayer = player
            player.isLooping = true
            player.setVolume(0f, 0f)
            player.start()
            isPlayingMusic = true
            isDucked = false

            fadeJob?.cancel()
            fadeJob = scope.launch(Dispatchers.Main) {
                val steps = 10
                val stepDelay = 40L // 400 ms total fade-in
                for (i in 1..steps) {
                    if (!isActive || musicPlayer != player) break
                    val vol = TARGET_MUSIC_VOLUME * (i.toFloat() / steps.toFloat())
                    try { player.setVolume(vol, vol) } catch (_: Exception) {}
                    delay(stepDelay)
                }
                try { player.setVolume(TARGET_MUSIC_VOLUME, TARGET_MUSIC_VOLUME) } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting quiz background music", e)
        }
    }

    /**
     * Ducks music volume down during botanical pronunciation or feedback playback.
     */
    fun duckMusic() {
        if (!isPlayingMusic || musicPlayer == null || isDucked) return
        isDucked = true
        fadeJob?.cancel()
        val player = musicPlayer ?: return
        try {
            player.setVolume(DUCK_MUSIC_VOLUME, DUCK_MUSIC_VOLUME)
        } catch (_: Exception) {}
    }

    /**
     * Restores music volume to normal (~3%) after speech finishes.
     */
    fun restoreMusic() {
        if (!isPlayingMusic || musicPlayer == null || !isDucked) return
        isDucked = false
        fadeJob?.cancel()
        val player = musicPlayer ?: return
        fadeJob = scope.launch(Dispatchers.Main) {
            val steps = 6
            val stepDelay = 35L
            for (i in 1..steps) {
                if (!isActive || musicPlayer != player) break
                val vol = DUCK_MUSIC_VOLUME + (TARGET_MUSIC_VOLUME - DUCK_MUSIC_VOLUME) * (i.toFloat() / steps.toFloat())
                try { player.setVolume(vol, vol) } catch (_: Exception) {}
                delay(stepDelay)
            }
            try { player.setVolume(TARGET_MUSIC_VOLUME, TARGET_MUSIC_VOLUME) } catch (_: Exception) {}
        }
    }

    /**
     * Stops the quiz background music with a soft fade-out (~240 ms).
     */
    fun stopMusic(immediate: Boolean = false) {
        isPlayingMusic = false
        isDucked = false
        fadeJob?.cancel()
        val player = musicPlayer ?: return
        musicPlayer = null

        if (immediate) {
            safeReleasePlayer(player)
        } else {
            scope.launch(Dispatchers.Main) {
                val steps = 8
                val stepDelay = 30L // ~240 ms fade-out
                for (i in (steps - 1) downTo 0) {
                    val vol = TARGET_MUSIC_VOLUME * (i.toFloat() / steps.toFloat())
                    try { player.setVolume(vol, vol) } catch (_: Exception) {}
                    delay(stepDelay)
                }
                safeReleasePlayer(player)
            }
        }
    }

    /**
     * Plays the cheerful correct answer chime.
     */
    fun playCorrectSound() {
        playSfx(R.raw.sound_correct)
    }

    /**
     * Plays the gentle incorrect answer cue.
     */
    fun playWrongSound() {
        playSfx(R.raw.sound_wrong)
    }

    private fun playSfx(resId: Int) {
        try {
            sfxPlayer?.let { safeReleasePlayer(it) }
            sfxPlayer = null

            val player = MediaPlayer.create(context, resId) ?: run {
                Log.w(TAG, "Failed to create MediaPlayer for SFX resId=$resId")
                return
            }

            sfxPlayer = player
            player.isLooping = false
            player.setVolume(SFX_VOLUME, SFX_VOLUME)
            player.setOnCompletionListener {
                safeReleasePlayer(player)
                if (sfxPlayer == player) {
                    sfxPlayer = null
                }
            }
            player.setOnErrorListener { _, what, extra ->
                Log.w(TAG, "Error playing SFX resId=$resId: what=$what extra=$extra")
                safeReleasePlayer(player)
                if (sfxPlayer == player) {
                    sfxPlayer = null
                }
                true
            }
            player.start()
        } catch (e: Exception) {
            Log.e(TAG, "Exception while playing SFX resId=$resId", e)
        }
    }

    private fun safeReleasePlayer(player: MediaPlayer) {
        try {
            if (player.isPlaying) {
                player.stop()
            }
            player.reset()
            player.release()
        } catch (_: Exception) {}
    }

    /**
     * Releases all active player instances and cancels fade animations.
     */
    fun release() {
        isPlayingMusic = false
        isDucked = false
        fadeJob?.cancel()
        musicPlayer?.let { safeReleasePlayer(it) }
        musicPlayer = null
        sfxPlayer?.let { safeReleasePlayer(it) }
        sfxPlayer = null
    }
}
