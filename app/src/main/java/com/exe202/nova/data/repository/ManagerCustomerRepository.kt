package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_CUSTOMERS
import com.exe202.nova.data.model.Customer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerCustomerRepository @Inject constructor() {
    private val customers = MOCK_CUSTOMERS.toMutableList()

    suspend fun getAllCustomers(): List<Customer> = customers.toList()

    suspend fun createCustomer(customer: Customer): Customer {
        customers.add(customer)
        return customer
    }
}
