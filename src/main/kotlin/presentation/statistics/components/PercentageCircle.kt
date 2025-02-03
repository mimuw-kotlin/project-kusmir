package presentation.statistics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PercentageCircle(percentage: Float) {
    var animatedPercentage by remember { mutableStateOf(0f) }
    var animatedSweepAngle by remember { mutableStateOf(0f) }

    val animatedValue = animateFloatAsState(
        targetValue = animatedPercentage,
        animationSpec = tween(durationMillis = 1000), label = "Animated Percentage"
    )

    val animatedAngle = animateFloatAsState(
        targetValue = animatedSweepAngle,
        animationSpec = tween(durationMillis = 1000), label = "Animated Sweep Angle"
    )

    LaunchedEffect(percentage) {
        animatedPercentage = percentage
        animatedSweepAngle = percentage * 360
    }

    val color = Color(
        red = (255 * (1 - animatedValue.value)).roundToInt(),
        green = 255,
        blue = 0,
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.size(350.dp)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = animatedAngle.value,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        val percentageInt = (animatedValue.value * 100).roundToInt()
        Text(text = "$percentageInt%", style = MaterialTheme.typography.h4)
    }
}
