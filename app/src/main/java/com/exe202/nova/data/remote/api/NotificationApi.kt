package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.NotificationsResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): NotificationsResponse

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int)
}
