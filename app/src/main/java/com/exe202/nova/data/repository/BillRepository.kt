package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Bill
import com.exe202.nova.data.model.BillDetail
import com.exe202.nova.data.model.CreatePaymentLinkRequest
import com.exe202.nova.data.model.CreatePaymentLinkResponse
import com.exe202.nova.data.model.MarkPaidRequest
import com.exe202.nova.data.model.MarkPaidResponse
import com.exe202.nova.data.remote.api.BillApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillRepository @Inject constructor(
    private val billApi: BillApi
) {
    suspend fun getBills(): List<Bill> {
        val response = billApi.getBills()
        if (!response.isSuccessful) throw Exception("Get bills failed: ${response.code()}")
        return response.body()?.data ?: emptyList()
    }

    suspend fun getUpcomingBills(): List<Bill> {
        val response = billApi.getUpcomingBills()
        if (!response.isSuccessful) throw Exception("Get upcoming bills failed: ${response.code()}")
        return response.body()?.data ?: emptyList()
    }

    suspend fun getBillDetail(id: Int): BillDetail {
        val response = billApi.getBillDetail(id)
        if (!response.isSuccessful) throw Exception("Get bill detail failed: ${response.code()}")
        return response.body() ?: throw Exception("Empty response")
    }

    suspend fun markPaid(id: Int, request: MarkPaidRequest): MarkPaidResponse {
        val response = billApi.markPaid(id, request)
        if (!response.isSuccessful) throw Exception("Mark paid failed: ${response.code()}")
        return response.body() ?: throw Exception("Empty response")
    }

    suspend fun createPaymentLink(request: CreatePaymentLinkRequest): CreatePaymentLinkResponse {
        val response = billApi.createPaymentLink(request)
        if (!response.isSuccessful) throw Exception("Create payment link failed: ${response.code()}")
        return response.body() ?: throw Exception("Empty response")
    }
}
