package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.CreateFeeTypeRequest
import com.exe202.nova.data.model.FeeType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FeeTypeApi {
    @GET("fee-types")
    suspend fun getFeeTypes(): Response<List<FeeType>>

    @POST("fee-types")
    suspend fun createFeeType(@Body request: CreateFeeTypeRequest): Response<FeeType>

    @PATCH("fee-types/{id}")
    suspend fun updateFeeType(
        @Path("id") id: Int,
        @Body request: CreateFeeTypeRequest
    ): Response<FeeType>

    @DELETE("fee-types/{id}")
    suspend fun deleteFeeType(@Path("id") id: Int): Response<Unit>
}
