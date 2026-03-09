package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.remote.api.NotificationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
) {
    suspend fun getNotifications(): List<Notification> {
        val response = notificationApi.getNotifications()
        if (!response.isSuccessful) {
            throw Exception("Get notifications failed: ${response.code()}")
        }
        return response.body()?.data ?: emptyList()
    }

    suspend fun markAsRead(id: Int) {
        val response = notificationApi.markAsRead(id)
        if (!response.isSuccessful) {
            throw Exception("Mark as read failed: ${response.code()}")
        }
    }
}
