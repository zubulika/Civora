package com.civora.app.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.core.model.NotificationItem
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NotificationsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> = userRepository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun markAsRead(id: String) {
        userRepository.markNotificationAsRead(id)
    }

    fun markAllAsRead() {
        userRepository.markAllNotificationsAsRead()
    }

    companion object {
        fun provideFactory(repository: UserRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotificationsViewModel(repository) as T
                }
            }
    }
}
