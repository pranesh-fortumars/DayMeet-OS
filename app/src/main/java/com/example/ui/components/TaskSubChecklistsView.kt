package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeedItem
import com.example.model.SubChecklist
import com.example.model.SubChecklistItem
import com.example.ui.screens.TaskCategoryPill
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Interactive Sub-Checklists Section
 *
 * Allows creating multiple structured sub-checklists within a task, adding checklist items,
 * toggling completion state, and displaying progress indicators for each checklist.
 */
@Composable
fun TaskSubChecklistsSection(
    task: FeedItem,
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    var showCreateForm by remember { mutableStateOf(false) }
    var newChecklistTitle by remember { mutableStateOf("") }

    val totalItems = task.subChecklists.sumOf { it.items.size }
    val completedItems = task.subChecklists.sumOf { it.items.count { item -> item.isCompleted } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sub_checklist_section_${task.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
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
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAddCheck,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text(
                    text = "Sub-Checklists",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                if (task.subChecklists.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = if (totalItems > 0 && completedItems == totalItems) EmeraldSuccess.copy(alpha = 0.15f) else Primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (totalItems > 0) "$completedItems/$totalItems done" else "${task.subChecklists.size} lists",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (totalItems > 0 && completedItems == totalItems) EmeraldSuccess else Primary,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            TextButton(
                onClick = { showCreateForm = !showCreateForm },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("add_sub_checklist_btn_${task.id}")
            ) {
                Icon(
                    imageVector = if (showCreateForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (showCreateForm) "Cancel" else "New Checklist",
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showCreateForm) "Cancel" else "New Checklist",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }

        // Inline Create Sub-Checklist Form
        AnimatedVisibility(
            visible = showCreateForm,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerLowest,
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_sub_checklist_form_${task.id}")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Create New Sub-Checklist",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    OutlinedTextField(
                        value = newChecklistTitle,
                        onValueChange = { newChecklistTitle = it },
                        placeholder = {
                            Text(
                                "Checklist title (e.g. QA Checklist, Launch Steps...)",
                                fontSize = 12.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_sub_checklist_title_input_${task.id}")
                    )

                    // Quick Preset Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "QA / Testing",
                            "Pre-requisites",
                            "Deployment Steps",
                            "Review & Signoff",
                            "Documentation"
                        ).forEach { preset ->
                            SuggestionChip(
                                onClick = { newChecklistTitle = preset },
                                label = {
                                    Text(preset, fontSize = 11.sp)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                showCreateForm = false
                                newChecklistTitle = ""
                            }
                        ) {
                            Text("Dismiss", fontSize = 12.sp, color = OnSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                if (newChecklistTitle.isNotBlank()) {
                                    viewModel.createSubChecklist(task.id, newChecklistTitle.trim())
                                    newChecklistTitle = ""
                                    showCreateForm = false
                                }
                            },
                            enabled = newChecklistTitle.isNotBlank(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("create_sub_checklist_btn_${task.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Sub-Checklists List
        if (task.subChecklists.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                task.subChecklists.forEach { checklist ->
                    SubChecklistCard(
                        taskId = task.id,
                        checklist = checklist,
                        viewModel = viewModel
                    )
                }
            }
        } else if (!showCreateForm) {
            // Empty State
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLowest.copy(alpha = 0.7f),
                border = BorderStroke(0.8.dp, OutlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCreateForm = true }
                    .testTag("empty_sub_checklists_placeholder_${task.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "No sub-checklists yet",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Tap to create structured sub-checklists within this task.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Text(
                        text = "+ Add",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }
        }
    }
}

/**
 * Individual Sub-Checklist Card
 */
@Composable
fun SubChecklistCard(
    taskId: String,
    checklist: SubChecklist,
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    var newItemTitle by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }

    val itemCount = checklist.items.size
    val completedCount = checklist.items.count { it.isCompleted }
    val progress = if (itemCount > 0) completedCount.toFloat() / itemCount.toFloat() else 0f
    val isAllComplete = itemCount > 0 && completedCount == itemCount

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceContainerLowest,
        border = BorderStroke(1.dp, if (isAllComplete) EmeraldSuccess.copy(alpha = 0.35f) else OutlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("sub_checklist_card_${checklist.id}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Checklist Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isAllComplete) Icons.Default.TaskAlt else Icons.Default.ChecklistRtl,
                        contentDescription = null,
                        tint = if (isAllComplete) EmeraldSuccess else Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = checklist.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        ),
                        modifier = Modifier.testTag("sub_checklist_title_${checklist.id}")
                    )
                    if (itemCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAllComplete) EmeraldSuccess.copy(alpha = 0.15f) else Primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "$completedCount/$itemCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAllComplete) EmeraldSuccess else Primary,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.deleteSubChecklist(taskId, checklist.id) },
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("delete_sub_checklist_${checklist.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete sub-checklist",
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Progress Bar
            if (itemCount > 0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (isAllComplete) EmeraldSuccess else Primary,
                    trackColor = SurfaceContainerHigh
                )
            }

            // Checklist Items
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (checklist.items.isNotEmpty()) {
                        checklist.items.forEach { item ->
                            SubChecklistItemRow(
                                taskId = taskId,
                                checklistId = checklist.id,
                                item = item,
                                viewModel = viewModel
                            )
                        }
                    } else {
                        Text(
                            text = "No items yet. Add tasks below.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Add Item Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newItemTitle,
                            onValueChange = { newItemTitle = it },
                            placeholder = { Text("Add item to checklist...", fontSize = 11.5.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OutlineVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("add_checklist_item_input_${checklist.id}")
                        )

                        Button(
                            onClick = {
                                if (newItemTitle.isNotBlank()) {
                                    viewModel.addSubChecklistItem(taskId, checklist.id, newItemTitle.trim())
                                    newItemTitle = ""
                                }
                            },
                            enabled = newItemTitle.isNotBlank(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("add_checklist_item_btn_${checklist.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add item", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Add", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single SubChecklistItem Row with checkbox, strikethrough, and delete button
 */
@Composable
fun SubChecklistItemRow(
    taskId: String,
    checklistId: String,
    item: SubChecklistItem,
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceContainerLowest)
            .border(
                width = 0.5.dp,
                color = if (item.isCompleted) OutlineVariant.copy(alpha = 0.25f) else OutlineVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("sub_checklist_item_${item.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Custom Checkbox
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (item.isCompleted) EmeraldSuccess else SurfaceContainerHigh)
                .clickable { viewModel.toggleSubChecklistItem(taskId, checklistId, item.id) }
                .testTag("sub_checklist_checkbox_${item.id}"),
            contentAlignment = Alignment.Center
        ) {
            if (item.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Item text
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (item.isCompleted) OnSurfaceVariant.copy(alpha = 0.5f) else OnSurface,
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.Medium,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .weight(1f)
                .clickable { viewModel.toggleSubChecklistItem(taskId, checklistId, item.id) }
        )

        // Delete item button
        IconButton(
            onClick = { viewModel.deleteSubChecklistItem(taskId, checklistId, item.id) },
            modifier = Modifier
                .size(20.dp)
                .testTag("delete_sub_checklist_item_${item.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete item",
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/**
 * Full Task Details Modal Dialog displaying task metadata, notes, and all sub-checklists
 */
@Composable
fun TaskDetailsDialog(
    task: FeedItem,
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit,
    onEditTask: (() -> Unit)? = null
) {
    val liveTask = viewModel.feedItems.collectAsState().value.firstOrNull { it.id == task.id } ?: task

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = SurfaceContainerLowest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_details_dialog_${liveTask.id}")
            .testTag("task_details_view_${liveTask.id}"),
        title = {
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Task Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Overview & sub-checklists",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
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
                // Task Title
                Text(
                    text = liveTask.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 18.sp
                    )
                )

                // Category & Priority Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskCategoryPill(category = liveTask.statusTag ?: "Work")

                    liveTask.priority?.let { priority ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Priority: ${priority.label}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (!liveTask.dueDate.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "Due: ${liveTask.dueDate}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Notes section if present
                if (!liveTask.notes.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = liveTask.notes ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurface, lineHeight = 18.sp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = SurfaceContainerHigh)

                // Sub-Checklists Section (Interactive)
                TaskSubChecklistsSection(
                    task = liveTask,
                    viewModel = viewModel
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEditTask != null) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onEditTask()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
