package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MechanicApiService {

    // Register mechanic — garageId resolved from JWT by backend
    @POST("user/garage/register/mechanic")
    suspend fun registerMechanic(
        @Header("Authorization") token: String,
        @Body request: MechanicRegisterRequest
    ): Response<MechanicRegisterApiResponse>

    // Update mechanic profile with documents
    @Multipart
    @PUT("mechanic/update")
    suspend fun updateMechanicProfile(
        @Header("Authorization") token: String,
        @Part("mechanic") mechanic: RequestBody,
        @Part profilePic: MultipartBody.Part?,
        @Part nationalIDPic: MultipartBody.Part,
        @Part professionalCertificate: MultipartBody.Part?,
        @Part anyRelevantCertificate: MultipartBody.Part?,
        @Part policeClearanceCertificate: MultipartBody.Part
    ): Response<MechanicUpdateResponse>

    // All mechanics belonging to this garage admin's garage
    @GET("garage/mechanics")
    suspend fun getGarageMechanics(
        @Header("Authorization") token: String
    ): Response<GarageMechanicsApiResponse>
}