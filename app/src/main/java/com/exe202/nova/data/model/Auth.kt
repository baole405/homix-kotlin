package com.exe202.nova.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: User
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String
)

data class RegisterResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: User
)
