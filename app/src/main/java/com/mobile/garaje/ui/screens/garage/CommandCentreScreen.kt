package com.mobile.garaje.ui.screens.garage

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.*
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangeLight
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.CommandCentreState
import com.mobile.garaje.ui.viewmodel.CommandCentreViewModel


@Composable
fun CommandCentreScreen(
    onNavigateBack: () -> Unit = {},
    onPostJob: (bayNumber: Int) -> Unit = {},
    onCallMechanic: (NearbyMechanicResponse) -> Unit = {},
    onInsightAction: (AiInsightResponse) -> Unit = {},
    viewModel: CommandCentreViewModel = viewModel(),
) {
    val state           by viewModel.state.collectAsStateWithLifecycle()
    val nearbyMechanics by viewModel.nearbyMechanics.collectAsStateWithLifecycle()
    val todayBookings   by viewModel.todayBookings.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.loadCommandCentre()
        viewModel.refreshNearbyMechanics()
        viewModel.refreshTodayBookings()
    }

    GarajeTheme {
        when (state) {
            is CommandCentreState.Loading, CommandCentreState.Idle -> {
                CommandCentreLoadingScreen()
            }
            is CommandCentreState.Error -> {
                CommandCentreErrorScreen(
                    message = (state as CommandCentreState.Error).message,
                    onRetry = { viewModel.loadCommandCentre() },
                )
            }
            is CommandCentreState.Success -> {
                val data = (state as CommandCentreState.Success).data
                CommandCentreContent(
                    summary          = data.summary,
                    bays             = data.bays ?: emptyList(),
                    aiInsights       = data.aiInsights ?: emptyList(),
                    mapPins          = data.mapPins ?: emptyList(),
                    nearbyMechanics  = nearbyMechanics.ifEmpty { data.nearbyMechanics ?: emptyList() },
                    todayBookings    = todayBookings.ifEmpty { data.todayBookings ?: emptyList() },
                    onNavigateBack   = onNavigateBack,
                    onPostJob        = onPostJob,
                    onCallMechanic   = onCallMechanic,
                    onInsightAction  = onInsightAction,
                )
            }
        }
    }
}

@Composable
private fun CommandCentreLoadingScreen() {
    Scaffold(
        topBar = { CommandCentreTopBar("Loading…", "", {}) },
        containerColor = GarageSurface,
    ) { padding ->
        Box(
            modifier         = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = GarageBlue)
        }
    }
}

@Composable
private fun CommandCentreErrorScreen(message: String, onRetry: () -> Unit) {
    Scaffold(
        topBar = { CommandCentreTopBar("Command centre", "", {}) },
        containerColor = GarageSurface,
    ) { padding ->
        Column(
            modifier              = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
        ) {
            Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = GarageTextMuted, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(message, color = GarageTextMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick  = onRetry,
                colors   = ButtonDefaults.buttonColors(containerColor = GarageBlue),
                shape    = RoundedCornerShape(12.dp),
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun CommandCentreContent(
    summary: GarageSummaryResponse?,
    bays: List<BayStatusResponse>,
    aiInsights: List<AiInsightResponse>,
    mapPins: List<MapPinResponse>,
    nearbyMechanics: List<NearbyMechanicResponse>,
    todayBookings: List<TodayBookingResponse>,
    onNavigateBack: () -> Unit,
    onPostJob: (Int) -> Unit,
    onCallMechanic: (NearbyMechanicResponse) -> Unit,
    onInsightAction: (AiInsightResponse) -> Unit,
) {
    Scaffold(
        topBar = {
            CommandCentreTopBar(
                garageName     = summary?.garageName ?: "",
                garageLocation = summary?.garageLocation ?: "",
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = { CommandCentreBottomNav() },
        containerColor = GarageSurface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(12.dp))

            // Stat strip
            if (summary != null) {
                StatStrip(summary = summary)
                Spacer(Modifier.height(8.dp))
            }

            // Live map
            SectionHeader("Live map", "Updating live")
            LiveMapCard(pins = mapPins)
            Spacer(Modifier.height(8.dp))

            // Bay status
            SectionHeader("Bay status")
            if (bays.isEmpty()) {
                EmptySection("No bay data available")
            } else {
                BayGrid(bays = bays, onPostJob = onPostJob)
            }
            Spacer(Modifier.height(8.dp))

            // AI insights
            if (aiInsights.isNotEmpty()) {
                SectionHeader("AI insights", "${aiInsights.size} alerts")
                AiInsightsCard(insights = aiInsights, onAction = onInsightAction)
                Spacer(Modifier.height(8.dp))
            }

            // Today's bookings
            if (todayBookings.isNotEmpty()) {
                SectionHeader("Today's bookings")
                TodayBookingsCard(bookings = todayBookings)
                Spacer(Modifier.height(8.dp))
            }

            // Nearby mechanics
            SectionHeader("Nearby mechanics")
            if (nearbyMechanics.isEmpty()) {
                EmptySection("No mechanics nearby")
            } else {
                NearbyMechanicsSection(
                    mechanics      = nearbyMechanics,
                    onCallMechanic = onCallMechanic,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommandCentreTopBar(
    garageName: String,
    garageLocation: String,
    onNavigateBack: () -> Unit,
) {
    Surface(shadowElevation = 1.dp, color = Color.White) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = GarageTextMuted)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Command centre",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = GarageTextDark,
                )
                if (garageName.isNotEmpty()) {
                    Text(
                        text  = "$garageName · $garageLocation",
                        style = MaterialTheme.typography.labelSmall,
                        color = GarageTextMuted,
                    )
                }
            }
            IconButton(onClick = {}) {
                BadgedBox(badge = { Badge(containerColor = GarageOrange) }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = GarageTextMuted)
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = GarageTextMuted)
            }
        }
    }
}

@Composable
private fun StatStrip(summary: GarageSummaryResponse) {
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip(
            modifier = Modifier.weight(1f),
            value    = "${summary.activeBays ?: 0}/${summary.totalBays ?: 0}",
            label    = "Bays active",
            icon     = Icons.Outlined.Build,
            iconTint = GarageAmberDark,
            iconBg   = GarageAmberLight,
        )
        StatChip(
            modifier = Modifier.weight(1f),
            value    = "${summary.nearbyMechanicsCount ?: 0}",
            label    = "Mechs nearby",
            icon     = Icons.Outlined.People,
            iconTint = GarageTealDark,
            iconBg   = GarageTealLight,
        )
        StatChip(
            modifier = Modifier.weight(1f),
            value    = "${summary.overrunAlerts ?: 0}",
            label    = "Overruns",
            icon     = Icons.Outlined.Warning,
            iconTint = GarageRedDark,
            iconBg   = GarageRedLight,
        )
        StatChip(
            modifier = Modifier.weight(1f),
            value    = summary.revenueTodayKes ?: "0",
            label    = "Rev (KES)",
            icon     = Icons.Outlined.BarChart,
            iconTint = GarageBlue,
            iconBg   = GarageBlueLight,
        )
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
) {
    Surface(
        modifier       = modifier,
        shape          = RoundedCornerShape(12.dp),
        color          = Color.White,
        border         = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color      = GarageTextDark,
            )
            Text(
                label,
                style    = MaterialTheme.typography.labelSmall,
                color    = GarageTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LiveMapCard(pins: List<MapPinResponse>) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 0.8f,
        targetValue   = 1.3f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label         = "pulseScale",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp),
        shape  = RoundedCornerShape(16.dp),
        color  = Color(0xFFEEF0F8),
        border = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawMapGrid(this)
                drawPulseRings(this, pulseScale)
                drawRouteLines(this, pins)
            }

            // Pin labels for MECHANIC and OWNER pins
            pins.filter { it.type != "GARAGE" }.forEach { pin ->
                PinOverlay(pin = pin)
            }

            // Garage centre dot (always centre)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(GarageBlue)
                    .border(2.dp, Color.White, CircleShape),
            )

            // Live badge
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                shape    = RoundedCornerShape(20.dp),
                color    = Color.White,
                border   = BorderStroke(0.5.dp, GarageDivider),
            ) {
                Row(
                    modifier        = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    LiveDot()
                    Text("Live", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted)
                }
            }
        }
    }
}

@Composable
private fun LiveDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "liveDot")
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 0.2f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label         = "dotAlpha",
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(GarageTeal.copy(alpha = alpha)),
    )
}

private fun drawMapGrid(scope: DrawScope) {
    val step  = 28.dp.value * scope.density
    val color = Color(0xFFD8DCE8)
    var x = 0f
    while (x <= scope.size.width)  { scope.drawLine(color, Offset(x, 0f), Offset(x, scope.size.height), 0.5f); x += step }
    var y = 0f
    while (y <= scope.size.height) { scope.drawLine(color, Offset(0f, y), Offset(scope.size.width, y), 0.5f); y += step }
}

private fun drawPulseRings(scope: DrawScope, pulse: Float) {
    val cx = scope.size.width * 0.5f
    val cy = scope.size.height * 0.5f
    scope.drawCircle(GarageBlue.copy(alpha = 0.18f), radius = 40.dp.value * scope.density * pulse, center = Offset(cx, cy), style = Stroke(1f))
    scope.drawCircle(GarageBlue.copy(alpha = 0.10f), radius = 72.dp.value * scope.density,         center = Offset(cx, cy), style = Stroke(1f))
}

private fun drawRouteLines(scope: DrawScope, pins: List<MapPinResponse>) {
    val cx   = scope.size.width * 0.5f
    val cy   = scope.size.height * 0.5f
    val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
    pins.forEach { pin ->
        val color = when (pin.type) {
            "MECHANIC" -> GarageTeal.copy(alpha = 0.4f)
            "OWNER"    -> GarageAmber.copy(alpha = 0.4f)
            else       -> return@forEach
        }
        scope.drawLine(
            color       = color,
            start       = Offset(cx, cy),
            end         = Offset(scope.size.width * (pin.offsetX ?: 0.5f), scope.size.height * (pin.offsetY ?: 0.5f)),
            strokeWidth = 1f,
            pathEffect  = dash,
        )
    }
}

@Composable
private fun BoxScope.PinOverlay(pin: MapPinResponse) {
    val dotColor  = if (pin.type == "MECHANIC") GarageTeal else GarageAmber
    val textColor = if (pin.type == "MECHANIC") GarageTealDark else GarageAmberDark
    val ox        = pin.offsetX ?: return
    val oy        = pin.offsetY ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .offset(
                x = (ox * 344 - 30).dp,
                y = (oy * 160 - 20).dp,
            ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(1.5.dp, Color.White, CircleShape),
            )
            Surface(
                modifier = Modifier.padding(top = 2.dp),
                shape    = RoundedCornerShape(4.dp),
                color    = Color.White,
                border   = BorderStroke(0.5.dp, GarageDivider),
            ) {
                Text(
                    text     = pin.label ?: "",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                    style    = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color    = textColor,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun BayGrid(bays: List<BayStatusResponse>, onPostJob: (Int) -> Unit) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        bays.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { bay ->
                    BayCard(modifier = Modifier.weight(1f), bay = bay, onPostJob = { onPostJob(bay.bayNumber ?: 0) })
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BayCard(
    modifier: Modifier = Modifier,
    bay: BayStatusResponse,
    onPostJob: () -> Unit,
) {
    val isActive  = bay.status == "ACTIVE"
    val isOverrun = bay.status == "OVERRUN"
    val isEmpty   = bay.status == "EMPTY"

    val borderColor = when {
        isOverrun -> GarageRed.copy(alpha = 0.5f)
        else      -> GarageDivider
    }

    if (isEmpty) {
        Surface(
            modifier = modifier.clickable { onPostJob() },
            shape    = RoundedCornerShape(14.dp),
            color    = GarageCardBg,
            border   = BorderStroke(0.5.dp, GarageDivider),
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.Add, null, tint = GarageTextMuted.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(6.dp))
                Text("Bay ${bay.bayNumber ?: "–"} — empty", style = MaterialTheme.typography.labelMedium, color = GarageTextMuted)
                Text("Post job", style = MaterialTheme.typography.labelSmall, color = GarageBlue, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
            }
        }
        return
    }

    val progressColor = if (isOverrun) GarageRed else GarageTeal
    val timeColor     = if (isOverrun) GarageRedDark else GarageTealDark
    val badgeBg       = if (isOverrun) GarageRedLight else GarageTealLight
    val badgeText     = if (isOverrun) "Overrun" else "Active"
    val badgeColor    = if (isOverrun) GarageRedDark else GarageTealDark

    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        color    = Color.White,
        border   = BorderStroke(if (isOverrun) 1.dp else 0.5.dp, borderColor),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("Bay 0${bay.bayNumber ?: "–"}", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted.copy(alpha = 0.6f))
                Surface(shape = RoundedCornerShape(20.dp), color = badgeBg) {
                    Text(badgeText, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = badgeColor, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(8.dp))

            // Vehicle info
            Text(bay.vehiclePlate ?: "–", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
            Text(
                "${bay.vehicleModel ?: "–"} · ${bay.jobType ?: "–"}",
                style    = MaterialTheme.typography.labelSmall,
                color    = GarageTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
            )

            // Mechanic avatar row
            if (!bay.mechanicName.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Box(
                        modifier         = Modifier.size(20.dp).clip(CircleShape).background(GarageTealLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(bay.mechanicInitials ?: "?", fontSize = 8.sp, color = GarageTealDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(bay.mechanicName, style = MaterialTheme.typography.labelMedium, color = GarageTextDark)
                }
            }

            // Progress bar
            val progress = (bay.progressPercent ?: 0) / 100f
            LinearProgressIndicator(
                progress    = progress,
                modifier    = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color       = progressColor,
                trackColor  = GarageSurface,
            )
            Row(
                modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(bay.jobType ?: "", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted)
                Text("${bay.progressPercent ?: 0}%", style = MaterialTheme.typography.labelSmall, color = progressColor, fontWeight = FontWeight.SemiBold)
            }

            // Time estimate
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = GarageDivider)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Est. finish", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted.copy(alpha = 0.6f))
                Text(bay.estimatedFinish ?: "–", style = MaterialTheme.typography.labelSmall, color = timeColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AiInsightsCard(
    insights: List<AiInsightResponse>,
    onAction: (AiInsightResponse) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        color    = GarageAiPanelBg,
        border   = BorderStroke(0.5.dp, GarageAiPanelBorder),
    ) {
        Column {
            // Header
            Row(
                modifier        = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier         = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(GarageAiIconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Psychology, null, tint = GarageAiIconTint, modifier = Modifier.size(15.dp))
                }
                Text("Predictive engine", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GarageAiText)
                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = GarageRedLight) {
                    Text("${insights.size} alerts", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = GarageRedDark, fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(color = GarageAiPanelBorder.copy(alpha = 0.4f))

            insights.forEachIndexed { i, insight ->
                AiInsightRow(insight = insight, onAction = { onAction(insight) })
                if (i < insights.lastIndex) HorizontalDivider(modifier = Modifier.padding(start = 26.dp), color = GarageAiPanelBorder.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
private fun AiInsightRow(insight: AiInsightResponse, onAction: () -> Unit) {
    val barColor = when (insight.severity) {
        "HIGH"   -> GarageRed
        "MEDIUM" -> GarageAmber
        else     -> GarageTeal
    }
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .defaultMinSize(minHeight = 48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor),
        )
        Column {
            Text(insight.title ?: "", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GarageAiTextStrong)
            Spacer(Modifier.height(3.dp))
            Text(insight.body ?: "", style = MaterialTheme.typography.bodySmall, color = GarageAiText, lineHeight = 18.sp)
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text("${insight.actionLabel ?: "Action"} →", style = MaterialTheme.typography.labelMedium, color = GarageBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TodayBookingsCard(bookings: List<TodayBookingResponse>) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        color    = Color.White,
        border   = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Column {
            bookings.forEach { booking ->
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Service icon
                    Box(
                        modifier         = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(GarageTealLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Build, null, tint = GarageTealDark, modifier = Modifier.size(17.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(booking.customerName ?: "–", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                        Text("${booking.serviceName ?: "–"} · ${booking.timeSlot ?: "–"} · ${booking.vehiclePlate ?: "–"}", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                    }
                    // Status badge
                    val (bg, fg) = when (booking.status) {
                        "CONFIRMED"  -> GarageTealLight to GarageTealDark
                        "PENDING"    -> GarageAmberLight to GarageAmberDark
                        "COMPLETED"  -> GarageSurface to GarageTextMuted
                        else         -> GarageSurface to GarageTextMuted
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
                        Text(booking.status ?: "–", modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = fg, fontWeight = FontWeight.SemiBold)
                    }
                }
                HorizontalDivider(color = GarageDivider, modifier = Modifier.padding(horizontal = 14.dp))
            }
        }
    }
}

@Composable
private fun NearbyMechanicsSection(
    mechanics: List<NearbyMechanicResponse>,
    onCallMechanic: (NearbyMechanicResponse) -> Unit,
) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        mechanics.forEach { mech ->
            MechanicRow(mechanic = mech, onCall = { onCallMechanic(mech) })
        }
    }
}

@Composable
private fun MechanicRow(mechanic: NearbyMechanicResponse, onCall: () -> Unit) {
    val (avatarBg, avatarFg) = when (mechanic.avatarColorType) {
        "AMBER"  -> GarageAmberLight to GarageAmberDark
        "PURPLE" -> GaragePurpleLight to GaragePurple
        else     -> GarageTealLight to GarageTealDark   // default TEAL
    }
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier         = Modifier.size(38.dp).clip(CircleShape).background(avatarBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(mechanic.initials ?: "?", style = MaterialTheme.typography.labelMedium, color = avatarFg, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(mechanic.name ?: "–", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
                Text(mechanic.specialisations ?: "", style = MaterialTheme.typography.labelSmall, color = GarageTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                val distLabel = if (mechanic.isInBay == true) "In bay" else "${mechanic.distanceKm ?: "?"} km"
                val distColor = if (mechanic.isInBay == true) GarageTextMuted else GarageTeal
                Text(distLabel, style = MaterialTheme.typography.labelSmall, color = distColor, fontWeight = FontWeight.SemiBold)
                Text("${mechanic.rating ?: "–"} ★", style = MaterialTheme.typography.labelSmall, color = GarageAmberDark)
            }
            OutlinedButton(
                onClick        = onCall,
                shape          = RoundedCornerShape(8.dp),
                border         = BorderStroke(0.5.dp, GarageDivider),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    if (mechanic.isInBay == true) "Active" else "Call ↗",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (mechanic.isInBay == true) GarageTextMuted else GarageTextDark,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String? = null) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = GarageTextDark)
        if (trailing != null) Text(trailing, style = MaterialTheme.typography.labelSmall, color = GarageTextMuted)
    }
}

@Composable
private fun EmptySection(message: String) {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodySmall, color = GarageTextMuted)
    }
}

@Composable
private fun CommandCentreBottomNav() {
    val items = listOf(
        Triple(Icons.Outlined.Home,      "Home",    false),
        Triple(Icons.Outlined.Build,     "Jobs",    false),
        Triple(Icons.Outlined.Map,       "Command", true),
        Triple(Icons.Outlined.People,    "Team",    false),
        Triple(Icons.Outlined.BarChart,  "Revenue", false),
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        items.forEach { (icon, label, selected) ->
            NavigationBarItem(
                selected = selected,
                onClick  = {},
                icon     = { Icon(icon, label) },
                label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
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