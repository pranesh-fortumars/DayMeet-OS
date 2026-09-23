package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.model.CognitiveLoadAnalytics
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Focus vs Meeting Ratio & Context Switching Interruption Predictor
 */
@Composable
fun CognitiveLoadAnalysisCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.cognitiveAnalytics.collectAsState()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("cognitive_load_analysis_card")
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
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cognitive Load & Focus Ratio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Interruption & Context Switching Predictor",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (analytics.fragmentedGapsCount == 0) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = analytics.interruptionRiskLevel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (analytics.fragmentedGapsCount == 0) Color(0xFF15803D) else Color(0xFFB45309),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Focus vs Meeting Ratio Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2563EB)))
                    Text("Focus (${analytics.focusHours}h)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnSurface))
                }
                Text(
                    text = "${analytics.focusMeetingRatioPercent}% Focus Ratio",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Primary)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)))
                    Text("Meetings (${analytics.meetingHours}h)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dual Progress bar
            LinearProgressIndicator(
                progress = { analytics.focusMeetingRatioPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF2563EB),
                trackColor = Color(0xFFDDD6FE)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Recommendation Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = analytics.recommendation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurface,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (analytics.fragmentedGapsCount > 0) {
                        Button(
                            onClick = { viewModel.optimizeFragmentedGaps() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Consolidate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
