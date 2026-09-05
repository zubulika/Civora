package com.civora.app.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CivoraGreenLight,
    onPrimary = CivoraOnGreenContainer,
    primaryContainer = CivoraGreenDark,
    onPrimaryContainer = CivoraGreenContainer,
    secondary = CivoraGoldLight,
    onSecondary = CivoraOnSurfaceDark,
    background = CivoraBackgroundDark,
    onBackground = CivoraOnSurfaceDark,
    surface = CivoraSurfaceDark,
    onSurface = CivoraOnSurfaceDark,
    surfaceVariant = CivoraSurfaceVariantDark,
    onSurfaceVariant = CivoraOnSurfaceSubtleDark,
    outline = CivoraBorderDark,
    error = CivoraError,
    onError = CivoraOnSurfaceDark,
    errorContainer = CivoraErrorContainer,
    onErrorContainer = CivoraError
)

private val LightColorScheme = lightColorScheme(
    primary = CivoraGreenPrimary,
    onPrimary = CivoraSurfaceLight,
    primaryContainer = CivoraGreenContainer,
    onPrimaryContainer = CivoraOnGreenContainer,
    secondary = CivoraGold,
    onSecondary = CivoraSurfaceLight,
    background = CivoraBackgroundLight,
    onBackground = CivoraOnSurfaceLight,
    surface = CivoraSurfaceLight,
    onSurface = CivoraOnSurfaceLight,
    surfaceVariant = CivoraSurfaceVariantLight,
    onSurfaceVariant = CivoraOnSurfaceSubtleLight,
    outline = CivoraBorderLight,
    error = CivoraError,
    onError = CivoraSurfaceLight,
    errorContainer = CivoraErrorContainer,
    onErrorContainer = CivoraError
)

@Composable
fun CivoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = (if (darkTheme) CivoraBackgroundDark else CivoraGreenDark).toArgb()
            window.navigationBarColor = (if (darkTheme) CivoraSurfaceDark else CivoraSurfaceLight).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CivoraTypography,
        shapes = CivoraShapes,
        content = content
    )
}
