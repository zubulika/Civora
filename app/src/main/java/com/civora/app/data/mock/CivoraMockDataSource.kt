package com.civora.app.data.mock

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

object CivoraMockDataSource {

    val currentUser = UserProfile(
        id = "usr_992140",
        nationalId = "2495685261",
        fullNameEn = "MD ABDUL HALIM MEIA",
        fullNameAr = "مد عبد ال حليم مياه",
        dateOfBirth = "1988/02/03",
        dateOfBirthAr = "١٩٨٨/٠٢/٠٣",
        dateOfBirthHijri = "1408/10/18",
        nationality = "Bangladesh",
        nationalityAr = "بنجلاديش",
        placeOfBirthEn = "Bangladesh",
        placeOfBirthAr = "بنجلاديش",
        religionEn = "Islam",
        religionAr = "الاسلام",
        professionEn = "Laundry Worker",
        professionAr = "عامل غسيل ملابس",
        sponsorId = "7034884309",
        sponsorNameEn = "Durrat Najah Laundry",
        sponsorName = "مؤسسة درر نجاح للملابس",
        issuePlaceEn = "Elm Information Security",
        issuePlace = "شركة العلم لامن المعلومات",
        workPlaceAr = "منطقة الرياض",
        expiryDateEn = "2026/10/08",
        expiryDateAr = "٢٠٢٦/١٠/٠٨",
        versionNumber = "٢",
        expiryDateDigits = "081026",
        issueDateDigits = "070926",
        verificationLevel = VerificationLevel.TIER_3_VERIFIED,
        digitalIdActive = true,
        totalDocuments = 4,
        activeRequestsCount = 2,
        unreadNotificationsCount = 3,
        birthCity = "-",
        maritalStatus = "SINGLE",
        sponsorshipTransfers = "2",
        workPermit = "-",
        biometricsCollected = "Yes",
        travelStatus = "Inside Kingdom",
        establishmentStatus = "Active (Green)",
        insuranceCompany = "Bupa Arabia",
        insurancePolicyNo = "POL-9842144",
        insuranceStatus = "Valid & Active",
        insuranceExpiry = "-",
        insuranceIssuingDate = "-",
        hajjEligibility = "Eligible for Hajj",
        lastHajjYear = "-"
    )

    val documents = listOf(
        DigitalDocument(
            id = "doc_nat_id",
            type = DocumentType.NATIONAL_ID,
            title = "Digital National ID",
            subtitle = "Ministry of Interior - Civil Affairs",
            documentNumber = "1098442190",
            issueDate = "1441/06/10 H",
            expiryDate = "1451/06/10 H",
            status = DocumentStatus.ACTIVE,
            issuer = "General Directorate of Civil Affairs",
            details = mapOf(
                "Full Name" to "Tariq Abdulaziz Al-Mansoor",
                "Arabic Name" to "طارق عبدالعزيز المنصور",
                "Place of Birth" to "Riyadh",
                "Blood Type" to "O+"
            ),
            qrCodePayload = "CIVORA:NATID:1098442190:TIER3:VALID"
        ),
        DigitalDocument(
            id = "doc_driving_lic",
            type = DocumentType.DRIVING_LICENSE,
            title = "Private Driving License",
            subtitle = "General Directorate of Traffic",
            documentNumber = "DL-4819002-K",
            issueDate = "1443/02/15 H",
            expiryDate = "1448/02/15 H",
            status = DocumentStatus.ACTIVE,
            issuer = "Ministry of Interior - Traffic Dept",
            details = mapOf(
                "License Class" to "Private Vehicle (Automatic & Manual)",
                "Restrictions" to "Corrective Lenses Required",
                "Endorsement" to "Safe Driver Score 96/100"
            ),
            qrCodePayload = "CIVORA:DL:4819002:VALID:CLASS_PRIVATE"
        ),
        DigitalDocument(
            id = "doc_veh_reg",
            type = DocumentType.VEHICLE_REGISTRATION,
            title = "Vehicle Registration (Istimara)",
            subtitle = "Toyota Land Cruiser 2023 - [7742 KSA]",
            documentNumber = "VR-9920194",
            issueDate = "1444/08/01 H",
            expiryDate = "1447/08/01 H",
            status = DocumentStatus.EXPIRING_SOON,
            issuer = "Traffic Department - Vehicle Licensing",
            details = mapOf(
                "Plate Number" to "7742 KSA",
                "VIN" to "JTMHY7AJ9P4019283",
                "Color" to "Pearl White",
                "Insurance Status" to "Comprehensive - Valid"
            ),
            qrCodePayload = "CIVORA:ISTIMARA:7742KSA:EXPIRING_SOON"
        ),
        DigitalDocument(
            id = "doc_passport",
            type = DocumentType.PASSPORT,
            title = "Citizen E-Passport",
            subtitle = "General Directorate of Passports (Jawazat)",
            documentNumber = "P0841920",
            issueDate = "1442/11/04 H",
            expiryDate = "1452/11/04 H",
            status = DocumentStatus.ACTIVE,
            issuer = "General Directorate of Passports",
            details = mapOf(
                "Biometric Chip" to "Encoded & Signed (ICAO 9303)",
                "Pages" to "48 Pages",
                "Authorized Border Gates" to "All International Terminals"
            ),
            qrCodePayload = "CIVORA:PASSPORT:P0841920:BIOMETRIC_VERIFIED"
        )
    )

    val services = listOf(
        GovernmentService(
            id = "srv_renew_id",
            title = "Renew National ID",
            description = "Submit digital request to renew national identification with updated photo and biometric verification.",
            category = ServiceCategory.CIVIL_AFFAIRS,
            processingTime = "2-3 business days",
            fee = "Free",
            isPopular = true,
            isQuickAction = true,
            requiredDocuments = listOf("Digital portrait photo", "Current National ID")
        ),
        GovernmentService(
            id = "srv_renew_license",
            title = "Renew Driving License",
            description = "Renew your driver license online with linked digital medical checkup reports.",
            category = ServiceCategory.TRAFFIC_VEHICLES,
            processingTime = "Instant (Digital) / 2 days (Card delivery)",
            fee = "SAR 200 (5 Years)",
            isPopular = true,
            isQuickAction = true,
            requiredDocuments = listOf("Valid medical examination", "Cleared traffic violations")
        ),
        GovernmentService(
            id = "srv_vehicle_transfer",
            title = "Vehicle Ownership Transfer",
            description = "Direct private transfer of vehicle registration between citizens with escrow buyer protection.",
            category = ServiceCategory.TRAFFIC_VEHICLES,
            processingTime = "Instant upon payment confirmation",
            fee = "SAR 230 fee + tax",
            isPopular = true,
            isQuickAction = false,
            requiredDocuments = listOf("Valid Istimara", "Valid periodic vehicle inspection (MVPI)")
        ),
        GovernmentService(
            id = "srv_issue_passport",
            title = "Renew Citizen Passport",
            description = "Fast digital issuance or renewal of national biometric passport with home delivery.",
            category = ServiceCategory.PASSPORTS_TRAVEL,
            processingTime = "3 business days",
            fee = "SAR 300 (5 Years) / SAR 600 (10 Years)",
            isPopular = true,
            isQuickAction = true,
            requiredDocuments = listOf("National ID", "Digital photo with white background")
        ),
        GovernmentService(
            id = "srv_family_registry",
            title = "Digital Family Record Issuance",
            description = "Issue and view authenticated family book certifying dependents and marital records.",
            category = ServiceCategory.CIVIL_AFFAIRS,
            processingTime = "Instant Digital",
            fee = "Free",
            isPopular = false,
            isQuickAction = false,
            requiredDocuments = listOf("Civil registry record")
        ),
        GovernmentService(
            id = "srv_traffic_violations",
            title = "Traffic Violations & Objection",
            description = "Inquire into automated photo radar violations and file official dispute statements.",
            category = ServiceCategory.TRAFFIC_VEHICLES,
            processingTime = "Instant inquiry / 7 days objection review",
            fee = "Free inquiry",
            isPopular = true,
            isQuickAction = true,
            requiredDocuments = listOf("National ID number")
        ),
        GovernmentService(
            id = "srv_travel_permit",
            title = "Issue Travel Authorizations",
            description = "Manage dependents travel clearances and official international departure authorizations.",
            category = ServiceCategory.PASSPORTS_TRAVEL,
            processingTime = "Instant Digital",
            fee = "Free",
            isPopular = false,
            isQuickAction = false,
            requiredDocuments = listOf("Dependent ID")
        ),
        GovernmentService(
            id = "srv_security_clearance",
            title = "Police Clearance Certificate",
            description = "Official criminal history certification for employment, embassy, or commercial licensure.",
            category = ServiceCategory.PERMITS_SECURITY,
            processingTime = "24 hours",
            fee = "Free",
            isPopular = false,
            isQuickAction = false,
            requiredDocuments = listOf("Active biometrics on record")
        )
    )

    val activeRequests = listOf(
        ServiceRequest(
            id = "req_101",
            referenceNumber = "CIV-2026-99120",
            serviceTitle = "Renew Citizen Passport (10 Years)",
            category = ServiceCategory.PASSPORTS_TRAVEL,
            status = RequestStatus.PROCESSING,
            submissionDate = "2026-09-02",
            expectedCompletion = "2026-09-08",
            currentStepIndex = 2,
            timeline = listOf(
                RequestTimelineStep("Application Submitted & Authenticated", "Sep 02, 10:15 AM", true),
                RequestTimelineStep("Security & Document Verification", "Sep 03, 02:40 PM", true),
                RequestTimelineStep("Passport Printing & Chip Encoding", "In Progress", false),
                RequestTimelineStep("Handover to National Postal Express", "Pending", false)
            )
        ),
        ServiceRequest(
            id = "req_102",
            referenceNumber = "CIV-2026-88412",
            serviceTitle = "Renew Vehicle Istimara (7742 KSA)",
            category = ServiceCategory.TRAFFIC_VEHICLES,
            status = RequestStatus.ACTION_REQUIRED,
            submissionDate = "2026-09-04",
            expectedCompletion = "2026-09-06",
            currentStepIndex = 1,
            timeline = listOf(
                RequestTimelineStep("Renewal Draft Initiated", "Sep 04, 09:12 AM", true),
                RequestTimelineStep("Periodic Vehicle Inspection (MVPI) Check", "Action Required: Expired MVPI", false),
                RequestTimelineStep("Digital License Issuance", "Pending", false)
            )
        ),
        ServiceRequest(
            id = "req_100",
            referenceNumber = "CIV-2026-77310",
            serviceTitle = "Digital National ID Biometric Sync",
            category = ServiceCategory.CIVIL_AFFAIRS,
            status = RequestStatus.COMPLETED,
            submissionDate = "2026-08-20",
            expectedCompletion = "2026-08-21",
            currentStepIndex = 3,
            timeline = listOf(
                RequestTimelineStep("Biometric Sync Scheduled", "Aug 20, 11:00 AM", true),
                RequestTimelineStep("Live Verification Confirmed", "Aug 20, 11:30 AM", true),
                RequestTimelineStep("Digital Wallet Credentials Updated", "Aug 20, 11:45 AM", true)
            )
        )
    )

    val notifications = listOf(
        NotificationItem(
            id = "notif_01",
            title = "Action Required: Vehicle Inspection Expired",
            message = "Your vehicle (7742 KSA) requires a fresh MVPI test to complete your registration renewal.",
            timestamp = "2 hours ago",
            priority = NotificationPriority.WARNING,
            isRead = false,
            actionDeepLink = "requests/req_102"
        ),
        NotificationItem(
            id = "notif_02",
            title = "Passport Status Update",
            message = "Your 10-year e-Passport printing process has begun at the central issuance facility.",
            timestamp = "1 day ago",
            priority = NotificationPriority.INFO,
            isRead = false,
            actionDeepLink = "requests/req_101"
        ),
        NotificationItem(
            id = "notif_03",
            title = "Security Alert: New Sign-in Verified",
            message = "A recognized sign-in was confirmed on Pixel 9 Pro XL via Biometric Passkey.",
            timestamp = "3 days ago",
            priority = NotificationPriority.INFO,
            isRead = true
        )
    )
}
