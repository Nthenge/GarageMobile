package com.mobile.garaje.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.garaje.data.dto.ChatMessageDto
import com.mobile.garaje.data.dto.ChatSocketManager
import com.mobile.garaje.data.model.*
import com.mobile.garaje.data.network.RetrofitClient
import com.mobile.garaje.data.repository.SupportRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI states ─────────────────────────────────────────────────────────────────

sealed class SupportHomeState {
    object Loading : SupportHomeState()
    data class Success(
        val activeCount: Int,
        val resolvedCount: Int
    ) : SupportHomeState()
    data class Error(val message: String) : SupportHomeState()
}

sealed class IssueTypesState {
    object Loading : IssueTypesState()
    data class Success(val types: List<IssueTypeResponse>) : IssueTypesState()
    data class Error(val message: String) : IssueTypesState()
}

sealed class SubmitIssueState {
    object Idle    : SubmitIssueState()
    object Loading : SubmitIssueState()
    data class Success(val ticketNumber: String) : SubmitIssueState()
    data class Error(val message: String) : SubmitIssueState()
}

sealed class FaqState {
    object Loading : FaqState()
    data class Success(val faqs: List<FaqResponse>) : FaqState()
    data class Error(val message: String) : FaqState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class SupportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SupportRepository()
    private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private fun token() = "Bearer ${prefs.getString("token", "") ?: ""}"
    private fun userId() = prefs.getString("userId", "me") ?: "me"
    private fun userName() = prefs.getString("firstname", "Me") ?: "Me"

    private val _homeState = MutableStateFlow<SupportHomeState>(SupportHomeState.Loading)
    val homeState: StateFlow<SupportHomeState> = _homeState

    private val _issueTypesState = MutableStateFlow<IssueTypesState>(IssueTypesState.Loading)
    val issueTypesState: StateFlow<IssueTypesState> = _issueTypesState

    private val _submitState = MutableStateFlow<SubmitIssueState>(SubmitIssueState.Idle)
    val submitState: StateFlow<SubmitIssueState> = _submitState

    private val _faqState = MutableStateFlow<FaqState>(FaqState.Loading)
    val faqState: StateFlow<FaqState> = _faqState

    // ── Chat messages (local state until WS backend is ready) ─────────────────
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    init {
        loadHome()
        loadIssueTypes()
        loadFaqs()
    }

    private val _myIssues = MutableStateFlow<List<IssueResponse>>(emptyList())
    val myIssues: StateFlow<List<IssueResponse>> = _myIssues

    fun loadHome() {
        viewModelScope.launch {
            _homeState.value = SupportHomeState.Loading
            val result = repository.getMyIssues(prefs.getString("token", "") ?: "")
            _homeState.value = if (result.isSuccess) {
                val issues = result.getOrNull() ?: emptyList()
                _myIssues.value = issues   // NEW — keep the full list, not just derived counts
                SupportHomeState.Success(
                    activeCount   = issues.count { it.status == "OPEN" || it.status == "IN_PROGRESS" },
                    resolvedCount = issues.count { it.status == "RESOLVED" }
                )
            } else {
                SupportHomeState.Success(activeCount = 0, resolvedCount = 0)
            }
        }
    }

    fun loadIssueTypes() {
        viewModelScope.launch {
            _issueTypesState.value = IssueTypesState.Loading
            val result = repository.getIssueTypes(prefs.getString("token", "") ?: "")
            _issueTypesState.value = if (result.isSuccess) {
                IssueTypesState.Success(result.getOrNull() ?: emptyList())
            } else {
                IssueTypesState.Error(result.exceptionOrNull()?.message ?: "Failed to load types")
            }
        }
    }

    fun submitGeneralIssue(issueTypeId: Long, message: String) {
        viewModelScope.launch {
            _submitState.value = SubmitIssueState.Loading
            val request = IssueRequest(
                issueTypeId = issueTypeId,
                recipients  = listOf(RecipientSelection(recipientRole = "SYSTEM_ADMIN")),
                message     = message
            )
            val result = repository.submitIssue(prefs.getString("token", "") ?: "", request)
            _submitState.value = if (result.isSuccess) {
                loadHome()
                SubmitIssueState.Success(result.getOrNull()?.ticketNumber ?: "")
            } else {
                SubmitIssueState.Error(result.exceptionOrNull()?.message ?: "Submission failed")
            }
        }
    }

    fun submitServiceIssue(issueTypeId: Long, serviceRequestId: Long, message: String) {
        viewModelScope.launch {
            _submitState.value = SubmitIssueState.Loading
            val request = IssueRequest(
                issueTypeId      = issueTypeId,
                recipients       = emptyList(),  // backend auto-resolves from serviceRequestId
                message          = message,
                serviceRequestId = serviceRequestId
            )
            val result = repository.submitIssue(prefs.getString("token", "") ?: "", request)
            _submitState.value = if (result.isSuccess) {
                loadHome()
                SubmitIssueState.Success(result.getOrNull()?.ticketNumber ?: "")
            } else {
                SubmitIssueState.Error(result.exceptionOrNull()?.message ?: "Submission failed")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = SubmitIssueState.Idle
    }

    fun loadFaqs() {
        viewModelScope.launch {
            _faqState.value = FaqState.Loading
            val result = repository.getFaqs(prefs.getString("token", "") ?: "")
            _faqState.value = if (result.isSuccess) {
                FaqState.Success(result.getOrNull() ?: emptyList())
            } else {
                FaqState.Error(result.exceptionOrNull()?.message ?: "Failed to load FAQs")
            }
        }
    }

    // ── Chat ──────────────────────────────────────────────────────────────────
    // WebSocket integration point — currently stores messages locally.
    // When backend WS is ready, replace sendChatMessage() with a STOMP publish
    // and subscribe to the topic to receive incoming messages.

    private val chatSocketManager = ChatSocketManager(
        wsUrl = RetrofitClient.WS_BASE_URL
    )

    private var chatCollectJob: Job? = null

    private val _chatConnectionError = MutableStateFlow<String?>(null)
    val chatConnectionError: StateFlow<String?> = _chatConnectionError

    fun connectChat(issueId: Long) {
        viewModelScope.launch {
            try {
                if (!chatSocketManager.isConnected()) {
                    chatSocketManager.connect(prefs.getString("token", "") ?: "")
                }

                loadChatHistory(issueId)

                chatCollectJob?.cancel()
                chatCollectJob = launch {
                    chatSocketManager.subscribeToIssue(issueId).collect { dto ->
                        val incoming = dto.toUiMessage()
                        if (_chatMessages.value.none { it.id == incoming.id }) {
                            _chatMessages.value = _chatMessages.value + incoming
                        }
                    }
                }
            } catch (e: Exception) {
                _chatConnectionError.value = e.message ?: "Failed to connect to chat"
            }
        }
    }

    private suspend fun loadChatHistory(issueId: Long) {
        val result = repository.getMessages(prefs.getString("token", "") ?: "", issueId)
        if (result.isSuccess) {
            _chatMessages.value = (result.getOrNull() ?: emptyList()).map { it.toUiMessage() }
        } else {
            _chatConnectionError.value = "Failed to load message history"
        }
    }

    fun sendChatMessage(issueId: Long, content: String) {
        viewModelScope.launch {
            try {
                chatSocketManager.sendMessage(issueId, content)
                // No optimistic insert — the server echoes the send back over the
                // topic (see ChatController.sendMessage on the backend), and it'll
                // arrive through the collector above within milliseconds.
            } catch (e: Exception) {
                _chatConnectionError.value = "Message failed to send"
            }
        }
    }

    fun disconnectChat() {
        chatCollectJob?.cancel()
        viewModelScope.launch { chatSocketManager.disconnect() }
    }

    override fun onCleared() {
        super.onCleared()
        chatCollectJob?.cancel()
    }

    private fun ChatMessageDto.toUiMessage(): ChatMessage {
        val myId = prefs.getString("userId", null)?.toLongOrNull()
        return ChatMessage(
            id = id,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            content = content,
            sentAt = sentAt,
            isFromMe = myId != null && myId == senderId
        )
    }

    sealed class ServiceHistoryState {
        object Loading : ServiceHistoryState()
        data class Success(val items: List<ServiceHistoryForIssueResponse>) : ServiceHistoryState()
        data class Error(val message: String) : ServiceHistoryState()
    }

// Add to SupportViewModel:

    private val _serviceHistoryState =
        MutableStateFlow<ServiceHistoryState>(ServiceHistoryState.Loading)
    val serviceHistoryState: StateFlow<ServiceHistoryState> = _serviceHistoryState

    fun loadServiceHistory() {
        viewModelScope.launch {
            _serviceHistoryState.value = ServiceHistoryState.Loading
            val result = repository.getAllCarHistory(prefs.getString("token", "") ?: "")
            _serviceHistoryState.value = if (result.isSuccess) {
                ServiceHistoryState.Success(result.getOrNull() ?: emptyList())
            } else {
                ServiceHistoryState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load service history")
            }
        }
    }
}