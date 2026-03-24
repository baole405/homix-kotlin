package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.NotificationsResponse
import com.exe202.nova.data.model.MarkNotificationReadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationsResponse>

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: Int,
        @Body body: Map<String, String> = emptyMap()
    ): Response<MarkNotificationReadResponse>
}
