package com.civora.app.data.repository

import com.civora.app.core.model.NotificationItem
import com.civora.app.core.model.UserProfile
import com.civora.app.data.firebase.FirestoreMappers
import com.civora.app.data.mock.CivoraMockDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val initialIdentifier: String? = null
) {
    private val _userState = MutableStateFlow(CivoraMockDataSource.currentUser)
    val userProfile: Flow<UserProfile> = _userState.asStateFlow()
    private var activeDocListener: com.google.firebase.firestore.ListenerRegistration? = null

    fun setCurrentUser(profile: UserProfile) {
        _userState.value = profile
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
                            listenToUserDocument(snap.id)
                        }
                    } else {
                        db.collection("users").whereEqualTo("nationalId", clean).limit(1).get()
                            .addOnSuccessListener { qSnap ->
                                if (qSnap != null && !qSnap.isEmpty) {
                                    val doc = qSnap.documents[0]
                                    FirestoreMappers.toUserProfile(doc)?.let {
                                        _userState.value = it
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
                        }
                    }
                }
        } catch (_: Exception) {}
    }

    fun updateUserProfile(updated: UserProfile) {
        _userState.value = updated
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
                "verificationLevel" to updated.verificationLevel.name,
                "digitalIdActive" to updated.digitalIdActive,
                "totalDocuments" to updated.totalDocuments,
                "activeRequestsCount" to updated.activeRequestsCount,
                "unreadNotificationsCount" to updated.unreadNotificationsCount,
                "photoUrl" to updated.photoUrl,
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
        } else {
            listenToUserAndNotifications()
        }
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
}
