package com.example.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickScheduleMeetingDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("Product Strategy Review") }
    var customParticipant by remember { mutableStateOf("") }
    val availableParticipants = listOf("Alex Chen", "Sarah Lin", "Marcus Brody", "Elena Rostova", "Priya Sharma")
    val selectedParticipants = remember { mutableStateListOf("Alex Chen", "Sarah Lin") }
    var selectedTime by remember { mutableStateOf("Today, 03:00 PM") }
    var selectedDuration by remember { mutableStateOf("30 mins") }
    var selectedPlatform by remember { mutableStateOf("Google Meet") }

    val quickTitles = listOf("Product Strategy Review", "1-on-1 Catchup", "Sprint Standup", "Design Critique")
    val quickTimes = listOf("Today, 03:00 PM", "Today, 05:00 PM", "Tomorrow, 10:00 AM", "Tomorrow, 02:30 PM")
    val durations = listOf("15 mins", "30 mins", "45 mins", "60 mins")
    val platforms = listOf("Google Meet", "Zoom", "Teams", "In-person")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("quick_schedule_meeting_dialog"),
            color = Surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoCameraFront,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Schedule Meeting",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Quick sync via Quick Capture Hub",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("quick_schedule_meeting_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                Divider(color = SurfaceContainerHigh)

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Meeting Title",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = SurfaceContainerHigh
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meeting_title_input")
                    )

                    // Quick suggestion chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickTitles.forEach { suggestion ->
                            AssistChip(
                                onClick = { title = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // Participants Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Participants (${selectedParticipants.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    // Multi-select Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableParticipants.forEach { person ->
                            val isSelected = selectedParticipants.contains(person)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        if (selectedParticipants.size > 1) {
                                            selectedParticipants.remove(person)
                                        }
                                    } else {
                                        selectedParticipants.add(person)
                                    }
                                },
                                label = { Text(person, fontSize = 12.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainer,
                                    selectedLabelColor = Primary
                                )
                            )
                        }
                    }

                    // Optional custom participant input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customParticipant,
                            onValueChange = { customParticipant = it },
                            placeholder = { Text("Add attendee email or name...", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = SurfaceContainerHigh
                            )
                        )
                        Button(
                            onClick = {
                                if (customParticipant.isNotBlank() && !selectedParticipants.contains(customParticipant.trim())) {
                                    selectedParticipants.add(customParticipant.trim())
                                    customParticipant = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Text("Add", color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Time & Duration
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Scheduled Time & Duration",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    OutlinedTextField(
                        value = selectedTime,
                        onValueChange = { selectedTime = it },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Primary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meeting_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = SurfaceContainerHigh
                        )
                    )

                    // Quick time slots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickTimes.forEach { slot ->
                            AssistChip(
                                onClick = { selectedTime = slot },
                                label = { Text(slot, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Duration selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durations.forEach { d ->
                            val isSel = selectedDuration == d
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Primary else SurfaceContainerLow)
                                    .clickable { selectedDuration = d }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Color.White else OnSurfaceVariant,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                // Platform Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Platform",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        platforms.forEach { plat ->
                            val isSel = selectedPlatform == plat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) PrimaryContainer else SurfaceContainerLow,
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, Primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPlatform = plat }
                            ) {
                                Text(
                                    text = plat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Primary else OnSurfaceVariant,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
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
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel", color = OnSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            viewModel.scheduleMeetingFromHub(
                                title = title,
                                participants = selectedParticipants.toList(),
                                time = selectedTime,
                                platform = selectedPlatform,
                                duration = selectedDuration
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("quick_schedule_meeting_submit")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Schedule Meeting",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
