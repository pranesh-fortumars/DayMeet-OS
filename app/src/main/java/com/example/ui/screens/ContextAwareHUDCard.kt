package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.example.model.ContextTrigger
import com.example.model.GeofenceTriggerType
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Proactive contextual geofence alert banner that automatically pops up
 * when nearing a location (e.g., supermarket grocery list, arriving home, arriving office).
 */
@Composable
fun ContextAwareHUDCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val activeNotification by viewModel.activeContextNotification.collectAsState()

    AnimatedVisibility(
        visible = activeNotification != null,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = modifier
    ) {
        activeNotification?.let { trigger ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF93C5FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("context_aware_hud_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(Color(0xFF3B82F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = trigger.type.icon, fontSize = 16.sp)
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = trigger.type.label,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(99.dp),
                                        color = Color(0xFFDBEAFE)
                                    ) {
                                        Text(
                                            text = "LIVE CUE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1D4ED8),
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = trigger.locationName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF2563EB),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.dismissActiveContextNotification() },
                            modifier = Modifier.size(28.dp).testTag("dismiss_context_hud_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = trigger.actionPrompt,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggested Actions Pills
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        trigger.suggestedActions.forEach { action ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .clickable {
                                        viewModel.showToast("Checked: '$action'")
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurface,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 1-Tap Trigger Switcher (Demo / Quick Jump)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate Geofence:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(
                                onClick = { viewModel.triggerContextPrompt("trig_work") },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("🏢 Work", fontSize = 10.sp, color = Color(0xFF2563EB))
                            }
                            TextButton(
                                onClick = { viewModel.triggerContextPrompt("trig_home") },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("🏡 Home", fontSize = 10.sp, color = Color(0xFF2563EB))
                            }
                            TextButton(
                                onClick = { viewModel.triggerContextPrompt("trig_store") },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("🛒 Store", fontSize = 10.sp, color = Color(0xFF2563EB))
                            }
                        }
                    }
                }
            }
        }
    }
}
