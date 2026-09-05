package com.civora.app.core.model

enum class NotificationPriority {
    CRITICAL,
    WARNING,
    INFO
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val priority: NotificationPriority,
    val isRead: Boolean = false,
    val actionDeepLink: String? = null
)
