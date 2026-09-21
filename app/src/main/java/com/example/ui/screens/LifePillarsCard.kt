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
import com.example.model.LifePillar
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun LifePillarsCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val pillars = remember { viewModel.getLifePillars() }
    val averageScore = remember(pillars) {
        if (pillars.isNotEmpty()) pillars.map { it.score }.average().toInt() else 80
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("life_pillars_balance_card")
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
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Life Balance & Pillars",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "5 Core Areas of Life Operating System",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Balance Score Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFFEDE9FE))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$averageScore%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D28D9)
                        )
                    )
                    Text(
                        text = "Balance",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF6D28D9),
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Pillars List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pillars.forEach { pillar ->
                    PillarRowItem(
                        pillar = pillar,
                        onClick = {
                            when (pillar.targetModule) {
                                "tasks" -> viewModel.navigateTo("tasks")
                                "finance" -> viewModel.navigateTo("finance")
                                "habits" -> viewModel.openSubScreen("habits")
                                "home_vehicle" -> viewModel.openSubScreen("home_vehicle")
                                else -> viewModel.navigateTo("home")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Evening Wind-down quick trigger
            Button(
                onClick = { viewModel.openEveningWindDown() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("evening_wind_down_launcher_btn")
            ) {
                Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Evening Wind-Down & Retrospective 🌙",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun PillarRowItem(
    pillar: LifePillar,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = pillar.type.icon, fontSize = 18.sp)

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pillar.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                Text(
                    text = "${pillar.score}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(pillar.type.defaultColor))
                    )
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            LinearProgressIndicator(
                progress = { pillar.score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = Color(android.graphics.Color.parseColor(pillar.type.defaultColor)),
                trackColor = SurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${pillar.activeInitiative} • ${pillar.statusText}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OutlineVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
