package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient

class CommandCentreRepository {

    private val api = RetrofitClient.commandCentreApi
    suspend fun getCommandCentre(
        token: String,
        garageId: Long
    ): Result<CommandCentreResponse> = runCatching {
        val response = api.getCommandCentre("Bearer $token", garageId)
        if (response.isSuccessful) {
            response.body() ?: error("Empty response body")
        } else {
            error(response.errorBody()?.string() ?: "Failed to load command centre")
        }
    }

    suspend fun getNearbyMechanics(
        token: String,
        garageId: Long
    ): Result<List<NearbyMechanicResponse>> = runCatching {
        val response = api.getNearbyMechanics("Bearer $token", garageId)
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            error(response.errorBody()?.string() ?: "Failed to load nearby mechanics")
        }
    }

    suspend fun getTodayBookings(
        token: String,
        garageId: Long
    ): Result<List<TodayBookingResponse>> = runCatching {
        val response = api.getTodayBookings("Bearer $token", garageId)
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            error(response.errorBody()?.string() ?: "Failed to load bookings")
        }
    }
}