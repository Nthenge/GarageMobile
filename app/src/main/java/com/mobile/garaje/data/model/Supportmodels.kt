package com.mobile.garaje.data.model
data class IssueTypeResponse(
    val id: Long?,
    val name: String?,
    val description: String?,
    val audience: String?
)

data class IssueTypeApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: List<IssueTypeResponse>?
)

data class RecipientSelection(
    val recipientRole: String,   // "SYSTEM_ADMIN" | "GARAGE_ADMIN" | "MECHANIC"
    val recipientId: Long? = null
)

data class IssueRequest(
    val issueTypeId: Long,
    val recipients: List<RecipientSelection>,
    val message: String,
    val serviceRequestId: Long? = null  // added
)

data class ServiceHistoryForIssueResponse(
    val id: Long?,
    val serviceName: String?,
    val categoryIcon: String?,   // ← added
    val garageName: String?,
    val date: String?,
    val dateLabel: String?,
    val status: String?,         // ← added
    val priceKes: Double?,       // ← added
    val mechanicName: String?
)

data class ServiceHistoryApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: List<ServiceHistoryForIssueResponse>?
)

data class IssueRecipientInfo(
    val recipientId: Long?,
    val recipientName: String?,
    val recipientEmail: String?,
    val recipientRole: String?,
    val emailSent: Boolean?
)

data class IssueResponse(
    val id: Long?,
    val senderId: Long?,
    val senderName: String?,
    val senderRole: String?,
    val issueTypeId: Long?,
    val issueTypeName: String?,
    val message: String?,
    val status: String?,
    val createdAt: String?,
    val resolvedAt: String?,
    val ticketNumber: String?,
    val recipients: List<IssueRecipientInfo>?
)

data class IssueApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: IssueResponse?
)

data class IssueListApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: List<IssueResponse>?
)

// ── FAQs ──────────────────────────────────────────────────────────────────────

data class FaqResponse(
    val id: Long?,
    val question: String?,
    val answer: String?,
    val audience: String?,
    val audienceCode: Int?,
    val category: String?,
    val displayOrder: Int?
)

data class FaqApiResponse(
    val success: Boolean?,
    val message: String?,
    val data: List<FaqResponse>?
)

data class ChatMessage(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val senderRole: String,
    val content: String,
    val sentAt: String,
    val isFromMe: Boolean
)

enum class MessageStatus { SENDING, SENT, DELIVERED, READ }