package com.civora.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode
import kotlinx.coroutines.delay

@Composable
fun AbsherLoadingScreen(
    onLoadingFinished: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    LaunchedEffect(Unit) {
        delay(1800) // Simulated loading
        onLoadingFinished()
    }

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val textColor = if (isDark) Color(0xFFE2ECE7) else Color(0xFF333333)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AbsherAnimatedLoadingLogo(
                modifier = Modifier.size(80.dp),
                color = AbsherGreenHeader
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Loading Information",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp
            )
        }
    }
}
