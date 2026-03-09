package com.exe202.nova.data.model

data class ManagerBooking(
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
    val createdAt: String,
    val residentName: String,
    val apartmentUnit: String
)

data class ManagerBill(
    val id: String,
    val title: String,
    val amount: Double,
    val dueDate: String,
    val period: String,
    val status: BillStatus,
    val feeType: FeeType,
    val apartmentUnit: String,
    val apartmentBlock: String,
    val residentName: String
)

data class ManagerApartment(
    val id: String,
    val unitNumber: String,
    val floor: Int,
    val block: String,
    val area: Double,
    val status: ApartmentStatus,
    val residentName: String?,
    val residentId: String?,
    val monthlyFee: Double
)

data class Customer(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val apartmentUnit: String?,
    val role: AppRole
)

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val category: AnnouncementCategory,
    val priority: AnnouncementPriority,
    val createdAt: String,
    val imageUrl: String?,
    val pinned: Boolean
)

data class DashboardStats(
    val totalApartments: Int,
    val occupiedApartments: Int,
    val pendingBills: Int,
    val overdueBills: Int,
    val pendingBookings: Int,
    val pendingComplaints: Int
)

data class RevenueData(
    val month: String,
    val amount: Double
)

data class BookingStatsData(
    val serviceType: ServiceType,
    val count: Int
)
