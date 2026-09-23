package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Smart Audio Memo & Action Item Extraction Dialog:
 * Real-time audio recording simulation with automated transcript and 1-tap task creation.
 */
@Composable
fun AudioMemoDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf("Sync Action Items Takeaway") }
    var transcript by remember {
        mutableStateOf("Alex and Devon approved the database encryption specs. Need to finalize the grocery list proximity alert and trigger the weekly retrospective test.")
    }
    var action1 by remember { mutableStateOf("Finalize grocery list proximity alert") }
    var action2 by remember { mutableStateOf("Trigger weekly retrospective test on pipeline") }

    // Pulsing audio animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                recordingSeconds++
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("audio_memo_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Audio Memo & Task Extraction",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Automated voice transcription to tasks",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Recording Visualizer Bar
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRecording) Color(0xFFFEF2F2) else SurfaceContainerLow
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(if (isRecording) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(if (isRecording) Color(0xFFEF4444) else OutlineVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (isRecording) "Recording Voice Note..." else "Ready to Dictate",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRecording) Color(0xFFDC2626) else OnSurface
                                    )
                                )
                                Text(
                                    text = "00:${String.format("%02d", recordingSeconds)} duration",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }

                        Button(
                            onClick = { isRecording = !isRecording },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) Color(0xFFEF4444) else Primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isRecording) "Finish" else "Record")
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Memo Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = transcript,
                    onValueChange = { transcript = it },
                    label = { Text("Transcribed Spoken Text") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Extracted Actions
                Text(
                    text = "⚡ AUTO-EXTRACTED ACTION ITEMS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = action1,
                    onValueChange = { action1 = it },
                    label = { Text("Task 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = action2,
                    onValueChange = { action2 = it },
                    label = { Text("Task 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.saveAudioMemo(
                                title = title,
                                simulatedSpokenText = transcript,
                                extractedItems = listOf(action1, action2)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.testTag("save_audio_memo_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Create Tasks")
                    }
                }
            }
        }
    }
}
