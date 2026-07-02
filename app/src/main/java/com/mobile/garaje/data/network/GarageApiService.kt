package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.GarageResponse
import com.mobile.garaje.data.model.ServicesApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface GarageApiService {

    @Multipart
    @POST("garage/create")
    suspend fun createGarage(
        @Header("Authorization") token: String,
        @Part garage: MultipartBody.Part,                   // JSON part, name="garage", content-type=application/json
        @Part businessLicense: MultipartBody.Part?,
        @Part professionalCertificate: MultipartBody.Part?,
        @Part facilityPhotos: MultipartBody.Part?
    ): Response<GarageResponse>

    @GET("service/all")
    suspend fun getServices(
        @Header("Authorization") token: String
    ): Response<ServicesApiResponse>
}
