package com.mobile.garaje.ui.screens.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.data.model.MechanicPersonalData
import com.mobile.garaje.ui.screens.home.GarageOrange

private val FieldBg    = Color(0xFFF7F7F7)
private val FieldBorder = Color(0xFFE0E0E0)
private val HintGray   = Color(0xFF9E9E9E)
private val LabelGray  = Color(0xFF6B6B6B)
private val TitleBlack = Color(0xFF1A1A1A)

@Composable
fun MechanicOnboardingStep1(
    onNext: (MechanicPersonalData) -> Unit
) {
    var altPhone        by remember { mutableStateOf("") }
    var address         by remember { mutableStateOf("") }
    var nationalId      by remember { mutableStateOf("") }
    var emergencyName   by remember { mutableStateOf("") }
    var emergencyNumber by remember { mutableStateOf("") }

    var altPhoneError        by remember { mutableStateOf(false) }
    var addressError         by remember { mutableStateOf(false) }
    var nationalIdError      by remember { mutableStateOf(false) }
    var emergencyNameError   by remember { mutableStateOf(false) }
    var emergencyNumberError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Stepper
        MechanicStepper(currentStep = 1)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Personal details", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
                Text("Complete your profile to start accepting jobs.", fontSize = 13.sp, color = LabelGray, lineHeight = 19.sp)
            }

            MechanicField(
                label = "Alternative phone",
                value = altPhone,
                onValueChange = { altPhone = it; altPhoneError = false },
                placeholder = "07XXXXXXXX",
                icon = Icons.Outlined.Phone,
                isError = altPhoneError,
                errorMessage = "Required",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            MechanicField(
                label = "Physical address",
                value = address,
                onValueChange = { address = it; addressError = false },
                placeholder = "Street, City",
                icon = Icons.Outlined.LocationOn,
                isError = addressError,
                errorMessage = "Required",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            MechanicField(
                label = "National ID number",
                value = nationalId,
                onValueChange = { nationalId = it; nationalIdError = false },
                placeholder = "e.g. 12345678",
                icon = Icons.Outlined.Badge,
                isError = nationalIdError,
                errorMessage = "Required — numbers only",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Emergency contact side by side
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MechanicField(
                    modifier = Modifier.weight(1f),
                    label = "Emergency contact",
                    value = emergencyName,
                    onValueChange = { emergencyName = it; emergencyNameError = false },
                    placeholder = "Full name",
                    icon = Icons.Outlined.PersonAdd,
                    isError = emergencyNameError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                MechanicField(
                    modifier = Modifier.weight(1f),
                    label = "Their number",
                    value = emergencyNumber,
                    onValueChange = { emergencyNumber = it; emergencyNumberError = false },
                    placeholder = "07XX…",
                    icon = Icons.Outlined.Phone,
                    isError = emergencyNumberError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    altPhoneError        = altPhone.isBlank()
                    addressError         = address.isBlank()
                    nationalIdError      = nationalId.isBlank() || nationalId.toIntOrNull() == null
                    emergencyNameError   = emergencyName.isBlank()
                    emergencyNumberError = emergencyNumber.isBlank()

                    if (!altPhoneError && !addressError && !nationalIdError &&
                        !emergencyNameError && !emergencyNumberError) {
                        onNext(
                            MechanicPersonalData(
                                alternativePhone       = altPhone.trim(),
                                physicalAddress        = address.trim(),
                                nationalIdNumber       = nationalId.trim().toInt(),
                                emergencyContactName   = emergencyName.trim(),
                                emergencyContactNumber = emergencyNumber.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GarageOrange)
            ) {
                Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}

// ── Shared field component (used across all 3 steps) ─────────────────────────

@Composable
fun MechanicField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp,
            color = LabelGray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = HintGray) },
            leadingIcon = {
                Icon(icon, contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else HintGray,
                    modifier = Modifier.size(18.dp))
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldBg,
                focusedContainerColor   = Color.White,
                unfocusedBorderColor    = FieldBorder,
                focusedBorderColor      = GarageOrange,
                cursorColor             = GarageOrange
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(errorMessage, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

// ── Shared stepper (used across all 3 steps) ──────────────────────────────────

@Composable
fun MechanicStepper(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val step = index + 1
            val isDone   = step < currentStep
            val isActive = step == currentStep

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = when {
                            isDone || isActive -> GarageOrange
                            else -> Color(0xFFF0F0F0)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Outlined.Check, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = "$step",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }

            if (index < 2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (isDone) GarageOrange else Color(0xFFE0E0E0))
                )
            }
        }
    }
}