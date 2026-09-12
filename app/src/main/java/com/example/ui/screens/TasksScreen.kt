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

    var filterState by remember { mutableStateOf("All") } // "All", "Pending", "Completed"

    val displayedTasks = remember(tasksOnly, filterState) {
        when (filterState) {
            "Pending" -> tasksOnly.filter { !it.isCompleted }
            "Completed" -> tasksOnly.filter { it.isCompleted }
            else -> tasksOnly
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

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All (${tasksOnly.size})", "Pending ($pendingCount)", "Completed ($completedCount)").forEach { tab ->
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
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    task.statusTag?.let { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isWarning) Color(0xFFD32F2F) else Primary
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isWarning) Color(0xFFFFEBEE) else PrimaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
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
