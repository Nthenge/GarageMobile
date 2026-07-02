package com.mobile.garaje.ui.screens.vehicle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.ActiveJobResponse
import com.mobile.garaje.data.model.CarOwnerHomeResponse
import com.mobile.garaje.data.model.MyCarHomeResponse
import com.mobile.garaje.data.model.PopularServiceResponse
import com.mobile.garaje.data.model.UpcomingBookingResponse
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.CarOwnerHomeState
import com.mobile.garaje.ui.viewmodel.CarOwnerHomeViewModel

// ── Screen entry point ────────────────────────────────────────────────────────

@Composable
fun CarOwnerHomeScreen(
    onNavigateToBookService: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToMyCars: () -> Unit = {},
    onNavigateToGaragesNearby: () -> Unit = {},
    onNavigateToActiveService: (Long) -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: CarOwnerHomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = GarageSurface,
        bottomBar = {
            CarOwnerBottomNav(
                selectedIndex = 0,
                onHome        = {},
                onBookings    = onNavigateToBookings,
                onSupport     = onNavigateToSupport,
                onSettings    = onNavigateToSettings
            )
        }
    ) { padding ->
        when (val s = state) {
            is CarOwnerHomeState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            is CarOwnerHomeState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, null, tint = GarageTextMuted,
                            modifier = Modifier.size(48.dp))
                        Text(s.message, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.loadHome() },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp)
                        ) { Text("Retry") }
                    }
                }
            }
            is CarOwnerHomeState.Success -> {
                CarOwnerHomeContent(
                    data                      = s.data,
                    padding                   = padding,
                    onNavigateToBookService   = onNavigateToBookService,
                    onNavigateToBookings      = onNavigateToBookings,
                    onNavigateToMyCars        = onNavigateToMyCars,
                    onNavigateToGaragesNearby = onNavigateToGaragesNearby,
                    onNavigateToActiveService = onNavigateToActiveService
                )
            }
        }
    }
}

// ── Scrollable content ────────────────────────────────────────────────────────

@Composable
private fun CarOwnerHomeContent(
    data: CarOwnerHomeResponse,
    padding: PaddingValues,
    onNavigateToBookService: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToMyCars: () -> Unit,
    onNavigateToGaragesNearby: () -> Unit,
    onNavigateToActiveService: (Long) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding())
            .verticalScroll(scrollState)
    ) {
        GradientHero(
            firstName = data.firstName ?: "there",
            initials  = data.initials ?: "?",
            greeting  = buildGreeting()
        )

        Column(
            modifier = Modifier
                .offset(y = (-56).dp)
                .padding(horizontal = 16.dp)
        ) {
            // show first active job only — if multiple exist user taps through to bookings
            data.activeJobs?.firstOrNull()?.let { job ->
                ActiveJobCard(
                    job     = job,
                    onClick = { onNavigateToActiveService(job.id ?: -1L) }
                )
                Spacer(Modifier.height(14.dp))
            }

            QuickActionsCard(
                onBookService   = onNavigateToBookService,
                onMyBookings    = onNavigateToBookings,
                onMyCars        = onNavigateToMyCars,
                onGaragesNearby = onNavigateToGaragesNearby
            )

            Spacer(Modifier.height(18.dp))

            data.upcomingBookings?.takeIf { it.isNotEmpty() }?.let { bookings ->
                UpcomingBookingsSection(
                    bookings = bookings,
                    onSeeAll = onNavigateToBookings
                )
                Spacer(Modifier.height(18.dp))
            }

            data.popularServices?.takeIf { it.isNotEmpty() }?.let { services ->
                PopularServicesSection(
                    services      = services,
                    onBookService = onNavigateToBookService
                )
                Spacer(Modifier.height(18.dp))
            }

            data.myCars?.takeIf { it.isNotEmpty() }?.let { cars ->
                MyCarsSection(
                    cars     = cars,
                    onSeeAll = onNavigateToMyCars
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Greeting ──────────────────────────────────────────────────────────────────

fun buildGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }
}

// ── Gradient hero ─────────────────────────────────────────────────────────────

@Composable
private fun GradientHero(firstName: String, initials: String, greeting: String) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1D4ED8),
            Color(0xFF0EA5A0),
            Color(0xFF16A34A),
            Color(0xFFFACC15)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(bottomStart = 90.dp, bottomEnd = 90.dp))
            .background(heroGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(Icons.Outlined.Search, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.Notifications, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, fontSize = 14.sp,
                            fontWeight = FontWeight.Medium, color = Color(0xFF1D4ED8))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(greeting, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                        Text("$firstName \uD83D\uDC4B", fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Active job card ───────────────────────────────────────────────────────────

@Composable
private fun ActiveJobCard(job: ActiveJobResponse, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFACC15)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Active service", fontSize = 12.sp,
                        fontWeight = FontWeight.Medium, color = Color(0xFF633806))
                    Text(
                        "${job.serviceName ?: "Service"} in progress",
                        fontSize = 15.sp, fontWeight = FontWeight.Medium,
                        color = Color(0xFF412402), modifier = Modifier.padding(top = 4.dp)
                    )
                    job.garageName?.let { name ->
                        Text(name, fontSize = 11.sp,
                            color = Color(0xFF633806).copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp))
                    }
                    job.estimatedMinutes?.let { mins ->
                        Text("Est. ${mins.toInt()} min", fontSize = 11.sp,
                            color = Color(0xFF633806).copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp))
                    }
                }
                job.bayNumber?.let { bay ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.55f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Bay $bay", fontSize = 11.sp,
                            fontWeight = FontWeight.Medium, color = Color(0xFF412402))
                    }
                }
            }
            Button(
                onClick  = onClick,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(40.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Track progress", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = Color(0xFF633806))
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Outlined.ArrowForward, null,
                    tint = Color(0xFF633806), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Quick actions ─────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsCard(
    onBookService: () -> Unit,
    onMyBookings: () -> Unit,
    onMyCars: () -> Unit,
    onGaragesNearby: () -> Unit
) {
    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("What would you like to do?", fontSize = 13.sp,
                fontWeight = FontWeight.Medium, color = GarageTextMuted,
                modifier = Modifier.padding(bottom = 12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickActionItem(Icons.Outlined.Build,          "Book service",    GarageBlueLight,  GarageBlue,     onBookService)
                QuickActionItem(Icons.Outlined.CalendarToday,  "My bookings",     GarageTealLight,  GarageTealDark, onMyBookings)
                QuickActionItem(Icons.Outlined.DirectionsCar,  "My cars",         GarageAmberLight, GarageAmberDark,onMyCars)
                QuickActionItem(Icons.Outlined.LocationOn,     "Garages nearby",  GarageSurface,    GarageTextMuted,onGaragesNearby)
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector, label: String,
    iconBg: Color, iconTint: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(72.dp).clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(19.dp))
        }
        Text(label, fontSize = 10.5.sp, color = GarageTextMuted,
            textAlign = TextAlign.Center, maxLines = 2)
    }
}

// ── Upcoming bookings ─────────────────────────────────────────────────────────

@Composable
private fun UpcomingBookingsSection(
    bookings: List<UpcomingBookingResponse>,
    onSeeAll: () -> Unit
) {
    Column {
        SectionHeader(title = "Upcoming bookings", actionLabel = "View all", onAction = onSeeAll)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            bookings.take(3).forEach { booking ->
                UpcomingBookingRow(booking)
            }
        }
    }
}

@Composable
private fun UpcomingBookingRow(booking: UpcomingBookingResponse) {
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(GarageTealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CalendarToday, null,
                    tint = GarageTealDark, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.serviceName ?: "–", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextDark)
                Text(
                    "${booking.garageName ?: "–"} · ${booking.dateLabel ?: ""} ${booking.time ?: ""}",
                    fontSize = 11.sp, color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            StatusChip(status = booking.status ?: "PENDING")
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "CONFIRMED" -> GarageTealLight to GarageTealDark
        "PENDING"   -> GarageAmberLight to GarageAmberDark
        else        -> GarageSurface to GarageTextMuted
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = fg)
    }
}

// ── Popular services ──────────────────────────────────────────────────────────

@Composable
private fun PopularServicesSection(
    services: List<PopularServiceResponse>,
    onBookService: () -> Unit
) {
    Column {
        SectionHeader(title = "Popular services", actionLabel = "Book", onAction = onBookService)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            services.take(4).forEach { service ->
                PopularServiceRow(service)
            }
        }
    }
}

@Composable
private fun PopularServiceRow(service: PopularServiceResponse) {
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(GarageAmberLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Build, null,
                    tint = GarageAmberDark, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(service.serviceName ?: "–", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextDark)
                Text(
                    "${service.categoryName ?: "–"} · ${service.garageCount ?: 0} garages",
                    fontSize = 11.sp, color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                "From KES ${service.priceFrom?.toInt() ?: "–"}",
                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = GarageTextDark
            )
        }
    }
}

// ── My cars ───────────────────────────────────────────────────────────────────

@Composable
private fun MyCarsSection(cars: List<MyCarHomeResponse>, onSeeAll: () -> Unit) {
    Column {
        SectionHeader(title = "My cars", actionLabel = "See all", onAction = onSeeAll)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            cars.take(2).forEach { car ->
                MyCarRow(car)
            }
        }
    }
}

@Composable
private fun MyCarRow(car: MyCarHomeResponse) {
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(GarageBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.DirectionsCar, null,
                    tint = GarageBlue, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(car.model ?: "–", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextDark)
                Text(
                    "${car.plate ?: "–"} · ${car.year ?: "–"} · ${car.fuelType ?: "–"}",
                    fontSize = 11.sp, color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Outlined.ChevronRight, null,
                tint = GarageTextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

// ── Shared section header ─────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GarageTextMuted)
        Text(actionLabel, fontSize = 12.sp, color = GarageBlue,
            modifier = Modifier.clickable { onAction() })
    }
}

// ── Bottom nav ─────────────────────────────────────────────────────────────────

@Composable
private fun CarOwnerBottomNav(
    selectedIndex: Int,
    onHome: () -> Unit,
    onBookings: () -> Unit,
    onSupport: () -> Unit,
    onSettings: () -> Unit
) {
    val items = listOf(
        Triple(Icons.Outlined.Home,          "Home",     onHome),
        Triple(Icons.Outlined.CalendarToday, "Bookings", onBookings),
        Triple(Icons.Outlined.SupportAgent,  "Support",  onSupport),
        Triple(Icons.Outlined.Settings,      "Settings", onSettings)
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        items.forEachIndexed { i, (icon, label, onClick) ->
            NavigationBarItem(
                selected = i == selectedIndex,
                onClick  = onClick,
                icon     = { Icon(icon, label) },
                label    = { Text(label, fontSize = 10.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = GarageBlue,
                    selectedTextColor   = GarageBlue,
                    indicatorColor      = GarageBlueLight,
                    unselectedIconColor = GarageTextMuted,
                    unselectedTextColor = GarageTextMuted
                )
            )
        }
    }
}