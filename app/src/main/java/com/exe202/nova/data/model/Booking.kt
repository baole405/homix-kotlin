package com.exe202.nova.data.model

data class Booking(
    val id: Int,
    val serviceType: ServiceType,
    val slotNumber: String?,
    val date: String,
    val endDate: String?,
    val startTime: String,
    val endTime: String,
    val status: BookingStatus,
    val notes: String?,
    val numberOfParticipants: Int? = null,
    val createdAt: String? = null
)

data class CreateBookingRequest(
    val serviceType: String,
    val slotNumber: String?,
    val date: String,
    val endDate: String?,
    val startTime: String,
    val endTime: String,
    val notes: String?
)
