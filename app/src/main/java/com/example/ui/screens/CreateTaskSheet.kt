package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.Priority
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskSheet(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("Finalize Mobile Design Tokens") }
    var notes by remember { mutableStateOf("Review token architecture with mobile engineering squad before Q4 release freeze.") }
    var selectedPriority by remember { mutableStateOf(Priority.HIGH) }
    var selectedSpace by remember { mutableStateOf("Design") }
    var subtasks by remember {
        mutableStateOf(
            listOf(
                "Export Figma tokens to JSON",
                "Verify dark palette contrast ratios",
                "Approve typography scale with David"
            )
        )
    }
    var newSubtaskText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("create_task_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Task",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth().testTag("task_title_input")
            )

            // Notes / Context input
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes & Context") },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Metadata Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetaPill(icon = Icons.Default.CalendarToday, text = "Today, 05:00 PM")
                MetaPill(icon = Icons.Default.Repeat, text = "None")
                MetaPill(icon = Icons.Default.Alarm, text = "15m before")
            }

            // Priority Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { prio ->
                        val isSelected = selectedPriority == prio
                        val color = when (prio) {
                            Priority.LOW -> Outline
                            Priority.MEDIUM -> Secondary
                            Priority.HIGH -> Primary
                            Priority.URGENT -> Error
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) color.copy(alpha = 0.15f) else SurfaceContainerLow)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) color else SurfaceContainerHigh,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPriority = prio }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prio.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) color else OnSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Space / Category selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Workspace Space",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Work", "Design", "Client", "Personal").forEach { space ->
                        val isSelected = selectedSpace == space
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isSelected) Primary else SurfaceContainerLow)
                                .clickable { selectedSpace = space }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = space,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else OnSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Subtasks Builder
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subtasks (${subtasks.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                }

                subtasks.forEachIndexed { index, sub ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.5.dp, Primary, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurface)
                            )
                        }
                        IconButton(
                            onClick = {
                                subtasks = subtasks.filterIndexed { i, _ -> i != index }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSubtaskText,
                        onValueChange = { newSubtaskText = it },
                        placeholder = { Text("Add subtask...", style = MaterialTheme.typography.bodySmall) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    IconButton(
                        onClick = {
                            if (newSubtaskText.isNotBlank()) {
                                subtasks = subtasks + newSubtaskText
                                newSubtaskText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryFixed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add subtask",
                            tint = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveNewTask(title, notes, selectedPriority, selectedSpace, subtasks)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("save_task_submit_btn")
                ) {
                    Text(
                        text = "Save Task",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.closeCreateTask()
                        viewModel.navigateTo("meetings")
                        viewModel.showToast("Converted to Meeting draft")
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoCall,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Convert to Meeting")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun MetaPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        )
    }
}
