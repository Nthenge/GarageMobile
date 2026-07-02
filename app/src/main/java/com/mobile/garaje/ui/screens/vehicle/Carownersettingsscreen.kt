package com.mobile.garaje.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.ui.theme.*

// ── Colours local to this screen ─────────────────────────────────────────────

private val ScreenBg      = Color(0xFFEFF3F8)   // the blue-gray page background
private val CardBg        = Color.White
private val AvatarOrange  = Color(0xFFF5A623)
private val IconBlue      = Color(0xFF4A90C4)
private val ChevronGray   = Color(0xFFC8C9D0)
private val SectionLabel  = Color(0xFF1A1B26)
private val RowDivider    = Color(0xFFF0F4F8)
private val HeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0ECF8), Color(0xFFF0F8EE), Color(0xFFFFF8EE))
)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CarOwnerSettingsScreen(
    firstName: String = "",
    initials: String = "?",
    // Navigation callbacks — wire these up in MainActivity nav graph
    onEditProfile: () -> Unit = {},
    onPersonalDetails: () -> Unit = {},
    onMyFavorites: () -> Unit = {},
    onPushNotifications: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onAppearance: () -> Unit = {},
    onBiometricAuth: () -> Unit = {},
    onAboutUs: () -> Unit = {},
    onContactUs: () -> Unit = {},
    onFAQs: () -> Unit = {},
    onHelpSupport: () -> Unit = {},
    onLogout: () -> Unit = {},
    // Bottom nav callbacks
    onHome: () -> Unit = {},
    onServices: () -> Unit = {},
    onSupport: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SettingsBottomNav(
                onHome     = onHome,
                onServices = onServices,
                onSupport  = onSupport
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HeroGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

            // ── Top title ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile & Settings",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GarageTextDark
                )
            }

            // ── Profile header card ───────────────────────────────────────────
            SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AvatarOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    // Name
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = firstName.ifBlank { "My Profile" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GarageTextDark
                        )
                    }

                    // Edit pencil
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit profile",
                        tint = GarageTextMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEditProfile() }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Profile section ───────────────────────────────────────────────
            SettingsSection(label = "Profile") {
                SettingsRow(
                    icon  = Icons.Outlined.Person,
                    title = "Personal details",
                    onClick = onPersonalDetails
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.WorkOutline,
                    title = "My Favorites (2)",
                    onClick = onMyFavorites
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Settings section ──────────────────────────────────────────────
            SettingsSection(label = "Settings") {
                SettingsRow(
                    icon  = Icons.Outlined.Notifications,
                    title = "Push notifications",
                    onClick = onPushNotifications
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.Lock,
                    title = "Change password",
                    onClick = onChangePassword
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.Tune,
                    title = "Appearance",
                    onClick = onAppearance
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.Fingerprint,
                    title = "Biometric Authentication",
                    onClick = onBiometricAuth
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── About Us section ──────────────────────────────────────────────
            SettingsSection(label = "About Us") {
                SettingsRow(
                    icon  = Icons.Outlined.HelpOutline,
                    title = "About Us",
                    onClick = onAboutUs
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.Phone,
                    title = "Contact Us",
                    onClick = onContactUs
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.QuestionAnswer,
                    title = "Frequently Asked Questions (FAQs)",
                    onClick = onFAQs
                )
                SettingsDivider()
                SettingsRow(
                    icon  = Icons.Outlined.SupportAgent,
                    title = "Help & Support",
                    onClick = onHelpSupport
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Logout standalone card ────────────────────────────────────────
            SettingsCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout",
                            tint = GarageTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Logout",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = GarageTextDark
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
        }
        }
    }
}

// ── Reusable: section wrapper ─────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = SectionLabel,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        SettingsCard { content() }
    }
}

// ── Reusable: white card with shadow ─────────────────────────────────────────

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape     = RoundedCornerShape(16.dp),
                ambientColor  = Color.Black.copy(alpha = 0.05f),
                spotColor     = Color.Black.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
    ) {
        content()
    }
}

// ── Reusable: single settings row ────────────────────────────────────────────

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IconBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = title,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = GarageTextDark,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = ChevronGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Reusable: hairline divider between rows ───────────────────────────────────

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 66.dp)   // aligns with text, not icon
            .height(0.5.dp)
            .background(RowDivider)
    )
}

// ── Bottom navigation ─────────────────────────────────────────────────────────

@Composable
private fun SettingsBottomNav(
    onHome: () -> Unit,
    onServices: () -> Unit,
    onSupport: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = false,
            onClick  = onHome,
            icon     = { Icon(Icons.Outlined.Home, "Home") },
            label    = { Text("Home", fontSize = 10.5.sp) },
            colors   = navColors()
        )
        NavigationBarItem(
            selected = false,
            onClick  = onServices,
            icon     = { Icon(Icons.Outlined.GridView, "Services") },
            label    = { Text("Services", fontSize = 10.5.sp) },
            colors   = navColors()
        )
        NavigationBarItem(
            selected = false,
            onClick  = onSupport,
            icon     = { Icon(Icons.Outlined.CalendarMonth, "Support") },
            label    = { Text("Support", fontSize = 10.5.sp) },
            colors   = navColors()
        )
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = { Icon(Icons.Outlined.Settings, "Settings") },
            label    = { Text("Settings", fontSize = 10.5.sp) },
            colors   = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = GarageBlue,
    selectedTextColor   = GarageBlue,
    indicatorColor      = GarageBlueLight,
    unselectedIconColor = GarageTextMuted,
    unselectedTextColor = GarageTextMuted
)