package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CommandCentreApiService {

    @GET("garage/{garageId}/command-centre")
    suspend fun getCommandCentre(
        @Header("Authorization") token: String,
        @Path("garageId") garageId: Long
    ): Response<CommandCentreResponse>

    @GET("garage/{garageId}/nearby-mechanics")
    suspend fun getNearbyMechanics(
        @Header("Authorization") token: String,
        @Path("garageId") garageId: Long
    ): Response<List<NearbyMechanicResponse>>

    @GET("garage/{garageId}/bookings/today")
    suspend fun getTodayBookings(
        @Header("Authorization") token: String,
        @Path("garageId") garageId: Long
    ): Response<List<TodayBookingResponse>>
}