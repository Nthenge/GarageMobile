package com.mobile.garaje.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.IssueResponse
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.SupportViewModel

private val PageBg = Color(0xFFF5F6FA)

private val HeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0ECF8), Color(0xFFF0F8EE), Color(0xFFFFF8EE))
)
@Composable
fun ChatConversationsScreen(
    onBack           : () -> Unit = {},
    onOpenChat       : (issueId: Long, isReadOnly: Boolean) -> Unit = { _, _ -> },
    onReportIncident : () -> Unit = {},
    viewModel        : SupportViewModel = viewModel()
) {
    val issues by viewModel.myIssues.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {

        // ── Top bar — status bar inset here ───────────────────────────────────
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = GarageTextDark
                    )
                }
                Text(
                    "Chat with us",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GarageTextDark,
                    modifier = Modifier.weight(1f)
                )
                // Message count badge
                if (issues.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GarageBlueLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${issues.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GarageBlue
                        )
                    }
                }
            }
        }

        if (issues.isEmpty()) {
            EmptyConversationsState(onReportIncident = onReportIncident)
        } else {
            LazyColumn(
                modifier       = Modifier
                    .weight(1f)
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section header
                item {
                    Text(
                        "Your conversations",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = GarageTextMuted,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                items(issues.filter { it.id != null }, key = { it.id!! }) { issue ->
                    IssueConversationRow(
                        issue   = issue,
                        onClick = {
                            val isReadOnly = issue.status == "RESOLVED" ||
                                    issue.status == "CLOSED"
                            onOpenChat(issue.id!!, isReadOnly)
                        }
                    )
                }

                // New conversation CTA at the bottom
                item {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onReportIncident,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("New report / conversation", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyConversationsState(onReportIncident: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Illustration circle
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(GarageBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = GarageBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "No conversations yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = GarageTextDark
            )
            Text(
                "Report an incident to start a conversation with our support team. We typically respond within a few hours.",
                fontSize = 13.sp,
                color = GarageTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onReportIncident,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GarageBlue)
            ) {
                Icon(
                    Icons.Outlined.Flag,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Report an incident",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Single conversation row ───────────────────────────────────────────────────

@Composable
private fun IssueConversationRow(issue: IssueResponse, onClick: () -> Unit) {
    val isResolved = issue.status == "RESOLVED" || issue.status == "CLOSED"
    val isActive   = issue.status == "IN_PROGRESS"

    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape           = RoundedCornerShape(14.dp),
        color           = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isResolved -> Color(0xFFF0F0F0)
                            isActive   -> GarageTealLight
                            else       -> GarageBlueLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isResolved -> Icons.Outlined.CheckCircle
                        isActive   -> Icons.Outlined.SupportAgent
                        else       -> Icons.Outlined.ChatBubbleOutline
                    },
                    contentDescription = null,
                    tint = when {
                        isResolved -> GarageTextMuted
                        isActive   -> GarageTealDark
                        else       -> GarageBlue
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            // Text content
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    issue.issueTypeName ?: "Support Issue",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GarageTextDark,
                    maxLines = 1
                )
                Text(
                    "Ticket #${issue.ticketNumber ?: "–"}",
                    fontSize = 12.sp,
                    color = GarageTextMuted
                )
                Text(
                    issue.createdAt?.take(10) ?: "–",
                    fontSize = 11.sp,
                    color = GarageTextMuted
                )
            }

            // Right side
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(status = issue.status ?: "OPEN")
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = GarageTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Status badge ──────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(status: String) {
    val (bg, fg, label) = when (status.uppercase()) {
        "OPEN"        -> Triple(GarageBlueLight, GarageBlue, "Open")
        "IN_PROGRESS" -> Triple(GarageTealLight, GarageTealDark, "In progress")
        "RESOLVED"    -> Triple(Color(0xFFEEEEEE), GarageTextMuted, "Resolved")
        "CLOSED"      -> Triple(Color(0xFFEEEEEE), GarageTextMuted, "Closed")
        else          -> Triple(GarageBlueLight, GarageBlue, status)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}