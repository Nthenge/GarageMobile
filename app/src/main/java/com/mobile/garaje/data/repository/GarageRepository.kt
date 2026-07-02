package com.mobile.garaje.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody

class GarageRepository {

    private val api  = RetrofitClient.garageApi
    private val gson = Gson()

    // ── Fetch services ────────────────────────────────────────────────────────

    suspend fun getServices(token: String): Result<List<ServiceResponse>> {
        return try {
            val response = api.getServices("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.data ?: emptyList()
                Result.success(items)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Could not load services"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // ── Create garage — single multipart request ──────────────────────────────

    suspend fun createGarage(
        context: Context,
        token: String,
        businessName: String,
        businessEmail: String,
        phoneNumber: String,
        physicalAddress: String,
        latitude: Double?,
        longitude: Double?,
        openingTime: String,
        closingTime: String,
        operatingDays: List<String>,
        yearsInOperation: Int,
        registrationNumber: String,
        licenseNumber: String,
        professionalCertificate: String,
        serviceIds: List<Long>,
        paybillNumber: Int?,
        accountNumber: Int?,
        mpesaTill: Int?,
        businessLicenseUri: Uri?,
        professionalCertUri: Uri?,
        facilityPhotosUri: Uri?
    ): Result<GarageResponse> {
        return try {

            // ── 1. Build JSON garage part ─────────────────────────────────────
            val garageDto = CreateGarageRequest(
                businessName            = businessName,
                businessEmail           = businessEmail,
                phoneNumber             = phoneNumber,
                physicalAddress         = physicalAddress,
                businessLocation        = if (latitude != null && longitude != null)
                    LocationRequest(latitude, longitude) else null,
                openingTime             = openingTime,
                closingTime             = closingTime,
                operatingDays           = operatingDays,
                yearsInOperation        = yearsInOperation,
                registrationNumber      = registrationNumber,
                licenseNumber           = licenseNumber,
                professionalCertificate = professionalCertificate,
                services                = serviceIds,
                paybillNumber           = paybillNumber,
                accountNumber           = accountNumber,
                mpesaTill               = mpesaTill
            )

            // Must use MultipartBody.Part so Spring sees content-type: application/json
            // on the "garage" part and deserializes it into GarageRequestsDTO
            val garageJson = MultipartBody.Part.createFormData(
                name     = "garage",
                filename = null,
                body     = gson.toJson(garageDto).toRequestBody("application/json".toMediaTypeOrNull())
            )

            // ── 2. Build file parts (null = not selected, backend accepts optional) ──
            val licensePart = businessLicenseUri?.let {
                buildFilePart(context, it, "businessLicense")
            }
            val certPart = professionalCertUri?.let {
                buildFilePart(context, it, "professionalCertificate")
            }
            val photosPart = facilityPhotosUri?.let {
                buildFilePart(context, it, "facilityPhotos")
            }

            // ── 3. Fire single request ────────────────────────────────────────
            val response = api.createGarage(
                token                   = "Bearer $token",
                garage                  = garageJson,
                businessLicense         = licensePart,
                professionalCertificate = certPart,
                facilityPhotos          = photosPart
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to create garage"
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // ── Reads bytes from a content URI and wraps as a named multipart part ────

    private fun buildFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val bytes    = context.contentResolver.openInputStream(uri)?.readBytes()
            ?: throw Exception("Could not read file for: $partName")
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val fileName = uri.lastPathSegment ?: partName
        val body     = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, body)
    }
}