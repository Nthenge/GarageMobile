package com.mobile.garaje.ui.screens.garage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.mobile.garaje.data.model.CategoryResponse
import com.mobile.garaje.data.model.ServiceResponse
import com.mobile.garaje.ui.screens.home.GarageOrange
import com.mobile.garaje.ui.screens.home.GarageOrangeLight
import com.mobile.garaje.ui.screens.home.GarageTextDark
import com.mobile.garaje.ui.screens.home.GarageTextMuted
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.GarageServicesUiState
import com.mobile.garaje.ui.viewmodel.GarageServicesViewModel

// ── Category colors — cycles through your theme palette ──────────────────────

private val categoryColors = listOf(
    GarageOrange, GarageTeal, GarageBlue,
    GarageAmber, GarageRed, GaragePurple
)

private fun categoryColor(index: Int) = categoryColors[index % categoryColors.size]

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun GarageServicesScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: GarageServicesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        containerColor    = GarageSurface,
        snackbarHost      = { SnackbarHost(snackbarHostState) },
        topBar            = {
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, "Back", tint = GarageTextMuted)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Services", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = GarageTextDark
                        )
                        Text("What your garage offers", fontSize = 12.sp, color = GarageTextMuted)
                    }
                    IconButton(onClick = { viewModel.loadAll() }) {
                        Icon(Icons.Outlined.Refresh, "Refresh", tint = GarageTextMuted)
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, null,
                            tint = GarageTextMuted, modifier = Modifier.size(48.dp))
                        Text(uiState.error!!, color = GarageTextMuted, fontSize = 14.sp)
                        Button(
                            onClick = { viewModel.loadAll() },
                            colors  = ButtonDefaults.buttonColors(containerColor = GarageOrange),
                            shape   = RoundedCornerShape(12.dp)
                        ) { Text("Retry") }
                    }
                }
            }
            else -> {
                ServicesContent(
                    uiState    = uiState,
                    viewModel  = viewModel,
                    padding    = padding,
                    categories = uiState.categories
                )
            }
        }
    }
}

// ── Main content ──────────────────────────────────────────────────────────────

@Composable
private fun ServicesContent(
    uiState: GarageServicesUiState,
    viewModel: GarageServicesViewModel,
    padding: PaddingValues,
    categories: List<CategoryResponse>
) {
    val myFiltered       = viewModel.myServicesFiltered()
    val availableGrouped = viewModel.availableServicesGrouped()
    val myCategories     = viewModel.myServiceCategories()

    LazyColumn(
        modifier       = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Stats ─────────────────────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(Modifier.weight(1f), uiState.myServices.size.toString(), "Offered")
                StatCard(Modifier.weight(1f), myCategories.size.toString(), "Categories")
                StatCard(
                    Modifier.weight(1f),
                    (uiState.allServices.size - uiState.myServices.size).toString(),
                    "Available"
                )
            }
        }

        // ── My services section ───────────────────────────────────────────────
        item {
            Text("Your services", fontSize = 13.sp,
                fontWeight = FontWeight.Medium, color = GarageTextMuted
            )
        }

        // Category tabs for "Your services"
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryTab(
                        label     = "All",
                        color     = GarageOrange,
                        count     = uiState.myServices.size,
                        selected  = uiState.selectedCategoryId == null,
                        onClick   = { viewModel.selectCategory(null) }
                    )
                }
                items(myCategories) { cat ->
                    val idx   = categories.indexOf(cat)
                    val color = categoryColor(idx)
                    CategoryTab(
                        label    = cat.name ?: "",
                        color    = color,
                        count    = uiState.allServices.count {
                            it.categoryId == cat.id && (it.id ?: -1L) in uiState.myServices
                        },
                        selected = uiState.selectedCategoryId == cat.id,
                        onClick  = { viewModel.selectCategory(cat.id) }
                    )
                }
            }
        }

        // My services list
        if (myFiltered.isEmpty()) {
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
                    Text(
                        text = if (uiState.selectedCategoryId == null)
                            "You haven't added any services yet"
                        else
                            "No services in this category",
                        fontSize = 13.sp, color = GarageTextMuted
                    )
                }
            }
        } else {
            item {
                Surface(
                    shape  = RoundedCornerShape(14.dp),
                    color  = Color.White,
                    border = BorderStroke(0.5.dp, GarageDivider)
                ) {
                    Column {
                        myFiltered.forEachIndexed { index, svc ->
                            MyServiceRow(
                                service    = svc,
                                catColor   = categoryColor(
                                    categories.indexOfFirst { it.id == svc.categoryId }
                                        .coerceAtLeast(0)
                                ),
                                showCatLabel = uiState.selectedCategoryId == null,
                                onRemove   = { viewModel.removeService(svc.id ?: return@MyServiceRow) }
                            )
                            if (index < myFiltered.lastIndex) {
                                Divider(color = GarageDivider, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }

        // ── Available services section ─────────────────────────────────────────
        item {
            Spacer(Modifier.height(4.dp))
            Text("Add more services", fontSize = 13.sp,
                fontWeight = FontWeight.Medium, color = GarageTextMuted
            )
        }

        // Search bar
        item {
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder   = { Text("Search services...", fontSize = 13.sp, color = GarageTextMuted) },
                leadingIcon   = {
                    Icon(Icons.Outlined.Search, null, tint = GarageTextMuted,
                        modifier = Modifier.size(18.dp))
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor    = GarageDivider,
                    focusedBorderColor      = GarageOrange,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White
                )
            )
        }

        // Category filter chips for available services
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryTab(
                        label    = "All",
                        color    = GarageOrange,
                        count    = null,
                        selected = uiState.selectedCategoryId == null,
                        onClick  = { viewModel.selectCategory(null) }
                    )
                }
                items(categories) { cat ->
                    val idx   = categories.indexOf(cat)
                    val color = categoryColor(idx)
                    CategoryTab(
                        label    = cat.name ?: "",
                        color    = color,
                        count    = null,
                        selected = uiState.selectedCategoryId == cat.id,
                        onClick  = { viewModel.selectCategory(cat.id) }
                    )
                }
            }
        }

        // Available services grouped by category
        availableGrouped.forEach { (categoryName, services) ->
            item {
                val catIdx = categories.indexOfFirst { it.name == categoryName }.coerceAtLeast(0)
                AvailableServicesGroup(
                    categoryName = categoryName,
                    color        = categoryColor(catIdx),
                    services     = services,
                    myServices   = uiState.myServices,
                    onAdd        = { viewModel.addService(it) },
                    isAllTab     = uiState.selectedCategoryId == null && uiState.searchQuery.isEmpty()
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(modifier: Modifier, number: String, label: String) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        color    = Color.White,
        border   = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(number, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GarageTextDark)
            Text(label, fontSize = 11.sp, color = GarageTextMuted, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ── Category tab chip ─────────────────────────────────────────────────────────

@Composable
private fun CategoryTab(
    label: String,
    color: Color,
    count: Int?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor     = if (selected) color.copy(alpha = 0.12f) else Color.White
    val borderColor = if (selected) color else GarageDivider
    val textColor   = if (selected) color else GarageTextMuted

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (selected || label != "All") {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (selected) color else GarageTextMuted)
            )
        }
        Text(label, fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor)
        if (count != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GarageSurface)
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text("$count", fontSize = 10.sp, color = GarageTextMuted)
            }
        }
    }
}

// ── My service row ────────────────────────────────────────────────────────────

@Composable
private fun MyServiceRow(
    service: ServiceResponse,
    catColor: Color,
    showCatLabel: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(catColor)
        )
        Text(
            text     = service.serviceName ?: "",
            fontSize = 13.sp,
            color    = GarageTextDark,
            modifier = Modifier.weight(1f)
        )
        if (showCatLabel && service.categoryName != null) {
            Text(
                text     = service.categoryName,
                fontSize = 10.sp,
                color    = GarageTextMuted,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GarageRedLight)
                .border(0.5.dp, GarageRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Close, null, tint = GarageRedDark,
                modifier = Modifier.size(14.dp))
        }
    }
}

// ── Available services group ───────────────────────────────────────────────────

@Composable
private fun AvailableServicesGroup(
    categoryName: String,
    color: Color,
    services: List<ServiceResponse>,
    myServices: Set<Long>,
    onAdd: (Long) -> Unit,
    isAllTab: Boolean
) {
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = Color.White,
        border = BorderStroke(0.5.dp, GarageDivider)
    ) {
        Column {
            // Category header
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .background(GarageSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text       = categoryName,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GarageTextMuted,
                    modifier   = Modifier.weight(1f)
                )
                if (isAllTab) {
                    Text("Preview", fontSize = 10.sp, color = GarageTextMuted)
                }
            }

            Divider(color = GarageDivider, thickness = 0.5.dp)

            services.forEachIndexed { index, svc ->
                val isAdded = (svc.id ?: -1L) in myServices
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text     = svc.serviceName ?: "",
                        fontSize = 13.sp,
                        color    = if (isAdded) GarageTextMuted else GarageTextDark,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAdded) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GarageOrangeLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Added", fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GarageOrange
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GarageTealLight)
                                .border(0.5.dp, GarageTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onAdd(svc.id ?: return@clickable) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Add, null, tint = GarageTealDark,
                                modifier = Modifier.size(14.dp))
                        }
                    }
                }
                if (index < services.lastIndex) {
                    Divider(color = GarageDivider, thickness = 0.5.dp)
                }
            }
        }
    }
}