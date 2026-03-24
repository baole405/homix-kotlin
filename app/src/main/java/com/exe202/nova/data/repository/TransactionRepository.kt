package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Transaction
import com.exe202.nova.data.remote.api.TransactionApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val api: TransactionApi
) {
    suspend fun getTransactions(): Result<List<Transaction>> = runCatching {
        val response = api.getTransactions()
        if (response.isSuccessful) response.body()?.data ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun getTransactionsByMonth(month: String): Result<List<Transaction>> = runCatching {
        val response = api.getTransactionsByMonth(month)
        if (response.isSuccessful) response.body()?.data ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }
}
