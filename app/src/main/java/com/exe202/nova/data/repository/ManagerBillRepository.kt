package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_MANAGER_BILLS
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.ManagerBill
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerBillRepository @Inject constructor() {
    private val bills = MOCK_MANAGER_BILLS.toMutableList()

    suspend fun getAllBills(): List<ManagerBill> = bills.toList()

    suspend fun updateBillStatus(id: String, status: BillStatus): ManagerBill {
        val index = bills.indexOfFirst { it.id == id }
        if (index == -1) throw Exception("Bill not found")
        val updated = bills[index].copy(status = status)
        bills[index] = updated
        return updated
    }

    suspend fun createBill(bill: ManagerBill): ManagerBill {
        bills.add(bill)
        return bill
    }
}
