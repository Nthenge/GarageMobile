package com.mobile.garaje.ui.screens.vehicle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.GarageOfferingResponse
import com.mobile.garaje.data.model.ServiceDetailResponse
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.CarOwnerServiceDetailViewModel
import com.mobile.garaje.ui.viewmodel.GarageSortOption
import com.mobile.garaje.ui.viewmodel.ServiceDetailState

@Composable
fun CarOwnerServiceDetailScreen(
    serviceId: Long,
    userLatitude: Double,
    userLongitude: Double,
    onNavigateBack: () -> Unit = {},
    onGarageSelected: (garageId: Long, serviceId: Long) -> Unit = { _, _ -> },
    viewModel: CarOwnerServiceDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(serviceId) {
        viewModel.load(serviceId, userLatitude, userLongitude)
    }

    Scaffold(
        containerColor = GarageSurface,
        topBar = {
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, "Back", tint = GarageTextMuted)
                    }
                    Text("Service details", fontSize = 17.sp,
                        fontWeight = FontWeight.Medium, color = GarageTextDark
                    )
                }
            }
        }
    ) { padding ->
        when (val s = state) {
            is ServiceDetailState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            is ServiceDetailState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, null, tint = GarageTextMuted, modifier = Modifier.size(48.dp))
                        Text(s.message, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.load(serviceId, userLatitude, userLongitude) },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp)
                        ) { Text("Retry") }
                    }
                }
            }
            is ServiceDetailState.Success -> {
                ServiceDetailContent(
                    service          = s.service,
                    garages          = s.garages,
                    sortOption       = s.sortOption,
                    padding          = padding,
                    onSortChange     = { viewModel.changeSortOption(it) },
                    onGarageClick    = { garageId -> onGarageSelected(garageId, serviceId) }
                )
            }
        }
    }
}

@Composable
private fun ServiceDetailContent(
    service: ServiceDetailResponse,
    garages: List<GarageOfferingResponse>,
    sortOption: GarageSortOption,
    padding: PaddingValues,
    onSortChange: (GarageSortOption) -> Unit,
    onGarageClick: (Long) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier       = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Service header ────────────────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GarageAmberLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.WaterDrop, null, tint = GarageAmberDark, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(
                        service.serviceName ?: "Service",
                        fontSize = 18.sp, fontWeight = FontWeight.Medium, color = GarageTextDark
                    )
                    Text(
                        service.categoryName ?: "",
                        fontSize = 12.sp, color = GarageTextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        item {
            Text(
                service.description ?: "No description available.",
                fontSize = 13.sp, color = GarageTextMuted, lineHeight = 20.sp
            )
        }

        // ── Stat cards ─────────────────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(
                    modifier = Modifier.weight(1f),
                    label    = "Avg. price",
                    value    = "KES ${service.avgPrice?.toInt() ?: "–"}"
                )
                StatBox(
                    modifier = Modifier.weight(1f),
                    label    = "Avg. duration",
                    value    = formatDuration(service.avgDuration)
                )
            }
        }

        // ── Garages header with sort ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Garages near you", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextMuted
                )

                Box {
                    Row(
                        modifier = Modifier
                            .clickable { sortMenuExpanded = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Sort: ${sortLabel(sortOption)}",
                            fontSize = 12.sp, color = GarageBlue
                        )
                        Icon(Icons.Outlined.ArrowDropDown, null, tint = GarageBlue, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        GarageSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(sortLabel(option), fontSize = 13.sp) },
                                onClick = {
                                    onSortChange(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── Garage list ────────────────────────────────────────────────────────
        if (garages.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(0.5.dp, GarageDivider, RoundedCornerShape(14.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No garages currently offer this service",
                        fontSize = 13.sp, color = GarageTextMuted
                    )
                }
            }
        } else {
            items(garages, key = { it.garageId ?: 0L }) { garage ->
                GarageOfferingCard(
                    garage  = garage,
                    onClick = { onGarageClick(garage.garageId ?: return@GarageOfferingCard) }
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Stat box ──────────────────────────────────────────────────────────────────

@Composable
private fun StatBox(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GarageSurface)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 12.sp, color = GarageTextMuted)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Medium,
            color = GarageTextDark, modifier = Modifier.padding(top = 4.dp))
    }
}

// ── Garage card ───────────────────────────────────────────────────────────────

@Composable
private fun GarageOfferingCard(
    garage: GarageOfferingResponse,
    onClick: () -> Unit
) {
    val initials = garage.businessName
        ?.split(" ")
        ?.take(2)
        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.joinToString("") ?: "?"

    val isOpen = garage.isOpenNow == true

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(14.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(GarageBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GarageBlue)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(garage.businessName ?: "–", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextDark
                )
                Text(
                    "${garage.physicalAddress ?: "–"} · ${formatDistance(garage.distanceKm)}",
                    fontSize = 11.sp, color = GarageTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 3.dp)) {
                    Icon(Icons.Outlined.Star, null, tint = GarageAmberDark, modifier = Modifier.size(11.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${garage.rating ?: "–"} (${garage.reviewCount ?: 0})",
                        fontSize = 11.sp, color = GarageTextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("KES ${garage.price?.toInt() ?: "–"}", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = GarageTextDark
                )
                Text(
                    if (isOpen) "Open now" else "Opens ${garage.openingTime ?: "later"}",
                    fontSize = 10.sp,
                    color = if (isOpen) GarageTealDark else GarageTextMuted,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun sortLabel(option: GarageSortOption): String = when (option) {
    GarageSortOption.DISTANCE -> "Distance"
    GarageSortOption.PRICE    -> "Price"
    GarageSortOption.RATING   -> "Rating"
}

private fun formatDistance(km: Double?): String {
    if (km == null) return "–"
    return if (km < 1.0) "${(km * 1000).toInt()} m" else "${"%.1f".format(km)} km"
}

private fun formatDuration(minutes: Double?): String {
    if (minutes == null) return "–"
    val mins = minutes.toInt()
    return if (mins >= 60) {
        val hrs = mins / 60
        val rem = mins % 60
        if (rem == 0) "$hrs hr" else "$hrs hr $rem min"
    } else {
        "$mins min"
    }
}