package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Booking
import com.exe202.nova.data.model.CreateBookingRequest
import com.exe202.nova.data.remote.api.BookingApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingApi: BookingApi
) {
    suspend fun getMyBookings(): List<Booking> {
        val response = bookingApi.getMyBookings()
        if (!response.isSuccessful) throw Exception("Get bookings failed: ${response.code()}")
        return response.body() ?: emptyList()
    }

    suspend fun getBookingsByDate(date: String, serviceType: String): List<Booking> {
        val response = bookingApi.getBookingsByDate(date, serviceType)
        if (!response.isSuccessful) throw Exception("Get bookings by date failed: ${response.code()}")
        return response.body() ?: emptyList()
    }

    suspend fun createBooking(request: CreateBookingRequest): Booking {
        val response = bookingApi.createBooking(request)
        if (!response.isSuccessful) throw Exception("Create booking failed: ${response.code()}")
        return response.body() ?: throw Exception("Empty response")
    }
}
