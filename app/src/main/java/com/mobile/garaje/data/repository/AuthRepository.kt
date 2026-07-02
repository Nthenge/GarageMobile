package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient

class AuthRepository {

    private val api = RetrofitClient.authApi

    suspend fun register(
        email: String,
        firstname: String,
        secondname: String,
        password: String,
        phoneNumber: String,
        role: String
    ): Result<AuthResponse> {
        return try {
            val response = api.register(
                RegisterRequest(email, firstname, secondname, password, phoneNumber, role)
            )
            if (response.isSuccessful && response.body() != null) {
                val inner = response.body()!!.data
                if (inner != null) {
                    Result.success(inner)
                } else {
                    Result.failure(Exception(response.body()!!.message ?: "Registration failed"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val inner = response.body()!!.data
                if (inner != null) {
                    Result.success(inner)
                } else {
                    Result.failure(Exception(response.body()!!.message ?: "Login failed"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun forgotPassword(email: String): Result<MessageResponse> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Request failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}