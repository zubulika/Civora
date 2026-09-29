package com.civora.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.domain.usecase.DashboardData
import com.civora.app.domain.usecase.GetDashboardDataUseCase
import com.civora.app.data.mock.CivoraMockDataSource
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    getDashboardDataUseCase: GetDashboardDataUseCase,
    userRepository: UserRepository? = null
) : ViewModel() {

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
