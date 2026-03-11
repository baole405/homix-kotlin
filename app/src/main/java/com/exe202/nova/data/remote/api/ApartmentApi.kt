package com.exe202.nova.data.remote.api

import com.exe202.nova.data.model.Apartment
import retrofit2.Response
import retrofit2.http.GET

interface ApartmentApi {
    @GET("apartments/my")
    suspend fun getMyApartment(): Response<Apartment>
}
