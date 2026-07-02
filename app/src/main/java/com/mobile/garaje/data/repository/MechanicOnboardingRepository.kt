package com.mobile.garaje.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MechanicOnboardingRepository {

    private val api = RetrofitClient.mechanicApi
    private val gson = Gson()

    private fun uriToPart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val bytes = contentResolver.openInputStream(uri)?.readBytes() ?: return null
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            // Use the last path segment as filename fallback
            val filename = uri.lastPathSegment ?: partName
            MultipartBody.Part.createFormData(partName, filename, requestBody)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateMechanicProfile(
        context: Context,
        token: String,
        personal: MechanicPersonalData,
        skills: MechanicSkillsData,
        documents: MechanicDocumentsData,
        onProgress: (Int) -> Unit
    ): Result<MechanicUpdateResponse> {
        return try {

            onProgress(10)

            val dto = mapOf(
                "alternativePhone"       to personal.alternativePhone,
                "physicalAddress"        to personal.physicalAddress,
                "nationalIdNumber"       to personal.nationalIdNumber,
                "emergencyContactName"   to personal.emergencyContactName,
                "emergencyContactNumber" to personal.emergencyContactNumber,
                "areasofSpecialization"  to skills.areasOfSpecialization,
                "vehicleBrands"          to skills.vehicleBrands,
                "yearsofExperience"      to skills.yearsOfExperience,
                "availability"           to skills.availability
            )
            val mechanicJson = gson.toJson(dto)
            val mechanicPart = mechanicJson
                .toRequestBody("application/json".toMediaTypeOrNull())

            onProgress(20)

            // Build file parts
            val profilePicPart = documents.profilePicUri?.let {
                uriToPart(context, it, "profilePic")
            }
            onProgress(40)

            val nationalIdPart = uriToPart(context, documents.nationalIDPicUri, "nationalIDPic")
                ?: return Result.failure(Exception("Failed to read National ID file"))
            onProgress(55)

            val professionalCertPart = documents.professionalCertUri?.let {
                uriToPart(context, it, "professionalCertificate")
            }
            onProgress(65)

            val anyRelevantCertPart = documents.anyRelevantCertUri?.let {
                uriToPart(context, it, "anyRelevantCertificate")
            }
            onProgress(75)

            val policeClearancePart = uriToPart(context, documents.policeClearanceUri, "policeClearanceCertificate")
                ?: return Result.failure(Exception("Failed to read Police Clearance file"))
            onProgress(90)

            val response = api.updateMechanicProfile(
                token                    = "Bearer $token",
                mechanic                 = mechanicPart,
                profilePic               = profilePicPart,
                nationalIDPic            = nationalIdPart,
                professionalCertificate  = professionalCertPart,
                anyRelevantCertificate   = anyRelevantCertPart,
                policeClearanceCertificate = policeClearancePart
            )

            onProgress(100)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Profile update failed"
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}