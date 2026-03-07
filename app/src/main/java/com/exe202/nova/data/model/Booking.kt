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
    val numberOfParticipants: Int?,
    val createdAt: String
)

data class CreateBookingRequest(
    val serviceType: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val slotNumber: String?,
    val slotNumbers: List<String>?,
    val endDate: String?,
    val notes: String?,
    val price: Double?,
    val numberOfParticipants: Int?
)
