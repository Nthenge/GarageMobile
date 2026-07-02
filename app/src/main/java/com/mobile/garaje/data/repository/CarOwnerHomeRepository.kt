package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.CarOwnerHomeResponse
import com.mobile.garaje.data.network.RetrofitClient

class CarOwnerHomeRepository {

    private val api = RetrofitClient.carOwnerHomeApi

    suspend fun getHome(token: String): Result<CarOwnerHomeResponse> {
        return try {
            val response = api.getHome("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load home"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHomeByVehicle(token: String, vehicleId: Long): Result<CarOwnerHomeResponse> {
        return try {
            val response = api.getHomeByVehicle("Bearer $token", vehicleId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load home"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}