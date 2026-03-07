package com.exe202.nova.data.model

data class Apartment(
    val id: String,
    val unitNumber: String,
    val floor: Int,
    val block: String,
    val area: Double
)

data class Vehicle(
    val id: String,
    val type: VehicleType,
    val licensePlate: String,
    val imageUrl: String?,
    val ownerId: String
)

data class FamilyMember(
    val id: String,
    val name: String,
    val relation: String,
    val dob: String?,
    val userId: String
)

data class UserProfile(
    val id: String,
    val name: String,
    val idNumber: String,
    val dob: String?,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: String,
    val apartment: Apartment?,
    val vehicles: List<Vehicle>,
    val familyMembers: List<FamilyMember>
)
