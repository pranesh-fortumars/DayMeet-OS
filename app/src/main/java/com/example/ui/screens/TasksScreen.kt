package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeedCategory
import com.example.model.FeedItem
import com.example.model.Priority
import com.example.ui.theme.*
import com.example.util.TimeUtils
import com.example.viewmodel.DayMeetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Notification permission request for Android 13+ (API 33+)
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.widget.Toast.makeText(context, "Task reminders enabled", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val feedItems by viewModel.feedItems.collectAsState()
    val tasksOnly = remember(feedItems) {
        feedItems.filter { it.category == FeedCategory.TASK }
    }
    val pendingCount = tasksOnly.count { !it.isCompleted }
    val completedCount = tasksOnly.count { it.isCompleted }

    val weeklyGoalTasks = remember(tasksOnly) {
        tasksOnly.filter { item ->
            item.statusTag?.contains("Weekly", ignoreCase = true) == true ||
            item.statusTag?.contains("Goal", ignoreCase = true) == true ||
            item.title.contains("Weekly", ignoreCase = true) ||
            item.subtitle.contains("Weekly", ignoreCase = true)
        }
    }
    val totalWeeklyGoals = weeklyGoalTasks.size.coerceAtLeast(1)
    val completedWeeklyGoals = weeklyGoalTasks.count { it.isCompleted }
    val targetGoalProgress = (completedWeeklyGoals.toFloat() / totalWeeklyGoals.toFloat()).coerceIn(0f, 1f)

    val animatedGoalProgress by animateFloatAsState(
        targetValue = targetGoalProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "WeeklyGoalProgressAnimation"
    )

    val streakDays = remember(completedCount) {
        if (completedCount >= 3) 6 else 5
    }
    var showStreakDialog by remember { mutableStateOf(false) }

    var filterState by remember { mutableStateOf("All") } // "All", "High", "Medium", "Low", "Pending", "Completed"
    var isAutoSortByPriorityEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Multi-select state
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedTaskIds by remember { mutableStateOf(emptySet<String>()) }

    val displayedTasks = remember(tasksOnly, filterState, isAutoSortByPriorityEnabled, searchQuery) {
        var base = when (filterState) {
            "Pending" -> tasksOnly.filter { !it.isCompleted }
            "Completed" -> tasksOnly.filter { it.isCompleted }
            "High" -> tasksOnly.filter { it.priority == Priority.HIGH || it.priority == Priority.URGENT || it.statusTag?.contains("High", ignoreCase = true) == true }
            "Medium" -> tasksOnly.filter { it.priority == Priority.MEDIUM || (it.priority == null && it.statusTag?.contains("High", ignoreCase = true) != true && it.statusTag?.contains("Low", ignoreCase = true) != true) }
            "Low" -> tasksOnly.filter { it.priority == Priority.LOW || it.statusTag?.contains("Low", ignoreCase = true) == true }
            else -> tasksOnly
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            base = base.filter { task ->
                task.title.lowercase().contains(q) ||
                task.subtitle.lowercase().contains(q) ||
                task.statusTag?.lowercase()?.contains(q) == true ||
                (task.detail?.lowercase()?.contains(q) == true)
            }
        }

        if (isAutoSortByPriorityEnabled) {
            base.sortedWith(
                compareBy<FeedItem> { item ->
                    when (item.priority) {
                        Priority.URGENT -> 0
                        Priority.HIGH -> 1
                        Priority.MEDIUM -> 2
                        Priority.LOW -> 3
                        null -> when {
                            item.statusTag?.contains("Urgent", ignoreCase = true) == true -> 0
                            item.statusTag?.contains("High", ignoreCase = true) == true -> 1
                            item.statusTag?.contains("Low", ignoreCase = true) == true -> 3
                            else -> 2
                        }
                    }
                }.thenBy { item ->
                    TimeUtils.parseTime(item.time) ?: java.time.LocalTime.MAX
                }
            )
        } else {
            base
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            if (isSelectionMode) {
                // Multi-select Active Header Bar
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selection_mode_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isSelectionMode = false
                                    selectedTaskIds = emptySet()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("cancel_selection_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel Selection",
                                    tint = OnPrimaryContainer
                                )
                            }

                            Text(
                                text = "${selectedTaskIds.size} selected",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnPrimaryContainer
                                ),
                                modifier = Modifier.testTag("selected_count_text")
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    val selectable = displayedTasks.filter { !it.isCompleted }.map { it.id }.toSet()
                                    selectedTaskIds = if (selectedTaskIds.size == selectable.size && selectable.isNotEmpty()) {
                                        emptySet()
                                    } else {
                                        selectable
                                    }
                                },
                                modifier = Modifier.testTag("select_all_btn")
                            ) {
                                Text(
                                    text = if (selectedTaskIds.isNotEmpty() && selectedTaskIds.size == displayedTasks.filter { !it.isCompleted }.size) "Deselect All" else "Select All",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    if (selectedTaskIds.isNotEmpty()) {
                                        viewModel.bulkMarkTasksCompleted(selectedTaskIds)
                                        selectedTaskIds = emptySet()
                                        isSelectionMode = false
                                    }
                                },
                                enabled = selectedTaskIds.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White,
                                    disabledContainerColor = SurfaceContainerHigh,
                                    disabledContentColor = OnSurfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("bulk_complete_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Mark Done",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tasks & Backlog",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = OnSurface,
                                    fontSize = 24.sp
                                )
                            )
                            // Task Completion Streak Counter Badge
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = Color(0xFFFFF3E0),
                                border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .clickable { showStreakDialog = true }
                                    .testTag("task_completion_streak_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🔥", fontSize = 12.sp)
                                    Text(
                                        text = "${streakDays}d Streak",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "$pendingCount open • $completedCount completed today",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Multi-select toggle button
                        IconButton(
                            onClick = {
                                isSelectionMode = true
                                selectedTaskIds = emptySet()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerHigh)
                                .testTag("toggle_multi_select_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Select multiple tasks",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.openCreateTask() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("add_task_top_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Task", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Weekly Goal Tracking Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                border = BorderStroke(1.dp, SurfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_goal_tracking_bar")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎯", fontSize = 14.sp)
                            }
                            Text(
                                text = "Weekly Goals Progress",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }
                        Text(
                            text = "$completedWeeklyGoals / $totalWeeklyGoals Goals (${(animatedGoalProgress * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }

                    // Smooth Filling Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedGoalProgress)
                                .clip(CircleShape)
                                .background(
                                    if (animatedGoalProgress >= 1f) Color(0xFF2E7D32) else Primary
                                )
                        )
                    }
                }
            }
        }

        // Localized Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tasks by title, category, or notes...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh,
                    focusedContainerColor = SurfaceContainerLowest,
                    unfocusedContainerColor = SurfaceContainerLowest
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_search_input")
            )
        }

        // Filter Pills
        item {
            val highCount = remember(tasksOnly) {
                tasksOnly.count { it.priority == Priority.HIGH || it.priority == Priority.URGENT || it.statusTag?.contains("High", ignoreCase = true) == true }
            }
            val mediumCount = remember(tasksOnly) {
                tasksOnly.count { it.priority == Priority.MEDIUM || (it.priority == null && it.statusTag?.contains("High", ignoreCase = true) != true && it.statusTag?.contains("Low", ignoreCase = true) != true) }
            }
            val lowCount = remember(tasksOnly) {
                tasksOnly.count { it.priority == Priority.LOW || it.statusTag?.contains("Low", ignoreCase = true) == true }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All (${tasksOnly.size})",
                    "High ($highCount)",
                    "Medium ($mediumCount)",
                    "Low ($lowCount)",
                    "Pending ($pendingCount)",
                    "Completed ($completedCount)"
                ).forEach { tab ->
                    val rawTab = tab.substringBefore(" (")
                    val isSelected = filterState == rawTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isSelected) Primary else SurfaceContainer)
                            .clickable { filterState = rawTab }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("filter_tab_${rawTab.lowercase()}")
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else OnSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Auto-sort by Priority Toggle Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAutoSortByPriorityEnabled) Primary.copy(alpha = 0.08f) else SurfaceContainerLowest
                ),
                border = BorderStroke(
                    1.dp,
                    if (isAutoSortByPriorityEnabled) Primary.copy(alpha = 0.45f) else OutlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_sort_priority_container")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isAutoSortByPriorityEnabled) Primary else SurfaceContainerHighest
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LowPriority,
                                contentDescription = "Priority sort icon",
                                tint = if (isAutoSortByPriorityEnabled) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Auto-sort by Priority",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                if (isAutoSortByPriorityEnabled) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Primary)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isAutoSortByPriorityEnabled)
                                    "Dynamic: High → Medium → Low, then earliest due date"
                                else
                                    "Dynamically reorder tasks by priority and due date",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isAutoSortByPriorityEnabled) Primary else OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isAutoSortByPriorityEnabled,
                        onCheckedChange = { isAutoSortByPriorityEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = OnSurfaceVariant,
                            uncheckedTrackColor = SurfaceContainerHighest
                        ),
                        modifier = Modifier
                            .testTag("task_sort_priority_toggle")
                            .testTag("auto_sort_priority_toggle")
                    )
                }
            }
        }

        // Task Items with layout animation for smooth position transitions when auto-sorting by priority
        items(displayedTasks, key = { it.id }) { task ->
            AnimatedTaskItemRow(
                task = task,
                isSelectionMode = isSelectionMode,
                isSelected = selectedTaskIds.contains(task.id),
                onSelectToggle = {
                    selectedTaskIds = if (selectedTaskIds.contains(task.id)) {
                        selectedTaskIds - task.id
                    } else {
                        selectedTaskIds + task.id
                    }
                },
                onToggle = { viewModel.toggleFeedTaskDone(task.id) },
                onRemove = { viewModel.removeFeedTask(task.id) },
                onReschedule = { newTime -> viewModel.rescheduleTask(task.id, newTime) },
                onSetPriority = { priority -> viewModel.updateTaskPriority(task.id, priority) },
                onDelete = { viewModel.deleteTask(task.id) },
                onUpdateNotes = { newNotes -> viewModel.updateTaskNotes(task.id, newNotes) },
                onTriggerNotification = { viewModel.triggerTaskNotificationNow(context, task.id) },
                onScheduleAlert = { viewModel.scheduleTaskNotification(context, task.id) },
                onEditTask = { title, subtitle, priority, category, time, notes ->
                    viewModel.editTask(task.id, title, subtitle, priority, category, time, notes)
                },
                onMoveToCalendar = { timeSlot ->
                    viewModel.moveTaskToCalendar(task.id, timeSlot)
                },
                onSetReminder = { reminderTime ->
                    viewModel.setTaskReminder(context, task.id, reminderTime)
                },
                modifier = Modifier.animateItem(
                    fadeInSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    fadeOutSpec = spring(
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            )
        }
    }

    if (showStreakDialog) {
        AlertDialog(
            onDismissRequest = { showStreakDialog = false },
            confirmButton = {
                TextButton(onClick = { showStreakDialog = false }) {
                    Text("Awesome!", fontWeight = FontWeight.Bold)
                }
            },
            icon = {
                Text("🔥", fontSize = 36.sp)
            },
            title = {
                Text("$streakDays-Day Completion Streak!", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("You've completed at least 3 tasks every day for $streakDays consecutive days! Keep up the momentum to build great habits.")
            },
            containerColor = SurfaceContainerLowest
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedTaskItemRow(
    task: FeedItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onReschedule: (String) -> Unit = {},
    onSetPriority: (Priority) -> Unit = {},
    onDelete: () -> Unit = {},
    onUpdateNotes: (String) -> Unit = {},
    onTriggerNotification: () -> Unit = {},
    onScheduleAlert: () -> Unit = {},
    onEditTask: (title: String, subtitle: String, priority: Priority, category: String, time: String, notes: String?) -> Unit = { _, _, _, _, _, _ -> },
    onMoveToCalendar: (timeSlot: String) -> Unit = {},
    onSetReminder: (reminderTime: String) -> Unit = {},
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    var isToggledState by remember(task.isCompleted) { mutableStateOf(task.isCompleted) }
    var isVisible by remember { mutableStateOf(true) }
    var isAnimatingOut by remember { mutableStateOf(false) }

    val strikeProgress = remember { Animatable(if (task.isCompleted) 1f else 0f) }
    val isDueSoon = remember(task.time) { TimeUtils.isDueWithinNextTwoHours(task.time) }
    val isWarning = isDueSoon && !task.isCompleted && !isToggledState

    var showContextMenu by remember { mutableStateOf(false) }
    var showPrioritySubMenu by remember { mutableStateOf(false) }
    var showRescheduleSubMenu by remember { mutableStateOf(false) }
    var showQuickEditDialog by remember { mutableStateOf(false) }
    var showMoveToCalendarDialog by remember { mutableStateOf(false) }
    var showSetReminderDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isNotesExpanded by remember { mutableStateOf(false) }
    var isEditingNotes by remember { mutableStateOf(false) }
    var editedNotesText by remember(task.notes) { mutableStateOf(task.notes ?: "") }

    fun triggerCheckboxToggle() {
        if (isAnimatingOut) return
        // Provide tactile haptic feedback pattern on task completion
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
        } catch (_: Exception) {}

        if (!isToggledState) {
            // Smooth CSS strike-through transition and slide-out animation sequence
            isAnimatingOut = true
            isToggledState = true
            coroutineScope.launch {
                // Phase 1: Smooth CSS strikethrough line drawn across text
                strikeProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                )
                // Brief pause so the satisfying strike-through is visually experienced
                delay(80)
                // Phase 2: Slide-out horizontally & collapse vertically
                isVisible = false
                // Phase 3: Await completion of slide-out transition
                delay(380)
                // Phase 4: Officially remove task from the list
                onRemove()
            }
        } else {
            // If already completed (in Completed tab), uncheck normally
            onToggle()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * 1.3f).toInt() },
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 280, delayMillis = 30, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 280)
        ),
        modifier = modifier
    ) {
        val effectivePriority = task.priority ?: when {
            task.statusTag?.contains("High", ignoreCase = true) == true || isWarning -> Priority.HIGH
            task.statusTag?.contains("Low", ignoreCase = true) == true -> Priority.LOW
            task.subtitle.contains("Priority: High", ignoreCase = true) -> Priority.HIGH
            task.subtitle.contains("Priority: Low", ignoreCase = true) -> Priority.LOW
            else -> Priority.MEDIUM
        }
        // Small color-coded badges: Red for High, Amber for Medium, Blue for Low
        val (priorityBg, priorityTextColor, priorityBorderColor, priorityDotColor, priorityLabel) = when (effectivePriority) {
            Priority.URGENT, Priority.HIGH -> listOf(
                Color(0xFFFFEBEE), // Red background
                Color(0xFFC62828), // Red text
                Color(0xFFFFCDD2), // Red border
                Color(0xFFE53935), // Red dot indicator
                "High"
            )
            Priority.MEDIUM -> listOf(
                Color(0xFFFFF8E1), // Amber background
                Color(0xFFE65100), // Amber text
                Color(0xFFFFE082), // Amber border
                Color(0xFFFFA000), // Amber dot indicator
                "Medium"
            )
            Priority.LOW -> listOf(
                Color(0xFFE3F2FD), // Blue background
                Color(0xFF1565C0), // Blue text
                Color(0xFFBBDEFB), // Blue border
                Color(0xFF1E88E5), // Blue dot indicator
                "Low"
            )
        }

        val categoryName = remember(task.statusTag, task.subtitle) {
            when {
                task.statusTag in listOf("Work", "Personal", "Shopping", "Urgent", "Finance", "Health", "Engineering", "Design", "Security", "Documentation", "Deliverable", "Tech Debt") -> task.statusTag!!
                task.subtitle.contains("Work", ignoreCase = true) -> "Work"
                task.subtitle.contains("Personal", ignoreCase = true) -> "Personal"
                task.subtitle.contains("Shopping", ignoreCase = true) -> "Shopping"
                task.subtitle.contains("Urgent", ignoreCase = true) -> "Urgent"
                task.subtitle.contains("Finance", ignoreCase = true) -> "Finance"
                task.subtitle.contains("Health", ignoreCase = true) -> "Health"
                else -> task.statusTag?.takeIf { it != priorityLabel } ?: "Work"
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isSelectionMode && isSelected -> PrimaryContainer.copy(alpha = 0.45f)
                    isWarning -> Color(0xFFFFF8F8)
                    else -> SurfaceContainerLowest
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isWarning || (isSelectionMode && isSelected)) 2.dp else 1.dp),
            modifier = modifier
                .fillMaxWidth()
                .then(
                    when {
                        isSelectionMode && isSelected -> {
                            Modifier.border(
                                width = 2.dp,
                                color = Primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        isWarning -> {
                            Modifier.border(
                                width = 2.dp,
                                color = Color(0xFFE53935),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        else -> Modifier
                    }
                )
                .combinedClickable(
                    onClick = {
                        if (isSelectionMode) {
                            onSelectToggle()
                        } else {
                            triggerCheckboxToggle()
                        }
                    },
                    onLongClick = {
                        if (isSelectionMode) {
                            onSelectToggle()
                        } else {
                            showContextMenu = true
                        }
                    }
                )
                .testTag("task_item_${task.id}")
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                // Subtle priority left-border indicator bar (Red for High, Amber for Medium, Blue for Low)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                        .background(priorityDotColor as Color)
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox: In selection mode, acts as multi-select checkbox; otherwise toggles completion
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelectionMode && isSelected -> Primary
                                        isSelectionMode -> SurfaceContainerHigh
                                        isToggledState -> EmeraldSuccess
                                        isWarning -> Color(0xFFFFEBEE)
                                        else -> SurfaceContainerHigh
                                    }
                                )
                                .clickable {
                                    if (isSelectionMode) {
                                        onSelectToggle()
                                    } else {
                                        triggerCheckboxToggle()
                                    }
                                }
                                .testTag("task_checkbox_${task.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelectionMode) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else if (isToggledState) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isWarning) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = "Due Soon Warning",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // Title row with small color-coded priority badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f, fill = false)) {
                                    val textColor = if (isToggledState) {
                                        OnSurfaceVariant.copy(alpha = 0.55f)
                                    } else {
                                        OnSurface
                                    }
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = textColor,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.drawWithContent {
                                            drawContent()
                                            if (strikeProgress.value > 0f) {
                                                val strokeW = 2.dp.toPx()
                                                val y = size.height * 0.52f
                                                drawLine(
                                                    color = OnSurfaceVariant,
                                                    start = Offset(0f, y),
                                                    end = Offset(size.width * strikeProgress.value, y),
                                                    strokeWidth = strokeW,
                                                    cap = StrokeCap.Round
                                                )
                                            }
                                        }
                                    )
                                }

                                // Small color-coded priority badge (Red for High, Amber for Medium, Blue for Low)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = priorityBg as Color,
                                    border = BorderStroke(1.dp, priorityBorderColor as Color),
                                    modifier = Modifier
                                        .testTag("task_priority_badge_${task.id}")
                                        .testTag("priority_badge_${(priorityLabel as String).lowercase()}")
                                        .clickable {
                                            showPrioritySubMenu = true
                                            showContextMenu = true
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(priorityDotColor as Color)
                                        )
                                        Text(
                                            text = priorityLabel as String,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = priorityTextColor as Color,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Text(
                                text = task.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isToggledState) OnSurfaceVariant.copy(alpha = 0.45f) else OnSurfaceVariant
                                )
                            )

                            // Notes Badge / Toggle Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (task.notes.isNullOrBlank()) SurfaceContainerHigh.copy(alpha = 0.5f)
                                        else Primary.copy(alpha = 0.12f)
                                    )
                                    .clickable { isNotesExpanded = !isNotesExpanded }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("task_notes_toggle_${task.id}")
                            ) {
                                Icon(
                                    imageVector = if (task.notes.isNullOrBlank()) Icons.Default.NoteAdd else Icons.Default.Notes,
                                    contentDescription = "Notes",
                                    tint = if (task.notes.isNullOrBlank()) OnSurfaceVariant else Primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (task.notes.isNullOrBlank()) "Add Note" else "Note",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (task.notes.isNullOrBlank()) OnSurfaceVariant else Primary,
                                        fontSize = 11.sp
                                    )
                                )
                                Icon(
                                    imageVector = if (isNotesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isNotesExpanded) "Collapse Notes" else "Expand Notes",
                                    tint = if (task.notes.isNullOrBlank()) OnSurfaceVariant else Primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            if (!task.reminderTime.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Primary.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("task_reminder_badge_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Reminder",
                                        tint = Primary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = task.reminderTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Primary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Category Badge
                        val (catIcon, catBg, catColor) = when (categoryName) {
                            "Work" -> Triple("💼", Color(0xFFE8EAF6), Color(0xFF283593))
                            "Personal" -> Triple("👤", Color(0xFFF3E5F5), Color(0xFF6A1B9A))
                            "Shopping" -> Triple("🛒", Color(0xFFE0F2F1), Color(0xFF00695C))
                            "Urgent" -> Triple("⚡", Color(0xFFFFEBEE), Color(0xFFC62828))
                            "Finance" -> Triple("💰", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                            "Health" -> Triple("🏥", Color(0xFFE1F5FE), Color(0xFF0277BD))
                            "Engineering" -> Triple("⚙️", Color(0xFFEDE7F6), Color(0xFF512DA8))
                            "Design" -> Triple("🎨", Color(0xFFFCE4EC), Color(0xFFC2185B))
                            "Security" -> Triple("🔒", Color(0xFFFFF3E0), Color(0xFFE65100))
                            "Documentation" -> Triple("📝", Color(0xFFE0F2F1), Color(0xFF00796B))
                            "Deliverable" -> Triple("🚀", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                            "Tech Debt" -> Triple("🔧", Color(0xFFFBE9E7), Color(0xFFD84315))
                            else -> Triple("📌", Color(0xFFF5F5F5), Color(0xFF424242))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$catIcon $categoryName",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = catColor,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(catBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("task_category_badge_${task.id}")
                            )

                            // Fast Tap Context Menu Button
                            IconButton(
                                onClick = {
                                    try {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    } catch (_: Exception) {}
                                    showContextMenu = true
                                },
                                modifier = Modifier
                                    .size(22.dp)
                                    .testTag("task_menu_trigger_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Task Context Menu",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // Existing time display with warning indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Due Time",
                                tint = if (isWarning) Color(0xFFE53935) else OnSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = task.time,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isWarning) Color(0xFFE53935) else OnSurfaceVariant,
                                    fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                // Expandable Notes Section
                AnimatedVisibility(
                    visible = isNotesExpanded,
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLowest.copy(alpha = 0.9f))
                            .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .testTag("task_notes_section_${task.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Detailed Notes",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurface
                                    )
                                )
                            }

                            if (!isEditingNotes) {
                                TextButton(
                                    onClick = {
                                        editedNotesText = task.notes ?: ""
                                        isEditingNotes = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .height(28.dp)
                                        .testTag("task_notes_edit_button_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Notes",
                                        tint = Primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (task.notes.isNullOrBlank()) "Add" else "Edit",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Primary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isEditingNotes) {
                            OutlinedTextField(
                                value = editedNotesText,
                                onValueChange = { editedNotesText = it },
                                placeholder = {
                                    Text(
                                        "Add detailed task notes, checklist, links, or context...",
                                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant.copy(alpha = 0.6f))
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = OnSurface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("task_notes_input_${task.id}"),
                                minLines = 2,
                                maxLines = 5,
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        isEditingNotes = false
                                        editedNotesText = task.notes ?: ""
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("task_notes_cancel_button_${task.id}")
                                ) {
                                    Text("Cancel", style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant))
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = {
                                        onUpdateNotes(editedNotesText.trim())
                                        isEditingNotes = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("task_notes_save_button_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                }
                            }
                        } else {
                            if (!task.notes.isNullOrBlank()) {
                                Text(
                                    text = task.notes!!,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurface.copy(alpha = 0.85f),
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.testTag("task_notes_content_${task.id}")
                                )
                            } else {
                                Text(
                                    text = "No notes added yet. Tap Add to attach details, meeting points, or checklist.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }
                }
                }
            }

            // Long-press Context Mini-Menu
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = {
                    showContextMenu = false
                    showPrioritySubMenu = false
                    showRescheduleSubMenu = false
                },
                modifier = Modifier
                    .background(SurfaceContainerLowest)
                    .testTag("task_context_menu_${task.id}")
            ) {
                // Header: Task title preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            ),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Quick Options",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
                HorizontalDivider(color = SurfaceContainerHigh)

                // 1. Quick Option: 'Edit' (in-place dialog, no separate edit mode required)
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Modify title, priority, due date or notes",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                                )
                            }
                        }
                    },
                    onClick = {
                        showContextMenu = false
                        showQuickEditDialog = true
                    },
                    modifier = Modifier.testTag("task_action_edit_${task.id}")
                )

                // 2. Quick Option: 'Move to Calendar'
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0288D1).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Move to Calendar",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Schedule into timeline agenda",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                                )
                            }
                        }
                    },
                    onClick = {
                        showContextMenu = false
                        showMoveToCalendarDialog = true
                    },
                    modifier = Modifier.testTag("task_action_move_calendar_${task.id}")
                )

                // 3. Quick Option: 'Set Reminder'
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AmberWarning.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Set Reminder",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Configure push notification alert",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                                )
                            }
                        }
                    },
                    onClick = {
                        showContextMenu = false
                        showSetReminderDialog = true
                    },
                    modifier = Modifier.testTag("task_action_set_reminder_${task.id}")
                )

                HorizontalDivider(color = SurfaceContainerHigh)

                // 4. Quick Option: 'Delete'
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD32F2F)
                                    )
                                )
                                Text(
                                    text = "Remove task without edit mode",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFEF5350), fontSize = 10.sp)
                                )
                            }
                        }
                    },
                    onClick = {
                        showContextMenu = false
                        showDeleteConfirmDialog = true
                    },
                    modifier = Modifier.testTag("task_action_delete_${task.id}")
                )

                HorizontalDivider(color = SurfaceContainerHigh)

                // Secondary Inline Actions: Reschedule & Priority
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                            Text("Reschedule...", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                    },
                    onClick = {
                        showRescheduleSubMenu = !showRescheduleSubMenu
                        showPrioritySubMenu = false
                    },
                    modifier = Modifier.testTag("task_action_reschedule_${task.id}")
                )

                if (showRescheduleSubMenu) {
                    listOf("Today 05:00 PM", "Tomorrow 09:00 AM", "In 2 Hours", "Next Week").forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Text("  • $preset", style = MaterialTheme.typography.bodySmall.copy(color = Primary))
                            },
                            onClick = {
                                onReschedule(preset)
                                showContextMenu = false
                                showRescheduleSubMenu = false
                            }
                        )
                    }
                }

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                            Text("Set Priority...", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                    },
                    onClick = {
                        showPrioritySubMenu = !showPrioritySubMenu
                        showRescheduleSubMenu = false
                    },
                    modifier = Modifier.testTag("task_action_set_priority_${task.id}")
                )

                if (showPrioritySubMenu) {
                    listOf(
                        Triple(Priority.URGENT, "Urgent", Color(0xFFB71C1C)),
                        Triple(Priority.HIGH, "High Priority", Color(0xFFC62828)),
                        Triple(Priority.MEDIUM, "Medium Priority", Color(0xFFE65100)),
                        Triple(Priority.LOW, "Low Priority", Color(0xFF1565C0))
                    ).forEach { (p, label, color) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (task.priority == p) FontWeight.Bold else FontWeight.Normal,
                                            color = if (task.priority == p) color else OnSurface
                                        )
                                    )
                                }
                            },
                            onClick = {
                                onSetPriority(p)
                                showContextMenu = false
                                showPrioritySubMenu = false
                            },
                            modifier = Modifier.testTag("priority_select_${p.name.lowercase()}")
                        )
                    }
                }
            }
        }
    }

    // 1. Quick Edit Task Dialog (In-place modal editing without separate edit mode)
    if (showQuickEditDialog) {
        var editTitle by remember(task.title) { mutableStateOf(task.title) }
        var editSubtitle by remember(task.subtitle) { mutableStateOf(task.subtitle) }
        var editTime by remember(task.time) { mutableStateOf(task.time) }
        var editPriority by remember(task.priority) { mutableStateOf(effectivePriority) }
        var editCategory by remember(categoryName) { mutableStateOf(categoryName) }
        var editNotes by remember(task.notes) { mutableStateOf(task.notes ?: "") }

        AlertDialog(
            onDismissRequest = { showQuickEditDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = SurfaceContainerLowest,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Quick Edit Task",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Update details instantly in place",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_task_title_input_${task.id}"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Priority Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Priority Level",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple(Priority.LOW, "Low", Color(0xFF1565C0)),
                                Triple(Priority.MEDIUM, "Med", Color(0xFFE65100)),
                                Triple(Priority.HIGH, "High", Color(0xFFC62828)),
                                Triple(Priority.URGENT, "Urgent", Color(0xFFB71C1C))
                            ).forEach { (p, label, color) ->
                                val isSelected = editPriority == p
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) color.copy(alpha = 0.15f) else SurfaceContainerHigh.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (isSelected) color else Color.Transparent),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { editPriority = p }
                                        .testTag("edit_task_priority_${label.lowercase()}_${task.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) color else OnSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Category Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Work", "Personal", "Shopping", "Urgent", "Finance", "Health").forEach { cat ->
                                val isSelected = editCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Primary else SurfaceContainerHigh,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { editCategory = cat }
                                        .testTag("edit_task_cat_${cat.lowercase()}_${task.id}")
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else OnSurfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Due Time Input & Presets
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = { editTime = it },
                            label = { Text("Due Date & Time") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_task_time_input_${task.id}"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Today 05:00 PM", "Tomorrow 09:00 AM", "In 2 Hours", "Saturday").forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SurfaceContainerHigh.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { editTime = preset }
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Primary, fontSize = 10.sp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Notes Input
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Detailed Notes & Context") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_task_notes_input_${task.id}"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditTask(
                            editTitle.trim().ifBlank { task.title },
                            editSubtitle.trim().ifBlank { task.subtitle },
                            editPriority,
                            editCategory,
                            editTime.trim().ifBlank { task.time },
                            editNotes.trim().ifBlank { null }
                        )
                        showQuickEditDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.testTag("save_edit_task_btn_${task.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showQuickEditDialog = false },
                    modifier = Modifier.testTag("cancel_edit_task_btn_${task.id}")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Move to Calendar Dialog
    if (showMoveToCalendarDialog) {
        var selectedSlot by remember { mutableStateOf("Today, 03:00 PM") }
        var selectedDuration by remember { mutableStateOf("30 mins") }

        val presetSlots = listOf(
            "Today, 11:00 AM",
            "Today, 02:00 PM",
            "Today, 04:30 PM",
            "Tomorrow, 10:00 AM",
            "Tomorrow, 03:00 PM"
        )

        AlertDialog(
            onDismissRequest = { showMoveToCalendarDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = SurfaceContainerLowest,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0288D1).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Move to Calendar",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Schedule into your daily timeline",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select time block for '${task.title}':",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurface, fontWeight = FontWeight.Medium)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presetSlots.forEach { slot ->
                            val isSelected = selectedSlot == slot
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF0288D1).copy(alpha = 0.12f) else SurfaceContainerHigh.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF0288D1) else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedSlot = slot }
                                    .testTag("calendar_slot_${slot.replace(" ", "_").replace(",", "")}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFF0288D1) else OnSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = slot,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF0288D1) else OnSurface
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF0288D1),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = selectedSlot,
                        onValueChange = { selectedSlot = it },
                        label = { Text("Or Enter Custom Slot") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_calendar_slot_input_${task.id}"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Duration Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("15 mins", "30 mins", "45 mins", "60 mins").forEach { dur ->
                                val isDurSelected = selectedDuration == dur
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDurSelected) Color(0xFF0288D1) else SurfaceContainerHigh,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedDuration = dur }
                                ) {
                                    Text(
                                        text = dur,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isDurSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isDurSelected) Color.White else OnSurfaceVariant,
                                            fontSize = 11.sp
                                        ),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                        val durationInt = selectedDuration.substringBefore(" ").toIntOrNull() ?: 30
                        onMoveToCalendar(selectedSlot)
                        showMoveToCalendarDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    modifier = Modifier.testTag("confirm_move_calendar_btn_${task.id}")
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule on Calendar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showMoveToCalendarDialog = false },
                    modifier = Modifier.testTag("cancel_move_calendar_btn_${task.id}")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Set Reminder Dialog
    if (showSetReminderDialog) {
        var selectedReminder by remember(task.reminderTime) { mutableStateOf(task.reminderTime ?: "In 15 minutes") }

        val presetReminders = listOf(
            "In 15 minutes",
            "In 30 minutes",
            "In 1 hour",
            "Today at 05:00 PM",
            "Tomorrow at 09:00 AM"
        )

        AlertDialog(
            onDismissRequest = { showSetReminderDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = SurfaceContainerLowest,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmberWarning.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Set Reminder Alert",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Get push alerts when due",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Notify me for '${task.title}':",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurface, fontWeight = FontWeight.Medium)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presetReminders.forEach { preset ->
                            val isSelected = selectedReminder == preset
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AmberWarning.copy(alpha = 0.12f) else SurfaceContainerHigh.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSelected) AmberWarning else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedReminder = preset }
                                    .testTag("reminder_preset_${preset.replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Alarm,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFFE65100) else OnSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = preset,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFFE65100) else OnSurface
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = selectedReminder,
                        onValueChange = { selectedReminder = it },
                        label = { Text("Or Custom Reminder Time") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_reminder_input_${task.id}"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            onTriggerNotification()
                            showSetReminderDialog = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_alert_now_btn_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trigger Alert Immediately (Test)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Primary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetReminder(selectedReminder)
                        showSetReminderDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    modifier = Modifier.testTag("confirm_set_reminder_btn_${task.id}")
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Set Reminder", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSetReminderDialog = false },
                    modifier = Modifier.testTag("cancel_set_reminder_btn_${task.id}")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Delete Confirm Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = SurfaceContainerLowest,
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text("Delete Task?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text(
                    "Are you sure you want to delete '${task.title}'? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_task_btn_${task.id}")
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_delete_task_btn_${task.id}")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    }
}
