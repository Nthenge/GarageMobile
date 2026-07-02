package com.mobile.garaje.ui.screens.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.garaje.data.model.FaqResponse
import com.mobile.garaje.ui.theme.*
import com.mobile.garaje.ui.viewmodel.FaqState
import com.mobile.garaje.ui.viewmodel.SupportViewModel

private val PageBg = Color(0xFFF5F6FA)
private val HeroBg = Brush.linearGradient(
    colors = listOf(Color(0xFFE0ECF8), Color(0xFFF0F8EE), Color(0xFFFFF8EE))
)

@Composable
fun FaqScreen(
    onBack    : () -> Unit = {},
    viewModel : SupportViewModel = viewModel()
) {
    val faqState by viewModel.faqState.collectAsStateWithLifecycle()

    var searchQuery       by remember { mutableStateOf("") }
    var selectedCategory  by remember { mutableStateOf("All") }
    var expandedFaqId     by remember { mutableStateOf<Long?>(null) }

    // Derive categories from loaded FAQs
    val categories = remember(faqState) {
        val cats = if (faqState is FaqState.Success) {
            (faqState as FaqState.Success).faqs
                .mapNotNull { it.category }
                .distinct()
                .sorted()
        } else emptyList()
        listOf("All") + cats
    }

    // Filter FAQs
    val filteredFaqs = remember(faqState, searchQuery, selectedCategory) {
        if (faqState !is FaqState.Success) return@remember emptyList()
        val all = (faqState as FaqState.Success).faqs
        all.filter { faq ->
            val matchesCategory = selectedCategory == "All" || faq.category == selectedCategory
            val matchesSearch   = searchQuery.isBlank() ||
                    faq.question?.contains(searchQuery, ignoreCase = true) == true ||
                    faq.answer?.contains(searchQuery, ignoreCase = true) == true
            matchesCategory && matchesSearch
        }.sortedBy { it.displayOrder ?: Int.MAX_VALUE }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Gradient hero ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBg)
                .statusBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ArrowBack, null, tint = GarageTextDark, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        "Frequently asked questions",
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = GarageTextDark
                    )
                }

                Text("FAQs", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GarageTextDark)
                Spacer(Modifier.height(4.dp))
                Text("How can we help you today?", fontSize = 14.sp, color = GarageTextMuted)
                Spacer(Modifier.height(16.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ask a question", fontSize = 14.sp, color = GarageTextMuted) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, null, tint = GarageTextMuted, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Outlined.Close, null,
                                tint = GarageTextMuted,
                                modifier = Modifier.size(18.dp).clickable { searchQuery = "" }
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedBorderColor    = Color(0xFFEEEEEE),
                        focusedBorderColor      = GarageBlue,
                        cursorColor             = GarageBlue
                    )
                )
                Spacer(Modifier.height(16.dp))

                // Category chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) GarageBlue else Color.White)
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = if (isSelected) Color.White else GarageTextMuted
                            )
                        }
                    }
                }
            }
        }

        // ── FAQ list ──────────────────────────────────────────────────────────
        when (faqState) {
            is FaqState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GarageOrange)
                }
            }
            is FaqState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, null, tint = GarageTextMuted, modifier = Modifier.size(40.dp))
                        Text((faqState as FaqState.Error).message, color = GarageTextMuted, fontSize = 13.sp)
                        TextButton(onClick = { viewModel.loadFaqs() }) {
                            Text("Retry", color = GarageBlue)
                        }
                    }
                }
            }
            is FaqState.Success -> {
                if (filteredFaqs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No results found for \"$searchQuery\"",
                            fontSize = 14.sp, color = GarageTextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredFaqs, key = { it.id ?: it.hashCode() }) { faq ->
                            FaqAccordionItem(
                                faq        = faq,
                                isExpanded = expandedFaqId == faq.id,
                                onToggle   = {
                                    expandedFaqId = if (expandedFaqId == faq.id) null else faq.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Single accordion item ─────────────────────────────────────────────────────

@Composable
private fun FaqAccordionItem(
    faq: FaqResponse,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape           = RoundedCornerShape(12.dp),
        color           = Color.White,
        shadowElevation = 1.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = faq.question ?: "",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = GarageTextDark,
                    modifier   = Modifier.weight(1f).padding(end = 12.dp),
                    lineHeight = 20.sp
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp
                    else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GarageTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter   = expandVertically(tween(200)) + fadeIn(tween(200)),
                exit    = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                Column {
                    HorizontalDivider(color = Color(0xFFF0F4F8), thickness = 0.5.dp)
                    Text(
                        text       = faq.answer ?: "",
                        fontSize   = 13.sp,
                        color      = GarageTextMuted,
                        lineHeight = 20.sp,
                        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}
