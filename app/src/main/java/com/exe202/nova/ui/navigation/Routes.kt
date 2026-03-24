package com.exe202.nova.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object ResidentMainRoute

@Serializable
object ManagerMainRoute

// Resident nested routes
@Serializable
object DashboardRoute

@Serializable
object BillsRoute

@Serializable
data class BillDetailRoute(val billId: Int)

@Serializable
object TransactionHistoryRoute

@Serializable
object BookingRoute

@Serializable
object MyBookingsRoute

@Serializable
object NotificationsRoute

@Serializable
object ProfileRoute

@Serializable
object SettingsRoute

@Serializable
object ResidentChatRoute

// Manager nested routes
@Serializable
object ManagerDashboardRoute

@Serializable
object ManagerBookingsRoute

@Serializable
object ManagerBillingRoute

@Serializable
object ManagerCustomersRoute

@Serializable
data class ManagerCustomerDetailRoute(val customerId: String)

@Serializable
object ManagerApartmentsRoute

@Serializable
object ManagerFacilitiesRoute

@Serializable
object ManagerAnnouncementsRoute

@Serializable
object ManagerCreateAnnouncementRoute

@Serializable
object ManagerReportsRoute

@Serializable
object ManagerFeeTypesRoute

@Serializable
object ManagerChatRoute

// Resident feature routes
@Serializable
object FacilitiesRoute

@Serializable
object MaintenanceRoute

@Serializable
object MapRoute
