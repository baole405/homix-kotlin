package com.exe202.nova.data.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val fullName: String?,
    val role: AppRole,
    val phoneNumber: String?,
    val image: String?
)
