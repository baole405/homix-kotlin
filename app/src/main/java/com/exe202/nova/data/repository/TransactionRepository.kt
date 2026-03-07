package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Transaction
import com.exe202.nova.data.remote.api.TransactionApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionApi: TransactionApi
) {
    suspend fun getTransactions(): List<Transaction> = transactionApi.getTransactions()
}
