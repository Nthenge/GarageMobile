package com.mobile.garaje.ui.screens.utils

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.ServiceHistoryForIssueResponse
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.*

private val PageBg      = Color(0xFFF5F6FA)
private val FieldBg     = Color.White
private val FieldBorder = Color(0xFFEEEEEE)

private enum class ReportPath { NONE, GENERAL, SERVICE }

@Composable
fun ReportIncidentScreen(
    onBack    : () -> Unit = {},
    onSuccess : () -> Unit = {},
    viewModel : SupportViewModel = viewModel()
) {
    val context          = LocalContext.current
    val issueTypesState  by viewModel.issueTypesState.collectAsStateWithLifecycle()
    val submitState      by viewModel.submitState.collectAsStateWithLifecycle()
    val historyState     by viewModel.serviceHistoryState.collectAsStateWithLifecycle()

    var selectedPath     by remember { mutableStateOf(ReportPath.NONE) }
    var selectedTypeId   by remember { mutableStateOf<Long?>(null) }
    var selectedService  by remember { mutableStateOf<ServiceHistoryForIssueResponse?>(null) }
    var message          by remember { mutableStateOf("") }
    var typeError        by remember { mutableStateOf(false) }
    var serviceError     by remember { mutableStateOf(false) }
    var messageError     by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPath) {
        if (selectedPath == ReportPath.SERVICE) {
            viewModel.loadServiceHistory()
        }
        // reset form state whenever path switches
        selectedTypeId  = null
        selectedService = null
        message         = ""
        typeError       = false
        serviceError    = false
        messageError    = false
    }

    LaunchedEffect(submitState) {
        when (submitState) {
            is SubmitIssueState.Success -> {
                val ticket = (submitState as SubmitIssueState.Success).ticketNumber
                Toast.makeText(context, "Submitted! Ticket: $ticket", Toast.LENGTH_LONG).show()
                viewModel.resetSubmitState()
                onSuccess()
            }
            is SubmitIssueState.Error -> {
                Toast.makeText(
                    context,
                    (submitState as SubmitIssueState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetSubmitState()
            }
            else -> {}
        }
    }

    val isLoading = submitState is SubmitIssueState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ArrowBack, null,
                    tint = GarageTextDark, modifier = Modifier.size(18.dp))
            }
            Text("Report an incident", fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, color = GarageTextDark)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Text("Report an incident", fontSize = 22.sp,
                fontWeight = FontWeight.Bold, color = GarageTextDark)

            // ── Path toggle ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(0.5.dp, FieldBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PathToggleButton(
                    label      = "Service issue",
                    icon       = Icons.Outlined.Build,
                    isSelected = selectedPath == ReportPath.SERVICE,
                    modifier   = Modifier.weight(1f),
                    onClick    = { selectedPath = ReportPath.SERVICE }
                )
                PathToggleButton(
                    label      = "General issue",
                    icon       = Icons.Outlined.SupportAgent,
                    isSelected = selectedPath == ReportPath.GENERAL,
                    modifier   = Modifier.weight(1f),
                    onClick    = { selectedPath = ReportPath.GENERAL }
                )
            }

            // ── Animated form body ────────────────────────────────────────────
            AnimatedVisibility(
                visible = selectedPath != ReportPath.NONE,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                    // ── Service picker (service path only) ────────────────────
                    AnimatedVisibility(
                        visible = selectedPath == ReportPath.SERVICE,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SectionLabel("SELECT SERVICE *")
                            Text(
                                "Choose the completed service this issue is about.",
                                fontSize = 12.sp, color = GarageTextMuted
                            )
                            when (val s = historyState) {
                                is SupportViewModel.ServiceHistoryState.Loading -> {
                                    Box(
                                        Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = GarageOrange,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                                is SupportViewModel.ServiceHistoryState.Error -> {
                                    Text("Failed to load services",
                                        fontSize = 13.sp, color = GarageRed)
                                }
                                is SupportViewModel.ServiceHistoryState.Success -> {
                                    if (s.items.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .border(0.5.dp, FieldBorder, RoundedCornerShape(12.dp))
                                                .padding(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No completed services found.\nYou need at least one completed service to report a service issue.",
                                                fontSize = 12.sp, color = GarageTextMuted,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            s.items.forEach { item ->
                                                val isSelected = selectedService?.id == item.id
                                                ServicePickerRow(
                                                    item       = item,
                                                    isSelected = isSelected,
                                                    hasError   = serviceError && !isSelected,
                                                    onClick    = {
                                                        selectedService = item
                                                        serviceError = false
                                                    }
                                                )
                                            }
                                        }
                                        if (serviceError) {
                                            ErrorText("Please select a service")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Issue type ────────────────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionLabel("ISSUE TYPE *")
                        when (issueTypesState) {
                            is IssueTypesState.Loading -> {
                                CircularProgressIndicator(
                                    color = GarageOrange,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            is IssueTypesState.Error -> {
                                Text("Failed to load issue types",
                                    fontSize = 13.sp, color = GarageRed)
                            }
                            is IssueTypesState.Success -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    (issueTypesState as IssueTypesState.Success).types
                                        .forEach { type ->
                                            val isSelected = selectedTypeId == type.id
                                            SelectableRow(
                                                isSelected = isSelected,
                                                hasError   = typeError && !isSelected,
                                                icon       = Icons.Outlined.Report,
                                                title      = type.name ?: "",
                                                subtitle   = type.description,
                                                onClick    = {
                                                    selectedTypeId = type.id
                                                    typeError = false
                                                }
                                            )
                                        }
                                }
                                if (typeError) ErrorText("Please select an issue type")
                            }
                        }
                    }

                    // ── Message ───────────────────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionLabel("DESCRIBE THE ISSUE *")
                        OutlinedTextField(
                            value         = message,
                            onValueChange = {
                                if (it.length <= 500) {
                                    message = it
                                    messageError = false
                                }
                            },
                            modifier      = Modifier.fillMaxWidth().height(140.dp),
                            placeholder   = {
                                Text(
                                    "Please describe what happened in detail...",
                                    fontSize = 14.sp, color = GarageTextMuted
                                )
                            },
                            isError       = messageError,
                            shape         = RoundedCornerShape(12.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = FieldBg,
                                focusedContainerColor   = FieldBg,
                                unfocusedBorderColor    = FieldBorder,
                                focusedBorderColor      = GarageBlue,
                                errorBorderColor        = GarageRed,
                                cursorColor             = GarageBlue
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (messageError) ErrorText("Please describe the issue")
                            else Spacer(Modifier.weight(1f))
                            Text("${message.length}/500",
                                fontSize = 11.sp, color = GarageTextMuted)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        // ── Submit button — only shown once a path is selected ────────────────
        AnimatedVisibility(
            visible = selectedPath != ReportPath.NONE,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick  = {
                        typeError    = selectedTypeId == null
                        messageError = message.isBlank()
                        serviceError = selectedPath == ReportPath.SERVICE
                                && selectedService == null

                        if (!typeError && !messageError && !serviceError) {
                            when (selectedPath) {
                                ReportPath.GENERAL -> viewModel.submitGeneralIssue(
                                    issueTypeId = selectedTypeId!!,
                                    message     = message.trim()
                                )
                                ReportPath.SERVICE -> viewModel.submitServiceIssue(
                                    issueTypeId      = selectedTypeId!!,
                                    serviceRequestId = selectedService!!.id!!,
                                    message          = message.trim()
                                )
                                ReportPath.NONE -> {}
                            }
                        }
                    },
                    enabled  = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = GarageOrange,
                        disabledContainerColor = GarageOrange.copy(alpha = 0.6f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("Submit report", fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Path toggle button ────────────────────────────────────────────────────────

@Composable
private fun PathToggleButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (isSelected) GarageBlue else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, null,
            tint     = if (isSelected) Color.White else GarageTextMuted,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = if (isSelected) Color.White else GarageTextMuted
        )
    }
}

// ── Service picker row ────────────────────────────────────────────────────────

@Composable
private fun ServicePickerRow(
    item       : ServiceHistoryForIssueResponse,
    isSelected : Boolean,
    hasError   : Boolean,
    onClick    : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) GarageBlueLight else FieldBg)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = when {
                    isSelected -> GarageBlue
                    hasError   -> GarageRed
                    else       -> FieldBorder
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (isSelected) GarageBlue else Color(0xFFE1F5EE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Build, null,
                tint     = if (isSelected) Color.White else GarageTealDark,
                modifier = Modifier.size(17.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.serviceName ?: "–", fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) GarageBlue else GarageTextDark)
            Text(item.garageName ?: "–", fontSize = 11.sp,
                color = GarageTextMuted, modifier = Modifier.padding(top = 2.dp))
            Text(item.dateLabel ?: item.date ?: "–",
                fontSize = 11.sp, color = GarageTextMuted)
        }
        if (isSelected) {
            Icon(Icons.Outlined.CheckCircle, null,
                tint = GarageBlue, modifier = Modifier.size(18.dp))
        }
    }
}

// ── Shared ────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        letterSpacing = 0.6.sp, color = GarageTextMuted)
}

@Composable
private fun ErrorText(text: String) {
    Text(text, fontSize = 11.sp, color = GarageRed)
}

@Composable
private fun SelectableRow(
    isSelected : Boolean,
    hasError   : Boolean,
    icon       : ImageVector,
    title      : String,
    subtitle   : String?,
    onClick    : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) GarageBlueLight else FieldBg)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = when {
                    isSelected -> GarageBlue
                    hasError   -> GarageRed
                    else       -> FieldBorder
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) GarageBlue else Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null,
                tint = if (isSelected) Color.White else GarageTextMuted,
                modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                color = if (isSelected) GarageBlue else GarageTextDark)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, fontSize = 12.sp, color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp), lineHeight = 16.sp)
            }
        }
        if (isSelected) {
            Icon(Icons.Outlined.CheckCircle, null,
                tint = GarageBlue, modifier = Modifier.size(18.dp))
        }
    }
}