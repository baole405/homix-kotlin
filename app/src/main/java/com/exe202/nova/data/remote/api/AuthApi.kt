package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.LoginRequest
import com.exe202.nova.data.model.LoginResponse
import com.exe202.nova.data.model.RegisterRequest
import com.exe202.nova.data.model.RegisterResponse
import com.exe202.nova.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("auth/me")
    suspend fun getMe(): User
}
