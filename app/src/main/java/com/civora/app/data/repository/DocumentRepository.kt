package com.civora.app.data.repository

import com.civora.app.core.model.DigitalDocument
import com.civora.app.data.firebase.FirestoreMappers
import com.civora.app.data.mock.CivoraMockDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class DocumentRepository {
    private val _documents = MutableStateFlow(CivoraMockDataSource.documents)
    val documents: Flow<List<DigitalDocument>> = _documents.asStateFlow()

    init {
        listenToDocuments()
    }

    private fun listenToDocuments() {
        try {
            FirebaseFirestore.getInstance().collection("documents")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        val firestoreDocs = snapshot.documents.mapNotNull {
                            FirestoreMappers.toDigitalDocument(it)
                        }
                        if (firestoreDocs.isNotEmpty()) {
                            _documents.value = firestoreDocs
                        }
                    }
                }
        } catch (_: Exception) {
            // Graceful fallback to initial documents
        }
    }

    fun getDocumentById(id: String): Flow<DigitalDocument?> {
        return _documents.map { list -> list.find { it.id == id } }
    }
}
