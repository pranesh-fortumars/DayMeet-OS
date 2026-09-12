package com.example.ui.components

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    }
}
