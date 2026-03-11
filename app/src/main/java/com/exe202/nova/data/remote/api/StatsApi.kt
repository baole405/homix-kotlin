package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.StatsActivity
import com.exe202.nova.data.model.StatsOverview
import com.exe202.nova.data.model.StatsRevenue
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsApi {
    @GET("stats")
    suspend fun getOverview(
        @Query("type") type: String,
        @Query("period") period: String? = null
    ): Response<StatsOverview>

    @GET("stats")
    suspend fun getRevenue(
        @Query("type") type: String,
        @Query("period") period: String? = null
    ): Response<StatsRevenue>

    @GET("stats")
    suspend fun getActivity(
        @Query("type") type: String,
        @Query("period") period: String? = null
    ): Response<StatsActivity>
}
