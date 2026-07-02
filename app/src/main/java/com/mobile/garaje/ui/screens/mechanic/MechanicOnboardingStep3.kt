package com.mobile.garaje.ui.screens.mechanic

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.data.model.MechanicDocumentsData
import com.mobile.garaje.ui.screens.home.GarageOrange

private val OrangeLight  = Color(0xFFFEF0E6)
private val OrangeDark   = Color(0xFF993C1D)
private val LabelGray    = Color(0xFF6B6B6B)
private val TitleBlack   = Color(0xFF1A1A1A)
private val SuccessGreen = Color(0xFF2E7D32)
private val SuccessBg    = Color(0xFFE8F5E9)

@Composable
fun MechanicOnboardingStep3(
    isLoading: Boolean,
    onBack: () -> Unit,
    onSubmit: (MechanicDocumentsData) -> Unit
) {
    var profilePicUri       by remember { mutableStateOf<Uri?>(null) }
    var nationalIdPicUri    by remember { mutableStateOf<Uri?>(null) }
    var professionalCertUri by remember { mutableStateOf<Uri?>(null) }
    var anyRelevantCertUri  by remember { mutableStateOf<Uri?>(null) }
    var policeClearanceUri  by remember { mutableStateOf<Uri?>(null) }

    var nationalIdError     by remember { mutableStateOf(false) }
    var policeClearanceError by remember { mutableStateOf(false) }

    // File launchers — each targets its own state variable
    val profilePicLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> profilePicUri = uri }

    val nationalIdLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
        nationalIdPicUri = uri
        if (uri != null) nationalIdError = false
    }

    val professionalCertLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> professionalCertUri = uri }

    val anyRelevantCertLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> anyRelevantCertUri = uri }

    val policeClearanceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
        policeClearanceUri = uri
        if (uri != null) policeClearanceError = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MechanicStepper(currentStep = 3)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Documents & photo", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
                Text("Required for verification before you can accept jobs.", fontSize = 13.sp, color = LabelGray, lineHeight = 19.sp)
            }

            // Profile photo (optional)
            UploadRow(
                label = "Profile photo",
                hint = "JPG or PNG · max 5 MB",
                icon = Icons.Outlined.CameraAlt,
                uri = profilePicUri,
                isRequired = false,
                isError = false,
                onClick = { profilePicLauncher.launch("image/*") }
            )

            // National ID (required)
            UploadRow(
                label = "National ID photo",
                hint = "Both sides preferred · JPG, PNG or PDF",
                icon = Icons.Outlined.Badge,
                uri = nationalIdPicUri,
                isRequired = true,
                isError = nationalIdError,
                onClick = { nationalIdLauncher.launch("*/*") }
            )

            // Professional certificate (optional)
            UploadRow(
                label = "Professional certificate",
                hint = "PDF, JPG or PNG",
                icon = Icons.Outlined.WorkspacePremium,
                uri = professionalCertUri,
                isRequired = false,
                isError = false,
                onClick = { professionalCertLauncher.launch("*/*") }
            )

            // Any relevant certificate (optional)
            UploadRow(
                label = "Other relevant certificate",
                hint = "PDF, JPG or PNG · optional",
                icon = Icons.Outlined.FileCopy,
                uri = anyRelevantCertUri,
                isRequired = false,
                isError = false,
                onClick = { anyRelevantCertLauncher.launch("*/*") }
            )

            // Police clearance (required)
            UploadRow(
                label = "Police clearance certificate",
                hint = "PDF or JPG",
                icon = Icons.Outlined.VerifiedUser,
                uri = policeClearanceUri,
                isRequired = true,
                isError = policeClearanceError,
                onClick = { policeClearanceLauncher.launch("*/*") }
            )

            // Info banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangeLight)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = GarageOrange, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                Text(
                    "Documents are reviewed before you can accept jobs. You can skip optional uploads and add them later from your profile.",
                    fontSize = 12.sp, color = OrangeDark, lineHeight = 17.sp
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // Footer buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("← Back", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
            }

            Button(
                onClick = {
                    nationalIdError      = nationalIdPicUri == null
                    policeClearanceError = policeClearanceUri == null

                    if (!nationalIdError && !policeClearanceError) {
                        onSubmit(
                            MechanicDocumentsData(
                                profilePicUri       = profilePicUri,
                                nationalIDPicUri    = nationalIdPicUri!!,
                                professionalCertUri = professionalCertUri,
                                anyRelevantCertUri  = anyRelevantCertUri,
                                policeClearanceUri  = policeClearanceUri!!
                            )
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GarageOrange,
                    disabledContainerColor = GarageOrange.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Submit ✓", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }

        Text(
            "You can update your details anytime from your profile",
            fontSize = 11.sp, color = Color(0xFF9E9E9E),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )
    }
}

// ── Single upload row ──────────────────────────────────────────────────────────

@Composable
private fun UploadRow(
    label: String,
    hint: String,
    icon: ImageVector,
    uri: Uri?,
    isRequired: Boolean,
    isError: Boolean,
    onClick: () -> Unit
) {
    val isUploaded = uri != null
    val borderColor = when {
        isError    -> MaterialTheme.colorScheme.error
        isUploaded -> SuccessGreen
        else       -> Color(0xFFE0E0E0)
    }
    val bgColor = when {
        isUploaded -> SuccessBg
        else       -> Color(0xFFF7F7F7)
    }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row {
            Text(
                label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp,
                color = LabelGray
            )
            if (isRequired) {
                Text(" *", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .border(0.5.dp, borderColor, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Outlined.CheckCircle else icon,
                contentDescription = null,
                tint = if (isUploaded) SuccessGreen else Color(0xFF9E9E9E),
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isUploaded) uri!!.lastPathSegment ?: "File selected" else "Tap to upload",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isUploaded) SuccessGreen else Color(0xFF6B6B6B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(hint, fontSize = 11.sp, color = Color(0xFF9E9E9E))
            }
            Icon(
                imageVector = Icons.Outlined.Upload,
                contentDescription = null,
                tint = if (isUploaded) SuccessGreen else Color(0xFF9E9E9E),
                modifier = Modifier.size(18.dp)
            )
        }

        if (isError) {
            Text("This document is required", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}