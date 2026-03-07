package com.exe202.nova.data.model

data class ParkingSlot(
    val id: String,
    val label: String,
    val floor: String,
    val type: VehicleType,
    val status: SlotStatus,
    val pricePerDay: Double,
    val pricePerMonth: Double
)

data class BBQSlot(
    val id: String,
    val name: String,
    val capacity: Int,
    val pricePerHour: Double,
    val status: SlotStatus
)

data class PoolSlot(
    val id: String,
    val name: String,
    val location: String,
    val capacity: Int,
    val pricePerHour: Double,
    val openTime: String,
    val closeTime: String,
    val maxDurationHours: Int,
    val status: SlotStatus
)
