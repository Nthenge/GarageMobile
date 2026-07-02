package com.mobile.garaje.ui.screens.carowner.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.data.model.CarOwnerDetailsData
import com.mobile.garaje.ui.screens.home.GarageOrange

private val FieldBg     = Color(0xFFF7F7F7)
private val FieldBorder = Color(0xFFE0E0E0)
private val HintGray    = Color(0xFF9E9E9E)
private val LabelGray   = Color(0xFF6B6B6B)
private val TitleBlack  = Color(0xFF1A1A1A)

private val transmissionOptions = listOf("Automatic", "Manual")
private val engineTypeOptions   = listOf("Petrol", "Diesel", "Hybrid", "Electric")

@Composable
fun CarOwnerOnboardingStep1(
    onNext: (CarOwnerDetailsData) -> Unit
) {
    var make           by remember { mutableStateOf("") }
    var model          by remember { mutableStateOf("") }
    var year           by remember { mutableStateOf("") }
    var licensePlate   by remember { mutableStateOf("") }
    var engineCapacity by remember { mutableStateOf("") }
    var color          by remember { mutableStateOf("") }
    var transmission   by remember { mutableStateOf("") }
    var engineType     by remember { mutableStateOf("") }

    // Errors — only for required fields
    var makeError           by remember { mutableStateOf(false) }
    var modelError          by remember { mutableStateOf(false) }
    var yearError           by remember { mutableStateOf(false) }
    var licensePlateError   by remember { mutableStateOf(false) }
    var engineCapacityError by remember { mutableStateOf(false) }
    var colorError          by remember { mutableStateOf(false) }
    var transmissionError   by remember { mutableStateOf(false) }
    var engineTypeError     by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CarOwnerStepper(currentStep = 1)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Your vehicle", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
                Text("Tell us about yourself and your car.", fontSize = 13.sp, color = LabelGray, lineHeight = 19.sp)
            }

            // ── Vehicle details ───────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CarOwnerField(
                    modifier = Modifier.weight(1f),
                    label = "Make *",
                    value = make,
                    onValueChange = { make = it; makeError = false },
                    placeholder = "e.g. Toyota",
                    icon = Icons.Outlined.DirectionsCar,
                    isError = makeError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                CarOwnerField(
                    modifier = Modifier.weight(1f),
                    label = "Model *",
                    value = model,
                    onValueChange = { model = it; modelError = false },
                    placeholder = "e.g. Corolla",
                    icon = Icons.Outlined.DirectionsCar,
                    isError = modelError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CarOwnerField(
                    modifier = Modifier.weight(1f),
                    label = "Year *",
                    value = year,
                    onValueChange = { year = it; yearError = false },
                    placeholder = "e.g. 2019",
                    icon = Icons.Outlined.CalendarToday,
                    isError = yearError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                CarOwnerField(
                    modifier = Modifier.weight(1f),
                    label = "Color *",
                    value = color,
                    onValueChange = { color = it; colorError = false },
                    placeholder = "e.g. White",
                    icon = Icons.Outlined.Palette,
                    isError = colorError,
                    errorMessage = "Required",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            CarOwnerField(
                label = "License plate *",
                value = licensePlate,
                onValueChange = { licensePlate = it; licensePlateError = false },
                placeholder = "e.g. KAA 123A",
                icon = Icons.Outlined.CreditCard,
                isError = licensePlateError,
                errorMessage = "Required",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )

            CarOwnerField(
                label = "Engine capacity *",
                value = engineCapacity,
                onValueChange = { engineCapacity = it; engineCapacityError = false },
                placeholder = "e.g. 1800cc",
                icon = Icons.Outlined.Speed,
                isError = engineCapacityError,
                errorMessage = "Required"
            )

            // Engine type chips
            ChipSelector(
                label = "Engine type *",
                options = engineTypeOptions,
                selected = engineType,
                isError = engineTypeError,
                onSelect = { engineType = it; engineTypeError = false }
            )

            // Transmission chips
            ChipSelector(
                label = "Transmission *",
                options = transmissionOptions,
                selected = transmission,
                isError = transmissionError,
                onSelect = { transmission = it; transmissionError = false }
            )

            Spacer(Modifier.height(8.dp))
        }

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    makeError           = make.isBlank()
                    modelError          = model.isBlank()
                    yearError           = year.isBlank()
                    licensePlateError   = licensePlate.isBlank()
                    engineCapacityError = engineCapacity.isBlank()
                    colorError          = color.isBlank()
                    transmissionError   = transmission.isBlank()
                    engineTypeError     = engineType.isBlank()

                    if (!makeError && !modelError && !yearError && !licensePlateError &&
                        !engineCapacityError && !colorError && !transmissionError) {
                        onNext(
                            CarOwnerDetailsData(
                                make           = make.trim(),
                                model          = model.trim(),
                                year           = year.trim(),
                                licensePlate   = licensePlate.trim().uppercase(),
                                engineType     = engineType,
                                engineCapacity = engineCapacity.trim(),
                                color          = color.trim(),
                                transmission   = transmission
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

// ── Reusable field ────────────────────────────────────────────────────────────

@Composable
fun CarOwnerField(
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
            label.uppercase(),
            fontSize = 11.sp, fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp, color = LabelGray
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

// ── Single-select chip row ────────────────────────────────────────────────────

@Composable
fun ChipSelector(
    label: String,
    options: List<String>,
    selected: String,
    isError: Boolean,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label.uppercase(),
            fontSize = 11.sp, fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp, color = LabelGray
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = selected == option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) GarageOrange else FieldBg)
                        .border(
                            0.5.dp,
                            if (isError) MaterialTheme.colorScheme.error
                            else if (isSelected) GarageOrange else FieldBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        option,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else LabelGray
                    )
                }
            }
        }
        if (isError) {
            Text("Select an option", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

// ── Stepper ───────────────────────────────────────────────────────────────────

@Composable
fun CarOwnerStepper(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(2) { index ->
            val step     = index + 1
            val isDone   = step < currentStep
            val isActive = step == currentStep

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = if (isDone || isActive) GarageOrange else Color(0xFFF0F0F0),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Outlined.Check, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        "$step", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = if (isActive) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }

            if (index < 1) {
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