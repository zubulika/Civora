package com.civora.app.domain.usecase

import com.civora.app.core.model.DigitalDocument
import com.civora.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class GetUserDocumentsUseCase(
    private val documentRepository: DocumentRepository
) {
    operator fun invoke(): Flow<List<DigitalDocument>> {
        return documentRepository.documents
    }

    fun getById(id: String): Flow<DigitalDocument?> {
        return documentRepository.getDocumentById(id)
    }
}
