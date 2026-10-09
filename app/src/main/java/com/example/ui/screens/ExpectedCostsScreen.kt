package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpectedCostItem
import com.example.data.model.ExpectedCostStatus
import com.example.data.model.ExpectedCostUrgency
import com.example.ui.components.formatHuf
import com.example.ui.theme.Amber500
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MakBlue
import com.example.ui.theme.TbszPurple
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun ExpectedCostsScreen(
    viewModel: FinanceViewModel,
    onNavigateToWishes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expectedCosts by viewModel.expectedCosts.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalExpectedCostsAmount.collectAsStateWithLifecycle()
    val totalAllocated by viewModel.totalExpectedCostsAllocated.collectAsStateWithLifecycle()
    val totalRemaining by viewModel.totalExpectedCostsRemaining.collectAsStateWithLifecycle()

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ExpectedCostItem?>(null) }
    var itemToAllocate by remember { mutableStateOf<ExpectedCostItem?>(null) }
    var itemToDelete by remember { mutableStateOf<ExpectedCostItem?>(null) }
    var isTipDismissed by remember { mutableStateOf(false) }

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf<ExpectedCostStatus?>(null) }
    var showOnlyMandatory by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "Language & Education",
        "Travel & Trips",
        "Career & Certifications",
        "Living & Commitments",
        "Events & Occasions",
        "Health & Wellness",
        "Other Planned"
    )

    val filteredList = remember(expectedCosts, selectedCategoryFilter, selectedStatusFilter, showOnlyMandatory) {
        expectedCosts.filter { item ->
            val matchCategory = selectedCategoryFilter == "All" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchStatus = selectedStatusFilter == null || item.status == selectedStatusFilter
            val matchMandatory = !showOnlyMandatory || item.isMandatory
            matchCategory && matchStatus && matchMandatory
        }
    }

    val overallFundedRatio = if (totalAmount > 0) (totalAllocated / totalAmount).toFloat().coerceIn(0f, 1f) else 0f

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_expected_cost_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expected Cost")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Add Expected Cost",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1100.dp)
                    .testTag("expected_costs_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. CLEAR DISTINCTION BANNER: Expected Costs vs. Wishlist
                if (!isTipDismissed) {
                    item {
                        DistinctionBannerCard(
                            onNavigateToWishes = onNavigateToWishes,
                            onDismiss = { isTipDismissed = true }
                        )
                    }
                }

                // 2. EXECUTIVE OVERVIEW METRICS CARD
                item {
                    ExpectedCostsOverviewCard(
                        totalAmount = totalAmount,
                        totalAllocated = totalAllocated,
                        totalRemaining = totalRemaining,
                        overallFundedRatio = overallFundedRatio,
                        itemCount = expectedCosts.size,
                        fundedCount = expectedCosts.count { it.status == ExpectedCostStatus.FUNDED || it.status == ExpectedCostStatus.PAID },
                        planningCount = expectedCosts.count { it.status == ExpectedCostStatus.PLANNING }
                    )
                }

                // 3. CATEGORY & FILTER STRIP
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Filter by Category",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("expected_costs_category_chips")
                        ) {
                            items(categories) { cat ->
                                val isSelected = selectedCategoryFilter == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategoryFilter = cat },
                                    label = { Text(cat) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        // Status & Commitment Filters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = selectedStatusFilter == null,
                                onClick = { selectedStatusFilter = null },
                                label = { Text("All Status") }
                            )

                            ExpectedCostStatus.entries.forEach { status ->
                                val isSel = selectedStatusFilter == status
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedStatusFilter = if (isSel) null else status },
                                    label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (status) {
                                            ExpectedCostStatus.FUNDED -> IncomeGreen.copy(alpha = 0.18f)
                                            ExpectedCostStatus.COMMITTED -> Amber500.copy(alpha = 0.18f)
                                            ExpectedCostStatus.PAID -> MaterialTheme.colorScheme.surfaceVariant
                                            ExpectedCostStatus.PLANNING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }

                // 4. LIST OF EXPECTED COSTS
                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (expectedCosts.isEmpty()) "No Expected Costs Added Yet" else "No matching expected costs found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Plan ahead for language courses, upcoming travels, certifications, or major life commitments.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { isAddDialogOpen = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add First Expected Cost")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { item ->
                        ExpectedCostItemCard(
                            item = item,
                            onAllocateFunds = { itemToAllocate = item },
                            onEdit = { itemToEdit = item },
                            onDelete = { itemToDelete = item },
                            onToggleStatus = { newStatus ->
                                viewModel.markExpectedCostStatus(item, newStatus)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }

    // DIALOGS
    if (isAddDialogOpen) {
        AddOrEditExpectedCostDialog(
            item = null,
            onDismiss = { isAddDialogOpen = false },
            onSave = { title, estimated, allocated, category, date, status, urgency, notes, isMandatory ->
                viewModel.addExpectedCost(
                    title = title,
                    estimatedAmount = estimated,
                    allocatedAmount = allocated,
                    category = category,
                    targetDate = date,
                    status = status,
                    urgency = urgency,
                    notes = notes,
                    isMandatory = isMandatory
                )
                isAddDialogOpen = false
            }
        )
    }

    itemToEdit?.let { editItem ->
        AddOrEditExpectedCostDialog(
            item = editItem,
            onDismiss = { itemToEdit = null },
            onSave = { title, estimated, allocated, category, date, status, urgency, notes, isMandatory ->
                viewModel.updateExpectedCost(
                    editItem.copy(
                        title = title,
                        estimatedAmount = estimated,
                        allocatedAmount = allocated,
                        category = category,
                        targetDate = date,
                        status = status,
                        urgency = urgency,
                        notes = notes,
                        isMandatory = isMandatory
                    )
                )
                itemToEdit = null
            }
        )
    }

    itemToAllocate?.let { allocItem ->
        AllocateFundsDialog(
            item = allocItem,
            onDismiss = { itemToAllocate = null },
            onConfirmAllocate = { amount ->
                viewModel.allocateFundsToExpectedCost(allocItem, amount)
                itemToAllocate = null
            }
        )
    }

    itemToDelete?.let { delItem ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Expected Cost?") },
            text = { Text("Are you sure you want to remove '${delItem.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpectedCost(delItem)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Prominent banner explicitly educating and contrasting Expected Costs from Wishlist
 */
@Composable
private fun DistinctionBannerCard(
    onNavigateToWishes: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MakBlue.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MakBlue.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MakBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MakBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPECTED COSTS VS. WISHLIST",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp),
                            color = MakBlue
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp).testTag("dismiss_expected_costs_tip_btn")
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Close Tip",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Expected costs represent planned investments and upcoming commitments (e.g. language courses, travel expeditions, certifications, or living expenses) with planned timelines and dedicated funding progress.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "In contrast, your Wishlist tracks discretionary desires, photo gear, and personal rewards.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onNavigateToWishes,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MakBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("switch_to_wishes_btn")
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MakBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "View Discretionary Wishlist",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MakBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MakBlue
                        )
                    }
                }
            }
        }
    }
}

/**
 * Executive Metrics & Funding Progress
 */
@Composable
private fun ExpectedCostsOverviewCard(
    totalAmount: Double,
    totalAllocated: Double,
    totalRemaining: Double,
    overallFundedRatio: Float,
    itemCount: Int,
    fundedCount: Int,
    planningCount: Int
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL EXPECTED COMMITMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatHuf(totalAmount),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (overallFundedRatio >= 1f) IncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, if (overallFundedRatio >= 1f) IncomeGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${(overallFundedRatio * 100).toInt()}% Covered",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (overallFundedRatio >= 1f) IncomeGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { overallFundedRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (overallFundedRatio >= 1f) IncomeGreen else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Allocated / Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatHuf(totalAllocated),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IncomeGreen
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining to Fund",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatHuf(totalRemaining),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$itemCount Planned Costs",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$fundedCount Funded • $planningCount In Planning",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Individual Expected Cost Item Card
 */
@Composable
private fun ExpectedCostItemCard(
    item: ExpectedCostItem,
    onAllocateFunds: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: (ExpectedCostStatus) -> Unit
) {
    val categoryIcon: ImageVector = when {
        item.category.contains("Language", ignoreCase = true) || item.category.contains("Education", ignoreCase = true) -> Icons.Default.School
        item.category.contains("Travel", ignoreCase = true) || item.category.contains("Trip", ignoreCase = true) -> Icons.Default.FlightTakeoff
        item.category.contains("Career", ignoreCase = true) || item.category.contains("Exam", ignoreCase = true) -> Icons.Default.Work
        item.category.contains("Living", ignoreCase = true) -> Icons.Default.AssignmentTurnedIn
        else -> Icons.Default.DateRange
    }

    val categoryColor: Color = when {
        item.category.contains("Language", ignoreCase = true) || item.category.contains("Education", ignoreCase = true) -> MakBlue
        item.category.contains("Travel", ignoreCase = true) || item.category.contains("Trip", ignoreCase = true) -> Color(0xFF009688)
        item.category.contains("Career", ignoreCase = true) || item.category.contains("Exam", ignoreCase = true) -> Color(0xFFE76F51)
        item.category.contains("Living", ignoreCase = true) -> Amber500
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (item.status == ExpectedCostStatus.FUNDED) IncomeGreen.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expected_cost_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Urgency + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(categoryIcon, contentDescription = null, tint = categoryColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = categoryColor
                            )
                        }
                    }

                    if (item.isMandatory) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD32F2F).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Strict Deadline",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Status Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (item.status) {
                        ExpectedCostStatus.FUNDED -> IncomeGreen.copy(alpha = 0.18f)
                        ExpectedCostStatus.COMMITTED -> Amber500.copy(alpha = 0.18f)
                        ExpectedCostStatus.PAID -> MaterialTheme.colorScheme.surfaceVariant
                        ExpectedCostStatus.PLANNING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    },
                    modifier = Modifier.clickable {
                        val nextStatus = when (item.status) {
                            ExpectedCostStatus.PLANNING -> ExpectedCostStatus.COMMITTED
                            ExpectedCostStatus.COMMITTED -> ExpectedCostStatus.FUNDED
                            ExpectedCostStatus.FUNDED -> ExpectedCostStatus.PAID
                            ExpectedCostStatus.PAID -> ExpectedCostStatus.PLANNING
                        }
                        onToggleStatus(nextStatus)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.status == ExpectedCostStatus.FUNDED || item.status == ExpectedCostStatus.PAID) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (item.status == ExpectedCostStatus.FUNDED) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = item.status.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (item.status) {
                                ExpectedCostStatus.FUNDED -> IncomeGreen
                                ExpectedCostStatus.COMMITTED -> Amber500
                                ExpectedCostStatus.PAID -> MaterialTheme.colorScheme.onSurfaceVariant
                                ExpectedCostStatus.PLANNING -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Target Date
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.targetDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Target Deadline: ${item.targetDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amounts Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Allocated",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatHuf(item.allocatedAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (item.isFullyFunded) IncomeGreen else MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Coverage",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.progressPercent.toInt()}%",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (item.isFullyFunded) IncomeGreen else MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Estimated Cost",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatHuf(item.estimatedAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { item.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (item.isFullyFunded) IncomeGreen else categoryColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (item.remainingAmount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Remaining to cover: ${formatHuf(item.remainingAmount)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAllocateFunds,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("allocate_btn_${item.id}")
                    ) {
                        Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Allocate Funds",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("edit_expected_cost_${item.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(15.dp))
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_expected_cost_${item.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dialog to add or edit an expected cost
 */
@Composable
private fun AddOrEditExpectedCostDialog(
    item: ExpectedCostItem?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        estimated: Double,
        allocated: Double,
        category: String,
        targetDate: String,
        status: ExpectedCostStatus,
        urgency: ExpectedCostUrgency,
        notes: String,
        isMandatory: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(item?.title ?: "") }
    var estimatedText by remember { mutableStateOf(item?.estimatedAmount?.toLong()?.toString() ?: "") }
    var allocatedText by remember { mutableStateOf(item?.allocatedAmount?.toLong()?.toString() ?: "0") }
    var category by remember { mutableStateOf(item?.category ?: "Language & Education") }
    var targetDate by remember { mutableStateOf(item?.targetDate ?: "") }
    var status by remember { mutableStateOf(item?.status ?: ExpectedCostStatus.PLANNING) }
    var urgency by remember { mutableStateOf(item?.urgency ?: ExpectedCostUrgency.REQUIRED) }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var isMandatory by remember { mutableStateOf(item?.isMandatory ?: true) }

    val suggestedCategories = listOf(
        "Language & Education",
        "Travel & Trips",
        "Career & Certifications",
        "Living & Commitments",
        "Events & Occasions",
        "Health & Wellness",
        "Transport & Vehicle",
        "Other Planned"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (item == null) "Add Expected Cost" else "Edit Expected Cost",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Spanish Course, Japan Trip)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_expected_cost_title")
                )

                // Quick Title Suggestions
                if (item == null && title.isEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Spanish B2 Course", "Japan Trip 2027", "Driving License", "AWS Certification", "Conference Ticket")) { sug ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { title = sug }
                            ) {
                                Text(
                                    text = sug,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Estimated Cost in HUF
                OutlinedTextField(
                    value = estimatedText,
                    onValueChange = { estimatedText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Total Expected Cost (HUF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    trailingIcon = { Text("Ft", modifier = Modifier.padding(end = 12.dp)) },
                    modifier = Modifier.fillMaxWidth().testTag("input_expected_cost_estimated")
                )

                // Allocated / Saved so far
                OutlinedTextField(
                    value = allocatedText,
                    onValueChange = { allocatedText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Already Saved / Allocated (HUF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    trailingIcon = { Text("Ft", modifier = Modifier.padding(end = 12.dp)) },
                    modifier = Modifier.fillMaxWidth().testTag("input_expected_cost_allocated")
                )

                // Category selection
                Text("Category", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(suggestedCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Target Deadline
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Deadline (e.g. 2026-11-20 or Summer 2027)") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth().testTag("input_expected_cost_date")
                )

                // Mandatory / Strict Commitment Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isMandatory = !isMandatory },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isMandatory,
                        onCheckedChange = { isMandatory = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Strict / Unavoidable Commitment",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Distinguishes essential costs from discretionary wishes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Notes / Breakdown
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Cost Breakdown / Notes (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val est = estimatedText.toDoubleOrNull() ?: 0.0
                    val alc = allocatedText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && est > 0) {
                        onSave(title, est, alc, category, targetDate, status, urgency, notes, isMandatory)
                    }
                },
                enabled = title.isNotBlank() && (estimatedText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("save_expected_cost_btn")
            ) {
                Text(if (item == null) "Add Cost" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog to allocate/deposit additional funds towards an expected cost
 */
@Composable
private fun AllocateFundsDialog(
    item: ExpectedCostItem,
    onDismiss: () -> Unit,
    onConfirmAllocate: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("20000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Allocate Funds to '${item.title}'",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Current allocation: ${formatHuf(item.allocatedAmount)} / ${formatHuf(item.estimatedAmount)} (Remaining: ${formatHuf(item.remainingAmount)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Amount to Allocate (HUF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { Text("Ft", modifier = Modifier.padding(end = 12.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("allocate_input_field")
                )

                // Quick preset buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10000.0, 25000.0, 50000.0, item.remainingAmount).forEach { preset ->
                        if (preset > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { amountText = preset.toLong().toString() }
                            ) {
                                Text(
                                    text = if (preset == item.remainingAmount) "Full" else "+${(preset / 1000).toInt()}k",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) onConfirmAllocate(amt)
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("confirm_allocate_btn")
            ) {
                Text("Confirm Allocation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
