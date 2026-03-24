package com.exe202.nova.data.model

data class Bill(
    val id: Int,
    val title: String,
    val amount: String,
    val dueDate: String,
    val period: String? = null,
    val status: BillStatus,
    val createdAt: String? = null,
    val paidAt: String?
)

data class BillDetail(
    val id: Int,
    val title: String,
    val amount: String,
    val dueDate: String,
    val period: String? = null,
    val status: BillStatus,
    val createdAt: String? = null,
    val paidAt: String?,
    val apartment: ApartmentInfo?,
    val items: List<BillItem>
)

data class BillItem(
    val id: Int,
    val title: String,
    val usage: String?,
    val unitPrice: String?,
    val measureUnit: String?,
    val amount: String,
    val feeType: FeeTypeInfo?
)

data class FeeTypeInfo(val id: Int?, val name: String?)
data class ApartmentInfo(
    val unitNumber: String?,
    val floor: Int?,
    val block: String?
)

data class BillsResponse(
    val data: List<Bill>,
    val total: Int,
    val page: Int
)

data class MarkPaidRequest(
    val paymentMethod: String,
    val transactionRef: String?,
    val notes: String?
)

data class CreatePaymentLinkRequest(
    val billId: Int,
    val returnUrl: String,
    val cancelUrl: String
)

data class CreatePaymentLinkResponse(val checkoutUrl: String)

data class MarkPaidResponse(
    val message: String,
    val bill: PaidBillInfo? = null,
    val transaction: PaidTransactionInfo? = null
)

data class PaidBillInfo(
    val id: Int,
    val status: BillStatus,
    val paidAt: String?
)

data class PaidTransactionInfo(
    val id: Int,
    val amount: String,
    val method: String
)
