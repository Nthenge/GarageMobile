package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.CategoryResponse
import com.mobile.garaje.data.model.ServiceResponse
import com.mobile.garaje.data.network.RetrofitClient

class ServicesRepository {

    private val api = RetrofitClient.servicesApi

    suspend fun getAllServices(token: String): Result<List<ServiceResponse>> {
        return try {
            val response = api.getAllServices("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to load services"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getAllCategories(token: String): Result<List<CategoryResponse>> {
        return try {
            val response = api.getAllCategories("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to load categories"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getMyGarageServices(token: String): Result<List<ServiceResponse>> {
        return try {
            val response = api.getMyGarageServices("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to load garage services"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // ── Add service ───────────────────────────────────────────────────────────

    suspend fun addService(token: String, serviceId: Long): Result<List<ServiceResponse>> {
        return try {
            val response = api.addService("Bearer $token", serviceId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to add service"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun removeService(token: String, serviceId: Long): Result<List<ServiceResponse>> {
        return try {
            val response = api.removeService("Bearer $token", serviceId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val msg = response.errorBody()?.string() ?: "Failed to remove service"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}