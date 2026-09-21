package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.DailyHighlight
import com.example.model.EnergyStatus
import com.example.model.LifePillarType
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun MorningKickoffDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    val dailyHighlights by viewModel.dailyHighlights.collectAsState()
    val energyBudget = remember(dailyHighlights) { viewModel.getDailyEnergyBudget() }
    val meetings by viewModel.meetings.collectAsState()
    val inboxItems by viewModel.lifeInboxItems.collectAsState()

    var showAddHighlight by remember { mutableStateOf(false) }
    var newHighlightTitle by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf(LifePillarType.WORK) }
    var selectedMinutes by remember { mutableStateOf(45) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("morning_kickoff_dialog"),
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
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Morning Kickoff",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Life OS • Intentional Day Architecture",
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
                    // 1. Capacity & Energy Budget Meter
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (energyBudget.status) {
                                EnergyStatus.OVERLOADED -> Color(0xFFFEF2F2)
                                EnergyStatus.HEAVY -> Color(0xFFFFFBEB)
                                else -> PrimaryFixed
                            }
                        ),
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
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BatteryChargingFull,
                                        contentDescription = null,
                                        tint = when (energyBudget.status) {
                                            EnergyStatus.OVERLOADED -> Color(0xFFDC2626)
                                            EnergyStatus.HEAVY -> Color(0xFFD97706)
                                            else -> Primary
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "DAILY ENERGY & CAPACITY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp,
                                            color = OnSurface
                                        )
                                    )
                                }

                                Text(
                                    text = energyBudget.status.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (energyBudget.status) {
                                            EnergyStatus.OVERLOADED -> Color(0xFFDC2626)
                                            EnergyStatus.HEAVY -> Color(0xFFD97706)
                                            else -> Primary
                                        }
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(SurfaceContainerLowest)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${energyBudget.committedHours}h Committed",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface
                                        )
                                    )
                                    Text(
                                        text = "${energyBudget.remainingHours}h available capacity of ${energyBudget.totalCapacityHours}h",
                                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                    )
                                }

                                Text(
                                    text = "${energyBudget.loadPercentage}% Load",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (energyBudget.loadPercentage > 100) Color(0xFFDC2626) else Primary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { (energyBudget.loadPercentage / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = when (energyBudget.status) {
                                    EnergyStatus.OVERLOADED -> Color(0xFFDC2626)
                                    EnergyStatus.HEAVY -> Color(0xFFD97706)
                                    else -> Primary
                                },
                                trackColor = SurfaceContainerHigh
                            )

                            energyBudget.burnoutWarning?.let { warning ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = warning,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (energyBudget.status == EnergyStatus.OVERLOADED) Color(0xFFB91C1C) else OnSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // 2. Top 3 Non-Negotiable Daily Highlights
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOP 3 DAILY HIGHLIGHTS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            if (dailyHighlights.size < 4) {
                                Text(
                                    text = "+ Add Highlight",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .clickable { showAddHighlight = !showAddHighlight }
                                        .padding(4.dp)
                                )
                            }
                        }

                        if (showAddHighlight) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = newHighlightTitle,
                                        onValueChange = { newHighlightTitle = it },
                                        placeholder = { Text("What is your single non-negotiable intent?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            LifePillarType.values().take(3).forEach { pillar ->
                                                FilterChip(
                                                    selected = selectedPillar == pillar,
                                                    onClick = { selectedPillar = pillar },
                                                    label = { Text("${pillar.icon} ${pillar.name}") }
                                                )
                                            }
                                        }
                                        Button(
                                            onClick = {
                                                if (newHighlightTitle.isNotBlank()) {
                                                    viewModel.addDailyHighlight(
                                                        title = newHighlightTitle,
                                                        pillar = selectedPillar,
                                                        minutes = selectedMinutes
                                                    )
                                                    newHighlightTitle = ""
                                                    showAddHighlight = false
                                                }
                                            },
                                            shape = RoundedCornerShape(99.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text("Save", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        dailyHighlights.forEachIndexed { index, highlight ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (highlight.isCompleted) SurfaceContainerLow else SurfaceContainerLowest
                                ),
                                border = if (!highlight.isCompleted) androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleDailyHighlight(highlight.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (highlight.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (highlight.isCompleted) EmeraldSuccess else Primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = highlight.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (highlight.isCompleted) OnSurfaceVariant else OnSurface,
                                                textDecoration = if (highlight.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                            )
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "${highlight.pillar.icon} ${highlight.pillar.label}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            )
                                            Text(text = "•", color = OutlineVariant, fontSize = 10.sp)
                                            Text(
                                                text = "${highlight.estimatedMinutes} mins • ${highlight.timeSlot ?: "Flexible"}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeDailyHighlight(highlight.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = OutlineVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Quick Glance: Today's Schedule & Inbox
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "CALENDAR COMMITMENTS & INBOX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "📅 ${meetings.size} Meetings scheduled",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurface)
                                )
                                Text(
                                    text = "📥 ${inboxItems.size} Unprocessed captures",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (inboxItems.isNotEmpty()) Primary else OnSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lock in Plan CTA
                Button(
                    onClick = {
                        viewModel.confirmMorningPlan()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("lock_in_daily_plan_btn")
                ) {
                    Icon(imageVector = Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lock In Today's Plan 🚀",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
