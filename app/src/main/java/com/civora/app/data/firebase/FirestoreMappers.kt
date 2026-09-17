package com.civora.app.data.firebase

import com.civora.app.core.model.DigitalDocument
import com.civora.app.core.model.DocumentStatus
import com.civora.app.core.model.DocumentType
import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.NotificationItem
import com.civora.app.core.model.NotificationPriority
import com.civora.app.core.model.RequestStatus
import com.civora.app.core.model.RequestTimelineStep
import com.civora.app.core.model.ServiceCategory
import com.civora.app.core.model.ServiceRequest
import com.civora.app.core.model.UserProfile
import com.civora.app.core.model.VerificationLevel
import com.google.firebase.firestore.DocumentSnapshot

object FirestoreMappers {

    fun toUserProfile(doc: DocumentSnapshot): UserProfile? {
        return try {
            UserProfile(
                id = doc.getString("id") ?: doc.id,
                nationalId = doc.getString("nationalId") ?: "",
                appPassword = doc.getString("appPassword") ?: "Civora2026!",
                accountStatus = doc.getString("accountStatus") ?: "ACTIVE",
                fullNameEn = doc.getString("fullNameEn") ?: "",
                fullNameAr = doc.getString("fullNameAr") ?: "",
                dateOfBirth = doc.getString("dateOfBirth") ?: "1988/02/03",
                dateOfBirthAr = doc.getString("dateOfBirthAr") ?: "١٩٨٨/٠٢/٠٣",
                dateOfBirthHijri = doc.getString("dateOfBirthHijri") ?: "1408/10/18",
                nationality = doc.getString("nationality") ?: "Bangladesh",
                nationalityAr = doc.getString("nationalityAr") ?: "بنجلاديش",
                placeOfBirthEn = doc.getString("placeOfBirthEn") ?: "Bangladesh",
                placeOfBirthAr = doc.getString("placeOfBirthAr") ?: "بنجلاديش",
                religionEn = doc.getString("religionEn") ?: "Islam",
                religionAr = doc.getString("religionAr") ?: "الاسلام",
                professionEn = doc.getString("professionEn") ?: "Laundry Worker",
                professionAr = doc.getString("professionAr") ?: "عامل غسيل ملابس",
                sponsorId = doc.getString("sponsorId") ?: "7034884309",
                sponsorNameEn = doc.getString("sponsorNameEn") ?: "Durrat Najah Laundry",
                sponsorName = doc.getString("sponsorName") ?: "مؤسسة درر نجاح للملابس",
                issuePlaceEn = doc.getString("issuePlaceEn") ?: "Elm Information Security",
                issuePlace = doc.getString("issuePlace") ?: "شركة العلم لامن المعلومات",
                workPlaceAr = doc.getString("workPlaceAr") ?: "منطقة الرياض",
                expiryDateEn = doc.getString("expiryDateEn") ?: "2026/10/08",
                expiryDateAr = doc.getString("expiryDateAr") ?: "٢٠٢٦/١٠/٠٨",
                versionNumber = doc.getString("versionNumber") ?: "٢",
                expiryDateDigits = doc.getString("expiryDateDigits") ?: "081026",
                issueDateDigits = doc.getString("issueDateDigits") ?: "070926",
                verificationLevel = try {
                    VerificationLevel.valueOf(doc.getString("verificationLevel") ?: "TIER_3_VERIFIED")
                } catch (e: Exception) {
                    VerificationLevel.TIER_3_VERIFIED
                },
                digitalIdActive = doc.getBoolean("digitalIdActive") ?: true,
                totalDocuments = doc.getLong("totalDocuments")?.toInt() ?: 4,
                activeRequestsCount = doc.getLong("activeRequestsCount")?.toInt() ?: 0,
                unreadNotificationsCount = doc.getLong("unreadNotificationsCount")?.toInt() ?: 0,
                birthCity = doc.getString("birthCity") ?: "-",
                maritalStatus = doc.getString("maritalStatus") ?: "SINGLE",
                sponsorshipTransfers = doc.getString("sponsorshipTransfers") ?: "2",
                workPermit = doc.getString("workPermit") ?: "-",
                biometricsCollected = doc.getString("biometricsCollected") ?: "Yes",
                travelStatus = doc.getString("travelStatus") ?: "Inside Kingdom",
                establishmentStatus = doc.getString("establishmentStatus") ?: "Active (Green)",
                insuranceCompany = doc.getString("insuranceCompany") ?: "Bupa Arabia",
                insurancePolicyNo = doc.getString("insurancePolicyNo") ?: "POL-9842144",
                insuranceStatus = doc.getString("insuranceStatus") ?: "Valid & Active",
                insuranceExpiry = doc.getString("insuranceExpiry") ?: "14/04/2026",
                hajjEligibility = doc.getString("hajjEligibility") ?: "Not Eligible / Not Performed",
                lastHajjYear = doc.getString("lastHajjYear") ?: "-"
            )
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun toDigitalDocument(doc: DocumentSnapshot): DigitalDocument? {
        return try {
            val typeStr = doc.getString("type") ?: "NATIONAL_ID"
            val statusStr = doc.getString("status") ?: "ACTIVE"
            DigitalDocument(
                id = doc.getString("id") ?: doc.id,
                type = try { DocumentType.valueOf(typeStr) } catch (e: Exception) { DocumentType.NATIONAL_ID },
                title = doc.getString("title") ?: "",
                subtitle = doc.getString("subtitle") ?: "",
                documentNumber = doc.getString("documentNumber") ?: "",
                issueDate = doc.getString("issueDate") ?: "",
                expiryDate = doc.getString("expiryDate") ?: "",
                status = try { DocumentStatus.valueOf(statusStr) } catch (e: Exception) { DocumentStatus.ACTIVE },
                issuer = doc.getString("issuer") ?: "",
                details = (doc.get("details") as? Map<String, String>) ?: emptyMap(),
                qrCodePayload = doc.getString("qrCodePayload") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun toGovernmentService(doc: DocumentSnapshot): GovernmentService? {
        return try {
            val catStr = doc.getString("category") ?: "GENERAL_GOVERNMENT"
            GovernmentService(
                id = doc.getString("id") ?: doc.id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                category = try { ServiceCategory.valueOf(catStr) } catch (e: Exception) { ServiceCategory.CIVIL_AFFAIRS },
                processingTime = doc.getString("processingTime") ?: "",
                fee = doc.getString("fee") ?: "",
                isPopular = doc.getBoolean("isPopular") ?: false,
                isQuickAction = doc.getBoolean("isQuickAction") ?: false,
                requiredDocuments = (doc.get("requiredDocuments") as? List<String>) ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun toServiceRequest(doc: DocumentSnapshot): ServiceRequest? {
        return try {
            val catStr = doc.getString("category") ?: "GENERAL_GOVERNMENT"
            val statusStr = doc.getString("status") ?: "SUBMITTED"
            val rawTimeline = (doc.get("timeline") as? List<Map<String, Any>>) ?: emptyList()
            val timeline = rawTimeline.map {
                RequestTimelineStep(
                    title = it["title"] as? String ?: "",
                    timestamp = it["timestamp"] as? String ?: "",
                    completed = it["completed"] as? Boolean ?: false
                )
            }

            ServiceRequest(
                id = doc.getString("id") ?: doc.id,
                referenceNumber = doc.getString("referenceNumber") ?: "",
                serviceTitle = doc.getString("serviceTitle") ?: "",
                category = try { ServiceCategory.valueOf(catStr) } catch (e: Exception) { ServiceCategory.CIVIL_AFFAIRS },
                status = try { RequestStatus.valueOf(statusStr) } catch (e: Exception) { RequestStatus.SUBMITTED },
                submissionDate = doc.getString("submissionDate") ?: "",
                expectedCompletion = doc.getString("expectedCompletion") ?: "",
                currentStepIndex = doc.getLong("currentStepIndex")?.toInt() ?: 0,
                timeline = timeline
            )
        } catch (e: Exception) {
            null
        }
    }

    fun toNotificationItem(doc: DocumentSnapshot): NotificationItem? {
        return try {
            val priorityStr = doc.getString("priority") ?: "INFO"
            NotificationItem(
                id = doc.getString("id") ?: doc.id,
                title = doc.getString("title") ?: "",
                message = doc.getString("message") ?: "",
                timestamp = doc.getString("timestamp") ?: "",
                priority = try { NotificationPriority.valueOf(priorityStr) } catch (e: Exception) { NotificationPriority.INFO },
                isRead = doc.getBoolean("isRead") ?: false,
                actionDeepLink = doc.getString("actionDeepLink")
            )
        } catch (e: Exception) {
            null
        }
    }
}
