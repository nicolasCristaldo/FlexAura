package com.flexaura.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.appMainBackground(
    isDark: Boolean = isSystemInDarkTheme()
): Modifier = this.drawWithCache {
    val baseBg = if (isDark) Color(0xFF090913) else Color(0xFFF8F9FA)
    val centerGlow = if (isDark) Color(0xFF800000).copy(alpha = 0.18f) else Color(0xFFFF6B6B).copy(alpha = 0.12f)
    val flameCore = if (isDark) Color(0xFFB22222).copy(alpha = 0.40f) else Color(0xFFFF4500).copy(alpha = 0.25f)
    val flameMid = if (isDark) Color(0xFF660000).copy(alpha = 0.20f) else Color(0xFFFF7F50).copy(alpha = 0.12f)

    onDrawBehind {
        drawRect(color = baseBg)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(centerGlow, Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.5f
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, flameMid, flameCore),
                startY = size.height * 0.65f,
                endY = size.height
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(flameCore, Color.Transparent),
                center = Offset(size.width * 0.5f, size.height),
                radius = size.width * 0.75f
            )
        )
    }
}