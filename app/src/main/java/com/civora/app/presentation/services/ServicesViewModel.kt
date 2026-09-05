package com.civora.app.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceCategory
import com.civora.app.core.model.ServiceRequest
import com.civora.app.domain.usecase.GetServicesUseCase
import com.civora.app.domain.usecase.SubmitServiceRequestUseCase
import com.civora.app.data.mock.CivoraMockDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ServicesViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val submitServiceRequestUseCase: SubmitServiceRequestUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _submissionSuccess = MutableStateFlow<ServiceRequest?>(null)
    val submissionSuccess: StateFlow<ServiceRequest?> = _submissionSuccess.asStateFlow()

    val filteredServices: StateFlow<List<GovernmentService>> =
        kotlinx.coroutines.flow.combine(_selectedCategory, _searchQuery) { cat, query ->
            Pair(cat, query)
        }.flatMapLatest { (cat, query) ->
            getServicesUseCase(cat, query)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CivoraMockDataSource.services
        )

    fun getServiceById(id: String): GovernmentService? {
        return filteredServices.value.find { it.id == id } 
            ?: CivoraMockDataSource.services.find { it.id == id }
    }

    fun onCategorySelect(category: ServiceCategory?) {
        _selectedCategory.value = category
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun submitRequest(service: GovernmentService) {
        val req = submitServiceRequestUseCase(service)
        _submissionSuccess.value = req
    }

    fun clearSubmissionState() {
        _submissionSuccess.value = null
    }

    companion object {
        fun provideFactory(
            getServicesUseCase: GetServicesUseCase,
            submitServiceRequestUseCase: SubmitServiceRequestUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ServicesViewModel(getServicesUseCase, submitServiceRequestUseCase) as T
                }
            }
    }
}
