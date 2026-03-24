package com.exe202.nova.data.model

import com.google.gson.annotations.SerializedName

data class Apartment(
    val id: Int? = null,
    val unitNumber: String,
    val floor: Int? = null,
    val block: String? = null,
    @SerializedName("areaSqm") val areaSqm: String? = null,
    val owner: ApartmentOwner? = null,
    val monthlyFee: Double? = null,
    val status: String? = null
)

data class ApartmentOwner(
    val id: Int? = null,
    val fullName: String? = null,
    val email: String? = null
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
