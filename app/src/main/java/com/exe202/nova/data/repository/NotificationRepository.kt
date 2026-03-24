package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.remote.api.NotificationApi
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
) {
    suspend fun getNotifications(): List<Notification> {
        val response = notificationApi.getNotifications()
        if (!response.isSuccessful) {
            throw Exception(parseApiError(response, "Get notifications failed"))
        }
        return response.body()?.data ?: emptyList()
    }

    suspend fun markAsRead(id: Int) {
        val response = notificationApi.markAsRead(id)
        if (!response.isSuccessful) {
            throw Exception(parseApiError(response, "Mark as read failed"))
        }
        response.body() ?: throw Exception("Empty response")
    }

    private fun parseApiError(response: Response<*>, prefix: String): String {
        val fallback = "$prefix: ${response.code()}"
        val raw = response.errorBody()?.string() ?: return fallback
        return try {
            val json = JSONObject(raw)
            when (val message = json.opt("message")) {
                is String -> message.ifBlank { fallback }
                is JSONArray -> {
                    val msgs = mutableListOf<String>()
                    for (i in 0 until message.length()) {
                        msgs.add(message.optString(i))
                    }
                    msgs.filter { it.isNotBlank() }.joinToString("\n").ifBlank { fallback }
                }
                else -> json.optString("error").ifBlank { fallback }
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
