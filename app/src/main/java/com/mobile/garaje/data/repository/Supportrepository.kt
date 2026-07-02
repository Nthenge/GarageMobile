package com.mobile.garaje.data.repository

import com.mobile.garaje.data.dto.ChatMessageDto
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient
import com.mobile.garaje.data.network.RetrofitClient.supportApi

class SupportRepository {

    private val api = RetrofitClient.supportApi

    suspend fun getIssueTypes(token: String): Result<List<IssueTypeResponse>> {
        return try {
            val response = api.getCarOwnerIssueTypes("Bearer $token")
            if (response.isSuccessful && response.body()?.data != null)
                Result.success(response.body()!!.data!!)
            else
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load issue types"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun submitIssue(token: String, request: IssueRequest): Result<IssueResponse> {
        return try {
            val response = api.submitIssue("Bearer $token", request)
            if (response.isSuccessful && response.body()?.data != null)
                Result.success(response.body()!!.data!!)
            else
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to submit issue"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getMyIssues(token: String): Result<List<IssueResponse>> {
        return try {
            val response = api.getMySubmittedIssues("Bearer $token")
            if (response.isSuccessful && response.body()?.data != null)
                Result.success(response.body()!!.data!!)
            else
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load issues"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getFaqs(token: String): Result<List<FaqResponse>> {
        return try {
            val response = api.getCarOwnerFaqs("Bearer $token")
            if (response.isSuccessful && response.body()?.data != null)
                Result.success(response.body()!!.data!!)
            else
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load FAQs"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun getMessages(token: String, issueId: Long): Result<List<ChatMessageDto>> {
        return try {
            val response = supportApi.getMessages(token, issueId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCarHistory(token: String): Result<List<ServiceHistoryForIssueResponse>> {
        return try {
            val response = api.getAllCarHistory("Bearer $token")
            if (response.isSuccessful && response.body()?.data != null)
                Result.success(response.body()!!.data!!)
            else
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to load service history"))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}