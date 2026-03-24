package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.Transaction
import com.exe202.nova.data.model.TransactionsByMonthResponse
import com.exe202.nova.data.model.TransactionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TransactionApi {
    @GET("transactions")
    suspend fun getTransactions(): Response<TransactionsResponse>

    @GET("transactions/by-month/{month}")
    suspend fun getTransactionsByMonth(@Path("month") month: String): Response<TransactionsByMonthResponse>
}
