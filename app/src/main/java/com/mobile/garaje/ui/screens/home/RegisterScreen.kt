package com.mobile.garaje.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Role enum ────────────────────────────────────────────────────────────────

enum class UserRole { CAR_OWNER, GARAGE_ADMIN }

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun RegisterScreen(
    isLoading: Boolean = false,
    onCreateAccount: (
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole
    ) -> Unit = { _, _, _, _, _, _ -> },
    onSignInClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    var firstName       by rememberSaveable { mutableStateOf("") }
    var lastName        by rememberSaveable { mutableStateOf("") }
    var email           by rememberSaveable { mutableStateOf("") }
    var phone           by rememberSaveable { mutableStateOf("") }
    var password        by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmVisible  by rememberSaveable { mutableStateOf(false) }
    var selectedRole    by rememberSaveable { mutableStateOf(UserRole.CAR_OWNER) }
    var agreedToTerms   by rememberSaveable { mutableStateOf(false) }

    // slide-up animation
    var entered by remember { mutableStateOf(false) }
    val cardOffset by animateFloatAsState(
        targetValue = if (entered) 0f else 60f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "offset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "alpha"
    )
    LaunchedEffect(Unit) { entered = true }

    val passwordsMatch = password == confirmPassword
    val allFilled = firstName.isNotBlank()
            && lastName.isNotBlank()
            && email.isNotBlank()
            && phone.isNotBlank()
            && password.length >= 6
            && passwordsMatch
            && agreedToTerms

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Hero image top ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
        ) {
            // Replace with your actual Image() composable:
            // Image(painter = painterResource(R.drawable.register_hero),
            //       contentDescription = null, contentScale = ContentScale.Crop,
            //       modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.Companion.radialGradient(
                            listOf(GarageOrangeLight.copy(alpha = 0.3f), GarageOrangePale)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🚗", fontSize = 72.sp)
            }

            // fade to white at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.White))
                    )
            )
        }

        // ── Scrollable form ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(220.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardOffset.dp)
                    .alpha(cardAlpha)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GarageTextDark,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Join our garage management system",
                    fontSize = 13.sp,
                    color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // First + Last name side by side
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GarageTextField(
                        modifier = Modifier.weight(1f),
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "First name",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person, null,
                                tint = GarageTextMuted, modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    GarageTextField(
                        modifier = Modifier.weight(1f),
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Last name",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person, null,
                                tint = GarageTextMuted, modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                GarageTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email address",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Email, null,
                            tint = GarageTextMuted, modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                GarageTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "07XXXXXXXX",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Phone, null,
                            tint = GarageTextMuted, modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password + Confirm side by side
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GarageTextField(
                        modifier = Modifier.weight(1f),
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock, null,
                                tint = GarageTextMuted, modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    if (passwordVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    null, tint = GarageTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                    GarageTextField(
                        modifier = Modifier.weight(1f),
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock, null,
                                tint = if (!passwordsMatch && confirmPassword.isNotEmpty())
                                    Color(0xFFE53935) else GarageTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { confirmVisible = !confirmVisible },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    if (confirmVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    null, tint = GarageTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (confirmVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                }

                // mismatch hint
                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text(
                        text = "Passwords do not match",
                        fontSize = 11.sp,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Role selector ─────────────────────────────────────────────
                Text(
                    text = "I am a",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GarageTextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RoleCard(
                        modifier = Modifier.weight(1f),
                        label = "Car Owner",
                        icon = Icons.Outlined.DirectionsCar,
                        selected = selectedRole == UserRole.CAR_OWNER,
                        onClick = { selectedRole = UserRole.CAR_OWNER }
                    )
                    RoleCard(
                        modifier = Modifier.weight(1f),
                        label = "Garage Admin",
                        icon = Icons.Outlined.Store,
                        selected = selectedRole == UserRole.GARAGE_ADMIN,
                        onClick = { selectedRole = UserRole.GARAGE_ADMIN }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Terms row ─────────────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { agreedToTerms = !agreedToTerms }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GarageOrange,
                            uncheckedColor = GarageTextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val termsText = buildAnnotatedString {
                        withStyle(SpanStyle(color = GarageTextMuted, fontSize = 12.sp)) {
                            append("By continuing, you agree to the ")
                        }
                        withStyle(SpanStyle(color = GarageOrange, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold)) {
                            append("Terms & Conditions")
                        }
                    }
                    Text(
                        text = termsText,
                        modifier = Modifier.clickable { onTermsClick() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Create Account button ─────────────────────────────────────
                Button(
                    onClick = {
                        if (allFilled) {
                            onCreateAccount(firstName, lastName, email, phone, password, selectedRole)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = allFilled && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GarageOrange,
                        disabledContainerColor = GarageOrange.copy(alpha = 0.35f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White, strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Account", fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", color = GarageTextMuted, fontSize = 13.sp)
                    TextButton(onClick = onSignInClick, contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Text("Sign in", color = GarageOrange, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── Role selection card ───────────────────────────────────────────────────────

@Composable
fun RoleCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) GarageOrange else Color(0xFFEEEEEE)
    val bgColor     = if (selected) GarageOrangePale else Color(0xFFF9F9F9)
    val contentColor = if (selected) GarageOrange else GarageTextMuted

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() { RegisterScreen() }