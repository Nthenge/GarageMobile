package com.mobile.garaje.ui.screens.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.ChatMessage
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.SupportViewModel

private val PageBg        = Color(0xFFF5F6FA)
private val MyBubbleBg    = GarageBlue
private val TheirBubbleBg = Color.White

private val HeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0ECF8), Color(0xFFF0F8EE), Color(0xFFFFF8EE))
)

@Composable
fun ChatScreen(
    issueId    : Long,
    isReadOnly : Boolean = false,
    onBack     : () -> Unit = {},
    viewModel  : SupportViewModel = viewModel()
) {
    val messages  by viewModel.chatMessages.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(issueId) { viewModel.connectChat(issueId) }

    DisposableEffect(lifecycleOwner, issueId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.disconnectChat()
                Lifecycle.Event.ON_START -> viewModel.connectChat(issueId)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    // Full screen — no Scaffold, manage insets manually
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {

        // ── Top bar — status bar inset applied here ───────────────────────────
        Surface(
            color           = Color.Transparent,
            shadowElevation = 3.dp,
            modifier        = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeroGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = GarageTextDark
                    )
                }

                // Support avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GarageBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.SupportAgent,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Garaje Support",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GarageTextDark
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(GarageTeal)
                        )
                        Text("Online", fontSize = 12.sp, color = GarageTeal)
                    }
                }
            }
        }

        // ── Read-only banner ──────────────────────────────────────────────────
        if (isReadOnly) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E6))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color(0xFFD4A017),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "This issue is resolved — conversation is read-only.",
                    fontSize = 12.sp,
                    color = Color(0xFF856404)
                )
            }
        }

        // ── Messages ──────────────────────────────────────────────────────────
        LazyColumn(
            state           = listState,
            modifier        = Modifier.weight(1f),
            contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SystemMessage("Welcome to Garaje Support. How can we help you today?")
            }
            items(messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }
        }

        // ── Input bar — only when not read-only, nav bar inset applied here ──
        if (!isReadOnly) {
            Surface(
                color           = Color.White,
                shadowElevation = 8.dp,
                modifier        = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value         = inputText,
                        onValueChange = { inputText = it },
                        modifier      = Modifier.weight(1f),
                        placeholder   = {
                            Text(
                                "Type a message…",
                                fontSize = 14.sp,
                                color = GarageTextMuted
                            )
                        },
                        singleLine = false,
                        maxLines   = 4,
                        shape      = RoundedCornerShape(24.dp),
                        colors     = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = PageBg,
                            focusedContainerColor   = PageBg,
                            unfocusedBorderColor    = Color(0xFFE0E0E0),
                            focusedBorderColor      = GarageBlue,
                            cursorColor             = GarageBlue
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    // Send button
                    val canSend = inputText.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (canSend) GarageBlue else Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (canSend) {
                                    viewModel.sendChatMessage(issueId, inputText.trim())
                                    inputText = ""
                                }
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Send,
                                contentDescription = "Send",
                                tint = if (canSend) Color.White else GarageTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Still consume nav bar space when read-only
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}

// ── Chat bubble ───────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(message: ChatMessage) {
    val timeLabel = remember(message.sentAt) { formatSentAt(message.sentAt) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Their avatar
        if (!message.isFromMe) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(GarageBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SupportAgent,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 270.dp)
        ) {
            if (!message.isFromMe) {
                Text(
                    text = message.senderName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GarageBlue,
                    modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (message.isFromMe) 18.dp else 4.dp,
                            bottomEnd   = if (message.isFromMe) 4.dp else 18.dp
                        )
                    )
                    .background(if (message.isFromMe) MyBubbleBg else TheirBubbleBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text       = message.content,
                    fontSize   = 14.sp,
                    color      = if (message.isFromMe) Color.White else GarageTextDark,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(3.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(timeLabel, fontSize = 10.sp, color = GarageTextMuted)
                if (message.isFromMe) {
                    Icon(
                        Icons.Outlined.Done,
                        contentDescription = null,
                        tint = GarageTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        if (message.isFromMe) Spacer(Modifier.width(8.dp))
    }
}

// ── System message divider ────────────────────────────────────────────────────

@Composable
private fun SystemMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE8EDF2))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(text, fontSize = 11.5.sp, color = GarageTextMuted)
        }
    }
}

private fun formatSentAt(sentAt: String): String =
    if (sentAt.length >= 16) sentAt.substring(11, 16) else sentAt