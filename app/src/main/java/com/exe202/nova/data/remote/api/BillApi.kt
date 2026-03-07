package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.BillDetail
import com.exe202.nova.data.model.BillsResponse
import com.exe202.nova.data.model.CreatePaymentLinkRequest
import com.exe202.nova.data.model.CreatePaymentLinkResponse
import com.exe202.nova.data.model.MarkPaidRequest
import com.exe202.nova.data.model.MarkPaidResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BillApi {
    @GET("bills")
    suspend fun getBills(
        @Query("status") status: String = "all",
        @Query("sortBy") sortBy: String = "dueDate",
        @Query("sortOrder") sortOrder: String = "asc"
    ): BillsResponse

    @GET("bills/upcoming")
    suspend fun getUpcomingBills(): BillsResponse

    @GET("bills/{id}")
    suspend fun getBillDetail(@Path("id") id: Int): BillDetail

    @PATCH("bills/{id}/mark-paid")
    suspend fun markPaid(
        @Path("id") id: Int,
        @Body request: MarkPaidRequest
    ): MarkPaidResponse

    @POST("payments/create-link")
    suspend fun createPaymentLink(@Body request: CreatePaymentLinkRequest): CreatePaymentLinkResponse
}
