package com.civora.app.data.repository

import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.RequestStatus
import com.civora.app.core.model.RequestTimelineStep
import com.civora.app.core.model.ServiceRequest
import com.civora.app.data.firebase.FirestoreMappers
import com.civora.app.data.mock.CivoraMockDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class RequestRepository {
    private val _requests = MutableStateFlow(CivoraMockDataSource.activeRequests)
    val requests: Flow<List<ServiceRequest>> = _requests.asStateFlow()

    init {
        listenToRequests()
    }

    private fun listenToRequests() {
        try {
            FirebaseFirestore.getInstance().collection("requests")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        val firestoreRequests = snapshot.documents.mapNotNull {
                            FirestoreMappers.toServiceRequest(it)
                        }
                        if (firestoreRequests.isNotEmpty()) {
                            _requests.value = firestoreRequests
                        }
                    }
                }
        } catch (_: Exception) {
            // Graceful fallback to initial requests
        }
    }

    fun getRequestById(id: String): Flow<ServiceRequest?> {
        return _requests.map { list -> list.find { it.id == id } }
    }

    fun submitNewRequest(service: GovernmentService): ServiceRequest {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())
        val refNumber = "CIV-2026-" + Random.nextInt(10000, 99999)

        val newRequest = ServiceRequest(
            id = "req_${System.currentTimeMillis()}",
            referenceNumber = refNumber,
            serviceTitle = service.title,
            category = service.category,
            status = RequestStatus.SUBMITTED,
            submissionDate = todayStr,
            expectedCompletion = "Est. 3-5 business days",
            currentStepIndex = 0,
            timeline = listOf(
                RequestTimelineStep("Digital Submission Received", "Just now", true),
                RequestTimelineStep("Department Assessment", "Pending", false),
                RequestTimelineStep("Final Verification & Digital Issue", "Pending", false)
            )
        )
        _requests.value = listOf(newRequest) + _requests.value

        // Sync to Cloud Firestore
        try {
            val firestoreData = mapOf(
                "id" to newRequest.id,
                "referenceNumber" to newRequest.referenceNumber,
                "serviceTitle" to newRequest.serviceTitle,
                "category" to newRequest.category.name,
                "status" to newRequest.status.name,
                "submissionDate" to newRequest.submissionDate,
                "expectedCompletion" to newRequest.expectedCompletion,
                "currentStepIndex" to newRequest.currentStepIndex,
                "timeline" to newRequest.timeline.map {
                    mapOf(
                        "title" to it.title,
                        "timestamp" to it.timestamp,
                        "completed" to it.completed
                    )
                }
            )
            FirebaseFirestore.getInstance().collection("requests")
                .document(newRequest.id)
                .set(firestoreData)
        } catch (_: Exception) {
            // Persisted in memory
        }

        return newRequest
    }
}
