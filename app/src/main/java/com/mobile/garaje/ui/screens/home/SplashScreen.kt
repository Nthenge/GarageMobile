package com.mobile.garaje.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ─── Brand Colors ─────────────────────────────────────────────────────────────
val GarageOrange      = Color(0xFFE8520A)
val GarageOrangeLight = Color(0xFFFF7A3D)
val GarageOrangePale  = Color(0xFFFFF0EA)
val GarageTextDark    = Color(0xFF1A1A1A)
val GarageTextMuted   = Color(0xFF8A8A8A)

val SplashGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFFFFF5F0),
        Color(0xFFFFECE3),
        Color(0xFFFDE8D8),
    ),
    radius = 1200f
)

// ─── Onboarding page data ─────────────────────────────────────────────────────
data class OnboardingData(
    val emoji: String,
    val titleNormal: String,
    val titleHighlight: String,
    val titleSuffix: String,
    val subtitle: String,
    val features: List<Pair<String, String>>  // emoji + feature label
)

val onboardingPages = listOf(

    // Page 1 — Services Available
    OnboardingData(
        emoji = "🛠️",
        titleNormal = "All ",
        titleHighlight = "Garage Services",
        titleSuffix = " in One Place",
        subtitle = "From oil changes to full engine overhauls — find every service you need, all in one app.",
        features = listOf(
            "🔧" to "Oil Change & Tuneup",
            "🚗" to "Body & Paint Work",
            "⚙️" to "Engine & Transmission",
            "🔋" to "Electrical & Battery"
        )
    ),

    // Page 2 — Garages
    OnboardingData(
        emoji = "🏪",
        titleNormal = "1,000+ ",
        titleHighlight = "Verified Garages",
        titleSuffix = " near you",
        subtitle = "Browse rated and trusted garages around you. Read reviews, compare prices, and choose with confidence.",
        features = listOf(
            "⭐" to "5-Star Rated Garages",
            "📍" to "Near Your Location",
            "✅" to "Verified & Certified",
            "💬" to "Real Customer Reviews"
        )
    ),

    // Page 3 — Book Quick Service
    OnboardingData(
        emoji = "⚡",
        titleNormal = "Book ",
        titleHighlight = "Quick Service",
        titleSuffix = " in 30 Minutes",
        subtitle = "Schedule a garage visit or request a mechanic to come to you. Fast, simple, and reliable.",
        features = listOf(
            "📅" to "Easy Scheduling",
            "🏎️" to "30-Min Response",
            "🔔" to "Real-Time Updates",
            "💰" to "Save up to 30%%"
        )
    )
)

// ─── Main entry point ─────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {},
    onSkipClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    var logoVisible by remember { mutableStateOf(false) }
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.75f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    var tagVisible by remember { mutableStateOf(false) }
    val tagAlpha by animateFloatAsState(
        targetValue = if (tagVisible) 1f else 0f,
        animationSpec = tween(700, easing = LinearEasing),
        label = "tagAlpha"
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(700)
        tagVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> SplashPage(
                    logoAlpha = logoAlpha,
                    logoScale = logoScale,
                    tagAlpha = tagAlpha
                )
                else -> OnboardingPage(
                    data = onboardingPages[page - 1],
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }
        }

        // Skip — top right, splash page only
        if (pagerState.currentPage == 0) {
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 52.dp, end = 20.dp)
            ) {
                Text(
                    text = "Skip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GarageTextDark
                )
            }
        }

        // Page dots
        val dotsBottomPadding = if (pagerState.currentPage == 0) 48.dp else 170.dp
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dotsBottomPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                val isActive = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .animateContentSize()
                        .height(8.dp)
                        .width(if (isActive) 20.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isActive) GarageOrange
                            else GarageOrange.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

// ─── Page 0: Splash ───────────────────────────────────────────────────────────
@Composable
fun SplashPage(
    logoAlpha: Float,
    logoScale: Float,
    tagAlpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashGradient),
        contentAlignment = Alignment.Center
    ) {
        // Decorative blobs — same as original
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 120.dp, y = (-200).dp)
                .alpha(0.25f)
                .background(
                    brush = Brush.radialGradient(listOf(GarageOrangeLight, Color.Transparent)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = 220.dp)
                .alpha(0.18f)
                .background(
                    brush = Brush.radialGradient(listOf(GarageOrange, Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha)
        ) {
            Text(
                text = "🔧",
                fontSize = 72.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "MyGarage",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GarageTextDark,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your Garage Services, All in One Place",
                fontSize = 14.sp,
                color = GarageTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(tagAlpha)
                    .padding(horizontal = 40.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.alpha(tagAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 2.dp)
                        .background(GarageOrange.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                )
                Text(
                    text = "Swipe to explore",
                    fontSize = 12.sp,
                    color = GarageOrange.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 2.dp)
                        .background(GarageOrange.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

// ─── Pages 1–3: Onboarding ────────────────────────────────────────────────────
@Composable
fun OnboardingPage(
    data: OnboardingData,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashGradient)  // ← same warm gradient as splash
    ) {
        // Same decorative blobs as splash page
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 120.dp, y = (-200).dp)
                .alpha(0.2f)
                .background(
                    brush = Brush.radialGradient(listOf(GarageOrangeLight, Color.Transparent)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = 180.dp)
                .alpha(0.15f)
                .background(
                    brush = Brush.radialGradient(listOf(GarageOrange, Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(0.12f))

            // ── Big emoji icon ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = data.emoji, fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Title ─────────────────────────────────────────────────────────
            val titleText = buildAnnotatedString {
                withStyle(SpanStyle(color = GarageTextDark)) { append(data.titleNormal) }
                withStyle(SpanStyle(color = GarageOrange))   { append(data.titleHighlight) }
                withStyle(SpanStyle(color = GarageTextDark)) { append(data.titleSuffix) }
            }
            Text(
                text = titleText,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Subtitle ──────────────────────────────────────────────────────
            Text(
                text = data.subtitle,
                fontSize = 14.sp,
                color = GarageTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Feature pills ─────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                data.features.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { (emoji, label) ->
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = Color.White.copy(alpha = 0.75f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GarageTextDark,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── LOGIN button ──────────────────────────────────────────────────
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GarageOrange)
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── SIGN UP outlined button ───────────────────────────────────────
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.5.dp, GarageOrange
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = GarageOrange
                )
            ) {
                Text(
                    text = "SIGN UP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = GarageOrange
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}