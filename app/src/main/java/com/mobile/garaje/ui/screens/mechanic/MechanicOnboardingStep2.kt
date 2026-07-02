package com.mobile.garaje.ui.screens.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.mobile.garaje.data.model.MechanicSkillsData
import com.mobile.garaje.ui.screens.home.GarageOrange

private val OrangeLight = Color(0xFFFEF0E6)
private val LabelGray   = Color(0xFF6B6B6B)
private val TitleBlack  = Color(0xFF1A1A1A)

private val specialisations = listOf(
    "Engine", "Electrics", "Brakes", "Diagnostics",
    "Suspension", "Body work", "AC & cooling", "Transmission",
    "OBD / scanning", "EV systems", "Tyres", "Exhaust"
)

private val vehicleBrandList = listOf(
    "Toyota", "Subaru", "Nissan", "Mazda", "BMW",
    "Mercedes", "Mitsubishi", "Isuzu", "Volkswagen",
    "Land Rover", "Ford", "Honda", "Hyundai", "Kia"
)

private val availabilityOptions = listOf("Full-time", "Part-time", "On-call")

@Composable
fun MechanicOnboardingStep2(
    onBack: () -> Unit,
    onNext: (MechanicSkillsData) -> Unit
) {
    var yearsOfExp          by remember { mutableStateOf("") }
    val selectedSpecs       = remember { mutableStateListOf<String>() }
    val selectedBrands      = remember { mutableStateListOf<String>() }
    var selectedAvailability by remember { mutableStateOf("") }

    var yearsError        by remember { mutableStateOf(false) }
    var specsError        by remember { mutableStateOf(false) }
    var brandsError       by remember { mutableStateOf(false) }
    var availabilityError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MechanicStepper(currentStep = 2)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Skills & availability", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
                Text("Select everything that applies to you.", fontSize = 13.sp, color = LabelGray)
            }

            // Years of experience
            MechanicField(
                label = "Years of experience",
                value = yearsOfExp,
                onValueChange = { yearsOfExp = it; yearsError = false },
                placeholder = "e.g. 5",
                icon = Icons.Outlined.CalendarToday,
                isError = yearsError,
                errorMessage = "Required",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Areas of specialisation chips
            ChipSection(
                label = "Areas of specialisation",
                items = specialisations,
                selected = selectedSpecs,
                isError = specsError,
                errorMessage = "Select at least one",
                onToggle = { item ->
                    specsError = false
                    if (selectedSpecs.contains(item)) selectedSpecs.remove(item)
                    else selectedSpecs.add(item)
                }
            )

            // Vehicle brands chips
            ChipSection(
                label = "Vehicle brands",
                items = vehicleBrandList,
                selected = selectedBrands,
                isError = brandsError,
                errorMessage = "Select at least one",
                onToggle = { item ->
                    brandsError = false
                    if (selectedBrands.contains(item)) selectedBrands.remove(item)
                    else selectedBrands.add(item)
                }
            )

            // Availability — single select chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "AVAILABILITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                    color = LabelGray
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availabilityOptions.forEach { option ->
                        val isSelected = selectedAvailability == option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) GarageOrange else Color(0xFFF7F7F7))
                                .border(
                                    0.5.dp,
                                    if (availabilityError) MaterialTheme.colorScheme.error
                                    else if (isSelected) GarageOrange else Color(0xFFE0E0E0),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedAvailability = option
                                    availabilityError = false
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF6B6B6B)
                            )
                        }
                    }
                }
                if (availabilityError) {
                    Text("Select an option", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                }
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
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("← Back", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TitleBlack)
            }

            Button(
                onClick = {
                    yearsError        = yearsOfExp.isBlank()
                    specsError        = selectedSpecs.isEmpty()
                    brandsError       = selectedBrands.isEmpty()
                    availabilityError = selectedAvailability.isBlank()

                    if (!yearsError && !specsError && !brandsError && !availabilityError) {
                        onNext(
                            MechanicSkillsData(
                                areasOfSpecialization = selectedSpecs.joinToString(","),
                                vehicleBrands         = selectedBrands.joinToString(","),
                                yearsOfExperience     = yearsOfExp.trim(),
                                availability          = selectedAvailability
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GarageOrange)
            ) {
                Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}

// ── Multi-select chip group ────────────────────────────────────────────────────

@Composable
private fun ChipSection(
    label: String,
    items: List<String>,
    selected: List<String>,
    isError: Boolean,
    errorMessage: String,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.6.sp,
            color = LabelGray
        )
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            items.forEach { item ->
                val isSelected = selected.contains(item)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) GarageOrange else Color(0xFFF7F7F7))
                        .border(
                            0.5.dp,
                            if (isError) MaterialTheme.colorScheme.error
                            else if (isSelected) GarageOrange else Color(0xFFE0E0E0),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onToggle(item) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = item,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF6B6B6B)
                    )
                }
            }
        }
        if (isError) {
            Text(errorMessage, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}