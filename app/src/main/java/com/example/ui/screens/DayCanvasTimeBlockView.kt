package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeedCategory
import com.example.model.TimeBlockGap
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun DayCanvasTimeBlockCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val meetings by viewModel.meetings.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    val timeBlockGaps by viewModel.timeBlockGaps.collectAsState()
    val energyBudget = remember(meetings, feedItems) { viewModel.getDailyEnergyBudget() }

    val pendingTasks = remember(feedItems) {
        feedItems.filter { it.category == FeedCategory.TASK && !it.isCompleted }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("day_canvas_time_block_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewTimeline,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Day Canvas & Time Blocks",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "${energyBudget.committedHours}h planned • ${energyBudget.status.label}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Morning Kickoff Quick Trigger
                Button(
                    onClick = { viewModel.openMorningKickoff() },
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryFixed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp).testTag("plan_day_canvas_btn")
                ) {
                    Text(
                        text = "Plan Day ☀️",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Time Blocks Sequence
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Event 1: Meeting
                TimeBlockItem(
                    time = "09:30 AM",
                    duration = "45m",
                    title = "Product Strategy Review",
                    type = "Meeting",
                    typeColor = Color(0xFF2563EB),
                    icon = Icons.Default.Videocam
                )

                // Event 2: Scheduled Task
                TimeBlockItem(
                    time = "10:45 AM",
                    duration = "45m",
                    title = "Q4 Token Audit Workspace Signoff",
                    type = "Priority Task",
                    typeColor = Color(0xFFD97706),
                    icon = Icons.Default.CheckCircle
                )

                // SMART GAP 1 (11:30 AM - 12:30 PM)
                val gap1 = timeBlockGaps.find { it.id == "gap_1" }
                if (gap1 != null) {
                    SmartGapBlock(
                        gap = gap1,
                        onScheduleFocus = { viewModel.fillTimeBlockGapWithFocus(gap1.id) },
                        onFitTask = {
                            if (pendingTasks.isNotEmpty()) {
                                viewModel.fillTimeBlockGapWithTask(gap1.id, pendingTasks.first().id)
                            } else {
                                viewModel.showToast("All tasks completed! Scheduling deep work session.")
                                viewModel.fillTimeBlockGapWithFocus(gap1.id)
                            }
                        }
                    )
                }

                // Event 3: Deep Focus Block
                TimeBlockItem(
                    time = "02:00 PM",
                    duration = "90m",
                    title = "Deep Work: Mobile Tokens Architecture",
                    type = "Focus Sanctuary",
                    typeColor = Color(0xFF16A34A),
                    icon = Icons.Default.Shield
                )

                // SMART GAP 2 (03:30 PM - 04:15 PM)
                val gap2 = timeBlockGaps.find { it.id == "gap_2" }
                if (gap2 != null) {
                    SmartGapBlock(
                        gap = gap2,
                        onScheduleFocus = { viewModel.fillTimeBlockGapWithFocus(gap2.id) },
                        onFitTask = {
                            if (pendingTasks.isNotEmpty()) {
                                viewModel.fillTimeBlockGapWithTask(gap2.id, pendingTasks.last().id)
                            } else {
                                viewModel.fillTimeBlockGapWithFocus(gap2.id)
                            }
                        }
                    )
                }

                // Event 4: Workout / Health
                TimeBlockItem(
                    time = "05:30 PM",
                    duration = "45m",
                    title = "5km Sunset Cardio & Core Reset",
                    type = "Health Routine",
                    typeColor = Color(0xFF9333EA),
                    icon = Icons.Default.DirectionsRun
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer link to Full Calendar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo("calendar") }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Full Timeline & Calendar →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun TimeBlockItem(
    time: String,
    duration: String,
    title: String,
    type: String,
    typeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(62.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 11.sp
                )
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }

        Box(
            modifier = Modifier
                .size(4.dp, 28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(typeColor)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                ),
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = typeColor, modifier = Modifier.size(12.dp))
                Text(
                    text = type,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = typeColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SmartGapBlock(
    gap: TimeBlockGap,
    onScheduleFocus: () -> Unit,
    onFitTask: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(18.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${gap.durationMinutes}m Free Window (${gap.startTime} - ${gap.endTime})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                )
                Text(
                    text = "High-leverage focus slot detected",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF166534),
                        fontSize = 10.sp
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onScheduleFocus,
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("⚡ Focus", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onFitTask,
                    shape = RoundedCornerShape(99.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("📋 Fit Task", fontSize = 11.sp, color = Color(0xFF15803D))
                }
            }
        }
    }
}
