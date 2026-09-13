package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

        // Global Theme Switcher Card (Command Center Theme Branding)
        item {
            CommandCenterThemeSwitcherCard(viewModel = viewModel)
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

        // 7. System Appearance & Tailwind Theme Configuration
        item {
            SectionHeader(title = "SYSTEM APPEARANCE & TAILWIND THEME")
        }

        item {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_theme_mode_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEDE7F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Theme",
                                    tint = if (isDarkMode) Color(0xFF818CF8) else Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "System-Wide Dark Mode",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isDarkMode)
                                        "Tailwind Slate-900 canvas active • High-contrast OLED"
                                    else
                                        "Tailwind Light canvas active • High-clarity neutral theme",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF6366F1),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = SurfaceContainerHigh
                            ),
                            modifier = Modifier.testTag("dark_mode_toggle").testTag("command_center_dark_mode_toggle")
                        )
                    }

                    // Theme selector pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("light", "☀️ Light", !isDarkMode && themeMode == "light"),
                            Triple("dark", "🌙 Dark (Slate)", isDarkMode),
                            Triple("system", "⚙️ Auto", themeMode == "system")
                        ).forEach { (mode, label, isSelected) ->
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isDarkMode) Color(0xFF334155) else PrimaryFixed,
                                    selectedLabelColor = if (isDarkMode) Color(0xFFF8FAFC) else OnPrimaryFixedVariant,
                                    containerColor = SurfaceContainerHigh,
                                    labelColor = OnSurfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 8. System Notifications & Device Calendar Sync
        item {
            SectionHeader(title = "SYSTEM NOTIFICATIONS & CALENDAR SYNC")
        }

        item {
            val isCalendarSyncEnabled by viewModel.syncDeviceCalendarAlerts.collectAsStateWithLifecycle()

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_calendar_sync_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCalendarSyncEnabled) Color(0xFFE8F5E9) else SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = if (isCalendarSyncEnabled) Color(0xFF2E7D32) else OnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Sync Device Calendar Notifications",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isCalendarSyncEnabled)
                                    "Active: DayMeet alerts mirrored with local system calendar"
                                else
                                    "Disabled: Only internal DayMeet alert engine will notify you",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isCalendarSyncEnabled,
                        onCheckedChange = { viewModel.toggleSyncDeviceCalendarAlerts() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = SurfaceContainerHigh
                        ),
                        modifier = Modifier.testTag("calendar_sync_toggle")
                    )
                }
            }
        }

        // 8. Production Releases & In-App Auto Updates
        item {
            SectionHeader("PRODUCTION RELEASES & AUTO-UPDATES")
        }

        item {
            AppUpdatesCard(viewModel = viewModel)
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

@Composable
private fun AppUpdatesCard(viewModel: DayMeetViewModel) {
    val updateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()
    val isAutoCheckEnabled by viewModel.isAutoCheckUpdateEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_updates_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Cloud Sync",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Google Play In-App Updates",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Play Core API • Production Channel",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                val hasUpdate = updateInfo?.isUpdateAvailable == true
                Text(
                    text = if (hasUpdate) "Update Available" else "Latest Build",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (hasUpdate) Primary else EmeraldSuccess,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (hasUpdate) PrimaryFixed else EmeraldSuccess.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

            // Current Version & Status Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val effectiveVersionName = updateInfo?.currentVersionName ?: com.example.util.AppUpdateManager.getEffectiveVersionName(context)
                val effectiveVersionCode = updateInfo?.currentVersionCode ?: com.example.util.AppUpdateManager.getEffectiveVersionCode(context)

                Column {
                    Text(
                        text = "Installed Version",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "DayMeet v$effectiveVersionName (Build $effectiveVersionCode)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )
                }

                if (updateInfo?.isUpdateAvailable == true) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Play Store Release",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "v${updateInfo?.latestVersionName} (Build ${updateInfo?.latestVersionCode})",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Up to Date",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Automatic Check Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHigh.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Check on Startup",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Automatically checks Google Play when new releases are published to production",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
                Switch(
                    checked = isAutoCheckEnabled,
                    onCheckedChange = { viewModel.toggleAutoCheckUpdates() },
                    modifier = Modifier.testTag("toggle_auto_update")
                )
            }

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.checkForAppUpdates(context = context, manual = true) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("check_updates_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Check Play", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = { viewModel.openUpdateDialog(context = context) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_update_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (updateInfo?.isUpdateAvailable == true) "Update Now" else "Update Details",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommandCenterThemeSwitcherCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(if (isAppInDarkMode) "dark" else "light") }
    var isDark by remember { mutableStateOf(isAppInDarkMode) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("command_center_theme_switcher")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Global System Theme",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = if (isDark) "Dark Mode" else "Light Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryFixed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Synchronizes deep Slate-900 OLED dark branding across all 33 modules",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Switch(
                    checked = isDark,
                    onCheckedChange = { checked ->
                        isDark = checked
                        isAppInDarkMode = checked
                        activeMode = if (checked) "dark" else "light"
                        viewModel.showToast(if (checked) "🌙 Dark Mode (Slate-900 OLED) activated across modules" else "☀️ Light Mode activated")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.testTag("command_center_theme_toggle")
                )
            }

            // 3 Mode Selection Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("light", "Light", Icons.Default.LightMode),
                    Triple("dark", "Dark Slate", Icons.Default.DarkMode),
                    Triple("system", "Auto (OS)", Icons.Default.BrightnessAuto)
                ).forEach { (modeId, label, icon) ->
                    val isSelected = activeMode == modeId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SurfaceContainerLow else SurfaceContainerLowest,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Primary else SurfaceContainerHigh
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMode = modeId
                                val newDark = when (modeId) {
                                    "dark" -> true
                                    "light" -> false
                                    else -> isAppInDarkMode
                                }
                                isDark = newDark
                                isAppInDarkMode = newDark
                                viewModel.showToast("Theme switched to $label")
                            }
                            .testTag("theme_mode_btn_$modeId")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Primary else OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Primary else OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Info note
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLow,
                border = BorderStroke(1.dp, SurfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Adaptive contrast system: Slate-900 background, Slate-800 elevated surfaces, and high-legibility typography.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
