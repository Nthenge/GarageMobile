package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.CarOwnerCreateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CarOwnerApiService {
    @Multipart
    @POST("vehicle/create")
    suspend fun createCarOwner(
        @Header("Authorization") token: String,
        @Part("carOwner") carOwner: RequestBody,         // JSON part
        @Part profilePic: MultipartBody.Part?            // optional image
    ): Response<CarOwnerCreateResponse>
}