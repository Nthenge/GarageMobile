package com.mobile.garaje.data.dto

data class ChatMessageListApiResponse(
    val success: Boolean?,
    val message: String?,
    val path: String?,
    val timestamp: String?,
    val data: List<ChatMessageDto>?
)