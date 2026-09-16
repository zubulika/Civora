package com.civora.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.core.model.UserProfile
import com.civora.app.data.repository.UserRepository
import com.civora.app.data.mock.CivoraMockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CivoraMockDataSource.currentUser
        )

    fun updateProfile(updated: UserProfile) {
        userRepository.updateUserProfile(updated)
    }

    private val _biometricsEnabled = MutableStateFlow(true)
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled.asStateFlow()

    private val _smsAlertsEnabled = MutableStateFlow(true)
    val smsAlertsEnabled: StateFlow<Boolean> = _smsAlertsEnabled.asStateFlow()

    fun toggleBiometrics(enabled: Boolean) {
        _biometricsEnabled.value = enabled
    }

    fun toggleSmsAlerts(enabled: Boolean) {
        _smsAlertsEnabled.value = enabled
    }

    companion object {
        fun provideFactory(repository: UserRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(repository) as T
                }
            }
    }
}
