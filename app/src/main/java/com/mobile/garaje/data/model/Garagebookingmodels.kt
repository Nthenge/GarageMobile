package com.mobile.garaje.data.model

// ── Single booking row ────────────────────────────────────────────────────
data class GarageBooking(
    val id: Long?,
    val date: String?,            // e.g. "2026-05-22" — used for date grouping
    val dateLabel: String?,       // backend-provided friendly label e.g. "Today", "Tomorrow"
    val time: String?,            // e.g. "9:00"
    val period: String?,          // "AM" | "PM"
    val serviceName: String?,
    val customerName: String?,
    val vehiclePlate: String?,
    val mechanicId: Long?,
    val mechanicName: String?,    // null if unassigned
    val mechanicInitials: String?,
    val status: String?,          // "PENDING" | "CONFIRMED" | "COMPLETED" | "CANCELLED"
    val categoryIcon: String?     // backend-provided icon key, e.g. "oil", "engine", "tyre", "brake", "battery"
)

// ── Full bookings list response ───────────────────────────────────────────
data class GarageBookingsResponse(
    val bookings: List<GarageBooking>?
)

// ── Envelope wrapper ──────────────────────────────────────────────────────
data class GarageBookingsApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<GarageBooking>?
)

// ── Confirm / decline action response ─────────────────────────────────────
data class BookingActionApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: GarageBooking?
)