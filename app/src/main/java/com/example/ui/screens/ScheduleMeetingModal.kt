package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayMeetRepository
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

data class AvailableParticipant(
    val id: String,
    val name: String,
    val role: String,
    val email: String,
    val avatarUrl: String? = null,
    val initials: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleMeetingModal(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    // Pre-populated team members available for selection
    val defaultParticipants = remember {
        listOf(
            AvailableParticipant("p1", "Alex Chen (You)", "Product Owner", "alex.chen@daymeet.app", DayMeetRepository.ALEX_AVATAR, "AC"),
            AvailableParticipant("p2", "Sarah Lin", "Lead Designer", "sarah.lin@daymeet.app", DayMeetRepository.MAYA_AVATAR, "SL"),
            AvailableParticipant("p3", "Marcus Brody", "Engineering Mgr", "marcus.b@daymeet.app", DayMeetRepository.DAVID_AVATAR, "MB"),
            AvailableParticipant("p4", "Elena Rostova", "Frontend Lead", "elena.r@daymeet.app", DayMeetRepository.ELENA_AVATAR, "ER"),
            AvailableParticipant("p5", "Priya Sharma", "Product Manager", "priya.s@daymeet.app", null, "PS"),
            AvailableParticipant("p6", "David Kim", "Backend Architect", "david.k@daymeet.app", null, "DK"),
            AvailableParticipant("p7", "Rachel Green", "QA Lead", "rachel.g@daymeet.app", null, "RG")
        )
    }

    var title by remember { mutableStateOf("Product Strategy Review") }
    val selectedParticipantNames = remember {
        mutableStateListOf("Alex Chen (You)", "Sarah Lin", "Marcus Brody")
    }
    var customParticipantInput by remember { mutableStateOf("") }
    var participantSearchQuery by remember { mutableStateOf("") }

    // Meeting Duration selection state
    val durationOptions = listOf("15 mins", "30 mins", "45 mins", "60 mins", "90 mins")
    var selectedDurationMinutes by remember { mutableStateOf(30) }
    var selectedDurationLabel by remember { mutableStateOf("30 mins") }

    // Date & Time selection state
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedTime by remember { mutableStateOf("03:00 PM") }
    var selectedPlatform by remember { mutableStateOf("Google Meet") }
    var agendaNotes by remember { mutableStateOf("Review quarterly objectives, token design system, and sprint backlog items.") }

    val quickTitles = listOf(
        "Product Strategy Review",
        "1-on-1 Catchup",
        "Sprint Standup",
        "Design Critique",
        "Architecture Sync"
    )

    val platforms = listOf(
        "Google Meet" to Icons.Default.VideoCameraFront,
        "Zoom" to Icons.Default.Videocam,
        "Teams" to Icons.Default.Groups,
        "In-Person" to Icons.Default.MeetingRoom
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("schedule_meeting_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Schedule Meeting",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Select participants and duration for your sync",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_schedule_meeting_modal")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = SurfaceContainerHigh)

            // Meeting Title Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Meeting Title",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Product Strategy Review") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("schedule_meeting_title_input")
                )

                // Quick suggestions
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

            // ==========================================
            // PARTICIPANTS SELECTION SECTION
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, SurfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_meeting_participants_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PeopleAlt,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Select Participants",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        // Badge showing selected count
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = PrimaryContainer,
                            modifier = Modifier.testTag("selected_participants_count_badge")
                        ) {
                            Text(
                                text = "${selectedParticipantNames.size} Selected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Choose attendees from your team or add external guests",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 12.sp)
                    )

                    // Row of currently selected participants with quick removal
                    if (selectedParticipantNames.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedParticipantNames, key = { it }) { name ->
                                Surface(
                                    shape = RoundedCornerShape(99.dp),
                                    color = Primary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.35f)),
                                    modifier = Modifier.testTag("selected_chip_$name")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.take(1).uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = OnSurface
                                            )
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove $name",
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    selectedParticipantNames.remove(name)
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Multi-select Participant Cards / Chips
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        defaultParticipants.forEach { person ->
                            val isSelected = selectedParticipantNames.contains(person.name)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryContainer.copy(alpha = 0.5f) else SurfaceContainerLowest,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Primary else SurfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) {
                                            if (selectedParticipantNames.size > 1) {
                                                selectedParticipantNames.remove(person.name)
                                            }
                                        } else {
                                            selectedParticipantNames.add(person.name)
                                        }
                                    }
                                    .testTag("participant_chip_${person.name.replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Primary else SurfaceContainerHighest),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = person.initials,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) Color.White else OnSurfaceVariant,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = person.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = OnSurface
                                                )
                                            )
                                            Text(
                                                text = person.role,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = OnSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    // Checkbox circle
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Primary else SurfaceContainerHigh),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add Custom Participant Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customParticipantInput,
                            onValueChange = { customParticipantInput = it },
                            placeholder = { Text("Add attendee email or name...", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_participant_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = SurfaceContainerHigh
                            )
                        )
                        Button(
                            onClick = {
                                val trimmed = customParticipantInput.trim()
                                if (trimmed.isNotBlank() && !selectedParticipantNames.contains(trimmed)) {
                                    selectedParticipantNames.add(trimmed)
                                    customParticipantInput = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.testTag("add_participant_btn")
                        ) {
                            Text("Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // ==========================================
            // MEETING DURATION SELECTION SECTION
            // ==========================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, SurfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_meeting_duration_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Meeting Duration",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        // Selected duration highlight badge
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = Primary,
                            modifier = Modifier.testTag("selected_duration_badge")
                        ) {
                            Text(
                                text = selectedDurationLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Duration Quick Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            15 to "15 mins",
                            30 to "30 mins",
                            45 to "45 mins",
                            60 to "60 mins",
                            90 to "90 mins"
                        ).forEach { (minutes, label) ->
                            val isSelected = selectedDurationMinutes == minutes
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Primary else SurfaceContainerLowest,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Primary else SurfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedDurationMinutes = minutes
                                        selectedDurationLabel = label
                                    }
                                    .testTag("duration_chip_${minutes}m")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (isSelected) Color.White else OnSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = when (minutes) {
                                            15 -> "Quick Sync"
                                            30 -> "Standard"
                                            45 -> "Strategy"
                                            60 -> "1 Hour"
                                            else -> "Workshop"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else OnSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Fine-grained stepper adjustment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Duration:",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val newMinutes = (selectedDurationMinutes - 15).coerceAtLeast(15)
                                    selectedDurationMinutes = newMinutes
                                    selectedDurationLabel = "$newMinutes mins"
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh)
                                    .testTag("duration_minus_btn")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease duration", modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "$selectedDurationMinutes minutes",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                ),
                                modifier = Modifier.testTag("duration_stepper_value")
                            )

                            IconButton(
                                onClick = {
                                    val newMinutes = (selectedDurationMinutes + 15).coerceAtMost(180)
                                    selectedDurationMinutes = newMinutes
                                    selectedDurationLabel = "$newMinutes mins"
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh)
                                    .testTag("duration_plus_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase duration", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ==========================================
            // DATE, TIME & PLATFORM SECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Scheduled Time & Platform",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )

                // Date selection chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Today", "Tomorrow", "Thursday", "Friday", "Next Monday").forEach { day ->
                        val isSel = selectedDate == day
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedDate = day },
                            label = { Text(day, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryContainer,
                                selectedLabelColor = Primary
                            )
                        )
                    }
                }

                // Time slots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("09:30 AM", "11:00 AM", "01:30 PM", "03:00 PM", "04:30 PM", "05:30 PM").forEach { slot ->
                        val isSel = selectedTime == slot
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedTime = slot },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            label = { Text(slot, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Platform selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platforms.forEach { (plat, icon) ->
                        val isSel = selectedPlatform == plat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) PrimaryContainer else SurfaceContainerLow,
                            border = BorderStroke(
                                1.dp,
                                if (isSel) Primary else SurfaceContainerHigh
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPlatform = plat }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = plat,
                                    tint = if (isSel) Primary else OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = plat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Primary else OnSurfaceVariant,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Optional Agenda Notes
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Agenda & Notes (Optional)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                OutlinedTextField(
                    value = agendaNotes,
                    onValueChange = { agendaNotes = it },
                    placeholder = { Text("Add agenda topics or discussion points...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("schedule_meeting_cancel_button")
                ) {
                    Text("Cancel", color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val formattedTime = "$selectedDate, $selectedTime"
                        viewModel.scheduleMeeting(
                            title = title.ifBlank { "Team Sync" },
                            participants = selectedParticipantNames.toList(),
                            date = selectedDate,
                            time = formattedTime,
                            duration = selectedDurationLabel,
                            platform = selectedPlatform,
                            agenda = agendaNotes
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                        .testTag("schedule_meeting_submit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Schedule (${selectedDurationLabel})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
