package com.mobile.garaje.data.model

data class GarageSummaryResponse(
    val garageName: String?,
    val garageLocation: String?,
    val activeBays: Int?,
    val totalBays: Int?,
    val nearbyMechanicsCount: Int?,
    val overrunAlerts: Int?,
    val revenueTodayKes: String?
)
data class BayStatusResponse(
    val bayNumber: Int?,
    val vehiclePlate: String?,
    val vehicleModel: String?,
    val jobType: String?,
    val mechanicName: String?,
    val mechanicInitials: String?,
    val progressPercent: Int?,
    val status: String?,
    val estimatedFinish: String?
)
data class NearbyMechanicResponse(
    val id: Long?,
    val initials: String?,
    val name: String?,
    val specialisations: String?,
    val distanceKm: Double?,
    val rating: Double?,
    val isInBay: Boolean?,
    val avatarColorType: String?
)

data class MapPinResponse(
    val label: String?,
    val type: String?,
    val offsetX: Float?,
    val offsetY: Float?
)

data class AiInsightResponse(
    val severity: String?,
    val title: String?,
    val body: String?,
    val actionLabel: String?
)

data class TodayBookingResponse(
    val id: Long?,
    val customerName: String?,
    val serviceName: String?,
    val timeSlot: String?,
    val vehiclePlate: String?,
    val status: String?
)

data class CommandCentreResponse(
    val summary: GarageSummaryResponse?,
    val bays: List<BayStatusResponse>?,
    val nearbyMechanics: List<NearbyMechanicResponse>?,
    val aiInsights: List<AiInsightResponse>?,
    val mapPins: List<MapPinResponse>?,
    val todayBookings: List<TodayBookingResponse>?
)