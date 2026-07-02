package com.mobile.garaje.ui.screens.garage

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.*
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.GarageHomeState
import com.mobile.garaje.ui.viewmodel.GarageHomeViewModel

@Composable
fun GarageAdminHomeScreen(
    onNavigateToCommandCentre: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToTeam: () -> Unit = {},
    onNavigateToAddMechanic: () -> Unit = {},
    onNavigateToRevenue: () -> Unit = {},
    onPendingActionClick: () -> Unit = {},
    onAlertClick: (GarageAlert) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: GarageHomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = GarageSurface,
        bottomBar      = { GarageAdminBottomNav(selectedIndex = 0) }
    ) { paddingValues ->
        when (state) {
            is GarageHomeState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            is GarageHomeState.Error -> {
                val message = (state as GarageHomeState.Error).message
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.CloudOff, null, tint = GarageTextMuted, modifier = Modifier.size(48.dp))
                        Text(message, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.loadHome() },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp),
                        ) { Text("Retry") }
                    }
                }
            }
            is GarageHomeState.Success -> {
                val data = (state as GarageHomeState.Success).data
                GarageHomeContent(
                    data                       = data,
                    paddingValues              = paddingValues,
                    onNavigateToCommandCentre  = onNavigateToCommandCentre,
                    onNavigateToServices       = onNavigateToServices,
                    onNavigateToBookings       = onNavigateToBookings,
                    onNavigateToTeam           = onNavigateToTeam,
                    onNavigateToAddMechanic    = onNavigateToAddMechanic,
                    onNavigateToRevenue        = onNavigateToRevenue,
                    onPendingActionClick       = onPendingActionClick,
                    onAlertClick               = onAlertClick,
                    onLogout                   = onLogout,
                )
            }
        }
    }
}

@Composable
private fun GarageHomeContent(
    data: GarageHomeResponse,
    paddingValues: PaddingValues,
    onNavigateToCommandCentre: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToTeam: () -> Unit,
    onNavigateToAddMechanic: () -> Unit,
    onNavigateToRevenue: () -> Unit,
    onPendingActionClick: () -> Unit,
    onAlertClick: (GarageAlert) -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)          // ← reserves space for bottom nav
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Welcome back", fontSize = 11.sp, color = GarageTextMuted)
                Text(
                    data.summary?.garageName ?: "Garage",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GarageTextDark,
                )
            }
            Box(
                modifier         = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GarageBlueLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    data.summary?.garageInitials ?: "?",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GarageBlue,
                )
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Outlined.Logout, "Logout", tint = GarageTextMuted, modifier = Modifier.size(20.dp))
            }
        }

        // ── Alerts ────────────────────────────────────────────────────────
        data.alerts?.forEach { alert ->
            AlertBanner(alert = alert, onClick = { onAlertClick(alert) })
            Spacer(Modifier.height(8.dp))
        }

        // ── Pending action hero ──────────────────────────────────────────
        data.pendingAction?.let { pending ->
            if ((pending.pendingCount ?: 0) > 0) {
                PendingActionCard(pending = pending, onClick = onPendingActionClick)
                Spacer(Modifier.height(10.dp))
            }
        }

        // ── Jump to / quick actions ───────────────────────────────────────
        SectionHeader(title = "Jump to")
        Column(
            modifier            = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.Map,
                    label    = "Command centre",
                    onClick  = onNavigateToCommandCentre,
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.PersonAdd,
                    label    = "Add mechanic",
                    onClick  = onNavigateToAddMechanic,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.CalendarToday,
                    label    = "Bookings",
                    onClick  = onNavigateToBookings,
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.BarChart,
                    label    = "Revenue",
                    onClick  = onNavigateToRevenue,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Today's bookings ──────────────────────────────────────────────
        SectionHeader(title = "Today's bookings", trailing = "View all", onTrailingClick = onNavigateToBookings)
        val bookings = data.todayBookings ?: emptyList()
        if (bookings.isEmpty()) {
            EmptySectionText("No bookings scheduled for today")
        } else {
            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                bookings.forEach { booking -> BookingRow(booking = booking) }
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Team status ───────────────────────────────────────────────────
        SectionHeader(title = "Team status", trailing = "View team", onTrailingClick = onNavigateToTeam)
        val team = data.teamStatus ?: emptyList()
        if (team.isEmpty()) {
            EmptySectionText("No mechanics registered yet")
        } else {
            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                team.forEach { member -> TeamStatusRow(member = member) }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ── Alert banner ─────────────────────────────────────────────────────────────

@Composable
private fun AlertBanner(alert: GarageAlert, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(12.dp),
        color  = GarageRedLight,
    ) {
        Row(
            modifier              = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Warning, null, tint = GarageRedDark, modifier = Modifier.size(18.dp))
            Column {
                Text(alert.title ?: "", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GarageRedDark)
                Text(alert.subtitle ?: "", fontSize = 11.sp, color = GarageRedDark.copy(alpha = 0.85f), modifier = Modifier.padding(top = 1.dp))
            }
        }
    }
}

// ── Pending action hero card ──────────────────────────────────────────────────

@Composable
private fun PendingActionCard(pending: PendingActionSummary, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = GarageBlueLight,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(pending.title ?: "", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GarageBlueDark)
            Text(pending.subtitle ?: "", fontSize = 11.sp, color = GarageBlueDark.copy(alpha = 0.85f), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ── Quick action card ─────────────────────────────────────────────────────────

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape    = RoundedCornerShape(14.dp),
        color    = Color.White,
        border   = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GarageOrangeLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = GarageOrange, modifier = Modifier.size(20.dp))
            }
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark, textAlign = TextAlign.Center)
        }
    }
}

// ── Booking row ────────────────────────────────────────────────────────────────

@Composable
private fun BookingRow(booking: TodayBooking) {
    val (iconBg, iconTint, icon) = when (booking.categoryIcon) {
        "oil"    -> Triple(GarageBlueLight, GarageBlue, Icons.Outlined.Opacity)
        "engine" -> Triple(GarageAmberLight, GarageAmberDark, Icons.Outlined.Build)
        "brake"  -> Triple(GarageSurface, GarageTextMuted, Icons.Outlined.RadioButtonUnchecked)
        else     -> Triple(GarageSurface, GarageTextMuted, Icons.Outlined.Build)
    }
    val (badgeBg, badgeFg, badgeText) = when (booking.status) {
        "CONFIRMED" -> Triple(GarageTealLight, GarageTealDark, "Confirmed")
        "PENDING"   -> Triple(GarageAmberLight, GarageAmberDark, "Pending")
        "COMPLETED" -> Triple(GarageSurface, GarageTextMuted, "Completed")
        else        -> Triple(GarageSurface, GarageTextMuted, booking.status ?: "–")
    }

    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Time
            Column(modifier = Modifier.width(40.dp)) {
                Text(booking.time ?: "–", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                Text(booking.period ?: "", fontSize = 9.sp, color = GarageTextMuted)
            }

            // Icon
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(15.dp))
            }

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.serviceName ?: "–",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GarageTextDark,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Text(
                    listOfNotNull(booking.customerName, booking.vehiclePlate).joinToString(" · "),
                    fontSize = 10.5.sp,
                    color    = GarageTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Status badge
            Surface(shape = RoundedCornerShape(20.dp), color = badgeBg) {
                Text(
                    badgeText,
                    modifier   = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    fontSize   = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = badgeFg,
                )
            }
        }
    }
}

// ── Team status row ──────────────────────────────────────────────────────────

@Composable
private fun TeamStatusRow(member: TeamMemberStatus) {
    val isActive = member.isActive == true
    val (avBg, avFg)     = if (isActive) GarageTealLight to GarageTealDark else GarageSurface to GarageTextMuted
    val (badgeBg, badgeFg, badgeText) = if (isActive) Triple(GarageTealLight, GarageTealDark, "Active") else Triple(GarageSurface, GarageTextMuted, "Free")

    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(avBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(member.initials ?: "?", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = avFg)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name ?: "–", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                Text(member.statusText ?: "–", fontSize = 10.5.sp, color = GarageTextMuted)
            }
            Surface(shape = RoundedCornerShape(20.dp), color = badgeBg) {
                Text(
                    badgeText,
                    modifier   = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    fontSize   = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = badgeFg,
                )
            }
        }
    }
}

// ── Shared helpers ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, trailing: String? = null, onTrailingClick: () -> Unit = {}) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GarageTextMuted)
        if (trailing != null) {
            Text(
                trailing,
                fontSize = 11.5.sp,
                color    = GarageBlue,
                modifier = Modifier.clickable(onClick = onTrailingClick),
            )
        }
    }
}

@Composable
private fun EmptySectionText(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, fontSize = 12.sp, color = GarageTextMuted)
    }
}

// ── Bottom nav — 5 items ──────────────────────────────────────────────────────

@Composable
private fun GarageAdminBottomNav(selectedIndex: Int) {
    val items = listOf(
        Icons.Outlined.Home to "Home",
        Icons.Outlined.Map to "Command",
        Icons.Outlined.Build to "Services",
        Icons.Outlined.CalendarToday to "Requests",
        Icons.Outlined.People to "Team",
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        items.forEachIndexed { i, (icon, label) ->
            NavigationBarItem(
                selected = i == selectedIndex,
                onClick  = {},
                icon     = { Icon(icon, label) },
                label    = { Text(label, fontSize = 9.5.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = GarageOrange,
                    selectedTextColor   = GarageOrange,
                    indicatorColor      = GarageOrangeLight,
                    unselectedIconColor = GarageTextMuted,
                    unselectedTextColor = GarageTextMuted,
                ),
            )
        }
    }
}