package com.civora.app.domain.usecase

import com.civora.app.core.model.DigitalDocument
import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceRequest
import com.civora.app.core.model.UserProfile
import com.civora.app.data.repository.DocumentRepository
import com.civora.app.data.repository.RequestRepository
import com.civora.app.data.repository.ServiceRepository
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DashboardData(
    val user: UserProfile,
    val primaryDocument: DigitalDocument?,
    val quickActions: List<GovernmentService>,
    val activeRequests: List<ServiceRequest>
)

class GetDashboardDataUseCase(
    private val userRepository: UserRepository,
    private val documentRepository: DocumentRepository,
    private val serviceRepository: ServiceRepository,
    private val requestRepository: RequestRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            userRepository.userProfile,
            documentRepository.documents,
            serviceRepository.getQuickActions(),
            requestRepository.requests
        ) { user, docs, quickActions, requests ->
            DashboardData(
                user = user,
                primaryDocument = docs.firstOrNull(),
                quickActions = quickActions,
                activeRequests = requests.take(3)
            )
        }
    }
}
