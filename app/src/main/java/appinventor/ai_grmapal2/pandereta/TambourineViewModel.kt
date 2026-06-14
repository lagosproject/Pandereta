package appinventor.ai_grmapal2.pandereta

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TambourineViewModel(
    private val sensorRepository: SensorRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val isLoaded: StateFlow<Boolean> = audioPlayer.isLoaded

    private val _rotationDegree = mutableFloatStateOf(0f)
    val rotationDegree: State<Float> = _rotationDegree

    private val _impactTrigger = mutableIntStateOf(0)
    val impactTrigger: State<Int> = _impactTrigger

    private var lastShakeTime = 0L

    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var filteredRotation = 0f

    private var sensorJob: Job? = null

    fun startSensorTracking() {
        if (sensorJob != null) return
        sensorJob = viewModelScope.launch {
            sensorRepository.getAccelerometerData().collect { values ->
                processSensorData(values)
            }
        }
    }

    fun stopSensorTracking() {
        sensorJob?.cancel()
        sensorJob = null
    }

    fun processSensorData(values: FloatArray) {
        if (values.size < 3) return
        val rawX = values[0]
        val rawY = values[1]
        val rawZ = values[2]

        // 1. Isolate gravity (low-pass filter)
        val alpha = 0.8f
        gravityX = alpha * gravityX + (1 - alpha) * rawX
        gravityY = alpha * gravityY + (1 - alpha) * rawY
        gravityZ = alpha * gravityZ + (1 - alpha) * rawZ

        // 2. Subtract gravity components
        val linearX = rawX - gravityX
        val linearY = rawY - gravityY
        val linearZ = rawZ - gravityZ

        // 3. Compute acceleration magnitude in 3D space
        val magnitude = Math.sqrt(
            (linearX * linearX + linearY * linearY + linearZ * linearZ).toDouble()
        ).toFloat()

        // 4. Smooth out UI rotation
        val targetRotation = rawX * -5f
        val rotationSmoothing = 0.15f
        filteredRotation = filteredRotation + rotationSmoothing * (targetRotation - filteredRotation)
        _rotationDegree.floatValue = filteredRotation

        // 5. Shake detection threshold checks with cooldown
        val currentTime = System.currentTimeMillis()
        if (magnitude > 12.0f && currentTime - lastShakeTime > 150) {
            lastShakeTime = currentTime
            audioPlayer.playShake(magnitude)
            _impactTrigger.intValue += 1
        }
    }

    fun playTap() {
        audioPlayer.playTap()
        _impactTrigger.intValue += 1
    }

    override fun onCleared() {
        super.onCleared()
        stopSensorTracking()
        audioPlayer.release()
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TambourineViewModel::class.java)) {
                val appContext = context.applicationContext
                val sensorRepository = SensorRepository(appContext)
                val audioPlayer = AudioPlayer(appContext)
                return TambourineViewModel(sensorRepository, audioPlayer) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
