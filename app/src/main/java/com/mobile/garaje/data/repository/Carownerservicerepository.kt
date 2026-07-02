package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.GarageOfferingResponse
import com.mobile.garaje.data.model.ServiceDetailResponse
import com.mobile.garaje.data.network.RetrofitClient

class CarOwnerServiceRepository {

    private val api = RetrofitClient.carOwnerServiceApi

    suspend fun getServiceDetail(token: String, serviceId: Long): Result<ServiceDetailResponse> {
        return try {
            val response = api.getServiceDetail("Bearer $token", serviceId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load service"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getGaragesOfferingService(
        token: String,
        serviceId: Long,
        latitude: Double,
        longitude: Double
    ): Result<List<GarageOfferingResponse>> {
        return try {
            val response = api.getGaragesOfferingService("Bearer $token", serviceId, latitude, longitude)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load garages"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}