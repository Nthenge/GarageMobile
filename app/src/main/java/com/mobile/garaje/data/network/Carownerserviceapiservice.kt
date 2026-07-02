package com.mobile.garaje.data.network

import com.mobile.garaje.data.model.GaragesOfferingServiceApiResponse
import com.mobile.garaje.data.model.ServiceDetailApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CarOwnerServiceApiService {

    // ── PLACEHOLDER — confirm exact path with backend ──────────────────────────
    @GET("service/{serviceId}")
    suspend fun getServiceDetail(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long
    ): Response<ServiceDetailApiResponse>

    // ── PLACEHOLDER — confirm exact path with backend ──────────────────────────
    // lat/lng sent so backend can compute distanceKm per garage
    @GET("service/{serviceId}/garages")
    suspend fun getGaragesOfferingService(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: Long,
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): Response<GaragesOfferingServiceApiResponse>
}