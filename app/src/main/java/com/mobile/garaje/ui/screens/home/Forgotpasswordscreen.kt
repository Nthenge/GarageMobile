package com.mobile.garaje.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.mobile.garaje.R

@Composable
fun ForgotPasswordScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onNextClick: (email: String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    // Entrance animation
    var entered by remember { mutableStateOf(false) }
    val cardOffset by animateFloatAsState(
        targetValue = if (entered) 0f else 60f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "cardOffset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "cardAlpha"
    )
    LaunchedEffect(Unit) { entered = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ── Hero image area ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
        ) {
            // Replace with your hero image the same way as RegisterScreen:
            // Image(
            //     painter = painterResource(id = R.drawable.forgot_hero),
            //     contentDescription = null,
            //     contentScale = ContentScale.Crop,
            //     modifier = Modifier.fillMaxSize()
            // )
            Image(
                painter = painterResource(id = R.drawable.register_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient fade into card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)   // ← taller fade
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.4f to Color.White.copy(alpha = 0.2f),   // gentle start
                                0.75f to Color.White.copy(alpha = 0.7f), // mid ease
                                1.0f to Color.White                        // full white at card
                            )
                        )
                    )
            )

            // Back arrow — top left over the image
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 48.dp, start = 12.dp)
                    .size(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GarageTextDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().height(520.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardOffset.dp)
                    .alpha(cardAlpha)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                if (!submitted) {

                    // ── Input state ───────────────────────────────────────────
                    Text(
                        text = "Account recovery",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GarageTextDark,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enter the email you used to sign up for Garage App, and we shall help you reset your password.",
                        fontSize = 13.sp,
                        color = GarageTextMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    GarageTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email address",
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email, null,
                                tint = GarageTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                onNextClick(email)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (email.isNotBlank()) GarageOrange
                            else GarageOrange.copy(alpha = 0.35f)
                        ),
                        enabled = email.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "NEXT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = Color.White
                            )
                        }
                    }

                } else {

                    // ── Success state — shown after submit ────────────────────
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                color = GarageOrange.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✉️", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Check your email",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GarageTextDark,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "We've sent a password reset link to\n$email",
                        fontSize = 13.sp,
                        color = GarageTextMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GarageOrange)
                    ) {
                        Text(
                            text = "BACK TO LOGIN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Didn't receive it? ",
                            color = GarageTextMuted,
                            fontSize = 13.sp
                        )
                        TextButton(
                            onClick = { submitted = false; email = "" },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Try again",
                                color = GarageOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}