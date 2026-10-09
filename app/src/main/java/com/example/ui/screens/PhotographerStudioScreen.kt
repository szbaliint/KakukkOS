package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ContractStatus
import com.example.data.model.GearCategory
import com.example.data.model.GearCondition
import com.example.data.model.PhotoContract
import com.example.data.model.PhotoGearItem
import com.example.data.model.PhotoIdeaItem
import com.example.data.model.PhotoTask
import com.example.data.model.TaskPriority
import com.example.data.model.WishItem
import com.example.data.model.WishPriority
import com.example.ui.components.FacebookAddToCalendarButton
import com.example.ui.components.QuickGoogleCalendarButton
import com.example.ui.components.formatHuf
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.CalendarEventData
import com.example.ui.util.CalendarIntegrationHelper
import com.example.ui.viewmodel.FinanceViewModel

enum class StudioTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    GEAR("Gear", Icons.Outlined.CameraAlt),
    WISHES("Wishlist", Icons.Filled.Star),
    CONTRACTS("Contracts", Icons.Outlined.Description),
    IDEAS("Idea Board", Icons.Filled.Lightbulb),
    TASKS("Tasks", Icons.Outlined.TaskAlt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotographerStudioScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    initialTab: Int = 0,
    onNavigateToFinancesWishes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    // Dialog & Sheet States
    var isAddGearDialogOpen by remember { mutableStateOf(false) }
    var isAddWishDialogOpen by remember { mutableStateOf(false) }
    var isAddContractDialogOpen by remember { mutableStateOf(false) }
    var isAddTaskDialogOpen by remember { mutableStateOf(false) }
    var isAddIdeaDialogOpen by remember { mutableStateOf(false) }
    var isWalkthroughOpen by remember { mutableStateOf(false) }

    var selectedContractForDetails by remember { mutableStateOf<PhotoContract?>(null) }
    var selectedIdeaForDetail by remember { mutableStateOf<PhotoIdeaItem?>(null) }
    var wishToPromote by remember { mutableStateOf<WishItem?>(null) }
    var gearToEdit by remember { mutableStateOf<PhotoGearItem?>(null) }

    val contracts by viewModel.contracts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Photographer Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Gear, Wishes, Contracts, Ideas & Shoots",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("studio_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to LifeOS")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clickable { isWalkthroughOpen = true }
                            .padding(end = 8.dp)
                            .testTag("open_studio_walkthrough_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Studio Tour",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        0 -> isAddGearDialogOpen = true
                        1 -> isAddWishDialogOpen = true
                        2 -> isAddContractDialogOpen = true
                        3 -> isAddIdeaDialogOpen = true
                        4 -> isAddTaskDialogOpen = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (selectedTab) {
                            0 -> Icons.Default.CameraAlt
                            1 -> Icons.Default.Star
                            2 -> Icons.AutoMirrored.Filled.Assignment
                            3 -> Icons.Default.Lightbulb
                            else -> Icons.Default.Add
                        },
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        when (selectedTab) {
                            0 -> "Add Equipment"
                            1 -> "Add Gear Wish"
                            2 -> "New Booking"
                            3 -> "Save Idea"
                            4 -> "Add Task"
                            else -> "Add"
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("studio_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Secondary Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                StudioTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.testTag("studio_tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> StudioGearTabContent(
                        viewModel = viewModel,
                        onEditGear = { gearToEdit = it },
                        onAddGearClick = { isAddGearDialogOpen = true }
                    )
                    1 -> StudioWishesTabContent(
                        viewModel = viewModel,
                        onPromoteWish = { wishToPromote = it },
                        onAddWishClick = { isAddWishDialogOpen = true },
                        onNavigateToFinancesWishes = onNavigateToFinancesWishes
                    )
                    2 -> StudioContractsTabContent(
                        viewModel = viewModel,
                        onContractClick = { selectedContractForDetails = it },
                        onAddContractClick = { isAddContractDialogOpen = true }
                    )
                    3 -> StudioIdeasTabContent(
                        viewModel = viewModel,
                        onIdeaClick = { selectedIdeaForDetail = it },
                        onAddIdeaClick = { isAddIdeaDialogOpen = true }
                    )
                    4 -> StudioTasksTabContent(
                        viewModel = viewModel,
                        onAddTaskClick = { isAddTaskDialogOpen = true }
                    )
                }
            }
        }
    }

    // Walkthrough Tour Dialog
    if (isWalkthroughOpen) {
        PhotographerWalkthroughDialog(
            onDismiss = { isWalkthroughOpen = false }
        )
    }

    // Modal Bottom Sheet: Contract Details
    selectedContractForDetails?.let { contract ->
        PhotoContractDetailSheet(
            contract = contract,
            viewModel = viewModel,
            onDismiss = { selectedContractForDetails = null }
        )
    }

    // Detail Dialog: Photo Idea (Pinterest-style)
    selectedIdeaForDetail?.let { idea ->
        PhotoIdeaDetailDialog(
            idea = idea,
            onDismiss = { selectedIdeaForDetail = null },
            onTogglePin = { viewModel.togglePinPhotoIdea(idea) },
            onDelete = {
                viewModel.deletePhotoIdea(idea)
                selectedIdeaForDetail = null
            },
            onCreateTask = {
                viewModel.createTaskFromIdea(idea, null, null)
            }
        )
    }

    // Dialog: Add Photo Idea
    if (isAddIdeaDialogOpen) {
        AddPhotoIdeaDialog(
            contracts = contracts,
            onDismiss = { isAddIdeaDialogOpen = false },
            onConfirm = { title, board, tags, desc, settings, gear, hexes, theme, cId, cTitle ->
                viewModel.addPhotoIdea(
                    title = title,
                    boardName = board,
                    tags = tags,
                    description = desc,
                    cameraSettings = settings,
                    recommendedGear = gear,
                    colorPaletteHexes = hexes,
                    moodTheme = theme,
                    linkedContractId = cId,
                    linkedContractTitle = cTitle
                )
                isAddIdeaDialogOpen = false
            }
        )
    }

    // Dialog: Add Photo Gear
    if (isAddGearDialogOpen) {
        AddPhotoGearDialog(
            onDismiss = { isAddGearDialogOpen = false },
            onConfirm = { name, brand, model, cat, sn, date, price, cond, loc, insured, notes ->
                viewModel.addPhotoGear(name, brand, model, cat, sn, date, price, cond, loc, insured, notes)
                isAddGearDialogOpen = false
            }
        )
    }

    // Dialog: Add Gear Wish (Linked to Financial Wishes!)
    if (isAddWishDialogOpen) {
        AddGearWishDialog(
            onDismiss = { isAddWishDialogOpen = false },
            onConfirm = { title, price, saved, priority, targetDate, gearCategory, notes ->
                viewModel.addGearWish(title, price, saved, priority, targetDate, gearCategory, notes)
                isAddWishDialogOpen = false
            }
        )
    }

    // Dialog: Promote Wish to Gear Inventory
    wishToPromote?.let { wish ->
        PromoteGearWishDialog(
            wish = wish,
            onDismiss = { wishToPromote = null },
            onConfirm = { brand, model, cat, sn, loc ->
                viewModel.promoteGearWishToInventory(wish, brand, model, cat, sn, loc)
                wishToPromote = null
            }
        )
    }

    // Dialog: Add Contract
    if (isAddContractDialogOpen) {
        AddContractDialog(
            onDismiss = { isAddContractDialogOpen = false },
            onConfirm = { title, client, email, phone, date, startTime, endTime, loc, total, deposit, status, deliv, deadline, notes ->
                viewModel.addContract(title, client, email, phone, date, loc, total, deposit, status, deliv, deadline, notes, startTime, endTime)
                isAddContractDialogOpen = false
            }
        )
    }

    // Dialog: Add Task
    if (isAddTaskDialogOpen) {
        AddStudioTaskDialog(
            viewModel = viewModel,
            onDismiss = { isAddTaskDialogOpen = false },
            onConfirm = { title, date, priority, cat, contractId, contractTitle, notes ->
                viewModel.addTask(title, date, priority, cat, contractId, contractTitle, notes)
                isAddTaskDialogOpen = false
            }
        )
    }
}

// =========================================================================
// TAB 1: GEAR INVENTORY
// =========================================================================
@Composable
fun StudioGearTabContent(
    viewModel: FinanceViewModel,
    onEditGear: (PhotoGearItem) -> Unit,
    onAddGearClick: () -> Unit
) {
    val gearList by viewModel.photoGear.collectAsStateWithLifecycle()
    val totalGearValue by viewModel.totalGearValue.collectAsStateWithLifecycle()
    val insuredGearValue by viewModel.insuredGearValue.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf<GearCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredGear = remember(gearList, selectedCategory, searchQuery) {
        gearList.filter { gear ->
            (selectedCategory == null || gear.category == selectedCategory) &&
                    (searchQuery.isBlank() || gear.name.contains(searchQuery, ignoreCase = true) || gear.brand.contains(searchQuery, ignoreCase = true) || gear.serialNumber.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // KPI Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL GEAR VALUE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatHuf(totalGearValue), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${gearList.size} items registered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("INSURED RATIO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        val insuredPct = if (totalGearValue > 0) ((insuredGearValue / totalGearValue) * 100).toInt() else 100
                        Text("$insuredPct% Insured", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = IncomeGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(formatHuf(insuredGearValue), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (${gearList.size})") }
                )
                GearCategory.entries.forEach { cat ->
                    val count = gearList.count { it.category == cat }
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.displayName} ($count)") }
                    )
                }
            }
        }

        // Gear Items List
        if (filteredGear.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No gear found in this category", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = onAddGearClick) {
                            Text("Register New Equipment")
                        }
                    }
                }
            }
        } else {
            items(filteredGear) { gear ->
                PhotoGearCard(
                    gear = gear,
                    onDelete = { viewModel.deletePhotoGear(gear) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun PhotoGearCard(
    gear: PhotoGearItem,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("gear_item_${gear.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (gear.category) {
                                GearCategory.CAMERA_BODY -> Icons.Default.CameraAlt
                                GearCategory.LENS -> Icons.Default.CameraAlt
                                GearCategory.LIGHTING -> Icons.Default.FlashOn
                                GearCategory.AUDIO -> Icons.Default.Mic
                                GearCategory.DRONE_GIMBAL -> Icons.Default.Videocam
                                else -> Icons.AutoMirrored.Filled.Assignment
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = gear.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${gear.brand} • ${gear.category.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (gear.condition) {
                        GearCondition.MINT -> IncomeGreen.copy(alpha = 0.15f)
                        GearCondition.EXCELLENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        GearCondition.GOOD -> Amber500.copy(alpha = 0.15f)
                        GearCondition.NEEDS_SERVICE -> ExpenseRed.copy(alpha = 0.15f)
                        GearCondition.RETIRED -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = gear.condition.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = when (gear.condition) {
                            GearCondition.MINT -> IncomeGreen
                            GearCondition.EXCELLENT -> MaterialTheme.colorScheme.primary
                            GearCondition.GOOD -> Amber500
                            GearCondition.NEEDS_SERVICE -> ExpenseRed
                            GearCondition.RETIRED -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Value: ${formatHuf(gear.purchasePrice)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Loc: ${gear.location.ifBlank { "Studio" }} • SN: ${gear.serialNumber.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (gear.isInsured) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = IncomeGreen.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Insured", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = IncomeGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: GEAR WISHLIST (LINKED TO WEALTHFLOW FINANCE WISHES!)
// =========================================================================
@Composable
fun StudioWishesTabContent(
    viewModel: FinanceViewModel,
    onPromoteWish: (WishItem) -> Unit,
    onAddWishClick: () -> Unit,
    onNavigateToFinancesWishes: () -> Unit = {}
) {
    val gearWishes by viewModel.gearWishes.collectAsStateWithLifecycle()

    val totalWishCost = gearWishes.sumOf { it.targetPrice }
    val totalWishSaved = gearWishes.sumOf { it.currentSaved }
    val progressRatio = if (totalWishCost > 0) (totalWishSaved / totalWishCost).toFloat().coerceIn(0f, 1f) else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Explanatory cross-integration banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Amber500.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Amber500.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AccountBalance, contentDescription = null, tint = Amber500, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Linked to WealthFlow Finance Hub",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Amber500
                        )
                        Text(
                            text = "Wishes created here instantly sync with Financial Wishes in Finances. When acquired, promote to move into active gear inventory!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onNavigateToFinancesWishes,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Open in Finances", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Summary KPI
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GEAR WISHLIST FUNDING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${(progressRatio * 100).toInt()}% Saved", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Amber500)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatHuf(totalWishSaved)} / ${formatHuf(totalWishCost)}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Amber500,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Wishlist Items
        if (gearWishes.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(48.dp), tint = Amber500.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No gear wishes yet", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onAddWishClick, colors = ButtonDefaults.buttonColors(containerColor = Amber500)) {
                            Text("Add Gear Wish")
                        }
                    }
                }
            }
        } else {
            items(gearWishes) { wish ->
                GearWishCard(
                    wish = wish,
                    onPromote = { onPromoteWish(wish) },
                    onDelete = { viewModel.deleteWish(wish) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun GearWishCard(
    wish: WishItem,
    onPromote: () -> Unit,
    onDelete: () -> Unit
) {
    val ratio = if (wish.targetPrice > 0) (wish.currentSaved / wish.targetPrice).toFloat().coerceIn(0f, 1f) else 1f
    val isFulfilled = wish.isFulfilled || wish.currentSaved >= wish.targetPrice

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isFulfilled) IncomeGreen.copy(alpha = 0.4f) else Amber500.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("gear_wish_${wish.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isFulfilled) IncomeGreen.copy(alpha = 0.15f) else Amber500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFulfilled) Icons.Default.CheckCircle else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFulfilled) IncomeGreen else Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = wish.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Category: ${wish.gearCategory.ifBlank { "Photo Gear" }} • Target: ${wish.targetDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Saved: ${formatHuf(wish.currentSaved)} (${(ratio * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isFulfilled) IncomeGreen else Amber500
                )
                Text(
                    text = "Goal: ${formatHuf(wish.targetPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isFulfilled) IncomeGreen else Amber500,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (wish.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = wish.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Promote to Gear Inventory
            Button(
                onClick = onPromote,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFulfilled) IncomeGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isFulfilled) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("promote_wish_${wish.id}")
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFulfilled) "Acquired! Move to Owned Gear" else "Mark Purchased / Move to Inventory",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// =========================================================================
// TAB 3: CONTRACTS & CLIENTS
// =========================================================================
@Composable
fun StudioContractsTabContent(
    viewModel: FinanceViewModel,
    onContractClick: (PhotoContract) -> Unit,
    onAddContractClick: () -> Unit
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()
    val activeContracts by viewModel.activeContracts.collectAsStateWithLifecycle()
    val totalRevenueDue by viewModel.totalContractRevenueDue.collectAsStateWithLifecycle()

    var statusFilter by remember { mutableStateOf<ContractStatus?>(null) }

    val filteredContracts = remember(contracts, statusFilter) {
        if (statusFilter == null) contracts else contracts.filter { it.status == statusFilter }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ACTIVE SHOOTS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${activeContracts.size} Booked", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${contracts.size} total contracts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("BALANCE RECEIVABLE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(formatHuf(totalRevenueDue), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = IncomeGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Due upon delivery", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { statusFilter = null },
                    label = { Text("All (${contracts.size})") }
                )
                ContractStatus.entries.forEach { st ->
                    val count = contracts.count { it.status == st }
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        label = { Text("${st.label} ($count)") }
                    )
                }
            }
        }

        if (filteredContracts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No contracts under this filter", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onAddContractClick) {
                            Text("New Photography Contract")
                        }
                    }
                }
            }
        } else {
            items(filteredContracts) { contract ->
                ContractCard(
                    contract = contract,
                    onClick = { onContractClick(contract) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ContractCard(
    contract: PhotoContract,
    onClick: () -> Unit
) {
    val progressRatio = if (contract.totalAmount > 0) (contract.depositPaid / contract.totalAmount).toFloat().coerceIn(0f, 1f) else 1f

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("contract_card_${contract.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contract.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${contract.clientName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (contract.status) {
                        ContractStatus.COMPLETED_PAID, ContractStatus.DELIVERED -> IncomeGreen.copy(alpha = 0.15f)
                        ContractStatus.SIGNED, ContractStatus.SHOOT_COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ContractStatus.SENT, ContractStatus.DRAFT -> Amber500.copy(alpha = 0.15f)
                        ContractStatus.CANCELLED -> ExpenseRed.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = contract.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = when (contract.status) {
                            ContractStatus.COMPLETED_PAID, ContractStatus.DELIVERED -> IncomeGreen
                            ContractStatus.SIGNED, ContractStatus.SHOOT_COMPLETED -> MaterialTheme.colorScheme.primary
                            ContractStatus.SENT, ContractStatus.DRAFT -> Amber500
                            ContractStatus.CANCELLED -> ExpenseRed
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (contract.shootDate.isNotBlank() || contract.shootLocation.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contract.shootDate} • ${contract.shootLocation.ifBlank { "Location TBD" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Paid: ${formatHuf(contract.depositPaid)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = IncomeGreen
                )
                Text(
                    text = "Total: ${formatHuf(contract.totalAmount)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = IncomeGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (contract.shootDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickGoogleCalendarButton(
                        event = CalendarEventData(
                            title = contract.title,
                            description = "Client: ${contract.clientName}\nDeliverables: ${contract.deliverables}\n${contract.notes}",
                            location = contract.shootLocation,
                            dateStr = contract.shootDate,
                            startTimeStr = contract.shootStartTime,
                            endTimeStr = contract.shootEndTime
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FacebookAddToCalendarButton(
                        event = CalendarEventData(
                            title = contract.title,
                            description = "Client: ${contract.clientName}\nDeliverables: ${contract.deliverables}\n${contract.notes}",
                            location = contract.shootLocation,
                            dateStr = contract.shootDate,
                            startTimeStr = contract.shootStartTime,
                            endTimeStr = contract.shootEndTime
                        ),
                        isPrimary = false
                    )
                }
            }
        }
    }
}

// =========================================================================
// TAB 4: TASKS & SHOOTS
// =========================================================================
@Composable
fun StudioTasksTabContent(
    viewModel: FinanceViewModel,
    onAddTaskClick: () -> Unit
) {
    val tasks by viewModel.photoTasks.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingTasksCount.collectAsStateWithLifecycle()

    var filterCategory by remember { mutableStateOf("All") }

    val filteredTasks = remember(tasks, filterCategory) {
        when (filterCategory) {
            "All" -> tasks
            "Pending" -> tasks.filter { !it.isCompleted }
            "Completed" -> tasks.filter { it.isCompleted }
            else -> tasks.filter { it.category == filterCategory }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("STUDIO & SHOOT TASKS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$pendingCount Pending To-Dos", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
                    }

                    OutlinedButton(onClick = onAddTaskClick) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task")
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Shoot Prep", "Editing", "Gear Maintenance", "Completed").forEach { cat ->
                    FilterChip(
                        selected = filterCategory == cat,
                        onClick = { filterCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        if (filteredTasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks under this filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(filteredTasks) { task ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                            colors = CheckboxDefaults.colors(checkedColor = IncomeGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                                ),
                                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (task.contractTitle != null) {
                                    Text(
                                        text = task.contractTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(" • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = "${task.category} • Due: ${task.dueDate.ifBlank { "Anytime" }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (task.dueDate.isNotBlank()) {
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    CalendarIntegrationHelper.addToGoogleCalendar(
                                        context,
                                        CalendarEventData(
                                            title = task.title,
                                            description = "Category: ${task.category}\nContract: ${task.contractTitle ?: "None"}\n${task.notes}",
                                            location = "",
                                            dateStr = task.dueDate,
                                            startTimeStr = "09:00",
                                            endTimeStr = "10:00"
                                        )
                                    )
                                },
                                modifier = Modifier.size(28.dp).testTag("task_cal_${task.id}")
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Add to Google Calendar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteTask(task) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// =========================================================================
// DIALOGS
// =========================================================================

@Composable
fun AddPhotoGearDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, GearCategory, String, String, Double, GearCondition, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Sony") }
    var model by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GearCategory.CAMERA_BODY) }
    var serialNumber by remember { mutableStateOf("") }
    var purchasePriceInput by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf(GearCondition.MINT) }
    var location by remember { mutableStateOf("Main Pelican 1510") }
    var isInsured by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Photography Equipment") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Gear Name *") },
                        placeholder = { Text("e.g. Sony A7 IV, 24-70 GM II") },
                        modifier = Modifier.fillMaxWidth().testTag("gear_name_input")
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model Code") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = purchasePriceInput,
                        onValueChange = { purchasePriceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Purchase Price (HUF) *") },
                        modifier = Modifier.fillMaxWidth().testTag("gear_price_input")
                    )
                }
                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearCategory.entries.forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (category == cat) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (category == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Storage Location") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isInsured, onCheckedChange = { isInsured = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Covered under gear insurance policy", style = MaterialTheme.typography.bodySmall)
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Specs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = purchasePriceInput.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(name, brand, model, category, serialNumber, "2026-09-30", price, condition, location, isInsured, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_add_gear_btn")
            ) {
                Text("Add Equipment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddGearWishDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, WishPriority, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetPriceInput by remember { mutableStateOf("") }
    var currentSavedInput by remember { mutableStateOf("0") }
    var priority by remember { mutableStateOf(WishPriority.HIGH) }
    var targetDate by remember { mutableStateOf("2026-12-31") }
    var gearCategory by remember { mutableStateOf("Lens") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Gear Wish (Linked to Finance)") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "This item will be saved in your WealthFlow Financial Wishes tab and this Gear Wishlist simultaneously.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Gear Name / Model *") },
                        placeholder = { Text("e.g. Sony FE 70-200mm f/2.8 GM II") },
                        modifier = Modifier.fillMaxWidth().testTag("wish_title_input")
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetPriceInput,
                            onValueChange = { targetPriceInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Target Price (HUF) *") },
                            modifier = Modifier.weight(1f).testTag("wish_price_input")
                        )
                        OutlinedTextField(
                            value = currentSavedInput,
                            onValueChange = { currentSavedInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Already Saved") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = gearCategory,
                        onValueChange = { gearCategory = it },
                        label = { Text("Gear Type (e.g. Lens, Strobe, Camera)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Acquisition Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Why do you need this? Notes & Specs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = targetPriceInput.toDoubleOrNull() ?: 0.0
                    val saved = currentSavedInput.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && price > 0) {
                        onConfirm(title, price, saved, priority, targetDate, gearCategory, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_gear_wish_btn")
            ) {
                Text("Add to Wishlist & Finance")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PromoteGearWishDialog(
    wish: WishItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String, GearCategory, String, String) -> Unit
) {
    var brand by remember { mutableStateOf("Sony") }
    var model by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GearCategory.LENS) }
    var serialNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Main Pelican 1510") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Acquired Gear! Move to Inventory") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Congratulations! '${wish.title}' will be marked as fulfilled in Financial Wishes, and registered into your active Gear Inventory with a value of ${formatHuf(wish.targetPrice)}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = { serialNumber = it },
                    label = { Text("Serial Number") },
                    placeholder = { Text("Optional") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Storage Location / Bag") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(brand, model, category, serialNumber, location)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                modifier = Modifier.testTag("confirm_promote_btn")
            ) {
                Text("Confirm Move to Inventory")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddContractDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, Double, Double, ContractStatus, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var shootDate by remember { mutableStateOf("2026-11-15") }
    var shootStartTime by remember { mutableStateOf("10:00") }
    var shootEndTime by remember { mutableStateOf("16:00") }
    var shootLocation by remember { mutableStateOf("") }
    var totalAmountInput by remember { mutableStateOf("") }
    var depositPaidInput by remember { mutableStateOf("0") }
    var status by remember { mutableStateOf(ContractStatus.SIGNED) }
    var deliverables by remember { mutableStateOf("Edited high-res photos + online gallery") }
    var deliverableDeadline by remember { mutableStateOf("2026-11-30") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Photography Contract") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Contract / Shoot Title *") },
                        placeholder = { Text("e.g. Balaton Wedding Photography") },
                        modifier = Modifier.fillMaxWidth().testTag("contract_title_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("contract_client_input")
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = clientPhone,
                            onValueChange = { clientPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = clientEmail,
                            onValueChange = { clientEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = shootDate,
                            onValueChange = { shootDate = it },
                            label = { Text("Shoot Date") },
                            placeholder = { Text("YYYY-MM-DD") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = shootLocation,
                            onValueChange = { shootLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = shootStartTime,
                            onValueChange = { shootStartTime = it },
                            label = { Text("Start Time") },
                            placeholder = { Text("10:00") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = shootEndTime,
                            onValueChange = { shootEndTime = it },
                            label = { Text("End Time") },
                            placeholder = { Text("16:00") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = totalAmountInput,
                            onValueChange = { totalAmountInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Total Fee (HUF) *") },
                            modifier = Modifier.weight(1f).testTag("contract_total_input")
                        )
                        OutlinedTextField(
                            value = depositPaidInput,
                            onValueChange = { depositPaidInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Deposit Paid") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = deliverables,
                        onValueChange = { deliverables = it },
                        label = { Text("Deliverables") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Timeline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalAmountInput.toDoubleOrNull() ?: 0.0
                    val deposit = depositPaidInput.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && clientName.isNotBlank() && total > 0) {
                        onConfirm(title, clientName, clientEmail, clientPhone, shootDate, shootStartTime, shootEndTime, shootLocation, total, deposit, status, deliverables, deliverableDeadline, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_contract_btn")
            ) {
                Text("Create Contract")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddStudioTaskDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskPriority, String, Long?, String?, String) -> Unit
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-10-15") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf("Shoot Prep") }
    var selectedContractId by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Studio Task") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Description *") },
                        placeholder = { Text("e.g. Charge batteries, format SD cards") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Shoot Prep", "Editing", "Client & Billing", "Gear Maintenance", "Delivery").forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (category == cat) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (category == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                if (contracts.isNotEmpty()) {
                    item {
                        Text("Link to Contract (Optional):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        contracts.forEach { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedContractId = if (selectedContractId == c.id) null else c.id
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.RadioButton(
                                    selected = selectedContractId == c.id,
                                    onClick = {
                                        selectedContractId = if (selectedContractId == c.id) null else c.id
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(c.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val contractTitle = contracts.find { it.id == selectedContractId }?.title
                        onConfirm(title, dueDate, priority, category, selectedContractId, contractTitle, notes)
                    }
                }
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
