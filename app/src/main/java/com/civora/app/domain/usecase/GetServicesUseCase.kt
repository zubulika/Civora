package com.civora.app.domain.usecase

import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceCategory
import com.civora.app.data.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetServicesUseCase(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(
        selectedCategory: ServiceCategory? = null,
        searchQuery: String = ""
    ): Flow<List<GovernmentService>> {
        return serviceRepository.services.map { list ->
            list.filter { service ->
                val matchesCategory = selectedCategory == null || service.category == selectedCategory
                val matchesQuery = searchQuery.isBlank() ||
                        service.title.contains(searchQuery, ignoreCase = true) ||
                        service.description.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }
    }
}
