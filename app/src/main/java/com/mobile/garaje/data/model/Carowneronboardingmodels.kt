package com.mobile.garaje.data.model

import android.net.Uri

// ── Accumulates data across the 2 steps ──────────────────────────────────────

data class CarOwnerOnboardingState(
    val details: CarOwnerDetailsData? = null,
    val photo: CarOwnerPhotoData? = null
)

// ── Step 1 — vehicle + personal details ──────────────────────────────────────

data class CarOwnerDetailsData(
    val make: String,           // required
    val model: String,          // required
    val year: String,           // required
    val licensePlate: String,   // required
    val engineType: String,     // required
    val engineCapacity: String, // required
    val color: String,          // required
    val transmission: String,   // required
)

// ── Step 2 — profile photo (optional) ────────────────────────────────────────

data class CarOwnerPhotoData(
    val profilePicUri: Uri? = null
)

// ── Server response ───────────────────────────────────────────────────────────

data class CarOwnerCreateResponse(
    val message: String?,
    val success: Boolean?
)