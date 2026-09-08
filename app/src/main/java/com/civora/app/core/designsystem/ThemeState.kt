package com.civora.app.core.designsystem

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppThemeMode(val title: String, val subtitle: String) {
    LIGHT("Light Mode", "Crisp emerald header with clean white surfaces"),
    DARK("Dark Mode", "Sleek dark obsidian with emerald accents"),
    SYSTEM("System Default", "Follows device appearance settings")
}

object ThemeState {
    var currentThemeMode by mutableStateOf(AppThemeMode.DARK)

    fun setTheme(mode: AppThemeMode) {
        currentThemeMode = mode
    }
}

val LocalThemeMode = compositionLocalOf { AppThemeMode.DARK }
