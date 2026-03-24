package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Booking
import com.exe202.nova.data.model.CreateBookingRequest
import com.exe202.nova.data.remote.api.BookingApi
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingApi: BookingApi
) {
    suspend fun getMyBookings(): List<Booking> {
        val response = bookingApi.getMyBookings()
        if (!response.isSuccessful) throw Exception(parseApiError(response, "Get bookings failed"))
        return response.body() ?: emptyList()
    }

    suspend fun getBookingsByDate(date: String, serviceType: String): List<Booking> {
        val response = bookingApi.getBookingsByDate(date, serviceType)
        if (!response.isSuccessful) throw Exception(parseApiError(response, "Get bookings by date failed"))
        return response.body() ?: emptyList()
    }

    suspend fun createBooking(request: CreateBookingRequest): Booking {
        val response = bookingApi.createBooking(request)
        if (!response.isSuccessful) throw Exception(parseApiError(response, "Create booking failed"))
        return response.body() ?: throw Exception("Empty response")
    }

    private fun parseApiError(response: Response<*>, prefix: String): String {
        val fallback = "$prefix: ${response.code()}"
        val raw = response.errorBody()?.string() ?: return fallback
        return try {
            val json = JSONObject(raw)
            val message = json.opt("message")
            when (message) {
                is JSONArray -> {
                    val msgs = mutableListOf<String>()
                    for (i in 0 until message.length()) {
                        msgs.add(message.optString(i))
                    }
                    msgs.filter { it.isNotBlank() }.joinToString("\n").ifBlank { fallback }
                }
                is String -> message.ifBlank { fallback }
                else -> json.optString("error").ifBlank { fallback }
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
