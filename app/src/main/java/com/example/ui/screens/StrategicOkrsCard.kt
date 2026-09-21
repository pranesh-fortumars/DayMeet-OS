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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GoalHorizon
import com.example.model.LifePillarType
import com.example.model.StrategicOKR
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun StrategicOkrsCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val okrs by viewModel.strategicOkrs.collectAsState()
    var selectedHorizon by remember { mutableStateOf<GoalHorizon?>(null) } // null = All
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredOkrs = remember(okrs, selectedHorizon) {
        if (selectedHorizon != null) okrs.filter { it.horizon == selectedHorizon } else okrs
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("strategic_okrs_card")
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
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Strategic OKRs & Horizons",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Quarterly & Annual Pillars Alignment",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.size(32.dp).testTag("add_strategic_okr_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add OKR", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizon Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedHorizon == null,
                    onClick = { selectedHorizon = null },
                    label = { Text("All (${okrs.size})", fontSize = 11.sp) },
                    shape = RoundedCornerShape(99.dp),
                    modifier = Modifier.height(28.dp)
                )
                GoalHorizon.values().forEach { horizon ->
                    FilterChip(
                        selected = selectedHorizon == horizon,
                        onClick = { selectedHorizon = horizon },
                        label = { Text(horizon.badge, fontSize = 11.sp) },
                        shape = RoundedCornerShape(99.dp),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // OKR items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredOkrs.forEach { okr ->
                    StrategicOkrItemRow(
                        okr = okr,
                        onUpdateProgress = { newProgress -> viewModel.updateOkrProgress(okr.id, newProgress) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateOkrDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun StrategicOkrItemRow(
    okr: StrategicOKR,
    onUpdateProgress: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("okr_item_${okr.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = okr.pillar.icon, fontSize = 14.sp)
                    Text(
                        text = okr.horizon.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(android.graphics.Color.parseColor(okr.colorHex))
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(android.graphics.Color.parseColor(okr.colorHex)).copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = okr.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = when (okr.status) {
                            "Ahead", "Achieved" -> Color(0xFF15803D)
                            "On Track" -> Color(0xFF2563EB)
                            else -> Color(0xFFD97706)
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = okr.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = okr.targetDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar & Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { okr.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = Color(android.graphics.Color.parseColor(okr.colorHex)),
                    trackColor = SurfaceContainerHigh
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${okr.progressPercent}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(okr.colorHex))
                    )
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = SurfaceContainerHigh, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Key Results:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                okr.keyResults.forEach { kr ->
                    Text(
                        text = "• $kr",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Increment Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update Progress:",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                    )
                    listOf(25, 50, 75, 100).forEach { p ->
                        OutlinedButton(
                            onClick = { onUpdateProgress(p) },
                            shape = RoundedCornerShape(99.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("$p%", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateOkrDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetDesc by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf(LifePillarType.WORK) }
    var selectedHorizon by remember { mutableStateOf(GoalHorizon.QUARTERLY) }
    var deadline by remember { mutableStateOf("30 September 2026") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier.fillMaxWidth().testTag("create_okr_dialog")
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
                        text = "New Strategic OKR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Objective Title") },
                    placeholder = { Text("e.g. Master React Native & Android Jetpack") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetDesc,
                    onValueChange = { targetDesc = it },
                    label = { Text("Measurable Target / Key Result") },
                    placeholder = { Text("e.g. Complete 5 apps and publish 2 to store") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Pillar Selector
                Column {
                    Text("Pillar Domain", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        LifePillarType.values().forEach { pillar ->
                            FilterChip(
                                selected = selectedPillar == pillar,
                                onClick = { selectedPillar = pillar },
                                label = { Text("${pillar.icon} ${pillar.label.substringBefore(" &")}", fontSize = 10.sp) },
                                shape = RoundedCornerShape(99.dp),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }

                // Horizon Selector
                Column {
                    Text("Horizon Timeline", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        GoalHorizon.values().forEach { horizon ->
                            FilterChip(
                                selected = selectedHorizon == horizon,
                                onClick = { selectedHorizon = horizon },
                                label = { Text(horizon.label, fontSize = 10.sp) },
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
                            if (title.isNotBlank()) {
                                viewModel.addStrategicOkr(
                                    title = title,
                                    pillar = selectedPillar,
                                    horizon = selectedHorizon,
                                    targetDesc = targetDesc.ifBlank { "Achieve core milestone" },
                                    deadline = deadline
                                )
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Create OKR")
                    }
                }
            }
        }
    }
}
