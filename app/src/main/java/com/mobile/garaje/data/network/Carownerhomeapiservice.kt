package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.CarOwnerHomeApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CarOwnerHomeApiService {

    @GET("vehicle/home")
    suspend fun getHome(
        @Header("Authorization") token: String
    ): Response<CarOwnerHomeApiResponse>

    @GET("vehicle/{vehicleId}/home")
    suspend fun getHomeByVehicle(
        @Header("Authorization") token: String,
        @Path("vehicleId") vehicleId: Long
    ): Response<CarOwnerHomeApiResponse>
}