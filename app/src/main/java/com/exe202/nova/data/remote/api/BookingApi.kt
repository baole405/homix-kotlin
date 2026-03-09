package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.Booking
import com.exe202.nova.data.model.CreateBookingRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookingApi {
    @GET("bookings/me")
    suspend fun getMyBookings(): Response<List<Booking>>

    @GET("bookings")
    suspend fun getBookingsByDate(
        @Query("date") date: String,
        @Query("serviceType") serviceType: String
    ): Response<List<Booking>>

    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<Booking>
}
