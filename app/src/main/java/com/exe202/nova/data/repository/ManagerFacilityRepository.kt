package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_BBQ_SLOTS
import com.exe202.nova.data.mock.MOCK_PARKING_SLOTS
import com.exe202.nova.data.mock.MOCK_POOL
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerFacilityRepository @Inject constructor() {
    private val parkingSlots = MOCK_PARKING_SLOTS.toMutableList()
    private val bbqSlots = MOCK_BBQ_SLOTS.toMutableList()
    private var pool = MOCK_POOL

    suspend fun getParkingSlots(): List<ParkingSlot> = parkingSlots.toList()

    suspend fun updateParkingSlot(slot: ParkingSlot): ParkingSlot {
        val index = parkingSlots.indexOfFirst { it.id == slot.id }
        if (index == -1) throw Exception("Parking slot not found")
        parkingSlots[index] = slot
        return slot
    }

    suspend fun getBbqSlots(): List<BBQSlot> = bbqSlots.toList()

    suspend fun updateBbqSlot(slot: BBQSlot): BBQSlot {
        val index = bbqSlots.indexOfFirst { it.id == slot.id }
        if (index == -1) throw Exception("BBQ slot not found")
        bbqSlots[index] = slot
        return slot
    }

    suspend fun getPool(): PoolSlot = pool

    suspend fun updatePool(updated: PoolSlot): PoolSlot {
        pool = updated
        return pool
    }
}
