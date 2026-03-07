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
        tokenManager.saveToken(response.accessToken)
        return response
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
        tokenManager.saveToken(response.accessToken)
        return response
    }

    suspend fun getMe(): User = authApi.getMe()

    fun getToken(): String? = tokenManager.getToken()

    fun clearToken() = tokenManager.clearToken()

    fun saveToken(token: String) = tokenManager.saveToken(token)
}
