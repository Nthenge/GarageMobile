package com.mobile.garaje.ui.screens.carowner.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile.garaje.ui.screens.home.GarageOrange

private val OrangeLight = Color(0xFFFEF0E6)
private val OrangeDark  = Color(0xFF993C1D)
private val LabelGray   = Color(0xFF6B6B6B)
private val TitleBlack  = Color(0xFF1A1A1A)

@Composable
fun CarOwnerOnboardingStep2(
    isLoading: Boolean,
    onBack: () -> Unit,
    onSubmit: (Uri?) -> Unit
) {
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> profilePicUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CarOwnerStepper(currentStep = 2)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Profile photo",
                    fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TitleBlack
                )
                Text(
                    "Add a photo so garages can recognise you. You can skip this and add it later.",
                    fontSize = 13.sp, color = LabelGray, lineHeight = 19.sp
                )
            }

            // ── Photo picker circle ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .border(2.dp, if (profilePicUri != null) GarageOrange else Color(0xFFE0E0E0), CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePicUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profilePicUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Tap to upload",
                            fontSize = 12.sp, color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (profilePicUri != null) {
                TextButton(onClick = { profilePicUri = null }) {
                    Text("Remove photo", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                }
            }

            // Info banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangeLight)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Outlined.Info, contentDescription = null,
                    tint = GarageOrange,
                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                )
                Text(
                    "Your photo helps garages verify your identity when you arrive for a service. This step is optional.",
                    fontSize = 12.sp, color = OrangeDark, lineHeight = 17.sp
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // Footer
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
                onClick = { onSubmit(profilePicUri) },
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GarageOrange,
                    disabledContainerColor = GarageOrange.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        if (profilePicUri != null) "Submit ✓" else "Skip & Submit",
                        fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White
                    )
                }
            }
        }

        Text(
            "You can update your photo anytime from your profile",
            fontSize = 11.sp, color = Color(0xFF9E9E9E),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )
    }
}