package com.mobile.garaje.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.mobile.garaje.data.model.CarOwnerDetailsData
import com.mobile.garaje.data.model.CarOwnerCreateResponse
import com.mobile.garaje.data.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CarOwnerOnboardingRepository {

    private val api  = RetrofitClient.carOwnerApi
    private val gson = Gson()

    private fun uriToPart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/*"
            val bytes = contentResolver.openInputStream(uri)?.readBytes() ?: return null
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filename = uri.lastPathSegment ?: partName
            MultipartBody.Part.createFormData(partName, filename, requestBody)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createCarOwner(
        context: Context,
        token: String,
        details: CarOwnerDetailsData,
        profilePicUri: Uri?,
        onProgress: (Int) -> Unit
    ): Result<CarOwnerCreateResponse> {
        return try {
            onProgress(10)

            // Build JSON part — matches CarOwnerRequestsDTO on backend
            val dto = mapOf(
                "make"           to details.make,
                "model"          to details.model,
                "year"           to details.year,
                "licensePlate"   to details.licensePlate,
                "engineType"     to details.engineType,
                "engineCapacity" to details.engineCapacity,
                "color"          to details.color,
                "transmission"   to details.transmission
            )
            val carOwnerPart = gson.toJson(dto)
                .toRequestBody("application/json".toMediaTypeOrNull())

            onProgress(40)

            // Optional profile picture
            val profilePicPart = profilePicUri?.let {
                uriToPart(context, it, "profilePic")
            }

            onProgress(70)

            val response = api.createCarOwner(
                token      = "Bearer $token",
                carOwner   = carOwnerPart,
                profilePic = profilePicPart
            )

            onProgress(100)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to create profile"
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}