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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.GarageMechanicResponse
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangeLight
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.GarageTeamViewModel
import com.mobile.garaje.ui.viewmodel.TeamListState   // ← correct import

@Composable
fun GarageTeamScreen(
    onNavigateBack: () -> Unit = {},
    onAddMechanic: () -> Unit = {},
    viewModel: GarageTeamViewModel = viewModel(),
) {
    val teamState by viewModel.teamState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = GarageSurface,
        topBar = {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Team", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GarageTextDark)
                        Text("Your registered mechanics", fontSize = 12.sp, color = GarageTextMuted)
                    }
                    IconButton(onClick = { viewModel.loadMechanics() }) {
                        Icon(Icons.Outlined.Refresh, "Refresh", tint = GarageTextMuted)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick       = onAddMechanic,
                containerColor = GarageOrange,
                contentColor  = Color.White,
                shape         = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Outlined.PersonAdd, "Add mechanic", modifier = Modifier.size(20.dp))
                    Text("Add mechanic", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { padding ->
        when (val state = teamState) {   // smart cast via local val
            is TeamListState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }

            is TeamListState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, null,
                            tint = GarageTextMuted, modifier = Modifier.size(48.dp))
                        Text(state.message, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.loadMechanics() },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp),
                        ) { Text("Retry") }
                    }
                }
            }

            is TeamListState.Success -> {
                val mechanics = state.mechanics
                if (mechanics.isEmpty()) {
                    EmptyTeamState(
                        onAddMechanic = onAddMechanic,
                        modifier      = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize().padding(padding),
                        contentPadding      = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            Text(
                                "${mechanics.size} mechanic${if (mechanics.size != 1) "s" else ""} in your team",
                                fontSize = 13.sp,
                                color    = GarageTextMuted,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                        items(
                            items = mechanics,
                            key   = { mechanic -> mechanic.id ?: 0L }  // Long? → Long
                        ) { mechanic ->
                            MechanicCard(mechanic = mechanic)
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyTeamState(onAddMechanic: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier            = Modifier.padding(32.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GarageOrangeLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.People, null,
                    tint = GarageOrange, modifier = Modifier.size(36.dp))
            }
            Text("No mechanics yet", fontSize = 18.sp,
                fontWeight = FontWeight.Bold, color = GarageTextDark
            )
            Text(
                "Add your first mechanic. They'll receive login credentials by email " +
                        "and can complete their profile after logging in.",
                fontSize   = 14.sp,
                color      = GarageTextMuted,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp,
            )
            Button(
                onClick  = onAddMechanic,
                colors   = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.PersonAdd, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add first mechanic", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Mechanic card ─────────────────────────────────────────────────────────────

@Composable
private fun MechanicCard(mechanic: GarageMechanicResponse) {
    val initials = buildString {
        mechanic.firstname?.firstOrNull()?.let { append(it.uppercaseChar()) }
        mechanic.secondname?.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifEmpty { "?" }

    val profileComplete = mechanic.detailsCompleted == true

    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Avatar
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GarageOrangeLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = GarageOrange
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${mechanic.firstname ?: ""} ${mechanic.secondname ?: ""}".trim(),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GarageTextDark,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Text(
                    mechanic.email ?: "–",
                    fontSize = 12.sp,
                    color    = GarageTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Text(
                    mechanic.phoneNumber ?: "–",
                    fontSize = 12.sp,
                    color    = GarageTextMuted,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }

            // Status badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (profileComplete) GarageTealLight else GarageAmberLight,
                ) {
                    Text(
                        if (profileComplete) "Active" else "Pending",
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (profileComplete) GarageTealDark else GarageAmberDark,
                    )
                }
                if (!profileComplete) {
                    Text("Awaiting signup", fontSize = 10.sp, color = GarageTextMuted)
                }
            }
        }
    }
}