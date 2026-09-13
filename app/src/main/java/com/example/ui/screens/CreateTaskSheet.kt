package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
    val initialTab by viewModel.quickAddInitialTab.collectAsState()
    var selectedType by remember { mutableStateOf(initialTab) }

    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var extraValue by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.HIGH) }

    val types = listOf("Task", "Meeting", "Expense", "Reminder", "Note", "Habit", "Goal", "Bill", "Message")

    LaunchedEffect(initialTab) {
        selectedType = initialTab
        when (initialTab) {
            "Meeting" -> {
                title = "Design Sync"
                detail = "Google Meet • 30 mins"
                extraValue = "Today, 03:00 PM"
            }
            "Expense" -> {
                title = "Coffee & Bakery"
                detail = "Food & Dining"
                extraValue = "240"
            }
            "Reminder" -> {
                title = "Review quarterly presentation"
                detail = "Time-based"
                extraValue = "Today, 06:00 PM"
            }
            "Note" -> {
                title = "Sprint Retrospective Notes"
                detail = "Key action items: refactor state management and streamline navigation."
                extraValue = ""
            }
            "Habit" -> {
                title = "Read 15 mins before bed"
                detail = "Daily habit"
                extraValue = ""
            }
            "Goal" -> {
                title = "Save ₹25,000 for travel fund"
                detail = "Financial goal"
                extraValue = "₹25,000"
            }
            "Bill" -> {
                title = "Internet Fiber Bill"
                detail = "Airtel Broadband"
                extraValue = "1179"
            }
            "Message" -> {
                title = "Alex Chen"
                detail = "Hey, let's sync up before the sprint review tomorrow!"
                extraValue = "Tomorrow, 09:00 AM"
            }
            else -> {
                title = "Finalize Mobile Design Tokens"
                detail = "Review token architecture with mobile engineering squad."
                extraValue = ""
            }
        }
    }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Universal Quick Add",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }

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

            // Type Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedType = type
                            // Provide quick defaults
                            when (type) {
                                "Expense" -> {
                                    title = "Coffee & Bakery"
                                    detail = "Food & Dining"
                                    extraValue = "240"
                                }
                                "Meeting" -> {
                                    title = "Design Sync"
                                    detail = "Google Meet"
                                    extraValue = "Today, 03:00 PM"
                                }
                                "Bill" -> {
                                    title = "Internet Bill"
                                    detail = "Broadband"
                                    extraValue = "1179"
                                }
                                else -> {
                                    if (title.isBlank()) title = "New $type"
                                }
                            }
                        },
                        label = {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        shape = RoundedCornerShape(99.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceContainerLow,
                            labelColor = OnSurfaceVariant
                        )
                    )
                }
            }

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        when (selectedType) {
                            "Expense" -> "Expense Description"
                            "Meeting" -> "Meeting Title"
                            "Message" -> "Recipient Name"
                            "Bill" -> "Biller / Provider Name"
                            "Goal" -> "Goal Name"
                            else -> "Title"
                        }
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth().testTag("quick_add_title_input")
            )

            // Detail / Notes input
            OutlinedTextField(
                value = detail,
                onValueChange = { detail = it },
                label = {
                    Text(
                        when (selectedType) {
                            "Expense" -> "Category (e.g. Dining, Transit)"
                            "Meeting" -> "Agenda / Platform"
                            "Message" -> "Message Content"
                            "Bill" -> "Department / Utility"
                            "Goal" -> "Category / Target Description"
                            else -> "Notes & Context"
                        }
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Optional 3rd parameter input (e.g., Amount, Time, Target Value)
            if (selectedType in listOf("Expense", "Income", "Meeting", "Reminder", "Bill", "Goal", "Message")) {
                OutlinedTextField(
                    value = extraValue,
                    onValueChange = { extraValue = it },
                    label = {
                        Text(
                            when (selectedType) {
                                "Expense", "Income", "Bill" -> "Amount (₹)"
                                "Meeting", "Reminder", "Message" -> "Scheduled Date & Time"
                                "Goal" -> "Target Metric"
                                else -> "Value"
                            }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Priority Selector for Tasks
            if (selectedType == "Task") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Priority Level",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(Priority.HIGH, "High", Color(0xFFD32F2F)),
                            Triple(Priority.MEDIUM, "Medium", Color(0xFFF57C00)),
                            Triple(Priority.LOW, "Low", Color(0xFF1976D2))
                        ).forEach { (p, label, accentColor) ->
                            val isSelected = selectedPriority == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) accentColor.copy(alpha = 0.15f) else SurfaceContainerLow,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, accentColor) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPriority = p }
                                    .testTag("priority_selector_${label.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accentColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) accentColor else OnSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Cancel", color = OnSurfaceVariant)
                }

                Button(
                    onClick = {
                        viewModel.universalQuickAdd(
                            type = selectedType,
                            title = title,
                            detail = detail,
                            extraValue = extraValue,
                            priority = selectedPriority
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.weight(2f).height(48.dp).testTag("quick_add_submit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save $selectedType",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
