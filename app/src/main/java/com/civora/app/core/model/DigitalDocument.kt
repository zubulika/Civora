package com.civora.app.core.model

enum class DocumentType {
    NATIONAL_ID,
    DRIVING_LICENSE,
    VEHICLE_REGISTRATION,
    PASSPORT
}

enum class DocumentStatus {
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED,
    UNDER_RENEWAL
}

data class DigitalDocument(
    val id: String,
    val type: DocumentType,
    val title: String,
    val subtitle: String,
    val documentNumber: String,
    val issueDate: String,
    val expiryDate: String,
    val status: DocumentStatus,
    val issuer: String,
    val details: Map<String, String> = emptyMap(),
    val qrCodePayload: String = ""
)
