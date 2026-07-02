package com.mobile.garaje.ui.screens.onboarding

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangePale
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted

// ── Data models ───────────────────────────────────────────────────────────────

// Mirrors your backend Service entity — fetched from API
data class ServiceItem(
    val id: Long,
    val name: String
)

data class ComplianceData(
    val registrationNumber: String,
    val licenseNumber: String,
    val professionalCertificate: String,
    val certificateUri: Uri?,
    val facilityPhotoUris: List<Uri>,
    val selectedServiceIds: List<Long>
)

// ── Step 2 Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GarageOnboardingStep2(
    // Pass services loaded from your ViewModel (GET /api/services)
    availableServices: List<ServiceItem> = emptyList(),
    isLoadingServices: Boolean = false,
    onBack: () -> Unit = {},
    onNext: (ComplianceData) -> Unit = {}
) {
    var registrationNumber  by remember { mutableStateOf("") }
    var licenseNumber        by remember { mutableStateOf("") }
    var professionalCert     by remember { mutableStateOf("") }
    var certificateUri       by remember { mutableStateOf<Uri?>(null) }
    var facilityPhotoUris    by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedServiceIds   by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // File picker — single doc (certificate/license)
    val certPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { certificateUri = it } }

    // File picker — multiple photos (facility)
    val photosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // Cap at 5 photos
        facilityPhotoUris = (facilityPhotoUris + uris).take(5)
    }

    val canProceed = registrationNumber.isNotBlank()
            && licenseNumber.isNotBlank()
            && professionalCert.isNotBlank()
            && selectedServiceIds.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        OnboardingStepBar(currentStep = 2)

        Text(
            text = "Compliance & services",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GarageTextDark,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "Verification documents & what you offer",
            fontSize = 13.sp,
            color = GarageTextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
        )

        // ── Registration section ──────────────────────────────────────────────
        SectionLabel(text = "Registration & licensing")
        Spacer(Modifier.height(12.dp))

        GarageOnboardingField(
            value = registrationNumber,
            onValueChange = { registrationNumber = it },
            label = "Business registration number",
            placeholder = "e.g. BN-2024-XXXXX",
            leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )

        Spacer(Modifier.height(12.dp))

        GarageOnboardingField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it },
            label = "Garage license number",
            placeholder = "e.g. GRG-2024-001",
            leadingIcon = { Icon(Icons.Outlined.CardMembership, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )

        Spacer(Modifier.height(12.dp))

        GarageOnboardingField(
            value = professionalCert,
            onValueChange = { professionalCert = it },
            label = "Professional certificate number",
            placeholder = "e.g. KEBS-CERT-001",
            leadingIcon = { Icon(Icons.Outlined.VerifiedUser, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )

        Spacer(Modifier.height(24.dp))

        // ── Document uploads ──────────────────────────────────────────────────
        SectionLabel(text = "Documents & facility photos")
        Spacer(Modifier.height(12.dp))

        // Certificate upload
        UploadBox(
            label = if (certificateUri != null) "Certificate uploaded ✓" else "Upload certificate / license",
            subLabel = "PDF or image • max 5MB",
            isUploaded = certificateUri != null,
            icon = { Icon(Icons.Outlined.UploadFile, null, tint = if (certificateUri != null) GarageOrange else GarageTextMuted, modifier = Modifier.size(24.dp)) },
            onClick = { certPickerLauncher.launch("*/*") }
        )

        Spacer(Modifier.height(10.dp))

        // Facility photos upload
        UploadBox(
            label = if (facilityPhotoUris.isEmpty()) "Upload facility photos"
            else "${facilityPhotoUris.size} photo(s) selected",
            subLabel = "Up to 5 photos • tap to add more",
            isUploaded = facilityPhotoUris.isNotEmpty(),
            icon = { Icon(Icons.Outlined.PhotoCamera, null, tint = if (facilityPhotoUris.isNotEmpty()) GarageOrange else GarageTextMuted, modifier = Modifier.size(24.dp)) },
            onClick = { photosPickerLauncher.launch("image/*") }
        )

        // Show photo count chips if any selected
        if (facilityPhotoUris.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                facilityPhotoUris.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GarageOrangePale)
                            .border(1.dp, GarageOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GarageOrange)
                    }
                }
                if (facilityPhotoUris.size < 5) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable { photosPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Add, null, tint = GarageTextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Services section ──────────────────────────────────────────────────
        SectionLabel(text = "Services offered")
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Select all services your garage provides",
            fontSize = 12.sp,
            color = GarageTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoadingServices) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GarageOrange, modifier = Modifier.size(28.dp))
            }
        } else if (availableServices.isEmpty()) {
            // Empty state — API not loaded yet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F9F9))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No services available. Check your connection.",
                    fontSize = 12.sp,
                    color = GarageTextMuted
                )
            }
        } else {
            // Wrap chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableServices.forEach { service ->
                    val isSelected = service.id in selectedServiceIds
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) GarageOrange else Color(0xFFF5F5F5))
                            .border(
                                1.dp,
                                if (isSelected) GarageOrange else Color(0xFFEEEEEE),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                selectedServiceIds = if (isSelected)
                                    selectedServiceIds - service.id
                                else
                                    selectedServiceIds + service.id
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = service.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else GarageTextMuted
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Navigation buttons ────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder,
            ) {
                Icon(Icons.Outlined.ArrowBack, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Back", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    onNext(
                        ComplianceData(
                            registrationNumber    = registrationNumber,
                            licenseNumber         = licenseNumber,
                            professionalCertificate = professionalCert,
                            certificateUri        = certificateUri,
                            facilityPhotoUris     = facilityPhotoUris,
                            selectedServiceIds    = selectedServiceIds.toList()
                        )
                    )
                },
                modifier = Modifier.weight(2f).height(54.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = canProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GarageOrange,
                    disabledContainerColor = GarageOrange.copy(alpha = 0.35f)
                )
            ) {
                Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Outlined.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Upload box component ──────────────────────────────────────────────────────

@Composable
fun UploadBox(
    label: String,
    subLabel: String,
    isUploaded: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isUploaded) GarageOrangePale else Color(0xFFF9F9F9))
            .border(
                width = if (isUploaded) 1.5.dp else 1.dp,
                color = if (isUploaded) GarageOrange.copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUploaded) GarageOrange else GarageTextDark
            )
            Text(
                text = subLabel,
                fontSize = 11.sp,
                color = GarageTextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            if (isUploaded) Icons.Outlined.CheckCircle else Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = if (isUploaded) GarageOrange else GarageTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GarageOnboardingStep2Preview() {
    GarageOnboardingStep2(
        availableServices = listOf(
            ServiceItem(1, "Oil change"),
            ServiceItem(2, "Brake repair"),
            ServiceItem(3, "Tyre fitting"),
            ServiceItem(4, "Bodywork"),
            ServiceItem(5, "Electrical"),
            ServiceItem(6, "AC repair"),
            ServiceItem(7, "Diagnostics"),
            ServiceItem(8, "Engine repair")
        )
    )
}