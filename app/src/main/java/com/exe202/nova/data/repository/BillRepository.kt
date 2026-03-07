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
    suspend fun getBills(): List<Bill> = billApi.getBills().data

    suspend fun getUpcomingBills(): List<Bill> = billApi.getUpcomingBills().data

    suspend fun getBillDetail(id: Int): BillDetail = billApi.getBillDetail(id)

    suspend fun markPaid(id: Int, request: MarkPaidRequest): MarkPaidResponse =
        billApi.markPaid(id, request)

    suspend fun createPaymentLink(request: CreatePaymentLinkRequest): CreatePaymentLinkResponse =
        billApi.createPaymentLink(request)
}
