package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.HealthBiometrics
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Health Connect & Telemetry Dashboard:
 * Sleep quality, HRV, resting heart rate, active calories, dynamic readiness score,
 * and sedentary stretch prompt.
 */
@Composable
fun HealthConnectReadinessCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val biometrics by viewModel.biometrics.collectAsState()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("health_connect_readiness_card")
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Health Connect & Telemetry",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "SYNCED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Sleep, HRV & Dynamic Readiness",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "${biometrics.readinessScore}/100",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Grid Telemetry Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tile 1: Sleep
                TelemetryMetricTile(
                    title = "Sleep",
                    value = "${biometrics.sleepDurationHours}h",
                    sub = "${biometrics.sleepQualityScore}% score",
                    icon = Icons.Default.Bedtime,
                    tint = Color(0xFF7C3AED),
                    bgColor = Color(0xFFEDE9FE),
                    modifier = Modifier.weight(1f)
                )

                // Tile 2: HRV
                TelemetryMetricTile(
                    title = "HRV",
                    value = "${biometrics.hrvMilliseconds}ms",
                    sub = "Optimal baseline",
                    icon = Icons.Default.Timeline,
                    tint = Color(0xFF0284C7),
                    bgColor = Color(0xFFE0F2FE),
                    modifier = Modifier.weight(1f)
                )

                // Tile 3: Resting HR
                TelemetryMetricTile(
                    title = "Resting HR",
                    value = "${biometrics.restingHeartRateBpm}",
                    sub = "bpm (low)",
                    icon = Icons.Default.FavoriteBorder,
                    tint = Color(0xFFDC2626),
                    bgColor = Color(0xFFFEE2E2),
                    modifier = Modifier.weight(1f)
                )

                // Tile 4: Steps
                TelemetryMetricTile(
                    title = "Steps",
                    value = "${biometrics.dailySteps}",
                    sub = "/ 10k goal",
                    icon = Icons.Default.DirectionsWalk,
                    tint = Color(0xFF16A34A),
                    bgColor = Color(0xFFDCFCE7),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sedentary Alert Box
            if (biometrics.sedentaryAlertActive) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openSedentaryStretch() }
                        .testTag("sedentary_alert_box")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Sedentary for ${biometrics.lastSedentaryMinutes} minutes",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                )
                                Text(
                                    text = "Tap for 30s eye-rest & shoulder roll routine",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFB45309),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openSedentaryStretch() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Stretch", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryMetricTile(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 9.sp))
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurface))
            Text(text = sub, style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 8.sp))
        }
    }
}
