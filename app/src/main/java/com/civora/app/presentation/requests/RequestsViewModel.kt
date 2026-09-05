package com.civora.app.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.core.model.ServiceRequest
import com.civora.app.data.repository.RequestRepository
import com.civora.app.data.mock.CivoraMockDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RequestsViewModel(
    requestRepository: RequestRepository
) : ViewModel() {

    val requests: StateFlow<List<ServiceRequest>> = requestRepository.requests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CivoraMockDataSource.activeRequests
        )

    companion object {
        fun provideFactory(repository: RequestRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RequestsViewModel(repository) as T
                }
            }
    }
}
