package com.civora.app.domain.usecase

import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceRequest
import com.civora.app.data.repository.RequestRepository

class SubmitServiceRequestUseCase(
    private val requestRepository: RequestRepository
) {
    operator fun invoke(service: GovernmentService): ServiceRequest {
        return requestRepository.submitNewRequest(service)
    }
}
