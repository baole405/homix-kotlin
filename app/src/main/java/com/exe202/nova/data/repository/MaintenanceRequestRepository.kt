package com.exe202.nova.data.repository

import com.exe202.nova.data.model.MaintenanceRequest
import com.exe202.nova.data.model.MaintenanceStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRequestRepository @Inject constructor() {
    private val requests = mutableListOf(
        MaintenanceRequest(1, "Bóng đèn hành lang bị hỏng", "Hành lang tầng 5 Block A thiếu sáng", "Hành lang tầng 5 Block A", MaintenanceStatus.IN_PROGRESS, "2026-03-01"),
        MaintenanceRequest(2, "Vòi nước nhỏ giọt", "Vòi nước bồn rửa bếp bị rỉ", "Nhà bếp", MaintenanceStatus.PENDING, "2026-03-05")
    )
    private var nextId = 3

    fun getAll(): List<MaintenanceRequest> = requests.toList()

    fun create(title: String, description: String, location: String): MaintenanceRequest {
        val request = MaintenanceRequest(
            id = nextId++,
            title = title,
            description = description,
            location = location,
            createdAt = java.time.LocalDate.now().toString()
        )
        requests.add(0, request)
        return request
    }
}
