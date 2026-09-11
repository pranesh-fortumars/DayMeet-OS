package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DayMeetRepository
import com.example.model.FeedCategory
import com.example.model.FeedItem
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun HomeScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val feedFilter by viewModel.feedFilter.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    val focusTimerSeconds by viewModel.focusTimerRemaining.collectAsState()
    val isFocusRunning by viewModel.isFocusRunning.collectAsState()
    val isFocusCompleted by viewModel.isFocusCompleted.collectAsState()

    val filteredList = remember(feedItems, feedFilter) {
        if (feedFilter == FeedCategory.ALL) {
            feedItems
        } else {
            feedItems.filter { it.category == feedFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Welcome & Status Bar
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
                        text = "Good Morning, Alex 👋",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Thursday, Oct 24",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(OutlineVariant)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "72° Sunny",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                // Synced Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(TertiaryFixed.copy(alpha = 0.35f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Synced",
                        tint = Tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Synced",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Hero Card: Your Day & Next Event
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("hero_event_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top header: Live pulse + Google Meet tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                            Text(
                                text = "UP NEXT • IN 25M",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(SecondaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = OnSecondaryFixedVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Google Meet",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSecondaryFixedVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Product Strategy Meeting",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "09:30 AM – 10:30 AM • Q4 Roadmap Finalization",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Avatars & Join Call Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                AvatarImage(DayMeetRepository.MAYA_AVATAR)
                                AvatarImage(DayMeetRepository.DAVID_AVATAR)
                                AvatarImage(DayMeetRepository.ELENA_AVATAR)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                        .border(1.5.dp, SurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+1",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = OnSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4 attending",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.openMeetingMinutes()
                            },
                            shape = RoundedCornerShape(99.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("join_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Join Call",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = SurfaceContainer.copy(alpha = 0.7f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mini succession ticker
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Primary))
                                Text(
                                    text = "10:30 AM",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "Submit Project Report",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                            Text(
                                text = "Task",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = OnSurfaceVariant
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Secondary))
                                Text(
                                    text = "12:30 PM",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "Lunch with Sarah",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                            Text(
                                text = "Event",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = OnSurfaceVariant
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Floating Action Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionPill(
                    icon = Icons.Default.Event,
                    iconTint = Primary,
                    label = "+ Meeting",
                    onClick = { viewModel.navigateTo("meetings") }
                )
                ActionPill(
                    icon = Icons.Default.TaskAlt,
                    iconTint = EmeraldSuccess,
                    label = "+ Task",
                    onClick = { viewModel.openCreateTask() }
                )
                ActionPill(
                    icon = Icons.Default.Alarm,
                    iconTint = Secondary,
                    label = "+ Reminder",
                    onClick = { viewModel.showToast("Reminder shortcut: Set for next slot") }
                )
                ActionPill(
                    icon = Icons.Default.EditNote,
                    iconTint = Outline,
                    label = "+ Quick Note",
                    onClick = { viewModel.showToast("Note added to Scratchpad") }
                )
            }
        }

        // Smart Daily Progress Metrics Widget
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Momentum",
                        style = MaterialTheme.typography.titleMedium.copy(color = OnSurface)
                    )
                    Text(
                        text = "Overall 74%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMeterCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Groups,
                        iconTint = Primary,
                        current = "3/5",
                        label = "Meetings",
                        progress = 0.6f
                    )
                    MetricMeterCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckBox,
                        iconTint = EmeraldSuccess,
                        current = "6/10",
                        label = "Tasks",
                        progress = 0.6f
                    )
                    MetricMeterCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = AmberWarning,
                        current = "4/5",
                        label = "Habits",
                        progress = 0.8f
                    )
                    MetricMeterCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        iconTint = PrimaryContainer,
                        current = "1h 45",
                        label = "Focus",
                        progress = 0.52f
                    )
                }
            }
        }

        // Active Focus Session Widget
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Deep Work",
                                    style = MaterialTheme.typography.titleMedium.copy(color = OnSurface)
                                )
                                // Live soundwave indicator
                                SoundwaveAnimation(isRunning = isFocusRunning && !isFocusCompleted)
                            }

                            val minutes = focusTimerSeconds / 60
                            val seconds = focusTimerSeconds % 60
                            val timeStr = String.format("%02d:%02d", minutes, seconds)

                            Text(
                                text = if (isFocusCompleted) "Completed!" else "$timeStr remaining",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isFocusCompleted) EmeraldSuccess else Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Controls: Pause/Play & Complete
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleFocusTimer() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerLowest)
                                .testTag("focus_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isFocusRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Pause focus timer",
                                tint = OnSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.completeFocusSession() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TertiaryFixed)
                                .testTag("focus_done_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Complete focus timer",
                                tint = OnTertiaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Today at a Glance / Combined Feed Header & Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today at a Glance",
                        style = MaterialTheme.typography.titleLarge.copy(color = OnSurface)
                    )
                    Text(
                        text = "${filteredList.size} scheduled",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FeedCategory.values().forEach { category ->
                        val isSelected = feedFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isSelected) InverseSurface else SurfaceContainer)
                                .clickable { viewModel.setFeedFilter(category) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("filter_${category.name.lowercase()}")
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) InverseOnSurface else OnSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Feed Items
        items(filteredList, key = { it.id }) { item ->
            FeedItemCard(
                item = item,
                onTaskToggle = { viewModel.toggleFeedTaskDone(item.id) },
                onHabitLog = { viewModel.logWaterIntake() },
                onCardClick = {
                    if (item.category == FeedCategory.MEETING) {
                        viewModel.openMeetingMinutes()
                    }
                }
            )
        }
    }
}

@Composable
private fun AvatarImage(url: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.5.dp, SurfaceContainerLowest, CircleShape)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier
            .defaultMinSize(minHeight = 36.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(17.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = OnSurface
                )
            )
        }
    }
}

@Composable
private fun MetricMeterCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    current: String,
    label: String,
    progress: Float
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(34.dp)) {
                    val stroke = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    drawCircle(
                        color = SurfaceContainer,
                        radius = size.minDimension / 2 - 2.dp.toPx(),
                        style = stroke
                    )
                    drawArc(
                        color = iconTint,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = stroke
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = current,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = OnSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SoundwaveAnimation(isRunning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, delayMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(16.dp)
    ) {
        val activeHeight1 = if (isRunning) h1.dp else 4.dp
        val activeHeight2 = if (isRunning) h2.dp else 8.dp
        val activeHeight3 = if (isRunning) h3.dp else 5.dp

        Box(modifier = Modifier.width(2.5.dp).height(activeHeight1).clip(CircleShape).background(Primary))
        Box(modifier = Modifier.width(2.5.dp).height(activeHeight2).clip(CircleShape).background(Primary.copy(alpha = 0.7f)))
        Box(modifier = Modifier.width(2.5.dp).height(activeHeight3).clip(CircleShape).background(Primary))
    }
}

@Composable
private fun FeedItemCard(
    item: FeedItem,
    onTaskToggle: () -> Unit,
    onHabitLog: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("feed_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Leading Icon or Checkbox
                when (item.category) {
                    FeedCategory.TASK -> {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.isCompleted) EmeraldSuccess else SurfaceContainer)
                                .clickable { onTaskToggle() }
                                .testTag("task_check_${item.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Task completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    FeedCategory.MEETING -> {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SecondaryFixed.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.statusTag?.contains("Zoom") == true) Icons.Default.ScreenShare else Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    FeedCategory.REMINDER -> {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SecondaryFixed.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    FeedCategory.HABIT -> {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TertiaryFixed.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    else -> {}
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.category == FeedCategory.MEETING) Primary else OnSurfaceVariant
                            )
                        )

                        item.statusTag?.let { tag ->
                            val tagBg = when {
                                tag.contains("High Priority") || tag.contains("Due in") -> ErrorContainer
                                tag.contains("Follow-up") || tag.contains("Zoom") -> SecondaryFixed
                                tag.contains("🔥") -> SurfaceContainer
                                else -> SurfaceContainerHigh
                            }
                            val tagColor = when {
                                tag.contains("High Priority") || tag.contains("Due in") -> OnErrorContainer
                                tag.contains("Follow-up") || tag.contains("Zoom") -> OnSecondaryFixedVariant
                                else -> OnSurface
                            }

                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tagColor
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(tagBg)
                                    .padding(horizontal = 7.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = OnSurface,
                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp)
                    )

                    // Habit progress bar if applicable
                    item.habitProgress?.let { progress ->
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(CircleShape),
                            color = SecondaryContainer,
                            trackColor = SurfaceContainer
                        )
                    }
                }
            }

            // Trailing Action Button
            when (item.category) {
                FeedCategory.TASK -> {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FeedCategory.REMINDER -> {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Secondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                FeedCategory.HABIT -> {
                    IconButton(
                        onClick = onHabitLog,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                            .testTag("log_habit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log",
                            tint = Tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                FeedCategory.MEETING -> {
                    IconButton(
                        onClick = onCardClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
