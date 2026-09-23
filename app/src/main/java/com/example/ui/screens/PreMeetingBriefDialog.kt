package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MeetingPreBrief
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * 15-Minute Pre-Meeting Briefing Dialog:
 * Summarizes attendees, past decisions, open action items, and personal notes.
 */
@Composable
fun PreMeetingBriefDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    val briefState by viewModel.upcomingMeetingPreBrief.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("pre_meeting_brief_dialog")
        ) {
            briefState?.let { brief ->
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
                                    .background(Color(0xFFEDE7F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF673AB7),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Meeting Pre-Brief",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = "Starts in ${brief.startsInMinutes}m",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB45309),
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = brief.scheduledTime,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_pre_brief_btn")) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Meeting Title & Context
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = brief.meetingTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = brief.keyContextSummary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        // 2. Confirmed Attendees
                        item {
                            Text(
                                text = "ATTENDEES & ROLES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                brief.attendees.forEach { attendee ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceContainerHigh
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = attendee,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Past Decisions Alignment
                        item {
                            Text(
                                text = "PREVIOUS DECISIONS ALIGNED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                brief.previousDecisions.forEach { decision ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Text(
                                            text = decision,
                                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurface)
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Open Action Items for Attendees
                        item {
                            Text(
                                text = "OPEN ACTION ITEMS TO REVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                brief.openActionItemsForAttendees.forEach { actionItem ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFFDE68A)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = null,
                                                tint = Color(0xFFD97706),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = actionItem,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF92400E),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Personal Quick Note / Talking Points
                        item {
                            Text(
                                text = "YOUR KEY TALKING POINT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3E8FF),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 ${brief.quickNotes}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B21A8),
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                viewModel.openAudioMemo()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Voice Memo")
                        }

                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Join & Start")
                        }
                    }
                }
            }
        }
    }
}
