package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun DayMeetHeader(
    isSyncing: Boolean = false,
    lastSyncedText: String = "Just now",
    onSyncClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAiClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface.copy(alpha = 0.95f),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.clickable { onAiClick() }
                ) {
                    // Custom DayMeet Icon representation
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "DayMeet Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "DayMeet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }

                // Action Icons & Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Manual Sync Refresh Button with continuous rotation when syncing
                    val infiniteTransition = rememberInfiniteTransition(label = "header_sync_spin")
                    val spinAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 900, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "sync_spin_angle"
                    )

                    IconButton(
                        onClick = onSyncClick,
                        enabled = !isSyncing,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("manual_sync_button")
                            .testTag("header_sync_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSyncing) Primary.copy(alpha = 0.12f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = if (isSyncing) "Syncing dashboard..." else "Manual sync refresh",
                                tint = if (isSyncing) Primary else OnSurfaceVariant,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isSyncing) spinAngle else 0f)
                            )
                        }
                    }

                    // DayMeet AI Sparkle Button
                    IconButton(
                        onClick = onAiClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("ai_assistant_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "DayMeet AI Copilot",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Search Button
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("quick_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Quick Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Notifications Button with badge
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("notifications_button")
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                                    .border(1.5.dp, Surface, CircleShape)
                            )
                        }
                    }

                    // User Profile Avatar
                    Box(
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .border(1.5.dp, Primary.copy(alpha = 0.5f), CircleShape)
                            .clickable { onProfileClick() }
                            .testTag("user_profile_avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainer,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .testTag("header_sync_progress_bar"),
                    color = Primary,
                    trackColor = Primary.copy(alpha = 0.15f)
                )
            }
        }
    }
}
