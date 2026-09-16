package com.civora.app.core.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val docTitle: String) {
    ARABIC("ar", "Arabic", "العربية", "هوية مقيم"),
    ENGLISH("en", "English", "English", "Resident ID")
}

object LanguageState {
    var currentLanguage by mutableStateOf(AppLanguage.ARABIC)

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
    }
}
