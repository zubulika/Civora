package com.civora.app.core.model

data class UserProfile(
    val id: String,
    val nationalId: String,
    val fullNameEn: String,
    val fullNameAr: String,
    val dateOfBirth: String,
    val nationality: String,
    val verificationLevel: VerificationLevel = VerificationLevel.TIER_3_VERIFIED,
    val digitalIdActive: Boolean = true,
    val totalDocuments: Int = 4,
    val activeRequestsCount: Int = 2,
    val unreadNotificationsCount: Int = 3
)

enum class VerificationLevel(val label: String) {
    BASIC("Level 1 Basic"),
    VERIFIED("Level 2 Verified"),
    TIER_3_VERIFIED("Tier 3 Fully Verified")
}
