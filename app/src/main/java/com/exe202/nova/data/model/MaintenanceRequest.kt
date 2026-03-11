package com.exe202.nova.data.model

data class MaintenanceRequest(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val status: MaintenanceStatus = MaintenanceStatus.PENDING,
    val createdAt: String
)

enum class MaintenanceStatus { PENDING, IN_PROGRESS, DONE }
