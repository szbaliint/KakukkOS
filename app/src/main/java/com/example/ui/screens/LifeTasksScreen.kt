package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PhotoTask
import com.example.data.model.TaskPriority
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.CalendarEventData
import com.example.ui.util.CalendarIntegrationHelper
import com.example.ui.util.DueDateDisplayInfo
import com.example.ui.util.DueUrgency
import com.example.ui.util.TaskDateFormatter
import com.example.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskDueFilter(val label: String) {
    ALL("All Tasks"),
    OVERDUE("Overdue"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    UPCOMING("Upcoming"),
    COMPLETED("Completed")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTasksScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val tasks by viewModel.photoTasks.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingTasksCount.collectAsStateWithLifecycle()

    var dueFilter by remember { mutableStateOf(TaskDueFilter.ALL) }
    var categoryFilter by remember { mutableStateOf("All") }
    var isAddTaskDialogOpen by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<PhotoTask?>(null) }
    var taskToDelete by remember { mutableStateOf<PhotoTask?>(null) }

    // Task counts by urgency
    val taskInfos = remember(tasks) {
        tasks.associateWith { TaskDateFormatter.parseAndFormat(it.dueDate) }
    }

    val overdueCount = remember(tasks, taskInfos) {
        tasks.count { !it.isCompleted && taskInfos[it]?.urgency == DueUrgency.OVERDUE }
    }
    val todayCount = remember(tasks, taskInfos) {
        tasks.count { !it.isCompleted && taskInfos[it]?.urgency == DueUrgency.TODAY }
    }
    val thisWeekCount = remember(tasks, taskInfos) {
        tasks.count {
            !it.isCompleted && (taskInfos[it]?.urgency == DueUrgency.TOMORROW || taskInfos[it]?.urgency == DueUrgency.THIS_WEEK)
        }
    }

    val filteredTasks = remember(tasks, dueFilter, categoryFilter, taskInfos) {
        tasks.filter { task ->
            val matchesCategory = (categoryFilter == "All") || (task.category == categoryFilter)
            val info = taskInfos[task]
            val matchesDue = when (dueFilter) {
                TaskDueFilter.ALL -> true
                TaskDueFilter.OVERDUE -> !task.isCompleted && info?.urgency == DueUrgency.OVERDUE
                TaskDueFilter.TODAY -> !task.isCompleted && info?.urgency == DueUrgency.TODAY
                TaskDueFilter.THIS_WEEK -> !task.isCompleted && (info?.urgency == DueUrgency.TOMORROW || info?.urgency == DueUrgency.THIS_WEEK)
                TaskDueFilter.UPCOMING -> !task.isCompleted && (info?.urgency == DueUrgency.UPCOMING || info?.urgency == DueUrgency.NO_DATE)
                TaskDueFilter.COMPLETED -> task.isCompleted
            }
            matchesCategory && matchesDue
        }.sortedWith(
            compareBy<PhotoTask> { it.isCompleted }
                .thenBy {
                    // Sort order: Overdue first, then Today, Tomorrow, This Week, Upcoming, No Date
                    when (taskInfos[it]?.urgency) {
                        DueUrgency.OVERDUE -> 0
                        DueUrgency.TODAY -> 1
                        DueUrgency.TOMORROW -> 2
                        DueUrgency.THIS_WEEK -> 3
                        DueUrgency.UPCOMING -> 4
                        DueUrgency.NO_DATE -> 5
                        null -> 6
                    }
                }
                .thenBy { it.dueDate }
                .thenByDescending { it.priority.ordinal }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tasks & Schedule",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (overdueCount > 0) {
                                "$pendingCount pending • $overdueCount overdue!"
                            } else {
                                "$pendingCount pending • Schedule on track"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (overdueCount > 0) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("tasks_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isAddTaskDialogOpen = true }, modifier = Modifier.testTag("tasks_add_top_btn")) {
                        Icon(Icons.Default.Add, contentDescription = "Quick Add Task", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isAddTaskDialogOpen = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Task") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_task_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Banner with clear deadline overview
            item {
                TaskScheduleOverviewCard(
                    totalPending = pendingCount,
                    overdueCount = overdueCount,
                    todayCount = todayCount,
                    thisWeekCount = thisWeekCount,
                    selectedFilter = dueFilter,
                    onSelectFilter = { dueFilter = it }
                )
            }

            // Timeline filter chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TIMELINE FILTER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskDueFilter.values().forEach { filter ->
                            val isSelected = dueFilter == filter
                            val badgeCount = when (filter) {
                                TaskDueFilter.ALL -> tasks.size
                                TaskDueFilter.OVERDUE -> overdueCount
                                TaskDueFilter.TODAY -> todayCount
                                TaskDueFilter.THIS_WEEK -> thisWeekCount
                                TaskDueFilter.UPCOMING -> tasks.count { !it.isCompleted && (taskInfos[it]?.urgency == DueUrgency.UPCOMING || taskInfos[it]?.urgency == DueUrgency.NO_DATE) }
                                TaskDueFilter.COMPLETED -> tasks.count { it.isCompleted }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { dueFilter = filter },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(filter.label)
                                        if (badgeCount > 0) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                                                } else if (filter == TaskDueFilter.OVERDUE && overdueCount > 0) {
                                                    ExpenseRed.copy(alpha = 0.2f)
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            ) {
                                                Text(
                                                    text = "$badgeCount",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                    color = if (filter == TaskDueFilter.OVERDUE && overdueCount > 0 && !isSelected) {
                                                        ExpenseRed
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (filter == TaskDueFilter.OVERDUE && overdueCount > 0) ExpenseRed else MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("filter_due_${filter.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Category filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Shoot Prep", "Editing", "Gear Maintenance", "Client & Billing", "Delivery", "General").forEach { cat ->
                        FilterChip(
                            selected = categoryFilter == cat,
                            onClick = { categoryFilter = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("filter_cat_${cat.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }

            // Tasks List
            if (filteredTasks.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(52.dp), tint = IncomeGreen)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (dueFilter) {
                                    TaskDueFilter.OVERDUE -> "No Overdue Tasks"
                                    TaskDueFilter.TODAY -> "Nothing Due Today"
                                    TaskDueFilter.THIS_WEEK -> "All Clear for This Week"
                                    TaskDueFilter.COMPLETED -> "No Completed Tasks Yet"
                                    else -> "All Caught Up!"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Switch filters above or tap 'Add Task' to plan your schedule.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { isAddTaskDialogOpen = true },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add a New Task")
                            }
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    val info = taskInfos[task] ?: TaskDateFormatter.parseAndFormat(task.dueDate)
                    TaskItemCard(
                        task = task,
                        dueInfo = info,
                        onToggleCompleted = { viewModel.toggleTaskCompleted(task) },
                        onEdit = { taskToEdit = task },
                        onDelete = { taskToDelete = task }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    if (isAddTaskDialogOpen) {
        EnhancedTaskDialog(
            viewModel = viewModel,
            initialTask = null,
            onDismiss = { isAddTaskDialogOpen = false },
            onConfirm = { title, date, priority, cat, contractId, contractTitle, notes ->
                viewModel.addTask(title, date, priority, cat, contractId, contractTitle, notes)
                isAddTaskDialogOpen = false
            }
        )
    }

    taskToEdit?.let { task ->
        EnhancedTaskDialog(
            viewModel = viewModel,
            initialTask = task,
            onDismiss = { taskToEdit = null },
            onConfirm = { title, date, priority, cat, contractId, contractTitle, notes ->
                viewModel.updateTask(
                    task.copy(
                        title = title,
                        dueDate = date,
                        priority = priority,
                        category = cat,
                        contractId = contractId,
                        contractTitle = contractTitle,
                        notes = notes
                    )
                )
                taskToEdit = null
            }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete '${task.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * High-visibility summary card detailing schedule health and countdowns.
 */
@Composable
fun TaskScheduleOverviewCard(
    totalPending: Int,
    overdueCount: Int,
    todayCount: Int,
    thisWeekCount: Int,
    selectedFilter: TaskDueFilter,
    onSelectFilter: (TaskDueFilter) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (overdueCount > 0) {
                ExpenseRed.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (overdueCount > 0) ExpenseRed.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("tasks_overview_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (overdueCount > 0) Icons.Default.Warning else Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (overdueCount > 0) ExpenseRed else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCHEDULE HEALTH & TIMELINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = if (overdueCount > 0) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (overdueCount > 0) ExpenseRed else IncomeGreen
                ) {
                    Text(
                        text = if (overdueCount > 0) "$overdueCount OVERDUE" else "ON SCHEDULE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric pills row: Overdue, Today, This Week, Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScheduleMetricPill(
                    title = "Overdue",
                    count = overdueCount,
                    accentColor = ExpenseRed,
                    isSelected = selectedFilter == TaskDueFilter.OVERDUE,
                    onClick = { onSelectFilter(TaskDueFilter.OVERDUE) },
                    modifier = Modifier.weight(1f)
                )

                ScheduleMetricPill(
                    title = "Today",
                    count = todayCount,
                    accentColor = GoldAccent,
                    isSelected = selectedFilter == TaskDueFilter.TODAY,
                    onClick = { onSelectFilter(TaskDueFilter.TODAY) },
                    modifier = Modifier.weight(1f)
                )

                ScheduleMetricPill(
                    title = "This Week",
                    count = thisWeekCount,
                    accentColor = MaterialTheme.colorScheme.primary,
                    isSelected = selectedFilter == TaskDueFilter.THIS_WEEK,
                    onClick = { onSelectFilter(TaskDueFilter.THIS_WEEK) },
                    modifier = Modifier.weight(1f)
                )

                ScheduleMetricPill(
                    title = "Pending",
                    count = totalPending,
                    accentColor = MaterialTheme.colorScheme.onSurface,
                    isSelected = selectedFilter == TaskDueFilter.ALL,
                    onClick = { onSelectFilter(TaskDueFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ScheduleMetricPill(
    title: String,
    count: Int,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (count > 0 && accentColor == ExpenseRed) ExpenseRed else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Re-architected Task Card with unmistakably clear, unambiguous due date indicators.
 */
@Composable
fun TaskItemCard(
    task: PhotoTask,
    dueInfo: DueDateDisplayInfo,
    onToggleCompleted: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    val badgeBackground = when {
        task.isCompleted -> IncomeGreen.copy(alpha = 0.15f)
        dueInfo.urgency == DueUrgency.OVERDUE -> ExpenseRed.copy(alpha = 0.15f)
        dueInfo.urgency == DueUrgency.TODAY -> GoldAccent.copy(alpha = 0.18f)
        dueInfo.urgency == DueUrgency.TOMORROW -> GoldAccent.copy(alpha = 0.15f)
        dueInfo.urgency == DueUrgency.THIS_WEEK -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val badgeTextColor = when {
        task.isCompleted -> IncomeGreen
        dueInfo.urgency == DueUrgency.OVERDUE -> ExpenseRed
        dueInfo.urgency == DueUrgency.TODAY -> GoldAccent
        dueInfo.urgency == DueUrgency.TOMORROW -> GoldAccent
        dueInfo.urgency == DueUrgency.THIS_WEEK -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val cardBorderColor = when {
        task.isCompleted -> IncomeGreen.copy(alpha = 0.25f)
        dueInfo.urgency == DueUrgency.OVERDUE -> ExpenseRed.copy(alpha = 0.45f)
        dueInfo.urgency == DueUrgency.TODAY -> GoldAccent.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("life_task_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Completion Checkbox + Task Title + Priority Flag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onToggleCompleted,
                    modifier = Modifier.size(28.dp).padding(top = 2.dp).testTag("toggle_task_${task.id}")
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (task.isCompleted) "Mark Incomplete" else "Mark Complete",
                        tint = if (task.isCompleted) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f) else MaterialTheme.colorScheme.onSurface
                    )

                    // Associated Contract Link if present
                    if (task.contractTitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Client: ${task.contractTitle}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (task.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Priority Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (task.priority) {
                        TaskPriority.URGENT -> ExpenseRed.copy(alpha = 0.15f)
                        TaskPriority.HIGH -> GoldAccent.copy(alpha = 0.15f)
                        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        TaskPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = task.priority.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = when (task.priority) {
                            TaskPriority.URGENT -> ExpenseRed
                            TaskPriority.HIGH -> GoldAccent
                            TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
                            TaskPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dedicated Due Date and Action strip (Unmistakable & clear)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Due Date Info with Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = when {
                                task.isCompleted -> Icons.Filled.CheckCircle
                                dueInfo.urgency == DueUrgency.OVERDUE -> Icons.Default.Warning
                                dueInfo.urgency == DueUrgency.TODAY || dueInfo.urgency == DueUrgency.TOMORROW -> Icons.Default.Alarm
                                else -> Icons.Default.CalendarMonth
                            },
                            contentDescription = null,
                            tint = badgeTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            // Relative label prominent: "Due Today", "Overdue by 2 days", "In 3 days"
                            Text(
                                text = if (task.isCompleted) "Completed" else dueInfo.relativeLabel,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = badgeTextColor
                            )
                            // Exact formatted calendar date underneath: "Thu, Oct 15, 2026"
                            if (task.dueDate.isNotBlank()) {
                                Text(
                                    text = dueInfo.formattedDate,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = badgeTextColor.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Calendar export action
                    if (task.dueDate.isNotBlank()) {
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
                            modifier = Modifier.size(30.dp).testTag("task_cal_${task.id}")
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Add to Google Calendar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Edit button
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp).testTag("edit_task_${task.id}")) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Delete button
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp).testTag("del_task_${task.id}")) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enhanced dialog with intuitive interactive DatePicker and preset quick chips.
 */
@Composable
fun EnhancedTaskDialog(
    viewModel: FinanceViewModel,
    initialTask: PhotoTask?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskPriority, String, Long?, String?, String) -> Unit
) {
    val context = LocalContext.current
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var dueDate by remember {
        mutableStateOf(
            initialTask?.dueDate?.ifBlank {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            } ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }
    var priority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf(initialTask?.category ?: "Shoot Prep") }
    var selectedContractId by remember { mutableStateOf<Long?>(initialTask?.contractId) }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }

    // Date picker dialog helper
    val openDatePicker = {
        val calendar = Calendar.getInstance()
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDate)
            if (parsed != null) calendar.time = parsed
        } catch (e: Exception) {
            // fallback to today
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val chosenCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(chosenCal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val dueInfo = TaskDateFormatter.parseAndFormat(dueDate)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialTask == null) "New Task & Schedule" else "Edit Task")
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Description *") },
                        placeholder = { Text("e.g. Clean A7 IV sensor, send gallery proof") },
                        modifier = Modifier.fillMaxWidth().testTag("task_title_input")
                    )
                }

                // Interactive Due Date section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Due Date & Timeline:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        // Big Clickable Date Selector Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openDatePicker() }
                                .testTag("task_datepicker_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = dueInfo.relativeLabel,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = dueInfo.formattedDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                TextButton(onClick = { openDatePicker() }) {
                                    Text("Change")
                                }
                            }
                        }

                        // Quick Date preset pills (Today, Tomorrow, In 3 Days, In 1 Week)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val iso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val todayCal = Calendar.getInstance()
                            val todayStr = iso.format(todayCal.time)

                            val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                            val tomorrowStr = iso.format(tomorrowCal.time)

                            val threeDaysCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }
                            val threeDaysStr = iso.format(threeDaysCal.time)

                            val nextWeekCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
                            val nextWeekStr = iso.format(nextWeekCal.time)

                            listOf(
                                "Today" to todayStr,
                                "Tomorrow" to tomorrowStr,
                                "In 3 Days" to threeDaysStr,
                                "In 1 Week" to nextWeekStr
                            ).forEach { (label, dateVal) ->
                                val isSelected = dueDate == dateVal
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier.clickable { dueDate = dateVal }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Priority Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Priority Level:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TaskPriority.values().forEach { prio ->
                                val isSelected = priority == prio
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) {
                                        when (prio) {
                                            TaskPriority.URGENT -> ExpenseRed.copy(alpha = 0.2f)
                                            TaskPriority.HIGH -> GoldAccent.copy(alpha = 0.2f)
                                            TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            TaskPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) {
                                            when (prio) {
                                                TaskPriority.URGENT -> ExpenseRed
                                                TaskPriority.HIGH -> GoldAccent
                                                TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
                                                TaskPriority.LOW -> MaterialTheme.colorScheme.outline
                                            }
                                        } else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { priority = prio }
                                ) {
                                    Text(
                                        text = prio.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) {
                                            when (prio) {
                                                TaskPriority.URGENT -> ExpenseRed
                                                TaskPriority.HIGH -> GoldAccent
                                                TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
                                                TaskPriority.LOW -> MaterialTheme.colorScheme.onSurface
                                            }
                                        } else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Category Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Shoot Prep", "Editing", "Client & Billing", "Gear Maintenance", "Delivery", "General").forEach { cat ->
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
                }

                // Link to Contract (Optional)
                if (contracts.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Link to Client Booking (Optional):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
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
                                    RadioButton(
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

                // Notes
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Checklists (Optional)") },
                        placeholder = { Text("Details, battery counts, delivery links...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val contractTitle = contracts.find { it.id == selectedContractId }?.title
                        onConfirm(title.trim(), dueDate.trim(), priority, category, selectedContractId, contractTitle, notes.trim())
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_task_btn")
            ) {
                Text(if (initialTask == null) "Save Task" else "Update Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
