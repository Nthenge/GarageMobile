package com.mobile.garaje.data.model
// ── Service detail ────────────────────────────────────────────────────────────

data class ServiceDetailResponse(
    val id: Long?,
    val serviceName: String?,
    val description: String?,
    val categoryName: String?,
    val categoryIcon: String?,
    val avgPrice: Double?,
    val avgDuration: Double?
)

data class ServiceDetailApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: ServiceDetailResponse?
)

// ── Garage offering this service ──────────────────────────────────────────────

data class GarageOfferingResponse(
    val garageId: Long?,
    val businessName: String?,
    val physicalAddress: String?,
    val distanceKm: Double?,        // backend-calculated from car owner's lat/lng
    val rating: Double?,
    val reviewCount: Int?,
    val price: Double?,             // this garage's price for the specific service
    val isOpenNow: Boolean?,
    val openingTime: String?,
    val closingTime: String?
)

data class GaragesOfferingServiceApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<GarageOfferingResponse>?
)