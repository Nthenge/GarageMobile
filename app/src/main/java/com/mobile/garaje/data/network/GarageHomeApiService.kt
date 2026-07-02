package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.GarageHomeApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface GarageHomeApiService {

    /**
     * PLACEHOLDER ENDPOINT — backend route not yet confirmed.
     * Expected to return: garage summary, alerts, pending action count,
     * today's bookings, and live team status — all scoped to the
     * authenticated garage admin's garage via JWT.
     *
     * Update this path once the backend exposes the real endpoint.
     */
    @GET("garage/home")
    suspend fun getGarageHome(
        @Header("Authorization") token: String
    ): Response<GarageHomeApiResponse>
}