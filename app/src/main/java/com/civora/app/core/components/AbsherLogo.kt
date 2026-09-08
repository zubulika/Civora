package com.civora.app.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.civora.app.R
import com.civora.app.core.designsystem.AbsherGreenHeader

/**
 * Native, crisp Vector representation of the official Absher Barcode Logo
 * extracted directly from the authentic SVG asset.
 */
@Composable
fun AbsherBarcodeLogo(
    modifier: Modifier = Modifier.size(36.dp),
    color: Color = AbsherGreenHeader
) {
    Canvas(modifier = modifier) {
        val scale = size.width / 360f

        // 1. Bar 1 (x=64, y=138, w=30, h=218, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(64f * scale, 138f * scale),
            size = Size(30f * scale, 218f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // 2. Bar 2 (x=103, y=138, w=30, h=170, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(103f * scale, 138f * scale),
            size = Size(30f * scale, 170f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // 3. Bar 3 (x=142, y=138, w=30, h=170, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(142f * scale, 138f * scale),
            size = Size(30f * scale, 170f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // 4. Bar 4 (x=181, y=138, w=30, h=170, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(181f * scale, 138f * scale),
            size = Size(30f * scale, 170f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // 5. Bar 5 (x=220, y=89, w=30, h=219, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(220f * scale, 89f * scale),
            size = Size(30f * scale, 219f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // 6. Bar 6 (x=259, y=78, w=30, h=242, rx=10)
        drawRoundRect(
            color = color,
            topLeft = Offset(259f * scale, 78f * scale),
            size = Size(30f * scale, 242f * scale),
            cornerRadius = CornerRadius(10f * scale, 10f * scale)
        )

        // Bar 6 Top Notch: M259 67C259 53 270 42 284 42H289V67Z
        val notchPath = Path().apply {
            moveTo(259f * scale, 67f * scale)
            cubicTo(
                259f * scale, 53f * scale,
                270f * scale, 42f * scale,
                284f * scale, 42f * scale
            )
            lineTo(289f * scale, 42f * scale)
            lineTo(289f * scale, 67f * scale)
            close()
        }
        drawPath(path = notchPath, color = color)

        // Dot 1 (cx=79, cy=113, r=13)
        drawCircle(
            color = color,
            radius = 13f * scale,
            center = Offset(79f * scale, 113f * scale)
        )

        // Dot 2 (cx=118, cy=113, r=13)
        drawCircle(
            color = color,
            radius = 13f * scale,
            center = Offset(118f * scale, 113f * scale)
        )

        // Dot 3 (cx=157, cy=113, r=13)
        drawCircle(
            color = color,
            radius = 13f * scale,
            center = Offset(157f * scale, 113f * scale)
        )

        // Dot 4 (cx=235, cy=332, r=13)
        drawCircle(
            color = color,
            radius = 13f * scale,
            center = Offset(235f * scale, 332f * scale)
        )
    }
}

/**
 * Absher Branding Header with the raw SVG barcode alongside the Saudi Ministry emblem
 */
@Composable
fun AbsherHeaderBranding(
    modifier: Modifier = Modifier,
    logoHeight: Dp = 38.dp,
    color: Color = AbsherGreenHeader,
    showEmblem: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AbsherBarcodeLogo(
            modifier = Modifier.size(logoHeight),
            color = color
        )

        if (showEmblem) {
            Icon(
                painter = painterResource(id = R.drawable.ic_saudi_ministry_interior),
                contentDescription = "Saudi Ministry of Interior Emblem",
                tint = color,
                modifier = Modifier
                    .size(logoHeight)
            )
        }
    }
}
