package com.exe202.nova.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    val id: Int,
    val billTitle: String?,
    val amount: String,
    @SerializedName("paymentDate") val paidDate: String?,
    @SerializedName("paymentMethod") val method: String?,
    @SerializedName("transactionRef") val transactionCode: String?,
    val notes: String? = null
)

data class TransactionsResponse(
    val data: List<Transaction>,
    val total: Int? = null
)

data class TransactionsByMonthResponse(
    val data: List<Transaction>,
    val month: String
)
