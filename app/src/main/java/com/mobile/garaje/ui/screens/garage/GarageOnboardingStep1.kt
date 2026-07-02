package com.mobile.garaje.ui.screens.onboarding

import android.Manifest
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangePale
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted

data class BusinessDetailsData(
    val businessName: String,
    val businessEmail: String,
    val phoneNumber: String,
    val physicalAddress: String,
    val latitude: Double?,
    val longitude: Double?,
    val openingTime: String,
    val closingTime: String,
    val operatingDays: List<String>,
    val yearsInOperation: Int
)

@Composable
fun OnboardingStepBar(currentStep: Int, totalSteps: Int = 3) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val stepNum  = index + 1
            val isDone   = stepNum < currentStep
            val isActive = stepNum == currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone || isActive -> GarageOrange
                            else               -> Color(0xFFF0F0F0)
                        }
                    )
                    .then(
                        if (isActive) Modifier.border(3.dp, GarageOrange.copy(alpha = 0.3f), CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("$stepNum", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else GarageTextMuted)
                }
            }

            if (index < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (isDone) GarageOrange else Color(0xFFEEEEEE))
                )
            }
        }
    }
}

@Composable
fun GarageOnboardingStep1(
    onNext: (BusinessDetailsData) -> Unit = {}
) {
    val context = LocalContext.current
    val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val fullDayNames = mapOf(
        "Mon" to "MONDAY", "Tue" to "TUESDAY", "Wed" to "WEDNESDAY",
        "Thu" to "THURSDAY", "Fri" to "FRIDAY", "Sat" to "SATURDAY", "Sun" to "SUNDAY"
    )

    var businessName    by remember { mutableStateOf("") }
    var businessEmail   by remember { mutableStateOf("") }
    var phoneNumber     by remember { mutableStateOf("") }
    var physicalAddress by remember { mutableStateOf("") }
    var latitudeText    by remember { mutableStateOf("") }
    var longitudeText   by remember { mutableStateOf("") }
    var openingTime     by remember { mutableStateOf("") }
    var closingTime     by remember { mutableStateOf("") }
    var yearsText       by remember { mutableStateOf("") }
    var selectedDays    by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri")) }
    var isLocating      by remember { mutableStateOf(false) }
    var locationError   by remember { mutableStateOf<String?>(null) }

    // ── Permission launcher ───────────────────────────────────────────────────
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isLocating = true
            locationError = null
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    isLocating = false
                    if (location != null) {
                        latitudeText  = location.latitude.toString()
                        longitudeText = location.longitude.toString()
                    } else {
                        locationError = "Could not get location. Try again."
                    }
                }
                .addOnFailureListener { e ->
                    isLocating = false
                    locationError = e.message ?: "Location failed"
                }
        } else {
            locationError = "Location permission denied"
        }
    }

    val canProceed = businessName.isNotBlank()
            && businessEmail.isNotBlank()
            && phoneNumber.isNotBlank()
            && physicalAddress.isNotBlank()
            && openingTime.isNotBlank()
            && closingTime.isNotBlank()
            && selectedDays.isNotEmpty()
            && yearsText.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        OnboardingStepBar(currentStep = 1)

        Text("Business details", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
            color = GarageTextDark, letterSpacing = (-0.5).sp)
        Text("Tell us about your garage", fontSize = 13.sp, color = GarageTextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 24.dp))

        GarageOnboardingField(
            value = businessName, onValueChange = { businessName = it },
            label = "Business name", placeholder = "e.g. Speedy Auto Garage",
            leadingIcon = { Icon(Icons.Outlined.Store, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )
        Spacer(Modifier.height(12.dp))
        GarageOnboardingField(
            value = businessEmail, onValueChange = { businessEmail = it },
            label = "Business email", placeholder = "business@email.com",
            keyboardType = KeyboardType.Email,
            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )
        Spacer(Modifier.height(12.dp))
        GarageOnboardingField(
            value = phoneNumber, onValueChange = { phoneNumber = it },
            label = "Phone number", placeholder = "07XXXXXXXX",
            keyboardType = KeyboardType.Phone,
            leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )
        Spacer(Modifier.height(12.dp))
        GarageOnboardingField(
            value = physicalAddress, onValueChange = { physicalAddress = it },
            label = "Physical address", placeholder = "Street, area, city",
            leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )

        Spacer(Modifier.height(16.dp))
        SectionLabel(text = "Business location (GPS)")
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GarageOnboardingField(
                modifier = Modifier.weight(1f),
                value = latitudeText, onValueChange = { latitudeText = it },
                label = "Latitude", placeholder = "-1.2921",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = { Icon(Icons.Outlined.MyLocation, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
            )
            GarageOnboardingField(
                modifier = Modifier.weight(1f),
                value = longitudeText, onValueChange = { longitudeText = it },
                label = "Longitude", placeholder = "36.8219",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = { Icon(Icons.Outlined.MyLocation, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Use current location button ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GarageOrangePale)
                .border(1.dp, GarageOrange.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .clickable(enabled = !isLocating) {
                    locationError = null
                    locationLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLocating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = GarageOrange,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Outlined.GpsFixed, null, tint = GarageOrange, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isLocating) "Getting location..." else "Use my current location",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GarageOrange
            )
        }

        // Show error if location failed
        locationError?.let { error ->
            Spacer(Modifier.height(6.dp))
            Text(text = error, fontSize = 11.sp, color = Color(0xFFE53935),
                modifier = Modifier.padding(start = 4.dp))
        }

        // Show filled confirmation
        if (latitudeText.isNotBlank() && longitudeText.isNotBlank() && !isLocating) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, null, tint = GarageOrange,
                    modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Location captured", fontSize = 11.sp, color = GarageOrange,
                    fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel(text = "Operating hours")
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GarageOnboardingField(
                modifier = Modifier.weight(1f),
                value = openingTime, onValueChange = { openingTime = it },
                label = "Opening time", placeholder = "07:00",
                leadingIcon = { Icon(Icons.Outlined.Schedule, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
            )
            GarageOnboardingField(
                modifier = Modifier.weight(1f),
                value = closingTime, onValueChange = { closingTime = it },
                label = "Closing time", placeholder = "18:00",
                leadingIcon = { Icon(Icons.Outlined.Schedule, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel(text = "Operating days")
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            allDays.forEach { day ->
                val isSelected = day in selectedDays
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GarageOrange else Color(0xFFF5F5F5))
                        .clickable {
                            selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else GarageTextMuted)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        GarageOnboardingField(
            value = yearsText, onValueChange = { yearsText = it },
            label = "Years in operation", placeholder = "e.g. 5",
            keyboardType = KeyboardType.Number,
            leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) }
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                onNext(
                    BusinessDetailsData(
                        businessName     = businessName,
                        businessEmail    = businessEmail,
                        phoneNumber      = phoneNumber,
                        physicalAddress  = physicalAddress,
                        latitude         = latitudeText.toDoubleOrNull(),
                        longitude        = longitudeText.toDoubleOrNull(),
                        openingTime      = openingTime,
                        closingTime      = closingTime,
                        operatingDays    = selectedDays.map { fullDayNames[it] ?: it },
                        yearsInOperation = yearsText.toIntOrNull() ?: 0
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
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

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        color = GarageOrange, letterSpacing = 0.5.sp)
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF5F5F5)))
}

@Composable
fun GarageOnboardingField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            color = GarageTextMuted, letterSpacing = 0.3.sp,
            modifier = Modifier.padding(bottom = 5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFFCCCCCC)) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = Color(0xFFEEEEEE),
                focusedBorderColor      = GarageOrange,
                unfocusedContainerColor = Color(0xFFF7F7F7),
                focusedContainerColor   = Color.White
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = GarageTextDark)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GarageOnboardingStep1Preview() { GarageOnboardingStep1() }