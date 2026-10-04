package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val targetX: Float,
    val targetY: Float,
    val color: Color,
    val size: Float,
    val emoji: String? = null
)

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFFB238), // Amber
            Color(0xFF22C3A6), // Teal
            Color(0xFFFF7A6B), // Coral
            Color(0xFF8B7FE8), // Grape
            Color(0xFFE9932A)
        )
        val emojis = listOf("✨", "🎉", "⭐", "🌟")
        List(40) {
            val emoji = if (it % 4 == 0) emojis.random() else null
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = 0.6f + Random.nextFloat() * 0.3f,
                targetX = Random.nextFloat(),
                targetY = 0.1f + Random.nextFloat() * 0.4f,
                color = colors.random(),
                size = 8f + Random.nextFloat() * 14f,
                emoji = emoji
            )
        }
    }

    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
        delay(200)
        onDismiss()
    }

    val currentProgress = progress.value
    val alphaVal = if (currentProgress < 0.8f) 1f else (1f - (currentProgress - 0.8f) / 0.2f).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                if (p.emoji == null) {
                    val currX = (p.initialX + (p.targetX - p.initialX) * currentProgress) * widthPx
                    val currY = (p.initialY - (p.initialY - p.targetY) * currentProgress) * heightPx
                    drawCircle(
                        color = p.color.copy(alpha = alphaVal),
                        radius = p.size * (1f - currentProgress * 0.3f),
                        center = androidx.compose.ui.geometry.Offset(currX, currY)
                    )
                }
            }
        }

        particles.filter { it.emoji != null }.forEach { p ->
            val currX = (p.initialX + (p.targetX - p.initialX) * currentProgress) * widthPx
            val currY = (p.initialY - (p.initialY - p.targetY) * currentProgress) * heightPx
            Text(
                text = p.emoji ?: "",
                fontSize = 24.sp,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = currX
                        translationY = currY
                        alpha = alphaVal
                    }
            )
        }
    }
}
