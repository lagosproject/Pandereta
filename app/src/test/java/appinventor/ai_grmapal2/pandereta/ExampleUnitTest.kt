package appinventor.ai_grmapal2.pandereta

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class ExampleUnitTest {

    private lateinit var mockSensorRepository: SensorRepository
    private lateinit var mockAudioPlayer: AudioPlayer
    private lateinit var viewModel: TambourineViewModel

    @Before
    fun setUp() {
        mockSensorRepository = mock(SensorRepository::class.java)
        mockAudioPlayer = mock(AudioPlayer::class.java)
        viewModel = TambourineViewModel(mockSensorRepository, mockAudioPlayer)
    }

    @Test
    fun testProcessSensorDataUpdatesRotation() {
        // Initially, rotationDegree should be 0.0f
        assertEquals(0f, viewModel.rotationDegree.value)

        // Pass some sensor values where X is 4f
        // targetRotation = rawX * -5f = -20f
        // filteredRotation = filteredRotation + 0.15f * (-20f - filteredRotation)
        // With filteredRotation starting at 0f: filteredRotation = 0f + 0.15 * -20f = -3f
        viewModel.processSensorData(floatArrayOf(4f, 0f, 0f))

        assertEquals(-3f, viewModel.rotationDegree.value, 0.01f)
    }

    @Test
    fun testProcessSensorDataTriggersShakeOnHighMagnitude() {
        // A shake should trigger playShake if magnitude exceeds 12.0f
        // Normal gravity is filtered out, let's inject a sudden spike
        // Initially gravityX = 0f, gravityY = 0f, gravityZ = 0f
        // First sample will isolate gravity slightly:
        // gravityX = 0.8 * 0 + 0.2 * 20 = 4.0f
        // linearX = 20 - 4 = 16.0f
        // Magnitude will be approx 16.0f, which is > 12.0f
        viewModel.processSensorData(floatArrayOf(20f, 0f, 0f))

        verify(mockAudioPlayer, times(1)).playShake(anyFloat())
    }

    @Test
    fun testProcessSensorDataThrottlesShakes() {
        // Inject a high magnitude shake
        viewModel.processSensorData(floatArrayOf(20f, 0f, 0f))
        verify(mockAudioPlayer, times(1)).playShake(anyFloat())

        // Injecting another high magnitude shake immediately should be throttled (less than 150ms difference)
        viewModel.processSensorData(floatArrayOf(20f, 0f, 0f))
        verify(mockAudioPlayer, times(1)).playShake(anyFloat()) // Still 1 because of cooldown throttling
    }

    @Test
    fun testProcessSensorDataSizeCheckPreventsCrash() {
        // If the values array is too small, it should return gracefully without crashing
        viewModel.processSensorData(floatArrayOf(1.0f, 0.0f))
        // Verify no crash occurred and rotationDegree remains 0.0f
        assertEquals(0f, viewModel.rotationDegree.value)
    }
}