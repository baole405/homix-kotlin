package com.exe202.nova.data.repository

import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FacilityRepository @Inject constructor() {

    fun getParkingSlots(): List<ParkingSlot> = listOf(
        ParkingSlot("A1", "A1", "B1", VehicleType.CAR, SlotStatus.AVAILABLE, 20000.0, 500000.0),
        ParkingSlot("A2", "A2", "B1", VehicleType.CAR, SlotStatus.OCCUPIED, 20000.0, 500000.0),
        ParkingSlot("A3", "A3", "B1", VehicleType.CAR, SlotStatus.AVAILABLE, 20000.0, 500000.0),
        ParkingSlot("A4", "A4", "B1", VehicleType.CAR, SlotStatus.MAINTENANCE, 20000.0, 500000.0),
        ParkingSlot("A5", "A5", "B1", VehicleType.CAR, SlotStatus.AVAILABLE, 20000.0, 500000.0),
        ParkingSlot("B1", "B1", "B1", VehicleType.MOTORBIKE, SlotStatus.AVAILABLE, 5000.0, 120000.0),
        ParkingSlot("B2", "B2", "B1", VehicleType.MOTORBIKE, SlotStatus.OCCUPIED, 5000.0, 120000.0),
        ParkingSlot("B3", "B3", "B1", VehicleType.MOTORBIKE, SlotStatus.AVAILABLE, 5000.0, 120000.0),
        ParkingSlot("P12", "P12", "B2", VehicleType.CAR, SlotStatus.AVAILABLE, 20000.0, 500000.0),
        ParkingSlot("P05", "P05", "B2", VehicleType.MOTORBIKE, SlotStatus.AVAILABLE, 5000.0, 120000.0)
    )

    fun getBbqSlots(): List<BBQSlot> = listOf(
        BBQSlot("BBQ-1", "Khu vuc Ho boi", 20, 150000.0, SlotStatus.AVAILABLE),
        BBQSlot("BBQ-2", "San thuong", 30, 200000.0, SlotStatus.AVAILABLE),
        BBQSlot("BBQ-3", "San vuon", 25, 180000.0, SlotStatus.AVAILABLE),
        BBQSlot("BBQ-4", "Ven song", 15, 250000.0, SlotStatus.MAINTENANCE),
        BBQSlot("BBQ-5", "VIP Lounge", 50, 350000.0, SlotStatus.AVAILABLE)
    )

    fun getPoolSlots(): List<PoolSlot> = listOf(
        PoolSlot(
            id = "POOL-1",
            name = "Ho boi Vo cuc",
            location = "Tang thuong Block A",
            capacity = 30,
            pricePerHour = 50000.0,
            openTime = "08:00",
            closeTime = "22:00",
            maxDurationHours = 4,
            status = SlotStatus.AVAILABLE
        )
    )
}
