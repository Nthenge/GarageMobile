package com.mobile.garaje.data.model

data class CreateGarageRequest(
    val businessName: String,
    val businessEmail: String,
    val phoneNumber: String,
    val physicalAddress: String,
    val businessLocation: LocationRequest?,
    val openingTime: String,
    val closingTime: String,
    val operatingDays: List<String>,
    val yearsInOperation: Int,
    val registrationNumber: String,
    val licenseNumber: String,
    val professionalCertificate: String,
    val services: List<Long>,
    val paybillNumber: Int?,
    val accountNumber: Int?,
    val mpesaTill: Int?
)

data class LocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class GarageResponse(
    val id: Long?,
    val garageId: Long?,
    val businessName: String?,
    val message: String?,
    val success: Boolean?
)