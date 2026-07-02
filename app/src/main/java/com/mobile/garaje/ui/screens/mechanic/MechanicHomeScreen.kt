package com.mobile.garaje.ui.screens.mechanic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.garaje.ui.theme.GarageSurface
import com.mobile.garaje.ui.theme.GarageDivider
import com.mobile.garaje.ui.theme.GarageOrange
import com.mobile.garaje.ui.theme.GarageTextDark
import com.mobile.garaje.ui.theme.GarageTextMuted

@Composable
fun MechanicHomeScreen(
    onNavigateToJobFeed: () -> Unit = {},
    onNavigateToMyJobs: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Scaffold(
        containerColor = GarageSurface,
        bottomBar = { MechanicBottomNav(selectedIndex = 0) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Welcome back", fontSize = 12.sp, color = GarageTextMuted)
                        Text("Mechanic", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GarageTextDark)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = GarageTextMuted)
                    }
                }
            }

            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Quick actions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)

                // Job feed — primary
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    color    = GarageOrange,
                    onClick  = onNavigateToJobFeed,
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Outlined.WorkOutline, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Column {
                            Text("Job Feed", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Browse & apply for nearby jobs", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Outlined.ArrowForward, null, tint = Color.White)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MechanicActionCard(Modifier.weight(1f), Icons.Outlined.CheckCircle, "My Jobs", onNavigateToMyJobs)
                    MechanicActionCard(Modifier.weight(1f), Icons.Outlined.Wallet, "Earnings", onNavigateToEarnings)
                }
            }
        }
    }
}

@Composable
private fun MechanicActionCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        color    = Color.White,
        border   = BorderStroke(0.5.dp, GarageDivider),
        onClick  = onClick,
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFFF0EA), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = GarageOrange, modifier = Modifier.size(20.dp))
            }
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
        }
    }
}

@Composable
private fun MechanicBottomNav(selectedIndex: Int) {
    val items = listOf(
        Icons.Outlined.Home to "Home",
        Icons.Outlined.WorkOutline to "Jobs",
        Icons.Outlined.CheckCircle to "My Jobs",
        Icons.Outlined.Wallet to "Earnings",
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        items.forEachIndexed { i, (icon, label) ->
            NavigationBarItem(
                selected = i == selectedIndex,
                onClick  = {},
                icon     = { Icon(icon, label) },
                label    = { Text(label, fontSize = 10.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = GarageOrange,
                    selectedTextColor   = GarageOrange,
                    indicatorColor      = Color(0xFFFFF0EA),
                    unselectedIconColor = GarageTextMuted,
                    unselectedTextColor = GarageTextMuted,
                ),
            )
        }
    }
}