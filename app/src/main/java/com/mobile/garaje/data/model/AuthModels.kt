package com.mobile.garaje.data.model

data class RegisterRequest(
    val email: String,
    val firstname: String,
    val secondname: String,
    val password: String,
    val phoneNumber: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class AuthResponse(
    val token: String?,
    val role: String?,
    val firstname: String?,
    val secondname: String?,         // ← added
    val detailsCompleted: Boolean?,
    val userId: Long?,
    val message: String?,
    val success: Boolean?
)

data class AuthApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: AuthResponse?
)

data class MessageResponse(
    val message: String?,
    val success: Boolean?
)