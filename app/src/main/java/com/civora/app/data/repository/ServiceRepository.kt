package com.civora.app.data.repository

import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceCategory
import com.civora.app.data.firebase.FirestoreMappers
import com.civora.app.data.mock.CivoraMockDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ServiceRepository {
    private val _services = MutableStateFlow(CivoraMockDataSource.services)
    val services: Flow<List<GovernmentService>> = _services.asStateFlow()

    init {
        listenToServices()
    }

    private fun listenToServices() {
        try {
            FirebaseFirestore.getInstance().collection("services")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        val firestoreServices = snapshot.documents.mapNotNull {
                            FirestoreMappers.toGovernmentService(it)
                        }
                        if (firestoreServices.isNotEmpty()) {
                            _services.value = firestoreServices
                        }
                    }
                }
        } catch (_: Exception) {
            // Graceful fallback to initial data
        }
    }

    fun getQuickActions(): Flow<List<GovernmentService>> {
        return _services.map { list -> list.filter { it.isQuickAction } }
    }

    fun getServicesByCategory(category: ServiceCategory): Flow<List<GovernmentService>> {
        return _services.map { list -> list.filter { it.category == category } }
    }

    fun getServiceById(id: String): Flow<GovernmentService?> {
        return _services.map { list -> list.find { it.id == id } }
    }
}
