package com.mobile.garaje.data.repository
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient

class GarageHomeRepository {

    private val api = RetrofitClient.garageHomeApi

    suspend fun getGarageHome(token: String): Result<GarageHomeResponse> = runCatching {
        val response = api.getGarageHome("Bearer $token")
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.data
                ?: error(response.body()!!.message ?: "Failed to load home data")
        } else {
            error(response.errorBody()?.string() ?: "Failed to load home data")
        }
    }

    /**
     * PLACEHOLDER DATA — used until the backend endpoint above is ready.
     * Mirrors the exact shape the real API is expected to return so swapping
     * in the live call later requires no changes to the ViewModel or screen.
     */
    fun getPlaceholderHome(): GarageHomeResponse {
        return GarageHomeResponse(
            summary = GarageHomeSummary(
                garageName     = "Westlands AutoFix",
                garageInitials = "WA"
            ),
            alerts = listOf(
                GarageAlert(
                    id       = 1L,
                    title    = "Bay 1 running over time",
                    subtitle = "Reassign or notify the customer",
                    severity = "HIGH"
                )
            ),
            pendingAction = PendingActionSummary(
                pendingCount = 3,
                title        = "3 bookings need confirmation",
                subtitle     = "Tap to review and assign mechanics"
            ),
            todayBookings = listOf(
                TodayBooking(
                    id           = 1L,
                    time         = "9:00",
                    period       = "AM",
                    serviceName  = "Oil change",
                    customerName = "Sarah Kamau",
                    vehiclePlate = "KCA 989P",
                    mechanicName = "James Mutua",
                    status       = "CONFIRMED",
                    categoryIcon = "oil"
                ),
                TodayBooking(
                    id           = 2L,
                    time         = "11:00",
                    period       = "AM",
                    serviceName  = "Engine diagnostic",
                    customerName = "David Ochieng",
                    vehiclePlate = "KBZ 445T",
                    mechanicName = null,
                    status       = "PENDING",
                    categoryIcon = "engine"
                ),
                TodayBooking(
                    id           = 3L,
                    time         = "2:30",
                    period       = "PM",
                    serviceName  = "Brake pad replacement",
                    customerName = "Unassigned",
                    vehiclePlate = null,
                    mechanicName = null,
                    status       = "PENDING",
                    categoryIcon = "brake"
                )
            ),
            teamStatus = listOf(
                TeamMemberStatus(
                    id         = 1L,
                    name       = "James Mutua",
                    initials   = "JM",
                    statusText = "In Bay 2 · Engine diagnostic",
                    isActive   = true
                ),
                TeamMemberStatus(
                    id         = 2L,
                    name       = "Aisha Wanjiru",
                    initials   = "AW",
                    statusText = "Free until 1:00 PM",
                    isActive   = false
                )
            )
        )
    }
}