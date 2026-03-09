package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_MANAGER_APARTMENTS
import com.exe202.nova.data.model.ApartmentStatus
import com.exe202.nova.data.model.ManagerApartment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerApartmentRepository @Inject constructor() {
    private val apartments = MOCK_MANAGER_APARTMENTS.toMutableList()

    suspend fun getAllApartments(): List<ManagerApartment> = apartments.toList()

    suspend fun updateApartmentStatus(id: String, status: ApartmentStatus): ManagerApartment {
        val index = apartments.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Apartment not found")
        val updated = apartments[index].copy(status = status)
        apartments[index] = updated
        return updated
    }
}
