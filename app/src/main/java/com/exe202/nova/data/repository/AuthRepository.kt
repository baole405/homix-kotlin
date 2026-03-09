package com.exe202.nova.data.repository

import com.exe202.nova.data.local.TokenManager
import com.exe202.nova.data.model.LoginRequest
import com.exe202.nova.data.model.LoginResponse
import com.exe202.nova.data.model.RegisterRequest
import com.exe202.nova.data.model.RegisterResponse
import com.exe202.nova.data.model.User
import com.exe202.nova.data.remote.api.AuthApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(usernameOrEmail: String, password: String): LoginResponse {
        val response = authApi.login(LoginRequest(usernameOrEmail, password))
        if (!response.isSuccessful) {
            throw Exception("Login failed: ${response.code()}")
        }
        val body = response.body() ?: throw Exception("Empty response")
        tokenManager.saveToken(body.accessToken)
        return body
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): RegisterResponse {
        val response = authApi.register(
            RegisterRequest(username, email, password, fullName, phoneNumber)
        )
        if (!response.isSuccessful) {
            throw Exception("Register failed: ${response.code()}")
        }
        val body = response.body() ?: throw Exception("Empty response")
        tokenManager.saveToken(body.accessToken)
        return body
    }

    suspend fun getMe(): User {
        val response = authApi.getMe()
        if (!response.isSuccessful) {
            throw Exception("Get user failed: ${response.code()}")
        }
        return response.body() ?: throw Exception("Empty response")
    }

    fun getToken(): String? = tokenManager.getToken()

    fun clearToken() = tokenManager.clearToken()

    fun saveToken(token: String) = tokenManager.saveToken(token)
}
