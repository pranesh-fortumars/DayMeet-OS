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
import com.example.model.LifePillarType
import com.example.model.PillarWeeklySummary
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun WeeklyReviewDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    val weeklyReviews by viewModel.weeklyReviews.collectAsState()
    val pillarSummaries = remember { viewModel.getPillarWeeklySummaries() }

    var win1 by remember { mutableStateOf("Shipped mobile tokens system with zero crash regression") }
    var win2 by remember { mutableStateOf("Maintained 10k daily steps and 2.5L hydration target") }
    var win3 by remember { mutableStateOf("Saved ₹12,000 under monthly discretionary spending cap") }
    var improvement by remember { mutableStateOf("Guard morning focus sanctuary against meeting sprawl.") }
    var next1 by remember { mutableStateOf("Sprint review & Q4 roadmap signoff with stakeholders") }
    var next2 by remember { mutableStateOf("Schedule dental cleaning & car maintenance inspection") }
    var next3 by remember { mutableStateOf("Deep reading session for 'Atomic Habits' notes") }
    var scoreSlider by remember { mutableFloatStateOf(92f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("weekly_review_dialog")
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
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Weekly Retrospective & Review",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Week 38: Sep 15 - Sep 21, 2026",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_weekly_review_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Weekly Performance Score Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Weekly Executive Score",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6D28D9)
                                        )
                                    )
                                    Text(
                                        text = "${scoreSlider.toInt()}/100",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6D28D9)
                                        )
                                    )
                                }

                                Slider(
                                    value = scoreSlider,
                                    onValueChange = { scoreSlider = it },
                                    valueRange = 50f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF7C3AED),
                                        activeTrackColor = Color(0xFF7C3AED)
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Moderate (50)", fontSize = 10.sp, color = OnSurfaceVariant)
                                    Text("Peak Flow (100)", fontSize = 10.sp, color = OnSurfaceVariant)
                                }
                            }
                        }
                    }

                    // 2. Pillars Performance Delta
                    item {
                        Text(
                            text = "5 PILLARS WEEKLY VELOCITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                letterSpacing = 0.6.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            pillarSummaries.forEach { summary ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceContainerLow)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = summary.pillar.icon, fontSize = 16.sp)
                                        Column {
                                            Text(
                                                text = summary.pillar.label,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = OnSurface
                                                )
                                            )
                                            Text(
                                                text = summary.highlight,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "+${summary.deltaFromLastWeek}%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A)
                                            )
                                        )
                                        Text(
                                            text = "${summary.score}%",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = OnSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Top 3 Wins
                    item {
                        Text(
                            text = "🏆 TOP 3 BREAKTHROUGHS & WINS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                letterSpacing = 0.6.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = win1,
                                onValueChange = { win1 = it },
                                label = { Text("Win #1") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = win2,
                                onValueChange = { win2 = it },
                                label = { Text("Win #2") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = win3,
                                onValueChange = { win3 = it },
                                label = { Text("Win #3") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // 4. Critical Friction & Lesson
                    item {
                        Text(
                            text = "🔍 LESSON & SYSTEM FRICTION TO REMOVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                letterSpacing = 0.6.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = improvement,
                            onValueChange = { improvement = it },
                            placeholder = { Text("What drained energy or created friction? How to solve it?") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    // 5. Next Week's 3 Big Outcomes
                    item {
                        Text(
                            text = "🎯 NEXT WEEK'S TOP 3 OUTCOMES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                letterSpacing = 0.6.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = next1,
                                onValueChange = { next1 = it },
                                label = { Text("Outcome #1") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = next2,
                                onValueChange = { next2 = it },
                                label = { Text("Outcome #2") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = next3,
                                onValueChange = { next3 = it },
                                label = { Text("Outcome #3") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Dismiss") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            viewModel.saveWeeklyReview(
                                topWins = listOf(win1, win2, win3),
                                areasToImprove = improvement,
                                nextWeekFocus = listOf(next1, next2, next3),
                                score = scoreSlider.toInt()
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        modifier = Modifier.testTag("save_weekly_review_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Archive to Vault", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
