package com.exe202.nova.data.model

data class Transaction(
    val id: String,
    val billId: String,
    val billTitle: String,
    val amount: Double,
    val paidDate: String,
    val method: PaymentMethod,
    val transactionCode: String
)
