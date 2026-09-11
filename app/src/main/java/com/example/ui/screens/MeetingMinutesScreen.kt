package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DayMeetRepository
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun MeetingMinutesScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val actionItems by viewModel.minutesActions.collectAsState()
    val doneCount = actionItems.count { it.isDone }
    val totalCount = actionItems.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface.copy(alpha = 0.95f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.closeMeetingMinutes() },
                        modifier = Modifier.testTag("back_button_minutes")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }

                    Text(
                        text = "Meeting Minutes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    IconButton(onClick = { viewModel.showToast("Options menu") }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = OnSurfaceVariant
                        )
                    }
                }
            }

            // Scrollable Body
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Meeting Meta Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(SurfaceContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Tertiary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "AI Generated • Verified by Organizer",
                                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(SurfaceContainerLow)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Google Meet",
                                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Product Strategy & Roadmap Review",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Oct 24, 2024",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                                Text(text = "•", color = OutlineVariant)
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "45 mins (09:30 AM – 10:15 AM)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = SurfaceContainer, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Attendees List
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ATTENDEES (5)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "All present",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy((-6).dp)
                            ) {
                                AvatarSmall(DayMeetRepository.ALEX_AVATAR)
                                AvatarSmall(DayMeetRepository.MAYA_AVATAR)
                                AvatarSmall(DayMeetRepository.DAVID_AVATAR)
                                AvatarSmall(DayMeetRepository.ELENA_AVATAR)
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryFixed)
                                        .border(2.dp, SurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "DK",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnPrimaryFixedVariant
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Alex Chen, Sarah Lee, Mark D., Elena R., David K.",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Key Takeaways & Executive Summary Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Key Takeaways & Executive Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                    )
                                    Text(
                                        text = "Q4 Priority Shift: Strategic focus transitioned definitively toward the mobile navigation architecture and core responsiveness to capture enterprise field adoption.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = OnSurface,
                                            lineHeight = 20.sp
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                    )
                                    Text(
                                        text = "Resource Scaling: Executive committee formally approved headcount budget for 2 senior frontend engineers dedicated to mobile delivery.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = OnSurface,
                                            lineHeight = 20.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Key Decisions Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TertiaryFixed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Gavel,
                                            contentDescription = null,
                                            tint = OnTertiaryFixed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "Key Decisions",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                }

                                Text(
                                    text = "3 Approved",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnTertiaryFixed
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(TertiaryFixed)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DecisionItem(
                                    title = "Launch DayMeet Mobile Beta by Nov 15th",
                                    subtitle = "Unanimous consensus across design and engineering squads"
                                )
                                DecisionItem(
                                    title = "Adopt Plus Jakarta Sans & 8pt Design Grid",
                                    subtitle = "Full token harmonization across Android and iOS release tracks"
                                )
                                DecisionItem(
                                    title = "Next budget review scheduled for Friday",
                                    subtitle = "David K. will present finalized engineering compensation sheets"
                                )
                            }
                        }
                    }
                }

                // Action Items & Ownership
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PrimaryFixed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Checklist,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "Action Items & Ownership",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                }

                                Text(
                                    text = "$doneCount/$totalCount Done",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                actionItems.forEach { item ->
                                    ActionItemRow(
                                        item = item,
                                        onToggleDone = { viewModel.toggleMinuteAction(item.id) },
                                        onConvert = { viewModel.convertMinuteActionToTask(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Toolbar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfaceContainerLowest.copy(alpha = 0.95f),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.showToast("Export prepared for Slack #product-sync") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("export_minutes_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export & Share PDF / Slack",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = { viewModel.showToast("Follow-up drafted for Oct 31, 09:30 AM") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainer,
                        contentColor = OnSurface
                    ),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventRepeat,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Schedule Follow-up Meeting",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarSmall(url: String) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .border(2.dp, SurfaceContainerLowest, CircleShape)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DecisionItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLow)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Tertiary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
private fun ActionItemRow(
    item: com.example.viewmodel.MinuteActionItem,
    onToggleDone: () -> Unit,
    onConvert: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (item.isDone) Tertiary else SurfaceContainerHighest)
                    .clickable { onToggleDone() },
                contentAlignment = Alignment.Center
            ) {
                if (item.isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    val tagBg = if (item.priority == "High Priority") ErrorContainer else if (item.priority == "Medium") SecondaryFixed else SurfaceContainerHigh
                    val tagColor = if (item.priority == "High Priority") OnErrorContainer else if (item.priority == "Medium") OnSecondaryFixedVariant else OnSurfaceVariant

                    Text(
                        text = item.priority,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(tagBg)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.assignee,
                            style = MaterialTheme.typography.labelMedium.copy(color = OnSurfaceVariant)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (item.due.contains("Today")) Error else OnSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.due,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (item.due.contains("Today")) Error else OnSurfaceVariant,
                                fontWeight = if (item.due.contains("Today")) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (item.isConverted) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TertiaryFixed)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = OnTertiaryFixed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Assigned & Synced",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnTertiaryFixed
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainer)
                        .clickable { onConvert() }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddTask,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Convert to Task",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )
                }
            }
        }
    }
}
