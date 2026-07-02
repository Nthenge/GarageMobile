package com.mobile.garaje.data.model

// ── Active job (list item) ────────────────────────────────────────────────────
data class ActiveJobResponse(
    val id: Long?,
    val serviceName: String?,
    val garageName: String?,
    val bayNumber: Int?,
    val estimatedMinutes: Double?
)

// ── Popular service ───────────────────────────────────────────────────────────
data class PopularServiceResponse(
    val id: Long?,
    val serviceName: String?,
    val categoryName: String?,
    val categoryIcon: String?,
    val garageCount: Int?,
    val priceFrom: Double?
)

// ── Upcoming booking ──────────────────────────────────────────────────────────
data class UpcomingBookingResponse(
    val id: Long?,
    val serviceName: String?,
    val categoryIcon: String?,
    val garageName: String?,
    val dateLabel: String?,
    val time: String?,
    val status: String?
)

// ── My car (home card)Carownersettingsscreen ────────────────────────────────────────────────────────
data class MyCarHomeResponse(
    val id: Long?,
    val model: String?,
    val plate: String?,
    val fuelType: String?,
    val year: String?
)

// ── Root home response ────────────────────────────────────────────────────────
data class CarOwnerHomeResponse(
    val firstName: String?,
    val initials: String?,
    val activeJobs: List<ActiveJobResponse>?,
    val popularServices: List<PopularServiceResponse>?,
    val upcomingBookings: List<UpcomingBookingResponse>?,
    val myCars: List<MyCarHomeResponse>?
)

data class CarOwnerHomeApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: CarOwnerHomeResponse?
)