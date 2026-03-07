package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.remote.api.NotificationApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
) {
    suspend fun getNotifications(): List<Notification> =
        notificationApi.getNotifications().data

    suspend fun markAsRead(id: Int) = notificationApi.markAsRead(id)
}
