package com.exe202.nova.data.repository

import com.exe202.nova.data.model.StatsActivity
import com.exe202.nova.data.model.StatsOverview
import com.exe202.nova.data.model.StatsRevenue
import com.exe202.nova.data.remote.api.StatsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    private val api: StatsApi
) {
    suspend fun getOverview(period: String? = null): Result<StatsOverview> = runCatching {
        val response = api.getOverview(type = "overview", period = period)
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun getRevenue(period: String? = null): Result<StatsRevenue> = runCatching {
        val response = api.getRevenue(type = "revenue", period = period)
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }

    suspend fun getActivity(period: String? = null): Result<StatsActivity> = runCatching {
        val response = api.getActivity(type = "activity", period = period)
        if (response.isSuccessful) response.body() ?: error("Empty response body")
        else error("Lỗi ${response.code()}: ${response.message()}")
    }
}
