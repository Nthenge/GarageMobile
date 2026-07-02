package com.mobile.garaje.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    isLoading: Boolean = false,                                        // ← lifted from ViewModel
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Entrance animation
    var entered by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "contentAlpha"
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (entered) 0f else 40f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "contentOffset"
    )
    LaunchedEffect(Unit) { entered = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashGradient)
    ) {
        // Decorative blob
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 100.dp, y = (-80).dp)
                .alpha(0.2f)
                .background(
                    brush = Brush.radialGradient(listOf(GarageOrangeLight, Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .offset(y = contentOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo top ──────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, bottom = 32.dp)
            ) {
                Text(text = "🔧", fontSize = 52.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "MyGarage",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GarageTextDark,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your Garage Services, All in One Place",
                    fontSize = 12.sp,
                    color = GarageTextMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // ── Login card ────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = GarageTextDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                GarageTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email address",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = GarageTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(8.dp))

                GarageTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = GarageTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = GarageTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password
                )

                // Forgot password
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onForgotPasswordClick) {
                        Text(
                            text = "Forgot password?",
                            color = GarageOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login button — isLoading now comes from ViewModel via MainActivity
                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (email.isNotEmpty() && password.isNotEmpty())
                            GarageOrange else GarageOrange.copy(alpha = 0.4f),
                        disabledContainerColor = GarageOrange.copy(alpha = 0.4f)
                    ),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "LOGIN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New to MyGarage App?",
                        color = GarageTextMuted,
                        fontSize = 13.sp
                    )
                    TextButton(
                        onClick = onRegisterClick,
                        contentPadding = PaddingValues(start = 2.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
                    ) {
                        Text(
                            text = "Register",
                            color = GarageOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}