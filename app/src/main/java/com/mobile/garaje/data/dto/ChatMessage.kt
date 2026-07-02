package com.mobile.garaje.data.dto

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// ── Wire DTO — mirrors the backend's ChatMessageResponse exactly ──────────────
data class ChatMessageDto(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val senderRole: String,
    val content: String,
    val sentAt: String
)

private data class ChatSendPayload(val content: String)


class ChatSocketManager(private val wsUrl: String) {

    private var session: StompSession? = null
    private val gson = Gson()
    private val stompClient = StompClient(OkHttpWebSocketClient())

    suspend fun connect(token: String) {
        val encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name())
        val urlWithToken = "$wsUrl?token=$encodedToken"
        session = stompClient.connect(urlWithToken)
    }

    fun isConnected(): Boolean = session != null

    suspend fun subscribeToIssue(issueId: Long): Flow<ChatMessageDto> {
        val activeSession = session ?: error("ChatSocketManager: call connect() before subscribing")
        return activeSession.subscribeText("/topic/issue/$issueId")
            .map { gson.fromJson(it, ChatMessageDto::class.java) }
    }

    suspend fun sendMessage(issueId: Long, content: String) {
        val activeSession = session ?: error("ChatSocketManager: call connect() before sending")
        activeSession.sendText("/app/chat.send/$issueId", gson.toJson(ChatSendPayload(content)))
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
    }
}