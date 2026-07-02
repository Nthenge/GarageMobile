package com.mobile.garaje.ui.screens.utils

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.SupportHomeState
import com.mobile.garaje.ui.viewmodel.SupportViewModel

private val PageBg       = Color(0xFFF5F6FA)
private val HeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0ECF8), Color(0xFFF0F8EE), Color(0xFFFFF8EE))
)
private val AmberGold    = Color(0xFFD4A017)
private val AmberGoldBg  = Color(0xFFFFC928)

@Composable
fun SupportScreen(
    onReportIncident : () -> Unit = {},
    onChatWithUs     : () -> Unit = {},
    onFaqs           : () -> Unit = {},
    onViewAllReports : () -> Unit = {},
    onHome           : () -> Unit = {},
    onServices       : () -> Unit = {},
    onSettings       : () -> Unit = {},
    viewModel        : SupportViewModel = viewModel()
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = PageBg,
        bottomBar = {
            SupportBottomNav(
                onHome     = onHome,
                onServices = onServices,
                onSettings = onSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {

            // ── Gradient hero ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeroGradient)
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp)
                ) {
                    // Top title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GarageTextDark
                        )
                    }

                    Text(
                        text = "Support",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = GarageTextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "How can we help you today?",
                        fontSize = 14.sp,
                        color = GarageTextMuted
                    )
                    Spacer(Modifier.height(28.dp))

                    // ── Report an incident (full width) ───────────────────────
                    SupportCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick  = onReportIncident
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF8E6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Report an incident",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GarageTextDark
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF0F4F8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.NorthEast,
                                        contentDescription = null,
                                        tint = GarageTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── Chat + FAQs side by side ──────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SupportCard(
                            modifier = Modifier.weight(1f),
                            onClick  = onChatWithUs
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEEF5FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = GarageBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "Chat with us",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GarageTextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF0F4F8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.NorthEast,
                                            contentDescription = null,
                                            tint = GarageTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        SupportCard(
                            modifier = Modifier.weight(1f),
                            onClick  = onFaqs
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFF8E6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.HelpOutline,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "Frequently asked questions",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GarageTextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF0F4F8)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.NorthEast,
                                            contentDescription = null,
                                            tint = GarageTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Follow up on previous reports ─────────────────────────────────
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Follow up on previous reports",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GarageTextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AmberGoldBg)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val active   = if (homeState is SupportHomeState.Success) (homeState as SupportHomeState.Success).activeCount else 0
                            val resolved = if (homeState is SupportHomeState.Success) (homeState as SupportHomeState.Success).resolvedCount else 0
                            Text(
                                text = "Active Reports: $active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF3D2800)
                            )
                            Text(
                                text = "Resolved: $resolved",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF3D2800)
                            )
                        }
                        Button(
                            onClick = onViewAllReports,
                            shape  = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A5C))
                        ) {
                            Text("View All", fontSize = 13.sp, color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Outlined.ChevronRight, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Reusable white card ───────────────────────────────────────────────────────

@Composable
fun SupportCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        color     = Color.White,
        shadowElevation = 2.dp
    ) {
        Column { content() }
    }
}

// ── Bottom nav ────────────────────────────────────────────────────────────────

@Composable
fun SupportBottomNav(
    onHome: () -> Unit,
    onServices: () -> Unit,
    onSettings: () -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = false,
            onClick  = onHome,
            icon     = { Icon(Icons.Outlined.Home, "Home") },
            label    = { Text("Home", fontSize = 10.5.sp) },
            colors   = supportNavColors()
        )
        NavigationBarItem(
            selected = false,
            onClick  = onServices,
            icon     = { Icon(Icons.Outlined.GridView, "Services") },
            label    = { Text("Services", fontSize = 10.5.sp) },
            colors   = supportNavColors()
        )
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = { Icon(Icons.Outlined.SupportAgent, "Support") },
            label    = { Text("Support", fontSize = 10.5.sp) },
            colors   = supportNavColors()
        )
        NavigationBarItem(
            selected = false,
            onClick  = onSettings,
            icon     = { Icon(Icons.Outlined.Settings, "Settings") },
            label    = { Text("Settings", fontSize = 10.5.sp) },
            colors   = supportNavColors()
        )
    }
}

@Composable
fun supportNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = GarageBlue,
    selectedTextColor   = GarageBlue,
    indicatorColor      = GarageBlueLight,
    unselectedIconColor = GarageTextMuted,
    unselectedTextColor = GarageTextMuted
)