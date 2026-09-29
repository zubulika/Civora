package com.civora.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.civora.app.core.model.NotificationItem
import com.civora.app.core.model.UserProfile
import com.civora.app.core.model.VerificationLevel
import com.civora.app.data.firebase.FirestoreMappers
import com.civora.app.data.mock.CivoraMockDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class UserRepository(
    private val context: Context? = null,
    private val initialIdentifier: String? = null
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "civora_user_cache"
        private const val KEY_CACHED_USER = "cached_user_profile_json"
    }

    private val _userState: MutableStateFlow<UserProfile> = run {
        val cached = loadCachedProfile()
        if (cached != null) {
            MutableStateFlow(cached)
        } else {
            MutableStateFlow(CivoraMockDataSource.currentUser)
        }
    }

    val userProfile: Flow<UserProfile> = _userState.asStateFlow()
    val currentUserProfile: UserProfile
        get() = _userState.value

    private var activeDocListener: com.google.firebase.firestore.ListenerRegistration? = null

    private fun saveCachedProfile(profile: UserProfile) {
        try {
            val json = serializeUserProfile(profile)
            prefs?.edit()?.putString(KEY_CACHED_USER, json)?.apply()
        } catch (_: Exception) {}
    }

    private fun loadCachedProfile(): UserProfile? {
        val jsonStr = prefs?.getString(KEY_CACHED_USER, null) ?: return null
        return deserializeUserProfile(jsonStr)
    }

    fun clearUser() {
        activeDocListener?.remove()
        activeDocListener = null
        prefs?.edit()?.remove(KEY_CACHED_USER)?.apply()
        _userState.value = CivoraMockDataSource.currentUser
    }

    fun setCurrentUser(profile: UserProfile) {
        _userState.value = profile
        saveCachedProfile(profile)
        listenToUserDocument(profile.id.ifEmpty { "usr_${profile.nationalId}" })
    }

    fun loadUserByIdentifier(identifier: String) {
        val clean = identifier.trim()
        if (clean.isBlank()) return

        try {
            val db = FirebaseFirestore.getInstance()
            val docId = if (clean.startsWith("usr_")) clean else "usr_$clean"
            db.collection("users").document(docId).get()
                .addOnSuccessListener { snap ->
                    if (snap != null && snap.exists()) {
                        FirestoreMappers.toUserProfile(snap)?.let {
                            _userState.value = it
                            saveCachedProfile(it)
                            listenToUserDocument(snap.id)
                        }
                    } else {
                        db.collection("users").whereEqualTo("nationalId", clean).limit(1).get()
                            .addOnSuccessListener { qSnap ->
                                if (qSnap != null && !qSnap.isEmpty) {
                                    val doc = qSnap.documents[0]
                                    FirestoreMappers.toUserProfile(doc)?.let {
                                        _userState.value = it
                                        saveCachedProfile(it)
                                        listenToUserDocument(doc.id)
                                    }
                                }
                            }
                    }
                }
        } catch (_: Exception) {
            // Graceful offline fallback
        }
    }

    private fun listenToUserDocument(docId: String) {
        try {
            activeDocListener?.remove()
            val db = FirebaseFirestore.getInstance()
            activeDocListener = db.collection("users").document(docId)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val profile = FirestoreMappers.toUserProfile(snapshot)
                        if (profile != null) {
                            _userState.value = profile
                            saveCachedProfile(profile)
                        }
                    }
                }
        } catch (_: Exception) {}
    }

    fun updateUserProfile(updated: UserProfile) {
        _userState.value = updated
        saveCachedProfile(updated)
        try {
            val db = FirebaseFirestore.getInstance()
            val docId = updated.id.ifEmpty { "usr_${updated.nationalId}" }
            val map = mapOf(
                "fullNameEn" to updated.fullNameEn,
                "fullNameAr" to updated.fullNameAr,
                "nationalId" to updated.nationalId,
                "appPassword" to updated.appPassword,
                "accountStatus" to updated.accountStatus,
                "dateOfBirth" to updated.dateOfBirth,
                "nationality" to updated.nationality,
                "birthCity" to updated.birthCity,
                "birthCountry" to updated.birthCountry,
                "maritalStatus" to updated.maritalStatus,
                "sponsorshipTransfers" to updated.sponsorshipTransfers,
                "religionEn" to updated.religionEn,
                "religionAr" to updated.religionAr,
                "workPermit" to updated.workPermit,
                "biometricsCollected" to updated.biometricsCollected,
                "travelStatus" to updated.travelStatus,
                "establishmentStatus" to updated.establishmentStatus,
                "sponsorId" to updated.sponsorId,
                "sponsorName" to updated.sponsorName,
                "sponsorNameEn" to updated.sponsorNameEn,
                "insuranceCompany" to updated.insuranceCompany,
                "insurancePolicyNo" to updated.insurancePolicyNo,
                "insuranceStatus" to updated.insuranceStatus,
                "insuranceExpiry" to updated.insuranceExpiry,
                "insuranceIssuingDate" to updated.insuranceIssuingDate,
                "hajjEligibility" to updated.hajjEligibility,
                "lastHajjYear" to updated.lastHajjYear,
                "residentIdIssuingDate" to updated.residentIdIssuingDate,
                "verificationLevel" to updated.verificationLevel.name,
                "digitalIdActive" to updated.digitalIdActive,
                "totalDocuments" to updated.totalDocuments,
                "activeRequestsCount" to updated.activeRequestsCount,
                "unreadNotificationsCount" to updated.unreadNotificationsCount,
                "photoUrl" to updated.photoUrl,
                "passportNumber" to updated.passportNumber,
                "passportType" to updated.passportType,
                "passportIssueDate" to updated.passportIssueDate,
                "passportExpiryDate" to updated.passportExpiryDate,
                "passportIssuingCity" to updated.passportIssuingCity,
                "passportStatus" to updated.passportStatus,
                "hasDrivingLicense" to updated.hasDrivingLicense,
                "licenseTypeEn" to updated.licenseTypeEn,
                "licenseTypeAr" to updated.licenseTypeAr,
                "licenseIssueDateEn" to updated.licenseIssueDateEn,
                "licenseIssueDateAr" to updated.licenseIssueDateAr,
                "licenseExpiryDateEn" to updated.licenseExpiryDateEn,
                "licenseExpiryDateAr" to updated.licenseExpiryDateAr,
                "bloodType" to updated.bloodType
            )
            db.collection("users").document(docId)
                .set(map, com.google.firebase.firestore.SetOptions.merge())
        } catch (_: Exception) {
            // Fallback in-memory
        }
    }

    private val _notifications = MutableStateFlow(CivoraMockDataSource.notifications)
    val notifications: Flow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        if (!initialIdentifier.isNullOrBlank()) {
            loadUserByIdentifier(initialIdentifier)
        }
        listenToUserAndNotifications()
    }

    private fun listenToUserAndNotifications() {
        try {
            val db = FirebaseFirestore.getInstance()

            // Listen to notifications collection
            db.collection("notifications")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        val notifs = snapshot.documents.mapNotNull {
                            FirestoreMappers.toNotificationItem(it)
                        }
                        if (notifs.isNotEmpty()) {
                            _notifications.value = notifs
                            val unread = notifs.count { !it.isRead }
                            _userState.value = _userState.value.copy(unreadNotificationsCount = unread)
                        }
                    }
                }
        } catch (_: Exception) {
            // Graceful fallback to initial mock data
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        val unread = _notifications.value.count { !it.isRead }
        _userState.value = _userState.value.copy(unreadNotificationsCount = unread)

        try {
            FirebaseFirestore.getInstance().collection("notifications")
                .document(id)
                .update("isRead", true)
        } catch (_: Exception) {
            // Fallback in-memory
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        _userState.value = _userState.value.copy(unreadNotificationsCount = 0)

        try {
            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()
            _notifications.value.forEach {
                val docRef = db.collection("notifications").document(it.id)
                batch.update(docRef, "isRead", true)
            }
            batch.commit()
        } catch (_: Exception) {
            // Fallback in-memory
        }
    }

    private fun serializeUserProfile(user: UserProfile): String {
        val json = JSONObject()
        json.put("id", user.id)
        json.put("nationalId", user.nationalId)
        json.put("appPassword", user.appPassword)
        json.put("accountStatus", user.accountStatus)
        json.put("fullNameEn", user.fullNameEn)
        json.put("fullNameAr", user.fullNameAr)
        json.put("dateOfBirth", user.dateOfBirth)
        json.put("dateOfBirthAr", user.dateOfBirthAr)
        json.put("dateOfBirthHijri", user.dateOfBirthHijri)
        json.put("nationality", user.nationality)
        json.put("nationalityAr", user.nationalityAr)
        json.put("placeOfBirthEn", user.placeOfBirthEn)
        json.put("placeOfBirthAr", user.placeOfBirthAr)
        json.put("religionEn", user.religionEn)
        json.put("religionAr", user.religionAr)
        json.put("professionEn", user.professionEn)
        json.put("professionAr", user.professionAr)
        json.put("sponsorId", user.sponsorId)
        json.put("sponsorNameEn", user.sponsorNameEn)
        json.put("sponsorName", user.sponsorName)
        json.put("issuePlaceEn", user.issuePlaceEn)
        json.put("issuePlace", user.issuePlace)
        json.put("workPlaceAr", user.workPlaceAr)
        json.put("expiryDateEn", user.expiryDateEn)
        json.put("expiryDateAr", user.expiryDateAr)
        json.put("versionNumber", user.versionNumber)
        json.put("expiryDateDigits", user.expiryDateDigits)
        json.put("issueDateDigits", user.issueDateDigits)
        json.put("verificationLevel", user.verificationLevel.name)
        json.put("digitalIdActive", user.digitalIdActive)
        json.put("totalDocuments", user.totalDocuments)
        json.put("activeRequestsCount", user.activeRequestsCount)
        json.put("unreadNotificationsCount", user.unreadNotificationsCount)
        json.put("birthCity", user.birthCity)
        json.put("birthCountry", user.birthCountry)
        json.put("maritalStatus", user.maritalStatus)
        json.put("sponsorshipTransfers", user.sponsorshipTransfers)
        json.put("workPermit", user.workPermit)
        json.put("biometricsCollected", user.biometricsCollected)
        json.put("travelStatus", user.travelStatus)
        json.put("establishmentStatus", user.establishmentStatus)
        json.put("insuranceCompany", user.insuranceCompany)
        json.put("insurancePolicyNo", user.insurancePolicyNo)
        json.put("insuranceStatus", user.insuranceStatus)
        json.put("insuranceExpiry", user.insuranceExpiry)
        json.put("insuranceIssuingDate", user.insuranceIssuingDate)
        json.put("hajjEligibility", user.hajjEligibility)
        json.put("lastHajjYear", user.lastHajjYear)
        json.put("photoUrl", user.photoUrl)
        json.put("residentIdIssuingDate", user.residentIdIssuingDate)
        json.put("visaNumber", user.visaNumber)
        json.put("visaType", user.visaType)
        json.put("visaExitDate", user.visaExitDate)
        json.put("passportNumber", user.passportNumber)
        json.put("passportType", user.passportType)
        json.put("passportIssueDate", user.passportIssueDate)
        json.put("passportExpiryDate", user.passportExpiryDate)
        json.put("passportIssuingCity", user.passportIssuingCity)
        json.put("passportStatus", user.passportStatus)
        json.put("hasDrivingLicense", user.hasDrivingLicense)
        json.put("licenseTypeEn", user.licenseTypeEn)
        json.put("licenseTypeAr", user.licenseTypeAr)
        json.put("licenseIssueDateEn", user.licenseIssueDateEn)
        json.put("licenseIssueDateAr", user.licenseIssueDateAr)
        json.put("licenseExpiryDateEn", user.licenseExpiryDateEn)
        json.put("licenseExpiryDateAr", user.licenseExpiryDateAr)
        json.put("bloodType", user.bloodType)
        return json.toString()
    }

    private fun deserializeUserProfile(jsonStr: String): UserProfile? {
        return try {
            val json = JSONObject(jsonStr)
            UserProfile(
                id = json.optString("id", ""),
                nationalId = json.optString("nationalId", ""),
                appPassword = json.optString("appPassword", "Civora2026!"),
                accountStatus = json.optString("accountStatus", "ACTIVE"),
                fullNameEn = json.optString("fullNameEn", ""),
                fullNameAr = json.optString("fullNameAr", ""),
                dateOfBirth = json.optString("dateOfBirth", "1988/02/03"),
                dateOfBirthAr = json.optString("dateOfBirthAr", "١٩٨٨/٠٢/٠٣"),
                dateOfBirthHijri = json.optString("dateOfBirthHijri", "1408/10/18"),
                nationality = json.optString("nationality", "Bangladesh"),
                nationalityAr = json.optString("nationalityAr", "بنجلاديش"),
                placeOfBirthEn = json.optString("placeOfBirthEn", "Bangladesh"),
                placeOfBirthAr = json.optString("placeOfBirthAr", "بنجلاديش"),
                religionEn = json.optString("religionEn", "Islam"),
                religionAr = json.optString("religionAr", "الاسلام"),
                professionEn = json.optString("professionEn", "Laundry Worker"),
                professionAr = json.optString("professionAr", "عامل غسيل ملابس"),
                sponsorId = json.optString("sponsorId", "7034884309"),
                sponsorNameEn = json.optString("sponsorNameEn", "Durrat Najah Laundry"),
                sponsorName = json.optString("sponsorName", "مؤسسة درر نجاح للملابس"),
                issuePlaceEn = json.optString("issuePlaceEn", "Elm Information Security"),
                issuePlace = json.optString("issuePlace", "شركة العلم لامن المعلومات"),
                workPlaceAr = json.optString("workPlaceAr", "منطقة الرياض"),
                expiryDateEn = json.optString("expiryDateEn", "2026/10/08"),
                expiryDateAr = json.optString("expiryDateAr", "٢٠٢٦/١٠/٠٨"),
                versionNumber = json.optString("versionNumber", "٢"),
                expiryDateDigits = json.optString("expiryDateDigits", "081026"),
                issueDateDigits = json.optString("issueDateDigits", "070926"),
                verificationLevel = try {
                    VerificationLevel.valueOf(json.optString("verificationLevel", "TIER_3_VERIFIED"))
                } catch (_: Exception) {
                    VerificationLevel.TIER_3_VERIFIED
                },
                digitalIdActive = json.optBoolean("digitalIdActive", true),
                totalDocuments = json.optInt("totalDocuments", 4),
                activeRequestsCount = json.optInt("activeRequestsCount", 0),
                unreadNotificationsCount = json.optInt("unreadNotificationsCount", 0),
                birthCity = json.optString("birthCity", "-"),
                birthCountry = json.optString("birthCountry", "Bangladesh"),
                maritalStatus = json.optString("maritalStatus", "SINGLE"),
                sponsorshipTransfers = json.optString("sponsorshipTransfers", "2"),
                workPermit = json.optString("workPermit", "-"),
                biometricsCollected = json.optString("biometricsCollected", "Yes"),
                travelStatus = json.optString("travelStatus", "Inside Kingdom"),
                establishmentStatus = json.optString("establishmentStatus", "Active (Green)"),
                insuranceCompany = json.optString("insuranceCompany", "Bupa Arabia"),
                insurancePolicyNo = json.optString("insurancePolicyNo", "POL-9842144"),
                insuranceStatus = json.optString("insuranceStatus", "Valid & Active"),
                insuranceExpiry = json.optString("insuranceExpiry", "-"),
                insuranceIssuingDate = json.optString("insuranceIssuingDate", "-"),
                hajjEligibility = json.optString("hajjEligibility", "Not Eligible / Not Performed"),
                lastHajjYear = json.optString("lastHajjYear", "-"),
                photoUrl = json.optString("photoUrl", ""),
                residentIdIssuingDate = json.optString("residentIdIssuingDate", "28/03/2021"),
                visaNumber = json.optString("visaNumber", ""),
                visaType = json.optString("visaType", ""),
                visaExitDate = json.optString("visaExitDate", ""),
                passportNumber = json.optString("passportNumber", "EM0962248"),
                passportType = json.optString("passportType", "Normal"),
                passportIssueDate = json.optString("passportIssueDate", "07/01/2025"),
                passportExpiryDate = json.optString("passportExpiryDate", "06/01/2030"),
                passportIssuingCity = json.optString("passportIssuingCity", "دكا"),
                passportStatus = json.optString("passportStatus", "Valid"),
                hasDrivingLicense = json.optBoolean("hasDrivingLicense", true),
                licenseTypeEn = json.optString("licenseTypeEn", "Private"),
                licenseTypeAr = json.optString("licenseTypeAr", "خصوصي"),
                licenseIssueDateEn = json.optString("licenseIssueDateEn", "10/03/2026"),
                licenseIssueDateAr = json.optString("licenseIssueDateAr", "٢٠٢٦/٠٣/١٠"),
                licenseExpiryDateEn = json.optString("licenseExpiryDateEn", "21/11/2035"),
                licenseExpiryDateAr = json.optString("licenseExpiryDateAr", "٢٠٣٥/١١/٢١"),
                bloodType = json.optString("bloodType", "A+")
            )
        } catch (_: Exception) {
            null
        }
    }
}
