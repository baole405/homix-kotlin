package com.exe202.nova.data.model

data class Notification(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String?,
    val isRead: Boolean = false,
    val type: String?
)

data class NotificationsResponse(
    val data: List<Notification>,
    val total: Int,
    val unread: Int? = null
)

data class MarkNotificationReadResponse(
    val message: String,
    val notification: Notification
)
