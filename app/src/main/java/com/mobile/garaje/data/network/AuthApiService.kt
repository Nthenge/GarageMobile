package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthApiResponse>

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthApiResponse>

    @POST("user/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<MessageResponse>
}