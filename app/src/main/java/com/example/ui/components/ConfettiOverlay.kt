package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    milestoneText: String? = null,
    onDismiss: () -> Unit = {}
) {
    if (!visible) return

    val particles = remember {
        val colors = listOf(
            Color(0xFF3525CD),
            Color(0xFFF59E0B),
            Color(0xFF10B981),
            Color(0xFFEC4899),
            Color(0xFF8B5CF6),
            Color(0xFF3B82F6),
            Color(0xFFEF4444)
        )
        List(50) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = -0.1f - Random.nextFloat() * 0.3f,
                speed = 0.5f + Random.nextFloat() * 0.5f,
                size = 6f + Random.nextFloat() * 8f,
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(visible) {
        val startTime = System.currentTimeMillis()
        val duration = 2400L
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            if (progress >= 1f) break
            delay(16)
        }
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val alpha = (1f - (progress - 0.7f) / 0.3f).coerceIn(0f, 1f)

            particles.forEach { p ->
                val currentY = (p.startY + progress * p.speed * 1.4f) * canvasH
                val currentX = (p.x + kotlin.math.sin((progress * 4f + p.x * 8f).toDouble()).toFloat() * 0.04f) * canvasW
                val rotation = progress * p.rotationSpeed

                if (currentY in -50f..(canvasH + 50f)) {
                    rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                        if (p.isCircle) {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.size,
                                center = Offset(currentX, currentY)
                            )
                        } else {
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(currentX - p.size, currentY - p.size * 0.6f),
                                size = Size(p.size * 2f, p.size * 1.2f)
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = progress < 0.9f,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color(0xFF1E1B4B))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = milestoneText ?: "Streak Record Maintained! 🔥",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
