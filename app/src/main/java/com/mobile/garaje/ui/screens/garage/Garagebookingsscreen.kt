package com.mobile.garaje.ui.screens.garage

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.GarageBooking
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.BookingFilter
import com.mobile.garaje.ui.viewmodel.GarageBookingsState
import com.mobile.garaje.ui.viewmodel.GarageBookingsViewModel

@Composable
fun GarageBookingsScreen(
    onNavigateBack: () -> Unit = {},
    onBookingDetails: (GarageBooking) -> Unit = {},
    viewModel: GarageBookingsViewModel = viewModel(),
) {
    val state          by viewModel.state.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery    by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = GarageSurface,
        topBar = {
            Column {
                Surface(color = Color.White, shadowElevation = 1.dp) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Outlined.ArrowBack, "Back", tint = GarageTextMuted)
                        }
                        Text("Bookings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GarageTextDark)
                    }

                    Column {
                        // Search row
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedTextField(
                                value         = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder   = { Text("Search bookings...", fontSize = 13.sp, color = GarageTextMuted) },
                                leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = GarageTextMuted, modifier = Modifier.size(18.dp)) },
                                modifier      = Modifier.weight(1f).height(46.dp),
                                shape         = RoundedCornerShape(12.dp),
                                singleLine    = true,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor    = GarageDivider,
                                    focusedBorderColor      = GarageOrange,
                                    unfocusedContainerColor = GarageSurface,
                                    focusedContainerColor   = Color.White,
                                    cursorColor             = GarageOrange,
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = GarageTextDark),
                            )
                        }

                        // Filter tabs
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            FilterTab("All", selectedFilter == BookingFilter.ALL) { viewModel.setFilter(BookingFilter.ALL) }
                            FilterTab("Pending", selectedFilter == BookingFilter.PENDING) { viewModel.setFilter(BookingFilter.PENDING) }
                            FilterTab("Confirmed", selectedFilter == BookingFilter.CONFIRMED) { viewModel.setFilter(BookingFilter.CONFIRMED) }
                            FilterTab("Completed", selectedFilter == BookingFilter.COMPLETED) { viewModel.setFilter(BookingFilter.COMPLETED) }
                            FilterTab("Cancelled", selectedFilter == BookingFilter.CANCELLED) { viewModel.setFilter(BookingFilter.CANCELLED) }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when (state) {
            is GarageBookingsState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            is GarageBookingsState.Error -> {
                val message = (state as GarageBookingsState.Error).message
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.CloudOff, null, tint = GarageTextMuted, modifier = Modifier.size(48.dp))
                        Text(message, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.loadBookings() },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp),
                        ) { Text("Retry") }
                    }
                }
            }
            is GarageBookingsState.Success -> {
                val bookings = (state as GarageBookingsState.Success).bookings
                if (bookings.isEmpty()) {
                    EmptyBookingsState(modifier = Modifier.padding(padding))
                } else {
                    val grouped = bookings.groupBy { it.dateLabel ?: it.date ?: "" }
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        grouped.forEach { (dateLabel, bookingsForDate) ->
                            item {
                                Text(
                                    dateLabel,
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = GarageTextMuted,
                                    modifier   = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 6.dp),
                                )
                            }
                            items(bookingsForDate, key = { it.id ?: 0L }) { booking ->
                                BookingCard(
                                    booking      = booking,
                                    onConfirm    = { viewModel.confirmBooking(booking.id ?: return@BookingCard) },
                                    onDecline    = { viewModel.declineBooking(booking.id ?: return@BookingCard) },
                                    onDetails    = { onBookingDetails(booking) },
                                    modifier     = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Filter tab ────────────────────────────────────────────────────────────────

@Composable
private fun FilterTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape    = RoundedCornerShape(20.dp),
        color    = if (selected) GarageBlueLight else GarageSurface,
    ) {
        Text(
            label,
            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize   = 11.5.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (selected) GarageBlue else GarageTextMuted,
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyBookingsState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.CalendarToday, null, tint = GarageTextMuted, modifier = Modifier.size(40.dp))
            Text("No bookings found", fontSize = 14.sp, color = GarageTextMuted)
        }
    }
}

// ── Booking card ──────────────────────────────────────────────────────────────

@Composable
private fun BookingCard(
    booking: GarageBooking,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    onDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (iconBg, iconTint, icon) = categoryIconFor(booking.categoryIcon)
    val (badgeBg, badgeFg, badgeText) = statusBadgeFor(booking.status)
    val isCompleted = booking.status == "COMPLETED"

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = Color.White,
        border   = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Column(modifier = Modifier.padding(11.dp).then(if (isCompleted) Modifier.alpha(0.7f) else Modifier)) {

            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.width(42.dp)) {
                    Text(booking.time ?: "–", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                    Text(booking.period ?: "", fontSize = 9.5.sp, color = GarageTextMuted)
                }
                Box(
                    modifier         = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.serviceName ?: "–", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                    Text(
                        listOfNotNull(booking.customerName, booking.vehiclePlate).joinToString(" · "),
                        fontSize = 11.sp,
                        color    = GarageTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
                Surface(shape = RoundedCornerShape(20.dp), color = badgeBg) {
                    Text(badgeText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = badgeFg)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = GarageDivider)

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                if (booking.mechanicName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier         = Modifier.size(20.dp).clip(CircleShape).background(GarageTealLight),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(booking.mechanicInitials ?: "?", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = GarageTealDark)
                        }
                        Text(booking.mechanicName, fontSize = 11.sp, color = GarageTextDark)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.PersonOff, null, tint = GarageTextMuted, modifier = Modifier.size(14.dp))
                        Text("Unassigned", fontSize = 11.sp, color = GarageTextMuted)
                    }
                }

                when (booking.status) {
                    "PENDING" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ActionChip(label = "Decline", onClick = onDecline)
                            ActionChip(label = "Confirm", primary = true, onClick = onConfirm)
                        }
                    }
                    "COMPLETED" -> ActionChip(label = "Receipt", onClick = onDetails)
                    else        -> ActionChip(label = "Details", onClick = onDetails)
                }
            }
        }
    }
}

@Composable
private fun ActionChip(label: String, primary: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape    = RoundedCornerShape(8.dp),
        color    = if (primary) GarageBlueLight else Color.White,
        border   = if (primary) null else BorderStroke(0.5.dp, GarageDivider),
    ) {
        Text(
            label,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize   = 11.sp,
            fontWeight = if (primary) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (primary) GarageBlue else GarageTextMuted,
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun categoryIconFor(key: String?): Triple<Color, Color, ImageVector> = when (key) {
    "oil"     -> Triple(GarageBlueLight, GarageBlue, Icons.Outlined.WaterDrop)
    "engine"  -> Triple(GarageAmberLight, GarageAmberDark, Icons.Outlined.Build)
    "tyre"    -> Triple(GarageBlueLight, GarageBlue, Icons.Outlined.Circle)
    "brake"   -> Triple(GarageSurface, GarageTextMuted, Icons.Outlined.RadioButtonUnchecked)
    "battery" -> Triple(GarageSurface, GarageTextMuted, Icons.Outlined.BatteryFull)
    else      -> Triple(GarageSurface, GarageTextMuted, Icons.Outlined.Build)
}

@Composable
private fun statusBadgeFor(status: String?): Triple<Color, Color, String> = when (status) {
    "CONFIRMED" -> Triple(GarageTealLight, GarageTealDark, "Confirmed")
    "PENDING"   -> Triple(GarageAmberLight, GarageAmberDark, "Pending")
    "COMPLETED" -> Triple(GarageSurface, GarageTextMuted, "Completed")
    "CANCELLED" -> Triple(GarageRedLight, GarageRedDark, "Cancelled")
    else        -> Triple(GarageSurface, GarageTextMuted, status ?: "–")
}