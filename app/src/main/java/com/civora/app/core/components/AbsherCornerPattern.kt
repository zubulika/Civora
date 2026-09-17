package com.civora.app.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Authentic Absher Top-Left Guilloche Pattern.
 * Recreates the exact multi-column rounded rectangle (squircle) outline watermark
 * with flat top/bottom edges, ultra-slender thin lines, and subtle corner radii.
 */
@Composable
fun AbsherCornerPattern(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = if (isDark) Color(0xFF4A896F) else Color(0xFF4C8F73)

    // Pre-defined ultra-slender column definitions: (xOffset in dp, list of (yStart in dp, height in dp))
    // Thinner columns spaced cleanly to match the official Absher watermark
    val columns = remember {
        listOf(
            // Col 0
            Pair(-2f, listOf(Pair(-15f, 50f), Pair(43f, 58f))),
            // Col 1
            Pair(10f, listOf(Pair(-15f, 68f), Pair(61f, 72f))),
            // Col 2
            Pair(22f, listOf(Pair(-15f, 40f), Pair(33f, 55f), Pair(96f, 50f))),
            // Col 3
            Pair(34f, listOf(Pair(-15f, 75f), Pair(68f, 65f))),
            // Col 4
            Pair(46f, listOf(Pair(-15f, 46f), Pair(39f, 68f))),
            // Col 5
            Pair(58f, listOf(Pair(-15f, 72f), Pair(65f, 52f), Pair(125f, 25f))),
            // Col 6
            Pair(70f, listOf(Pair(-15f, 42f), Pair(35f, 62f))),
            // Col 7
            Pair(82f, listOf(Pair(-15f, 65f), Pair(58f, 48f))),
            // Col 8
            Pair(94f, listOf(Pair(-15f, 40f), Pair(33f, 64f))),
            // Col 9
            Pair(106f, listOf(Pair(-15f, 62f), Pair(55f, 35f))),
            // Col 10
            Pair(118f, listOf(Pair(-15f, 38f), Pair(31f, 40f))),
            // Col 11
            Pair(130f, listOf(Pair(-15f, 55f), Pair(48f, 28f))),
            // Col 12 - fades above the left shoulder of the logo
            Pair(142f, listOf(Pair(-15f, 40f)))
        )
    }

    Canvas(modifier = modifier) {
        val rectWidthPx = 7.5.dp.toPx()
        // 1.8dp corner radius keeps flat top & bottom with elegant slight rounded corner
        val cornerRadius = CornerRadius(1.8.dp.toPx(), 1.8.dp.toPx())
        val strokeWidthPx = 1.0.dp.toPx()
        val maxYPx = 155.dp.toPx()
        val maxXPx = 155.dp.toPx()

        for ((colXdp, segments) in columns) {
            val xPx = colXdp.dp.toPx()
            val xRatio = (xPx / maxXPx).coerceIn(0f, 1f)
            val hFade = (1f - (xRatio * 0.72f)).coerceIn(0.1f, 1f)

            for ((yStartDp, heightDp) in segments) {
                val yPx = yStartDp.dp.toPx()
                val heightPx = heightDp.dp.toPx()
                val segmentMidYPx = yPx + (heightPx * 0.5f)

                // Smooth linear vertical fade towards bottom of pattern area
                val vRatio = (segmentMidYPx / maxYPx).coerceIn(0f, 1f)
                val vFade = (1f - vRatio).coerceIn(0f, 1f)
                val combinedFade = (vFade * hFade)

                if (combinedFade <= 0.02f) continue

                val strokeAlpha = if (isDark) {
                    (0.35f * combinedFade).coerceIn(0f, 1f)
                } else {
                    (0.30f * combinedFade).coerceIn(0f, 1f)
                }

                val fillAlpha = if (isDark) {
                    (0.03f * combinedFade).coerceIn(0f, 1f)
                } else {
                    (0.015f * combinedFade).coerceIn(0f, 1f)
                }

                // 1. Very subtle frosted interior wash
                if (fillAlpha > 0.005f) {
                    drawRoundRect(
                        color = primaryColor.copy(alpha = fillAlpha),
                        topLeft = Offset(xPx, yPx),
                        size = Size(rectWidthPx, heightPx),
                        cornerRadius = cornerRadius,
                        style = Fill
                    )
                }

                // 2. Crisp, slender rounded-rectangle outline with flat top & bottom
                drawRoundRect(
                    color = primaryColor.copy(alpha = strokeAlpha),
                    topLeft = Offset(xPx, yPx),
                    size = Size(rectWidthPx, heightPx),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = strokeWidthPx)
                )
            }
        }
    }
}




