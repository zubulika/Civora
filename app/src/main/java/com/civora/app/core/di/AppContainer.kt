package com.civora.app.core.di

import com.civora.app.data.repository.DocumentRepository
import com.civora.app.data.repository.RequestRepository
import com.civora.app.data.repository.ServiceRepository
import com.civora.app.data.repository.UserRepository
import com.civora.app.domain.usecase.GetDashboardDataUseCase
import com.civora.app.domain.usecase.GetServicesUseCase
import com.civora.app.domain.usecase.GetUserDocumentsUseCase
import com.civora.app.domain.usecase.SubmitServiceRequestUseCase

class AppContainer {
    val userRepository = UserRepository()
    val documentRepository = DocumentRepository()
    val serviceRepository = ServiceRepository()
    val requestRepository = RequestRepository()

    val getDashboardDataUseCase = GetDashboardDataUseCase(
        userRepository,
        documentRepository,
        serviceRepository,
        requestRepository
    )

    val getServicesUseCase = GetServicesUseCase(serviceRepository)
    val getUserDocumentsUseCase = GetUserDocumentsUseCase(documentRepository)
    val submitServiceRequestUseCase = SubmitServiceRequestUseCase(requestRepository)
}
