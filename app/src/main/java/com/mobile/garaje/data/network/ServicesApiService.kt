package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.CategoriesApiResponse
import com.mobile.garaje.data.model.GarageServicesApiResponse
import com.mobile.garaje.data.model.ServicesApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ServicesApiService {

    @GET("service/all")
    suspend fun getAllServices(
        @Header("Authorization") token: String
    ): Response<ServicesApiResponse>

    @GET("category/all")
    suspend fun getAllCategories(
        @Header("Authorization") token: String
    ): Response<CategoriesApiResponse>

    @GET("garage/services")
    suspend fun getMyGarageServices(
        @Header("Authorization") token: String
    ): Response<GarageServicesApiResponse>

    @POST("garage/add-service/{serviceId}")
    suspend fun addService(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long
    ): Response<GarageServicesApiResponse>

    @DELETE("garage/remove-service/{serviceId}")
    suspend fun removeService(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long
    ): Response<GarageServicesApiResponse>
}