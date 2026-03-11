package com.exe202.nova.data.repository

import com.exe202.nova.data.model.CreateFeeTypeRequest
import com.exe202.nova.data.model.FeeType
import com.exe202.nova.data.remote.api.FeeTypeApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeeTypeRepository @Inject constructor(
    private val api: FeeTypeApi
) {
    suspend fun getFeeTypes(): Result<List<FeeType>> = runCatching {
        val response = api.getFeeTypes()
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun createFeeType(request: CreateFeeTypeRequest): Result<FeeType> = runCatching {
        val response = api.createFeeType(request)
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun updateFeeType(id: Int, request: CreateFeeTypeRequest): Result<FeeType> = runCatching {
        val response = api.updateFeeType(id, request)
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun deleteFeeType(id: Int): Result<Unit> = runCatching {
        val response = api.deleteFeeType(id)
        if (response.isSuccessful) Unit
        else error("Lỗi ${response.code()}: ${response.message()}")
    }
}
