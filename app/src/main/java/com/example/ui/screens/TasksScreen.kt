package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
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
    val feedItems by viewModel.feedItems.collectAsState()
    val tasksOnly = remember(feedItems) {
        feedItems.filter { it.category == FeedCategory.TASK }
    }
    val pendingCount = tasksOnly.count { !it.isCompleted }
    val completedCount = tasksOnly.count { it.isCompleted }

    var filterState by remember { mutableStateOf("All") } // "All", "Pending", "Completed", "High"
    var isAutoSortByPriorityEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val displayedTasks = remember(tasksOnly, filterState, isAutoSortByPriorityEnabled, searchQuery) {
        var base = when (filterState) {
            "Pending" -> tasksOnly.filter { !it.isCompleted }
            "Completed" -> tasksOnly.filter { it.isCompleted }
            "High" -> tasksOnly.filter { it.priority == Priority.HIGH || it.priority == Priority.URGENT || it.statusTag?.contains("High", ignoreCase = true) == true }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tasks & Backlog",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "$pendingCount open • $completedCount completed today",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All (${tasksOnly.size})",
                    "High ($highCount)",
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

        // Task Items
        items(displayedTasks, key = { it.id }) { task ->
            AnimatedTaskItemRow(
                task = task,
                onToggle = { viewModel.toggleFeedTaskDone(task.id) },
                onRemove = { viewModel.removeFeedTask(task.id) }
            )
        }
    }
}

@Composable
fun AnimatedTaskItemRow(
    task: FeedItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isToggledState by remember(task.isCompleted) { mutableStateOf(task.isCompleted) }
    var isVisible by remember { mutableStateOf(true) }
    var isAnimatingOut by remember { mutableStateOf(false) }

    val strikeProgress = remember { Animatable(if (task.isCompleted) 1f else 0f) }
    val isDueSoon = remember(task.time) { TimeUtils.isDueWithinNextTwoHours(task.time) }
    val isWarning = isDueSoon && !task.isCompleted && !isToggledState

    fun triggerCheckboxToggle() {
        if (isAnimatingOut) return
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
            else -> Priority.MEDIUM
        }
        val (priorityBg, priorityTextColor, priorityBorderColor, priorityLabel) = when (effectivePriority) {
            Priority.URGENT, Priority.HIGH -> listOf(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFFFFCDD2), "HIGH")
            Priority.MEDIUM -> listOf(Color(0xFFFFF8E1), Color(0xFFE65100), Color(0xFFFFE082), "MED")
            Priority.LOW -> listOf(Color(0xFFE3F2FD), Color(0xFF1565C0), Color(0xFFBBDEFB), "LOW")
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isWarning) Color(0xFFFFF8F8) else SurfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isWarning) 2.dp else 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isWarning) {
                        Modifier.border(
                            width = 2.dp,
                            color = Color(0xFFE53935),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable { triggerCheckboxToggle() }
                .testTag("task_item_${task.id}")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Left priority accent bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(priorityTextColor as Color)
                )

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
                        // Checkbox with spring/bounce and color transition
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isToggledState) EmeraldSuccess
                                    else if (isWarning) Color(0xFFFFEBEE)
                                    else SurfaceContainerHigh
                                )
                                .clickable { triggerCheckboxToggle() }
                                .testTag("task_checkbox_${task.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isToggledState) {
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

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            // Title with CSS-style strike-through animation
                            Box {
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
                            Text(
                                text = task.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isToggledState) OnSurfaceVariant.copy(alpha = 0.45f) else OnSurfaceVariant
                                )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Category Badge
                        val categoryName = remember(task.statusTag, task.subtitle) {
                            when {
                                task.statusTag in listOf("Work", "Personal", "Shopping", "Urgent", "Finance", "Health") -> task.statusTag!!
                                task.subtitle.contains("Work", ignoreCase = true) -> "Work"
                                task.subtitle.contains("Personal", ignoreCase = true) -> "Personal"
                                task.subtitle.contains("Shopping", ignoreCase = true) -> "Shopping"
                                task.subtitle.contains("Urgent", ignoreCase = true) -> "Urgent"
                                task.subtitle.contains("Finance", ignoreCase = true) -> "Finance"
                                task.subtitle.contains("Health", ignoreCase = true) -> "Health"
                                else -> task.statusTag?.takeIf { it != priorityLabel } ?: "Work"
                            }
                        }
                        val (catIcon, catBg, catColor) = when (categoryName) {
                            "Work" -> Triple("💼", Color(0xFFE8EAF6), Color(0xFF283593))
                            "Personal" -> Triple("👤", Color(0xFFF3E5F5), Color(0xFF6A1B9A))
                            "Shopping" -> Triple("🛒", Color(0xFFE0F2F1), Color(0xFF00695C))
                            "Urgent" -> Triple("⚡", Color(0xFFFFEBEE), Color(0xFFC62828))
                            "Finance" -> Triple("💰", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                            "Health" -> Triple("🏥", Color(0xFFE1F5FE), Color(0xFF0277BD))
                            else -> Triple("📌", Color(0xFFF5F5F5), Color(0xFF424242))
                        }

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

                        // Visual Priority Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(priorityBg as Color)
                                .border(1.dp, priorityBorderColor as Color, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("task_priority_badge_${task.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(priorityTextColor as Color)
                            )
                            Text(
                                text = priorityLabel as String,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = priorityTextColor as Color,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        task.statusTag?.takeIf { it != priorityLabel }?.let { tag ->
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isWarning) Color(0xFFD32F2F) else Primary
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isWarning) Color(0xFFFFEBEE) else PrimaryFixed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
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
            }
        }
    }
}
