package com.mobile.garaje.data.model

// ── Request — sent to backend ─────────────────────────────────────────────────
// garageId is NOT included — backend reads it from the JWT
data class MechanicRegisterRequest(
    val firstname: String,
    val secondname: String,
    val email: String,
    val phoneNumber: String
)

// ── Inner data object inside the register envelope ────────────────────────────
data class MechanicRegisterData(
    val id: Long?,
    val firstname: String?,
    val secondname: String?,
    val email: String?,
    val phoneNumber: String?,
    val role: String?
)

// ── Outer envelope — what the API actually returns ────────────────────────────
// Same pattern as AuthApiResponse and ServicesApiResponse
data class MechanicRegisterApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: MechanicRegisterData?
)

// ── Mechanic in the garage team list ─────────────────────────────────────────
data class GarageMechanicResponse(
    val id: Long?,
    val firstname: String?,
    val secondname: String?,
    val email: String?,
    val phoneNumber: String?,
    val role: String?,
    val enabled: Boolean?,
    val detailsCompleted: Boolean?
)

// ── Team list envelope ────────────────────────────────────────────────────────
data class GarageMechanicsApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<GarageMechanicResponse>?
)