package com.civora.app.core.model

enum class RequestStatus(val label: String) {
    SUBMITTED("Submitted"),
    PROCESSING("In Processing"),
    ACTION_REQUIRED("Action Required"),
    READY_FOR_PICKUP("Ready / Digital Issued"),
    COMPLETED("Completed"),
    REJECTED("Rejected")
}

data class RequestTimelineStep(
    val title: String,
    val timestamp: String,
    val completed: Boolean
)

data class ServiceRequest(
    val id: String,
    val referenceNumber: String,
    val serviceTitle: String,
    val category: ServiceCategory,
    val status: RequestStatus,
    val submissionDate: String,
    val expectedCompletion: String,
    val currentStepIndex: Int,
    val timeline: List<RequestTimelineStep>
)
