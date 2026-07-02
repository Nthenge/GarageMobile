package com.mobile.garaje.ui.screens.onboarding

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangePale
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted

// ── Data models ───────────────────────────────────────────────────────────────

enum class PaymentMethod { PAYBILL, TILL, NONE }

data class PaymentData(
    val paybillNumber: Int?,
    val accountNumber: Int?,
    val mpesaTill: Int?
)

// ── Step 3 Screen ─────────────────────────────────────────────────────────────

@Composable
fun GarageOnboardingStep3(
    isLoading: Boolean = false,
    onBack: () -> Unit = {},
    onSubmit: (PaymentData) -> Unit = {}
) {
    var selectedMethod  by remember { mutableStateOf(PaymentMethod.PAYBILL) }
    var paybillNumber   by remember { mutableStateOf("") }
    var accountNumber   by remember { mutableStateOf("") }
    var tillNumber      by remember { mutableStateOf("") }

    val canSubmit = when (selectedMethod) {
        PaymentMethod.PAYBILL -> paybillNumber.isNotBlank() && accountNumber.isNotBlank()
        PaymentMethod.TILL    -> tillNumber.isNotBlank()
        PaymentMethod.NONE    -> true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        OnboardingStepBar(currentStep = 3)

        Text(
            text = "Payment setup",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GarageTextDark,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "How customers will pay you",
            fontSize = 13.sp,
            color = GarageTextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
        )

        // ── Payment method selector ───────────────────────────────────────────
        SectionLabel(text = "Payment method")
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PaymentMethodCard(
                modifier = Modifier.weight(1f),
                label = "Paybill",
                description = "Paybill + account number",
                icon = { Icon(Icons.Outlined.AccountBalance, null, modifier = Modifier.size(22.dp),
                    tint = if (selectedMethod == PaymentMethod.PAYBILL) GarageOrange else GarageTextMuted) },
                selected = selectedMethod == PaymentMethod.PAYBILL,
                onClick = { selectedMethod = PaymentMethod.PAYBILL }
            )
            PaymentMethodCard(
                modifier = Modifier.weight(1f),
                label = "Till",
                description = "M-Pesa till number",
                icon = { Icon(Icons.Outlined.PhoneAndroid, null, modifier = Modifier.size(22.dp),
                    tint = if (selectedMethod == PaymentMethod.TILL) GarageOrange else GarageTextMuted) },
                selected = selectedMethod == PaymentMethod.TILL,
                onClick = { selectedMethod = PaymentMethod.TILL }
            )
            PaymentMethodCard(
                modifier = Modifier.weight(1f),
                label = "Skip",
                description = "Add later in settings",
                icon = { Icon(Icons.Outlined.SkipNext, null, modifier = Modifier.size(22.dp),
                    tint = if (selectedMethod == PaymentMethod.NONE) GarageOrange else GarageTextMuted) },
                selected = selectedMethod == PaymentMethod.NONE,
                onClick = { selectedMethod = PaymentMethod.NONE }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Paybill fields ────────────────────────────────────────────────────
        if (selectedMethod == PaymentMethod.PAYBILL) {
            SectionLabel(text = "Paybill details")
            Spacer(Modifier.height(12.dp))

            GarageOnboardingField(
                value = paybillNumber,
                onValueChange = { paybillNumber = it },
                label = "Paybill number",
                placeholder = "e.g. 400200",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(Icons.Outlined.CreditCard, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp))
                }
            )

            Spacer(Modifier.height(12.dp))

            GarageOnboardingField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = "Account number",
                placeholder = "e.g. Garage001 or your name",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(Icons.Outlined.Tag, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp))
                }
            )

            Spacer(Modifier.height(12.dp))

            PaymentInfoBox(
                message = "Customers will send payment to Paybill $paybillNumber, Account $accountNumber"
            )
        }

        // ── Till fields ───────────────────────────────────────────────────────
        if (selectedMethod == PaymentMethod.TILL) {
            SectionLabel(text = "Till number")
            Spacer(Modifier.height(12.dp))

            GarageOnboardingField(
                value = tillNumber,
                onValueChange = { tillNumber = it },
                label = "M-Pesa till number",
                placeholder = "e.g. 123456",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(Icons.Outlined.PhoneAndroid, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp))
                }
            )

            Spacer(Modifier.height(12.dp))

            PaymentInfoBox(
                message = "Customers will send payment to Till number $tillNumber"
            )
        }

        // ── Skip info ─────────────────────────────────────────────────────────
        if (selectedMethod == PaymentMethod.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF9F9F9))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(14.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = GarageTextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "You can add payment details later",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GarageTextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Go to Settings → Payment after your garage is approved to configure M-Pesa.",
                        fontSize = 12.sp,
                        color = GarageTextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
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
                    onSubmit(
                        PaymentData(
                            paybillNumber = paybillNumber.toIntOrNull(),
                            accountNumber = accountNumber.toIntOrNull(),
                            mpesaTill     = tillNumber.toIntOrNull()
                        )
                    )
                },
                modifier = Modifier.weight(2f).height(54.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = canSubmit && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GarageOrange,
                    disabledContainerColor = GarageOrange.copy(alpha = 0.35f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Submit", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Your garage will be reviewed within 24–48 hours",
            fontSize = 11.sp,
            color = GarageTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ── Payment method card ───────────────────────────────────────────────────────

@Composable
fun PaymentMethodCard(
    label: String,
    description: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) GarageOrangePale else Color(0xFFF9F9F9))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) GarageOrange else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) GarageOrange else GarageTextDark,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            fontSize = 9.sp,
            color = if (selected) GarageOrange.copy(alpha = 0.7f) else GarageTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// ── Info box ──────────────────────────────────────────────────────────────────

@Composable
fun PaymentInfoBox(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GarageOrangePale)
            .border(1.dp, GarageOrange.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            tint = GarageOrange,
            modifier = Modifier.size(16.dp).padding(top = 1.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            color = GarageTextDark,
            lineHeight = 18.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GarageOnboardingStep3Preview() { GarageOnboardingStep3() }