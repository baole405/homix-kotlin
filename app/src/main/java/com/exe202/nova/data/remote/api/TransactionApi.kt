package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.Transaction
import retrofit2.Response
import retrofit2.http.GET

interface TransactionApi {
    @GET("transactions")
    suspend fun getTransactions(): Response<List<Transaction>>
}
