package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProblemSolverLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 40.dp
) {
    Box(modifier = modifier.size(sizeDp)) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val w = size.width
            val h = size.height

            // 1. Deep Indigo rounded-square background
            val corner = w * 0.22f
            drawRoundRect(
                color = Color(0xFF262B45),
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                cornerRadius = CornerRadius(corner, corner)
            )

            val amber = Color(0xFFFFB238)
            val strokeW = w * 0.055f

            // 2. Simple idea rays radiating from the top
            val centerX = w * 0.46f
            val centerY = h * 0.44f

            // Top vertical ray
            drawLine(
                color = amber,
                start = Offset(centerX, h * 0.12f),
                end = Offset(centerX, h * 0.20f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            // Top-left diagonal ray
            drawLine(
                color = amber,
                start = Offset(w * 0.22f, h * 0.18f),
                end = Offset(w * 0.29f, h * 0.25f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            // Top-right diagonal ray
            drawLine(
                color = amber,
                start = Offset(w * 0.70f, h * 0.18f),
                end = Offset(w * 0.63f, h * 0.25f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // 3. Glowing amber lightbulb
            val bulbRadius = w * 0.19f
            drawCircle(
                color = amber,
                radius = bulbRadius,
                center = Offset(centerX, centerY)
            )

            // Bulb base / neck
            val neckPath = Path().apply {
                moveTo(centerX - bulbRadius * 0.75f, centerY + bulbRadius * 0.4f)
                lineTo(centerX - bulbRadius * 0.5f, centerY + bulbRadius * 1.35f)
                lineTo(centerX + bulbRadius * 0.5f, centerY + bulbRadius * 1.35f)
                lineTo(centerX + bulbRadius * 0.75f, centerY + bulbRadius * 0.4f)
                close()
            }
            drawPath(neckPath, color = amber, style = Fill)

            // Bulb screw thread lines
            val threadColor = Color(0xFFE9932A)
            drawLine(
                color = threadColor,
                start = Offset(centerX - bulbRadius * 0.45f, centerY + bulbRadius * 1.45f),
                end = Offset(centerX + bulbRadius * 0.45f, centerY + bulbRadius * 1.45f),
                strokeWidth = strokeW * 0.9f,
                cap = StrokeCap.Round
            )

            // 4. Small teal circular "solved" checkmark badge overlapping bottom-right
            val badgeCenter = Offset(w * 0.76f, h * 0.76f)
            val badgeRadius = w * 0.18f
            drawCircle(
                color = Color(0xFF22C3A6),
                radius = badgeRadius,
                center = badgeCenter
            )

            // White checkmark inside the badge
            val checkPath = Path().apply {
                moveTo(badgeCenter.x - badgeRadius * 0.48f, badgeCenter.y)
                lineTo(badgeCenter.x - badgeRadius * 0.12f, badgeCenter.y + badgeRadius * 0.40f)
                lineTo(badgeCenter.x + badgeRadius * 0.52f, badgeCenter.y - badgeRadius * 0.35f)
            }
            drawPath(
                path = checkPath,
                color = Color.White,
                style = Stroke(width = strokeW * 1.1f, cap = StrokeCap.Round)
            )
        }
    }
}
