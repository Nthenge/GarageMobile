package com.mobile.garaje.data.model

data class MechanicOnboardingState(
    val personal: MechanicPersonalData? = null,
    val skills: MechanicSkillsData? = null,
    val documents: MechanicDocumentsData? = null
)

data class MechanicPersonalData(
    val alternativePhone: String,
    val physicalAddress: String,
    val nationalIdNumber: Int,
    val emergencyContactName: String,
    val emergencyContactNumber: String
)

data class MechanicSkillsData(
    val areasOfSpecialization: String,
    val vehicleBrands: String,
    val yearsOfExperience: String,
    val availability: String
)

data class MechanicDocumentsData(
    val profilePicUri: android.net.Uri? = null,
    val nationalIDPicUri: android.net.Uri,
    val professionalCertUri: android.net.Uri? = null,
    val anyRelevantCertUri: android.net.Uri? = null,
    val policeClearanceUri: android.net.Uri
)

data class MechanicUpdateResponse(
    val message: String?,
    val success: Boolean?,
    val data: Any? = null
)