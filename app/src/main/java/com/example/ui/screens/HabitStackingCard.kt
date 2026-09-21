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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HabitStack
import com.example.model.LifePillarType
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun HabitStackingCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val stacks by viewModel.habitStacks.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val completedCount = stacks.count { it.isCompletedToday }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_stacking_card")
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
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Habit Stacking & Anchors",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Anchor → Tiny Action → Celebration",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = if (completedCount == stacks.size) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "$completedCount/${stacks.size} Done",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (completedCount == stacks.size) Color(0xFF15803D) else Color(0xFFB45309)
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.size(30.dp).testTag("add_habit_stack_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Stack", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Habit Stacks List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                stacks.forEach { stack ->
                    HabitStackItemRow(
                        stack = stack,
                        onToggle = { viewModel.toggleHabitStack(stack.id) },
                        onDelete = { viewModel.deleteHabitStack(stack.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateHabitStackDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun HabitStackItemRow(
    stack: HabitStack,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (stack.isCompletedToday) SurfaceContainerLowest else SurfaceContainerLow
        ),
        border = if (stack.isCompletedToday) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_stack_item_${stack.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Checkbox(
                checked = stack.isCompletedToday,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF16A34A),
                    uncheckedColor = OutlineVariant
                ),
                modifier = Modifier.testTag("toggle_habit_stack_${stack.id}")
            )

            Column(modifier = Modifier.weight(1f)) {
                // Time & Pillar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stack.pillar.icon, fontSize = 12.sp)
                    Text(
                        text = stack.timeOfDay,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryFixed)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                    Text(
                        text = "🔥 ${stack.streakDays}d streak",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD97706),
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Anchor Habit
                Text(
                    text = stack.anchorHabit,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                )

                // New Habit (Highlighted)
                Text(
                    text = "↳ ${stack.newTinyHabit}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (stack.isCompletedToday) Color(0xFF15803D) else OnSurface,
                        textDecoration = if (stack.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                // Celebration
                Text(
                    text = "🎉 ${stack.rewardOrCelebration}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = OutlineVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun CreateHabitStackDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var anchor by remember { mutableStateOf("") }
    var newHabit by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf(LifePillarType.HEALTH) }
    var selectedTime by remember { mutableStateOf("Morning") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier.fillMaxWidth().testTag("create_habit_stack_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stack a Tiny Habit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = anchor,
                    onValueChange = { anchor = it },
                    label = { Text("Anchor Habit (Existing Routine)") },
                    placeholder = { Text("After I pour my first morning coffee...") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newHabit,
                    onValueChange = { newHabit = it },
                    label = { Text("New Tiny Habit (2-minute rule)") },
                    placeholder = { Text("...I will review my top 3 daily priorities") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reward,
                    onValueChange = { reward = it },
                    label = { Text("Immediate Celebration / Reward") },
                    placeholder = { Text("Savor the first warm sip with satisfaction") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Pillar Selector
                Column {
                    Text("Pillar Category", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        LifePillarType.values().forEach { pillar ->
                            FilterChip(
                                selected = selectedPillar == pillar,
                                onClick = { selectedPillar = pillar },
                                label = { Text(pillar.icon, fontSize = 12.sp) },
                                shape = RoundedCornerShape(99.dp),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }

                // Time of Day
                Column {
                    Text("Time of Day", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        listOf("Morning", "Afternoon", "Evening").forEach { t ->
                            FilterChip(
                                selected = selectedTime == t,
                                onClick = { selectedTime = t },
                                label = { Text(t, fontSize = 11.sp) },
                                shape = RoundedCornerShape(99.dp),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (anchor.isNotBlank() && newHabit.isNotBlank()) {
                                viewModel.addHabitStack(
                                    anchor = anchor,
                                    newHabit = newHabit,
                                    reward = reward.ifBlank { "Feel proud and check off streak" },
                                    pillar = selectedPillar,
                                    timeOfDay = selectedTime
                                )
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Create Stack")
                    }
                }
            }
        }
    }
}
