package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun DayMeetBottomDock(
    currentScreen: String,
    onTabSelected: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val strings = LocalAppStrings.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Quick Add Pill Button above the dock
        FloatingActionButton(
            onClick = onCreateClick,
            shape = RoundedCornerShape(99.dp),
            containerColor = PrimaryContainer,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp),
            modifier = Modifier
                .padding(bottom = 66.dp)
                .height(46.dp)
                .testTag("floating_quick_add_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.navCreate,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = strings.navCreate,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
            }
        }

        // Bottom Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            color = Surface.copy(alpha = 0.98f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockNavItem(
                    label = strings.navHome,
                    icon = Icons.Default.Home,
                    isSelected = currentScreen == "home",
                    onClick = { onTabSelected("home") },
                    testTag = "nav_home"
                )

                DockNavItem(
                    label = strings.navCalendar,
                    icon = Icons.Default.CalendarToday,
                    isSelected = currentScreen == "calendar",
                    onClick = { onTabSelected("calendar") },
                    testTag = "nav_calendar"
                )

                DockNavItem(
                    label = strings.navTasks,
                    icon = Icons.Default.CheckCircle,
                    isSelected = currentScreen == "tasks",
                    badge = "4",
                    onClick = { onTabSelected("tasks") },
                    testTag = "nav_tasks"
                )

                DockNavItem(
                    label = strings.navInsights,
                    icon = Icons.Default.TrendingUp,
                    isSelected = currentScreen == "insights",
                    onClick = { onTabSelected("insights") },
                    testTag = "nav_insights"
                )

                DockNavItem(
                    label = strings.navMore,
                    icon = Icons.Default.GridView,
                    isSelected = currentScreen == "more" || currentScreen !in listOf("home", "calendar", "tasks", "insights"),
                    onClick = { onTabSelected("more") },
                    testTag = "nav_more"
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
            .clickable { onClick() }
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
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Primary else OnSurfaceVariant
            )
        )
    }
}
