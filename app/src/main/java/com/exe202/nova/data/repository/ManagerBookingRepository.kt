package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_MANAGER_BOOKINGS
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.ManagerBooking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerBookingRepository @Inject constructor() {
    private val bookings = MOCK_MANAGER_BOOKINGS.toMutableList()

    suspend fun getAllBookings(): List<ManagerBooking> = bookings.toList()

    suspend fun updateBookingStatus(id: Int, status: BookingStatus): ManagerBooking {
        val index = bookings.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Booking not found")
        val updated = bookings[index].copy(status = status)
        bookings[index] = updated
        return updated
    }

    suspend fun updateBookingNotes(id: Int, notes: String): ManagerBooking {
        val index = bookings.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Booking not found")
        val updated = bookings[index].copy(notes = notes)
        bookings[index] = updated
        return updated
    }
}
