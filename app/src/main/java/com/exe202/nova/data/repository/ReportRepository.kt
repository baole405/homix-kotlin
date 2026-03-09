package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_BOOKING_STATS
import com.exe202.nova.data.mock.MOCK_REVENUE_DATA
import com.exe202.nova.data.model.BookingStatsData
import com.exe202.nova.data.model.RevenueData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor() {
    suspend fun getRevenueData(): List<RevenueData> = MOCK_REVENUE_DATA
    suspend fun getBookingStats(): List<BookingStatsData> = MOCK_BOOKING_STATS
}
