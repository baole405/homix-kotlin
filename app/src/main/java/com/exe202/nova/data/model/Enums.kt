package com.exe202.nova.data.model

import com.google.gson.annotations.SerializedName

enum class AppRole {
    @SerializedName("manager") MANAGER,
    @SerializedName("resident") RESIDENT,
    @SerializedName("staff") STAFF
}

enum class BillStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("paid") PAID,
    @SerializedName("overdue") OVERDUE,
    @SerializedName("cancelled") CANCELLED
}

enum class PaymentMethod {
    @SerializedName("bank_transfer") BANK_TRANSFER,
    @SerializedName("cash") CASH,
    @SerializedName("e_wallet") E_WALLET,
    @SerializedName("credit_card") CREDIT_CARD
}

enum class ServiceType {
    @SerializedName("parking") PARKING,
    @SerializedName("bbq") BBQ,
    @SerializedName("swimming_pool") SWIMMING_POOL
}

enum class BookingStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("confirmed") CONFIRMED,
    @SerializedName("rejected") REJECTED,
    @SerializedName("cancelled") CANCELLED
}

enum class VehicleType {
    @SerializedName("car") CAR,
    @SerializedName("motorbike") MOTORBIKE,
    @SerializedName("bicycle") BICYCLE
}

enum class SlotStatus {
    @SerializedName("available") AVAILABLE,
    @SerializedName("occupied") OCCUPIED,
    @SerializedName("maintenance") MAINTENANCE
}

enum class FeeTypeCategory {
    @SerializedName("electricity") ELECTRICITY,
    @SerializedName("water") WATER,
    @SerializedName("parking") PARKING,
    @SerializedName("management") MANAGEMENT,
    @SerializedName("internet") INTERNET,
    @SerializedName("service") SERVICE
}

enum class ApartmentStatus {
    @SerializedName("occupied") OCCUPIED,
    @SerializedName("vacant") VACANT,
    @SerializedName("maintenance") MAINTENANCE
}

enum class AnnouncementCategory {
    @SerializedName("maintenance") MAINTENANCE,
    @SerializedName("event") EVENT,
    @SerializedName("policy") POLICY,
    @SerializedName("emergency") EMERGENCY,
    @SerializedName("general") GENERAL
}

enum class AnnouncementPriority {
    @SerializedName("normal") NORMAL,
    @SerializedName("important") IMPORTANT,
    @SerializedName("urgent") URGENT
}
