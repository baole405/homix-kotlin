package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Apartment
import com.exe202.nova.data.remote.api.ApartmentApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApartmentRepository @Inject constructor(
    private val api: ApartmentApi
) {
    suspend fun getMyApartment(): Result<Apartment> = runCatching {
        val response = api.getMyApartment()
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }
}
