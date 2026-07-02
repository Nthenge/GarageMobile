package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient

class MechanicRepository {

    private val api = RetrofitClient.mechanicApi

    // ── Register mechanic ─────────────────────────────────────────────────────

    suspend fun registerMechanic(
        token: String,
        firstname: String,
        secondname: String,
        email: String,
        phoneNumber: String
    ): Result<MechanicRegisterData> = runCatching {
        val response = api.registerMechanic(
            token   = "Bearer $token",
            request = MechanicRegisterRequest(
                firstname   = firstname,
                secondname  = secondname,
                email       = email,
                phoneNumber = phoneNumber
            )
        )
        if (response.isSuccessful && response.body() != null) {
            // Unwrap .data from the envelope — same pattern as auth and services
            response.body()!!.data
                ?: error(response.body()!!.message ?: "Registration failed")
        } else {
            error(response.errorBody()?.string() ?: "Registration failed")
        }
    }

    // ── Get garage mechanics ──────────────────────────────────────────────────

    suspend fun getGarageMechanics(
        token: String
    ): Result<List<GarageMechanicResponse>> = runCatching {
        val response = api.getGarageMechanics("Bearer $token")
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.data ?: emptyList()
        } else {
            error(response.errorBody()?.string() ?: "Failed to load mechanics")
        }
    }
}