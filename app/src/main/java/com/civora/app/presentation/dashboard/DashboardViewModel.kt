package com.civora.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.domain.usecase.DashboardData
import com.civora.app.domain.usecase.GetDashboardDataUseCase
import com.civora.app.data.mock.CivoraMockDataSource
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    getDashboardDataUseCase: GetDashboardDataUseCase,
    private val userRepository: UserRepository? = null
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<DashboardData> = getDashboardDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DashboardData(
                user = userRepository?.currentUserProfile ?: CivoraMockDataSource.currentUser,
                primaryDocument = CivoraMockDataSource.documents.firstOrNull(),
                quickActions = CivoraMockDataSource.services.filter { it.isQuickAction },
                activeRequests = CivoraMockDataSource.activeRequests.take(3)
            )
        )

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                userRepository?.refresh()
                delay(600)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    companion object {
        fun provideFactory(
            useCase: GetDashboardDataUseCase,
            userRepository: UserRepository? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(useCase, userRepository) as T
                }
            }
    }
}
