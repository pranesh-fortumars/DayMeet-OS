package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DayMeetBottomDock(
    currentScreen: String,
    onTabSelected: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Create Button above the dock
        FloatingActionButton(
            onClick = onCreateClick,
            shape = CircleShape,
            containerColor = PrimaryContainer,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp),
            modifier = Modifier
                .padding(bottom = 68.dp)
                .height(48.dp)
                .testTag("floating_create_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Item",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }

        // Bottom Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = Surface.copy(alpha = 0.96f),
            shadowElevation = 8.dp,
            border = null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockNavItem(
                    label = "Home",
                    icon = Icons.Default.Dashboard,
                    isSelected = currentScreen == "home",
                    onClick = { onTabSelected("home") },
                    testTag = "nav_home"
                )

                DockNavItem(
                    label = "Calendar",
                    icon = Icons.Default.CalendarMonth,
                    isSelected = currentScreen == "calendar",
                    onClick = { onTabSelected("calendar") },
                    testTag = "nav_calendar"
                )

                DockNavItem(
                    label = "Tasks",
                    icon = Icons.Default.CheckCircle,
                    isSelected = currentScreen == "tasks",
                    badge = "4",
                    onClick = { onTabSelected("tasks") },
                    testTag = "nav_tasks"
                )

                DockNavItem(
                    label = "Meetings",
                    icon = Icons.Default.VideoCameraFront,
                    isSelected = currentScreen == "meetings",
                    onClick = { onTabSelected("meetings") },
                    testTag = "nav_meetings"
                )

                DockNavItem(
                    label = "Finance",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentScreen == "finance",
                    onClick = { onTabSelected("finance") },
                    testTag = "nav_finance"
                )
            }
        }
    }
}

@Composable
private fun DockNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .defaultMinSize(minWidth = 56.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Primary else OnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-8).dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Primary else OnSurfaceVariant
            )
        )
    }
}
