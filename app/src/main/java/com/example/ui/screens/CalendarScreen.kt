package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.util.TimeUtils
import com.example.viewmodel.DayMeetViewModel

@Composable
fun CalendarScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val calendarMode by viewModel.calendarMode.collectAsState()
    val timelineEvents by viewModel.timelineEvents.collectAsState()
    val currentLifeMode by viewModel.currentLifeMode.collectAsState()
    val timeBlockGaps by viewModel.timeBlockGaps.collectAsState()

    val displayedEvents = remember(timelineEvents, calendarMode, currentLifeMode) {
        val modeFiltered = when (currentLifeMode) {
            LifeMode.WORK -> timelineEvents.filter {
                it.type == TimelineType.MEETING ||
                it.type == TimelineType.DEEP_FOCUS ||
                it.subtitle.contains("Work", ignoreCase = true) ||
                it.priorityTag?.contains("Work", ignoreCase = true) == true
            }
            LifeMode.PERSONAL -> timelineEvents.filter {
                it.type == TimelineType.PERSONAL ||
                it.type == TimelineType.REMINDER ||
                it.subtitle.contains("Personal", ignoreCase = true) ||
                it.priorityTag?.contains("Personal", ignoreCase = true) == true
            }
            LifeMode.ALL -> timelineEvents
        }

        if (calendarMode == "By Category") {
            modeFiltered.sortedBy { it.type.name }
        } else {
            modeFiltered
        }
    }

    val incompleteCount = remember(timelineEvents) {
        timelineEvents.count { !it.isCompleted }
    }

    Box(modifier = modifier.fillMaxSize().background(Surface)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Month Context & Today button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "OCTOBER 2024",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                        Text(
                            text = "Daily Planner",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = OnSurface,
                                fontSize = 24.sp
                            )
                        )
                    }

                    Button(
                        onClick = { viewModel.setSelectedDay(24) },
                        shape = RoundedCornerShape(99.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryFixed,
                            contentColor = OnPrimaryFixed
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("planner_today_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventRepeat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Horizontal Day Strip
            item {
                val days = listOf(
                    Triple("Mon", 21, false),
                    Triple("Tue", 22, false),
                    Triple("Wed", 23, false),
                    Triple("Thu", 24, true),
                    Triple("Fri", 25, false),
                    Triple("Sat", 26, false),
                    Triple("Sun", 27, false)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    days.forEach { (name, dayNum, isToday) ->
                        val isSelected = selectedDay == dayNum

                        Column(
                            modifier = Modifier
                                .width(if (isSelected) 64.dp else 58.dp)
                                .height(if (isSelected) 78.dp else 72.dp)
                                .clip(RoundedCornerShape(if (isSelected) 18.dp else 14.dp))
                                .background(if (isSelected) Primary else SurfaceContainer)
                                .clickable { viewModel.setSelectedDay(dayNum) }
                                .padding(vertical = 8.dp)
                                .testTag("day_strip_$dayNum"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else OnSurfaceVariant
                                )
                            )
                            Text(
                                text = dayNum.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else OnSurface
                                )
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.8f))
                                )
                            }
                        }
                    }
                }
            }

            // Mode Selector: Chronological vs By Category
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(4.dp)
                ) {
                    val isChrono = calendarMode == "Chronological"
                    Button(
                        onClick = { viewModel.setCalendarMode("Chronological") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isChrono) SurfaceContainerLowest else Color.Transparent,
                            contentColor = if (isChrono) OnSurface else OnSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isChrono) 1.dp else 0.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(1f).testTag("toggle_chrono")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewTimeline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chronological",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { viewModel.setCalendarMode("By Category") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isChrono) SurfaceContainerLowest else Color.Transparent,
                            contentColor = if (!isChrono) OnSurface else OnSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (!isChrono) 1.dp else 0.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(1f).testTag("toggle_category")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "By Category",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Daily Momentum Progress Summary
            item {
                val totalEvts = timelineEvents.size
                val doneEvts = timelineEvents.count { it.isCompleted }
                val progressEvts = if (totalEvts > 0) doneEvts.toFloat() / totalEvts else 0f

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(44.dp)) {
                                    val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                    drawCircle(
                                        color = SurfaceContainerHigh,
                                        radius = size.minDimension / 2 - 2.dp.toPx(),
                                        style = stroke
                                    )
                                    drawArc(
                                        color = Primary,
                                        startAngle = -90f,
                                        sweepAngle = 360f * progressEvts,
                                        useCenter = false,
                                        style = stroke
                                    )
                                }
                                Text(
                                    text = "${(progressEvts * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                            }

                            Column {
                                Text(
                                    text = "Focus & Flow",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "$doneEvts of $totalEvts objectives accomplished",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = OnPrimaryFixed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(TertiaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = OnTertiaryFixed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Smart Focus Gap Suggestion in Calendar
            if (timeBlockGaps.isNotEmpty()) {
                val firstGap = timeBlockGaps.first()
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Open Focus Window Detected",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                )
                                Text(
                                    text = "${firstGap.startTime} - ${firstGap.endTime} (${firstGap.durationMinutes}m) • Protect from meeting creep",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF166534), fontSize = 11.sp)
                                )
                            }
                            Button(
                                onClick = { viewModel.fillTimeBlockGapWithFocus(firstGap.id) },
                                shape = RoundedCornerShape(99.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("⚡ Block", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Timeline Items
            items(displayedEvents, key = { it.id }) { event ->
                TimelineRow(
                    event = event,
                    onToggleDone = { viewModel.toggleTimelineTask(event.id) },
                    onToggleSubtask = { subId -> viewModel.toggleSubtask(event.id, subId) },
                    onMeetingJoin = { viewModel.openMeetingMinutes() },
                    onReschedule = { viewModel.showToast("Rescheduled to next open slot") }
                )
            }
            if (incompleteCount > 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = InverseSurface,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showToast("$incompleteCount tasks rescheduled for optimal focus") }
                            .testTag("reschedule_pill_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Update,
                                    contentDescription = null,
                                    tint = SecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Smart Reschedule ($incompleteCount)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = InverseOnSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Auto-align pending items into optimal focus blocks",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = InverseOnSurface.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.showToast("$incompleteCount tasks rescheduled for optimal focus") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SecondaryContainer,
                                    contentColor = OnSecondaryContainer
                                ),
                                shape = RoundedCornerShape(99.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Align", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    event: TimelineEvent,
    onToggleDone: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onMeetingJoin: () -> Unit,
    onReschedule: () -> Unit
) {
    val isDueSoon = remember(event.time, event.period) { TimeUtils.isDueWithinNextTwoHours(event.time, event.period) }
    val isTaskWarning = isDueSoon && !event.isCompleted && (event.type == TimelineType.TASK || event.type == TimelineType.TASK_GROUP)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timeline_row_${event.id}"),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Time label column
        Column(
            modifier = Modifier
                .width(48.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = event.time,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (event.isHappeningNow) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (event.isHappeningNow) Primary else OnSurfaceVariant
                )
            )
            Text(
                text = event.period,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (event.isHappeningNow) Primary else Outline,
                    fontWeight = if (event.isHappeningNow) FontWeight.Bold else FontWeight.Normal
                )
            )
        }

        // Connector Line & Node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            if (event.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(Tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            } else if (event.isHappeningNow) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .border(3.dp, PrimaryFixed, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                        .border(1.5.dp, if (event.type == TimelineType.DEEP_FOCUS) TertiaryFixedDim else PrimaryContainer, CircleShape)
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .defaultMinSize(minHeight = 50.dp)
                    .background(SurfaceContainerHigh)
                    .padding(vertical = 4.dp)
            )
        }

        // Content Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isTaskWarning) Color(0xFFFFF8F8) else SurfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (event.isHappeningNow || isTaskWarning) 3.dp else 1.dp),
            modifier = Modifier
                .weight(1f)
                .then(
                    if (isTaskWarning) {
                        Modifier.border(1.5.dp, Color(0xFFE53935), RoundedCornerShape(16.dp))
                    } else if (event.isHappeningNow) {
                        Modifier.border(1.5.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Top tag & action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (event.isHappeningNow) {
                            Text(
                                text = "• Happening Now",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(PrimaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        val badgeText = when (event.type) {
                            TimelineType.HABIT -> "Habit"
                            TimelineType.TASK -> "Task"
                            TimelineType.MEETING -> "Meeting"
                            TimelineType.DEEP_FOCUS -> "⚡ Deep Focus"
                            TimelineType.PERSONAL -> "Personal"
                            TimelineType.TASK_GROUP -> "Task Group"
                            TimelineType.REMINDER -> "⏰ Reminder"
                        }

                        val badgeBg = when (event.type) {
                            TimelineType.HABIT -> SurfaceVariant
                            TimelineType.TASK, TimelineType.TASK_GROUP -> TertiaryFixed
                            TimelineType.MEETING -> PrimaryFixed
                            TimelineType.DEEP_FOCUS -> SecondaryFixed
                            TimelineType.PERSONAL -> SurfaceContainerHighest
                            TimelineType.REMINDER -> SecondaryFixed
                        }

                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )

                        Text(
                            text = "${event.durationMinutes}m",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }

                    // Reschedule & drag indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (event.type == TimelineType.TASK || event.type == TimelineType.TASK_GROUP) {
                            Text(
                                text = "Reschedule",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = OnSurfaceVariant
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainer)
                                    .clickable { onReschedule() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DragIndicator,
                            contentDescription = "Drag to reorder",
                            tint = Outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title & Done check button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (event.type == TimelineType.HABIT || event.type == TimelineType.TASK || event.type == TimelineType.REMINDER) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (event.isCompleted) TertiaryContainer else SurfaceContainerHigh)
                                .clickable { onToggleDone() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Complete",
                                tint = if (event.isCompleted) OnTertiaryContainer else Outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (event.subtitle.isNotBlank()) {
                    Text(
                        text = event.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (event.streakInfo != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = Tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = event.streakInfo,
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }
                }

                // Happening Now Google Meet banner
                if (event.isHappeningNow && event.linkUrl != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoCameraFront,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = event.linkUrl,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "${event.attendeesWaiting ?: 5} participants waiting",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }

                        Button(
                            onClick = onMeetingJoin,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("join_now_btn")
                        ) {
                            Text(
                                text = "Join",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Interactive Subtasks list for Task Group
                if (event.subtasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        event.subtasks.forEach { sub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLow)
                                    .clickable { onToggleSubtask(sub.id) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (sub.isCompleted) Primary else SurfaceContainerHighest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (sub.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (sub.isCompleted) OnSurfaceVariant else OnSurface,
                                        fontWeight = if (sub.isCompleted) FontWeight.Normal else FontWeight.Medium,
                                        textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                )
                            }
                        }
                    }
                }

                // Zoom launch action for Client Meeting
                if (event.priorityTag == "High" && event.linkUrl == "Zoom Conference") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Zoom Conference",
                                style = MaterialTheme.typography.labelMedium.copy(color = OnSurface)
                            )
                        }
                        Button(
                            onClick = onMeetingJoin,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Launch Zoom",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}
