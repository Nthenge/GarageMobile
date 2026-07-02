package com.mobile.garaje.data.network

import com.mobile.garaje.data.dto.ChatMessageDto
import com.mobile.garaje.data.dto.ChatMessageListApiResponse
import com.mobile.garaje.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SupportApiService {

    @GET("/issue-types/car-owner")
    suspend fun getCarOwnerIssueTypes(
        @Header("Authorization") token: String
    ): Response<IssueTypeApiResponse>

    @POST("/talk-to-us")
    suspend fun submitIssue(
        @Header("Authorization") token: String,
        @Body request: IssueRequest
    ): Response<IssueApiResponse>

    @GET("/talk-to-us/sent")
    suspend fun getMySubmittedIssues(
        @Header("Authorization") token: String
    ): Response<IssueListApiResponse>

    @GET("/faqs/car-owner")
    suspend fun getCarOwnerFaqs(
        @Header("Authorization") token: String
    ): Response<FaqApiResponse>

    // FIXED: was `AuthApiResponse<List<ChatMessage>>` — AuthApiResponse isn't generic
    // (it's hardcoded to AuthResponse), and this should return DTOs, not the UI model.
    @GET("/talk-to-us/{issueId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("issueId") issueId: Long
    ): Response<ChatMessageListApiResponse>

    @GET("/request/vehicle/history")
    suspend fun getAllCarHistory(
        @Header("Authorization") token: String
    ): Response<ServiceHistoryApiResponse>
}