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
    suspend fun getMyBookings(): List<Booking> = bookingApi.getMyBookings()

    suspend fun getBookingsByDate(date: String, serviceType: String): List<Booking> =
        bookingApi.getBookingsByDate(date, serviceType)

    suspend fun createBooking(request: CreateBookingRequest): Booking =
        bookingApi.createBooking(request)
}
