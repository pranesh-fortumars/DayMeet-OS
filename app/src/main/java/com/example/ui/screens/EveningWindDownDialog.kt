package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.FeedCategory
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun EveningWindDownDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    val feedItems by viewModel.feedItems.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val dailyHighlights by viewModel.dailyHighlights.collectAsState()

    val completedTasks = remember(feedItems) { feedItems.count { it.category == FeedCategory.TASK && it.isCompleted } }
    val pendingTasks = remember(feedItems) { feedItems.count { it.category == FeedCategory.TASK && !it.isCompleted } }
    val habitsDone = remember(habits) { habits.count { it.isCompletedToday } }
    val highlightsDone = remember(dailyHighlights) { dailyHighlights.count { it.isCompleted } }

    var winsText by remember { mutableStateOf("") }
    var gratitudeText by remember { mutableStateOf("") }
    var tomorrowFocusText by remember { mutableStateOf("") }
    var selectedEnergyRating by remember { mutableStateOf(4) }
    var rolloverTasksChecked by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("evening_wind_down_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Evening Wind-Down",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Life OS • Retrospective & Guilt-Free Rest",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Day Accomplishments Summary
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TODAY'S CELEBRATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6D28D9),
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "Great Effort! ⭐",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6D28D9)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MetricColumn(value = "$completedTasks", label = "Tasks Done")
                                MetricColumn(value = "$habitsDone/${habits.size}", label = "Habits")
                                MetricColumn(value = "$highlightsDone/${dailyHighlights.size}", label = "Highlights")
                                MetricColumn(value = "88", label = "Sleep Score")
                            }
                        }
                    }

                    // 2. Unfinished Tasks Handler (No-Guilt Rollover)
                    if (pendingTasks > 0) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = rolloverTasksChecked,
                                    onCheckedChange = { rolloverTasksChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Primary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Guilt-Free Rollover ($pendingTasks unfinished)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                    Text(
                                        text = "Automatically reschedule remaining tasks to tomorrow morning.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 3. 60-Second Micro-Reflection
                    Text(
                        text = "60-SECOND MICRO REFLECTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            letterSpacing = 0.5.sp
                        )
                    )

                    OutlinedTextField(
                        value = winsText,
                        onValueChange = { winsText = it },
                        label = { Text("🏆 What went well today? (Wins)") },
                        placeholder = { Text("e.g. Shipped mobile release, had great focus...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = gratitudeText,
                        onValueChange = { gratitudeText = it },
                        label = { Text("🙏 One thing you're grateful for") },
                        placeholder = { Text("e.g. Evening walk, supportive teammates, coffee...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = tomorrowFocusText,
                        onValueChange = { tomorrowFocusText = it },
                        label = { Text("💡 Key learning or priority for tomorrow") },
                        placeholder = { Text("e.g. Tackle toughest problem first thing...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Energy Rating
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Daily Energy Rating",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (1..5).forEach { star ->
                                val isSelected = selectedEnergyRating >= star
                                IconButton(
                                    onClick = { selectedEnergyRating = star },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Rating $star",
                                        tint = if (isSelected) AmberWarning else OutlineVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Complete Wind-down CTA
                Button(
                    onClick = {
                        viewModel.submitEveningReflection(
                            wins = winsText,
                            gratitude = gratitudeText,
                            lesson = tomorrowFocusText,
                            energyRating = selectedEnergyRating,
                            rolloverTasks = rolloverTasksChecked
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("complete_evening_wind_down_btn")
                ) {
                    Icon(imageVector = Icons.Default.NightsStay, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete Wind-Down & Rest 🌙",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = OnSurfaceVariant,
                fontSize = 11.sp
            )
        )
    }
}
