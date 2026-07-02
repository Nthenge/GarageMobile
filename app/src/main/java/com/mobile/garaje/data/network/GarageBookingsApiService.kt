package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface GarageBookingsApiService {

    /**
     * PLACEHOLDER ENDPOINT — backend route not yet confirmed.
     * Expected to return all bookings for the authenticated garage admin's
     * garage, scoped via JWT. Update path once the real endpoint exists.
     */
    @GET("garage/bookings")
    suspend fun getBookings(
        @Header("Authorization") token: String
    ): Response<GarageBookingsApiResponse>

    /**
     * PLACEHOLDER — confirm a pending booking.
     */
    @PUT("garage/bookings/{bookingId}/confirm")
    suspend fun confirmBooking(
        @Header("Authorization") token: String,
        @Path("bookingId") bookingId: Long
    ): Response<BookingActionApiResponse>

    /**
     * PLACEHOLDER — decline a pending booking.
     */
    @PUT("garage/bookings/{bookingId}/decline")
    suspend fun declineBooking(
        @Header("Authorization") token: String,
        @Path("bookingId") bookingId: Long
    ): Response<BookingActionApiResponse>
}