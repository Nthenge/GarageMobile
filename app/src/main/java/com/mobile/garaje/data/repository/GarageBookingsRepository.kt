package com.mobile.garaje.data.repository

import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient

class GarageBookingsRepository {

    private val api = RetrofitClient.garageBookingsApi

    suspend fun getBookings(token: String): Result<List<GarageBooking>> = runCatching {
        val response = api.getBookings("Bearer $token")
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.data ?: emptyList()
        } else {
            error(response.errorBody()?.string() ?: "Failed to load bookings")
        }
    }

    suspend fun confirmBooking(token: String, bookingId: Long): Result<GarageBooking> = runCatching {
        val response = api.confirmBooking("Bearer $token", bookingId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.data
                ?: error(response.body()!!.message ?: "Failed to confirm booking")
        } else {
            error(response.errorBody()?.string() ?: "Failed to confirm booking")
        }
    }

    suspend fun declineBooking(token: String, bookingId: Long): Result<GarageBooking> = runCatching {
        val response = api.declineBooking("Bearer $token", bookingId)
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.data
                ?: error(response.body()!!.message ?: "Failed to decline booking")
        } else {
            error(response.errorBody()?.string() ?: "Failed to decline booking")
        }
    }

    /**
     * PLACEHOLDER DATA — used until the backend endpoints above are ready.
     * Mirrors the exact shape the real API is expected to return, grouped
     * across Today / Tomorrow / Yesterday to match the approved mockup.
     */
    fun getPlaceholderBookings(): List<GarageBooking> {
        return listOf(
            GarageBooking(
                id                = 1L,
                date              = "2026-05-22",
                dateLabel         = "Today",
                time              = "9:00",
                period            = "AM",
                serviceName       = "Oil change",
                customerName      = "Sarah Kamau",
                vehiclePlate      = "KCA 989P",
                mechanicId        = 101L,
                mechanicName      = "James Mutua",
                mechanicInitials  = "JM",
                status            = "CONFIRMED",
                categoryIcon      = "oil"
            ),
            GarageBooking(
                id                = 2L,
                date              = "2026-05-22",
                dateLabel         = "Today",
                time              = "11:00",
                period            = "AM",
                serviceName       = "Engine diagnostic",
                customerName      = "David Ochieng",
                vehiclePlate      = "KBZ 445T",
                mechanicId        = null,
                mechanicName      = null,
                mechanicInitials  = null,
                status            = "PENDING",
                categoryIcon      = "engine"
            ),
            GarageBooking(
                id                = 3L,
                date              = "2026-05-22",
                dateLabel         = "Today",
                time              = "2:30",
                period            = "PM",
                serviceName       = "Brake pad replacement",
                customerName      = "Kevin Otieno",
                vehiclePlate      = "KDA 221L",
                mechanicId        = null,
                mechanicName      = null,
                mechanicInitials  = null,
                status            = "PENDING",
                categoryIcon      = "brake"
            ),
            GarageBooking(
                id                = 4L,
                date              = "2026-05-23",
                dateLabel         = "Tomorrow",
                time              = "10:00",
                period            = "AM",
                serviceName       = "Tyre rotation",
                customerName      = "Grace Wairimu",
                vehiclePlate      = "KBN 552C",
                mechanicId        = 102L,
                mechanicName      = "Aisha Wanjiru",
                mechanicInitials  = "AW",
                status            = "CONFIRMED",
                categoryIcon      = "tyre"
            ),
            GarageBooking(
                id                = 5L,
                date              = "2026-05-21",
                dateLabel         = "Yesterday",
                time              = "3:00",
                period            = "PM",
                serviceName       = "Battery replacement",
                customerName      = "Peter Njoroge",
                vehiclePlate      = "KCB 781D",
                mechanicId        = 101L,
                mechanicName      = "James Mutua",
                mechanicInitials  = "JM",
                status            = "COMPLETED",
                categoryIcon      = "battery"
            )
        )
    }
}