package appinventor.ai_grmapal2.pandereta

import android.content.Context
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections

class AudioPlayer(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(2).build()
    private val soundId1: Int
    private val soundId2: Int
    private val soundId3: Int

    private val loadedSounds = Collections.synchronizedSet(mutableSetOf<Int>())
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    init {
        val appContext = context.applicationContext
        soundId1 = soundPool.load(appContext, R.raw.audio1, 1)
        soundId2 = soundPool.load(appContext, R.raw.audio2, 1)
        soundId3 = soundPool.load(appContext, R.raw.membrana, 1)

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSounds.add(sampleId)
                if (loadedSounds.contains(soundId1) &&
                    loadedSounds.contains(soundId2) &&
                    loadedSounds.contains(soundId3)
                ) {
                    _isLoaded.value = true
                }
            } else {
                Log.e("AudioPlayer", "Failed to load sound sample: $sampleId, status: $status")
            }
        }
    }

    fun playShake(intensity: Float) {
        if (!_isLoaded.value) return
        val isLoud = intensity > 22.0f
        val soundId = if (isLoud) soundId2 else soundId1
        val volume = if (isLoud) VOLUME_SHAKE_LOUD else VOLUME_SHAKE_SOFT
        soundPool.play(soundId, volume, volume, 0, 0, 1.0f)
    }

    fun playTap() {
        if (!_isLoaded.value) return
        soundPool.play(soundId3, VOLUME_TAP, VOLUME_TAP, 0, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }

    companion object {
        private const val VOLUME_SHAKE_SOFT = 0.9f
        private const val VOLUME_SHAKE_LOUD = 0.75f
        private const val VOLUME_TAP = 0.85f
    }
}
