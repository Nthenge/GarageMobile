package com.mobile.garaje.data.model

// ── Garage profile summary shown in the top bar ───────────────────────────────
data class GarageHomeSummary(
    val garageName: String?,
    val garageInitials: String?
)

// ── Alert banner (e.g. bay running over time) ─────────────────────────────────
data class GarageAlert(
    val id: Long?,
    val title: String?,
    val subtitle: String?,
    val severity: String?   // "HIGH" | "MEDIUM" | "LOW"
)

// ── Pending action hero card (e.g. "3 bookings need confirmation") ───────────
data class PendingActionSummary(
    val pendingCount: Int?,
    val title: String?,
    val subtitle: String?
)

// ── Today's booking row ────────────────────────────────────────────────────
data class TodayBooking(
    val id: Long?,
    val time: String?,            // e.g. "9:00"
    val period: String?,          // "AM" | "PM"
    val serviceName: String?,
    val customerName: String?,
    val vehiclePlate: String?,
    val mechanicName: String?,    // null if unassigned
    val status: String?,          // "CONFIRMED" | "PENDING" | "COMPLETED"
    val categoryIcon: String?     // backend-provided icon key, e.g. "oil", "engine"
)

// ── Team member live status ────────────────────────────────────────────────
data class TeamMemberStatus(
    val id: Long?,
    val name: String?,
    val initials: String?,
    val statusText: String?,      // e.g. "In Bay 2 · Engine diagnostic" or "Free until 1:00 PM"
    val isActive: Boolean?
)

// ── Full home screen response, assembled or single endpoint ──────────────────
data class GarageHomeResponse(
    val summary: GarageHomeSummary?,
    val alerts: List<GarageAlert>?,
    val pendingAction: PendingActionSummary?,
    val todayBookings: List<TodayBooking>?,
    val teamStatus: List<TeamMemberStatus>?
)

// ── Envelope wrapper — same pattern as the rest of the app ────────────────────
data class GarageHomeApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: GarageHomeResponse?
)