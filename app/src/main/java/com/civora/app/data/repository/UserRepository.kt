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

class UserRepository {
    private val _userState = MutableStateFlow(CivoraMockDataSource.currentUser)
    val userProfile: Flow<UserProfile> = _userState.asStateFlow()

    fun updateUserProfile(updated: UserProfile) {
        _userState.value = updated
        try {
            val db = FirebaseFirestore.getInstance()
            val docId = updated.id.ifEmpty { "usr_992140" }
            val map = mapOf(
                "fullNameEn" to updated.fullNameEn,
                "fullNameAr" to updated.fullNameAr,
                "nationalId" to updated.nationalId,
                "dateOfBirth" to updated.dateOfBirth,
                "nationality" to updated.nationality,
                "verificationLevel" to updated.verificationLevel.name,
                "digitalIdActive" to updated.digitalIdActive,
                "totalDocuments" to updated.totalDocuments,
                "activeRequestsCount" to updated.activeRequestsCount,
                "unreadNotificationsCount" to updated.unreadNotificationsCount
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
        listenToUserAndNotifications()
    }

    private fun listenToUserAndNotifications() {
        try {
            val db = FirebaseFirestore.getInstance()
            // Listen to User document
            db.collection("users").document("usr_992140")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val profile = FirestoreMappers.toUserProfile(snapshot)
                        if (profile != null) {
                            _userState.value = profile
                        }
                    }
                }

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
