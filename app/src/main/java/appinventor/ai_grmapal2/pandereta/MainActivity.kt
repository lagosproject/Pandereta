package appinventor.ai_grmapal2.pandereta

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import appinventor.ai_grmapal2.pandereta.ui.theme.PanderetaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val factory = TambourineViewModel.Factory(applicationContext)
        val viewModel: TambourineViewModel by viewModels { factory }

        enableEdgeToEdge()
        setContent {
            PanderetaTheme {
                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F0C08), // Obsidian
                                        Color(0xFF221710), // Deep metallic bronze
                                        Color(0xFF0F0C08)  // Obsidian
                                    )
                                )
                            )

                    ) {
                        TambourineScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TambourineScreen(
    viewModel: TambourineViewModel,
    modifier: Modifier = Modifier
) {
    val rotation by viewModel.rotationDegree
    val isLoaded by viewModel.isLoaded.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe lifecycle events to start/stop sensor tracking
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.startSensorTracking()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.stopSensorTracking()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSensorTracking()
        }
    }

    // Animation trigger logic
    val impactTrigger by viewModel.impactTrigger
    var isTappedOrShaken by remember { mutableStateOf(false) }

    LaunchedEffect(impactTrigger) {
        if (impactTrigger > 0) {
            isTappedOrShaken = true
            delay(100) // 100ms peak bounce
            isTappedOrShaken = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isTappedOrShaken) 1.18f else 1.0f,
        animationSpec = if (isTappedOrShaken) {
            spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessHigh)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        },
        label = "tambourineScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isTappedOrShaken) 0.8f else 0.0f,
        animationSpec = tween(durationMillis = 150),
        label = "tambourineGlow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glassmorphic Instruction Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x12FFFFFF)),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x25FFFFFF), Color(0x05FFFFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                color = Color(0xFFFFD54F), // Premium gold primary text
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 24.dp)
            )
        }

        // Tambourine visual with glow effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Glow backdrop
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD54F).copy(alpha = glowAlpha * 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            val tambourineIcon: Painter = painterResource(id = R.drawable.tambourine_svgrepo_com)
            Image(
                painter = tambourineIcon,
                contentDescription = "Tambourine Icon",
                modifier = Modifier
                    .size(240.dp)
                    .scale(scale)
                    .rotate(rotation)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            if (isLoaded) {
                                viewModel.playTap()
                            }
                        })
                    }
            )
        }
    }
}