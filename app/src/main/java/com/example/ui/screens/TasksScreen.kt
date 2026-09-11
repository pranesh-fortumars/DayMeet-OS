package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeedCategory
import com.example.model.FeedItem
import com.example.model.Priority
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
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
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (task.isCompleted) EmeraldSuccess else SurfaceContainer)
                                .clickable { viewModel.toggleFeedTaskDone(task.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (task.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = OnSurface,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                            Text(
                                text = task.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                            )
                        }
                    }

                    task.statusTag?.let { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
