package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun MoreScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Command Center",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )

                    IconButton(
                        onClick = { viewModel.openSearch() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                            .testTag("more_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "All modules, connected automations, finance, health, and personal tools",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
            }
        }

        // 2. Intelligence & Core Super-App Engines
        item {
            SectionHeader(title = "CORE INTELLIGENCE & ENGINES")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    ModuleListRow(
                        title = "Automations & Rules Engine",
                        subtitle = "When → If → Then automated daily workflows",
                        badge = "5 Active",
                        badgeColor = Color(0xFF2E7D32),
                        icon = Icons.Default.Bolt,
                        iconTint = Primary,
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openSubScreen("automations") },
                        testTag = "module_automations"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "DayMeet AI Copilot",
                        subtitle = "Natural language schedule restructuring & insights",
                        badge = "Gemini",
                        badgeColor = Primary,
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Primary,
                        iconBg = PrimaryFixed,
                        onClick = { viewModel.openAiAssistant() },
                        testTag = "module_ai"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Morning & Evening Briefings",
                        subtitle = "High-level summary of day's commitments & vitals",
                        badge = "Scheduled",
                        badgeColor = Tertiary,
                        icon = Icons.Default.WbTwilight,
                        iconTint = Tertiary,
                        iconBg = TertiaryFixed,
                        onClick = { viewModel.openDailyBriefing() },
                        testTag = "module_briefing"
                    )
                }
            }
        }

        // 3. Productivity & Workspaces
        item {
            SectionHeader(title = "PRODUCTIVITY & WORKSPACES")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    ModuleListRow(
                        title = "Meetings & Video Conferences",
                        subtitle = "Sync Google Meet, Zoom, MS Teams & attendees",
                        badge = "3 Today",
                        badgeColor = Color(0xFF673AB7),
                        icon = Icons.Default.Videocam,
                        iconTint = Color(0xFF673AB7),
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.navigateTo("meetings") },
                        testTag = "module_meetings"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Meeting Minutes & Action Items",
                        subtitle = "Convert speaker takeaways into assigned tasks",
                        badge = "Live Sync",
                        badgeColor = Primary,
                        icon = Icons.Default.Summarize,
                        iconTint = Primary,
                        iconBg = Color(0xFFE8EAF6),
                        onClick = { viewModel.openMeetingMinutes() },
                        testTag = "module_minutes"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Deep Focus & Pomodoro Sanctuary",
                        subtitle = "DND mode with ambient sounds & auto-responder",
                        badge = "Active",
                        badgeColor = Primary,
                        icon = Icons.Default.FilterCenterFocus,
                        iconTint = Primary,
                        iconBg = PrimaryFixed,
                        onClick = { viewModel.navigateTo("tasks") },
                        testTag = "module_focus"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Notes, Docs & Knowledge Base",
                        subtitle = "Quick capture, voice notes & tags",
                        badge = "3 Notes",
                        badgeColor = SkyBlue,
                        icon = Icons.Default.EditNote,
                        iconTint = SkyBlue,
                        iconBg = SkyLight,
                        onClick = { viewModel.openSubScreen("notes") },
                        testTag = "module_notes"
                    )
                }
            }
        }

        // 4. Personal Growth & Habits
        item {
            SectionHeader(title = "HABITS, GOALS & WELLNESS")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    ModuleListRow(
                        title = "Habits & Routine Streaks",
                        subtitle = "5 habits tracked • 18-day morning streak 🔥",
                        badge = "4/5 Done",
                        badgeColor = AmberWarning,
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = AmberWarning,
                        iconBg = Color(0xFFFFF3E0),
                        onClick = { viewModel.openSubScreen("habits") },
                        testTag = "module_habits"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Goals & Milestones",
                        subtitle = "Financial, fitness & career target tracking",
                        badge = "3 Targets",
                        badgeColor = Tertiary,
                        icon = Icons.Default.Flag,
                        iconTint = Tertiary,
                        iconBg = TertiaryFixed,
                        onClick = { viewModel.openSubScreen("goals") },
                        testTag = "module_goals"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Smart Reminders & Location Alerts",
                        subtitle = "Location, time & cross-module triggers",
                        badge = "4 Pending",
                        badgeColor = Primary,
                        icon = Icons.Default.NotificationsActive,
                        iconTint = Primary,
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openSubScreen("reminders") },
                        testTag = "module_reminders"
                    )
                }
            }
        }

        // 5. Finance & Commerce
        item {
            SectionHeader(title = "FINANCE, BILLS & COMMERCE")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    ModuleListRow(
                        title = "Finance Dashboard & Ledger",
                        subtitle = "Daily spending, monthly budget & auto-categorization",
                        badge = "₹3,450 Today",
                        badgeColor = Color(0xFF2E7D32),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = Color(0xFF2E7D32),
                        iconBg = Color(0xFFE8F5E9),
                        onClick = { viewModel.navigateTo("finance") },
                        testTag = "module_finance"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Subscriptions & Recurring Bills",
                        subtitle = "Electricity, Fiber, Netflix & Figma tracker",
                        badge = "₹3,228/mo",
                        badgeColor = Color(0xFFE65100),
                        icon = Icons.Default.CreditCard,
                        iconTint = Color(0xFFE65100),
                        iconBg = Color(0xFFFFF3E0),
                        onClick = { viewModel.openSubScreen("subscriptions") },
                        testTag = "module_subscriptions"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Shopping & Grocery Lists",
                        subtitle = "Auto-budget estimation & aisle checkboxes",
                        badge = "4 Items",
                        badgeColor = Primary,
                        icon = Icons.Default.ShoppingCart,
                        iconTint = Primary,
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openSubScreen("shopping") },
                        testTag = "module_shopping"
                    )
                }
            }
        }

        // 6. Travel, Documents & Communication
        item {
            SectionHeader(title = "TRAVEL, SECURITY & COMMUNICATION")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    ModuleListRow(
                        title = "Travel Itinerary & Trips",
                        subtitle = "Chennai Flight 6E 412 • Taj Coromandel in 3 days",
                        badge = "In 3 Days",
                        badgeColor = Color(0xFF3949AB),
                        icon = Icons.Default.Flight,
                        iconTint = Color(0xFF3949AB),
                        iconBg = Color(0xFFE8EAF6),
                        onClick = { viewModel.openSubScreen("travel") },
                        testTag = "module_travel"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Secure Document Vault",
                        subtitle = "Passport, insurance, lease with on-device encryption",
                        badge = "Encrypted",
                        badgeColor = Color(0xFF00897B),
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF00897B),
                        iconBg = Color(0xFFE0F2F1),
                        onClick = { viewModel.openSubScreen("documents") },
                        testTag = "module_documents"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Contacts & Scheduled Messages",
                        subtitle = "CRM directory, birthday concierge & automated Slack/WhatsApp",
                        badge = "2 Scheduled",
                        badgeColor = Primary,
                        icon = Icons.Default.People,
                        iconTint = Primary,
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openSubScreen("contacts") },
                        testTag = "module_contacts"
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp
        ),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun ModuleListRow(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    fontSize = 10.sp
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
