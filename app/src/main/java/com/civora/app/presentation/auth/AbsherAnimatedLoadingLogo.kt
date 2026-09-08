package com.civora.app.presentation.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.civora.app.core.designsystem.AbsherGreenHeader

@Composable
fun AbsherAnimatedLoadingLogo(
    modifier: Modifier = Modifier.size(120.dp),
    color: Color = AbsherGreenHeader
) {
    val transition = rememberInfiniteTransition(label = "AbsherPulse")

    // Staggered pulse phases across the 6 bars and dots
    val phase1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val scaleFactor = size.width / 360f

        // Helper function to calculate pulse scale & alpha based on normalized time & offset delay
        fun getPulse(offsetSec: Float): Pair<Float, Float> {
            val progress = ((phase1 + (offsetSec / 1.25f)) % 1f)
            // Sine wave between 0.72 and 1.0
            val wave = kotlin.math.sin(progress * 2 * Math.PI.toFloat()) * 0.5f + 0.5f
            val scale = 0.72f + (0.28f * (1f - wave))
            val alpha = 0.65f + (0.35f * (1f - wave))
            return Pair(scale, alpha)
        }

        // 1. Bar 1 (x=64, y=138, w=30, h=218, rx=10, delay=0s)
        val (s1, a1) = getPulse(0.0f)
        val b1H = 218f * s1
        val b1Y = (138f + 218f) - b1H
        drawRoundRect(
            color = color.copy(alpha = a1),
            topLeft = Offset(64f * scaleFactor, b1Y * scaleFactor),
            size = Size(30f * scaleFactor, b1H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // 2. Bar 2 (x=103, y=138, w=30, h=170, rx=10, delay=0.1s)
        val (s2, a2) = getPulse(0.1f)
        val b2H = 170f * s2
        val b2Y = (138f + 170f) - b2H
        drawRoundRect(
            color = color.copy(alpha = a2),
            topLeft = Offset(103f * scaleFactor, b2Y * scaleFactor),
            size = Size(30f * scaleFactor, b2H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // 3. Bar 3 (x=142, y=138, w=30, h=170, rx=10, delay=0.2s)
        val (s3, a3) = getPulse(0.2f)
        val b3H = 170f * s3
        val b3Y = (138f + 170f) - b3H
        drawRoundRect(
            color = color.copy(alpha = a3),
            topLeft = Offset(142f * scaleFactor, b3Y * scaleFactor),
            size = Size(30f * scaleFactor, b3H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // 4. Bar 4 (x=181, y=138, w=30, h=170, rx=10, delay=0.3s)
        val (s4, a4) = getPulse(0.3f)
        val b4H = 170f * s4
        val b4Y = (138f + 170f) - b4H
        drawRoundRect(
            color = color.copy(alpha = a4),
            topLeft = Offset(181f * scaleFactor, b4Y * scaleFactor),
            size = Size(30f * scaleFactor, b4H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // 5. Bar 5 (x=220, y=89, w=30, h=219, rx=10, delay=0.4s)
        val (s5, a5) = getPulse(0.4f)
        val b5H = 219f * s5
        val b5Y = (89f + 219f) - b5H
        drawRoundRect(
            color = color.copy(alpha = a5),
            topLeft = Offset(220f * scaleFactor, b5Y * scaleFactor),
            size = Size(30f * scaleFactor, b5H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // 6. Bar 6 (x=259, y=78, w=30, h=242, rx=10, delay=0.5s)
        val (s6, a6) = getPulse(0.5f)
        val b6H = 242f * s6
        val b6Y = (78f + 242f) - b6H
        drawRoundRect(
            color = color.copy(alpha = a6),
            topLeft = Offset(259f * scaleFactor, b6Y * scaleFactor),
            size = Size(30f * scaleFactor, b6H * scaleFactor),
            cornerRadius = CornerRadius(10f * scaleFactor, 10f * scaleFactor)
        )

        // Bar 6 top flag/notch: M259 67C259 53 270 42 284 42H289V67Z
        val notchPath = Path().apply {
            moveTo(259f * scaleFactor, 67f * scaleFactor)
            cubicTo(
                259f * scaleFactor, 53f * scaleFactor,
                270f * scaleFactor, 42f * scaleFactor,
                284f * scaleFactor, 42f * scaleFactor
            )
            lineTo(289f * scaleFactor, 42f * scaleFactor)
            lineTo(289f * scaleFactor, 67f * scaleFactor)
            close()
        }
        drawPath(path = notchPath, color = color.copy(alpha = a6))

        // Dot 1 (cx=79, cy=113, r=13, delay=0.1s)
        val (sd1, ad1) = getPulse(0.1f)
        drawCircle(
            color = color.copy(alpha = ad1),
            radius = 13f * scaleFactor * sd1,
            center = Offset(79f * scaleFactor, 113f * scaleFactor)
        )

        // Dot 2 (cx=118, cy=113, r=13, delay=0.3s)
        val (sd2, ad2) = getPulse(0.3f)
        drawCircle(
            color = color.copy(alpha = ad2),
            radius = 13f * scaleFactor * sd2,
            center = Offset(118f * scaleFactor, 113f * scaleFactor)
        )

        // Dot 3 (cx=157, cy=113, r=13, delay=0.5s)
        val (sd3, ad3) = getPulse(0.5f)
        drawCircle(
            color = color.copy(alpha = ad3),
            radius = 13f * scaleFactor * sd3,
            center = Offset(157f * scaleFactor, 113f * scaleFactor)
        )

        // Dot 4 (cx=235, cy=332, r=13, delay=0.3s)
        val (sd4, ad4) = getPulse(0.3f)
        drawCircle(
            color = color.copy(alpha = ad4),
            radius = 13f * scaleFactor * sd4,
            center = Offset(235f * scaleFactor, 332f * scaleFactor)
        )
    }
}
