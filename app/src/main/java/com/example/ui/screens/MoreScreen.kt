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
import com.example.localization.AppLanguage
import com.example.localization.LocalAppLanguage
import com.example.localization.LocalAppStrings
import com.example.localization.LocalizationManager
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun MoreScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val lifeInboxItems by viewModel.lifeInboxItems.collectAsStateWithLifecycle()

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
                        text = strings.commandCenterTitle,
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
                    text = strings.commandCenterSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
            }
        }

        // Dedicated Multilingual Selector Card (13 Languages)
        item {
            CommandCenterLanguageCard(viewModel = viewModel)
        }

        // Global Theme Switcher Card (Command Center Theme Branding)
        item {
            CommandCenterThemeSwitcherCard(viewModel = viewModel)
        }

        // 2. Intelligence & Core Super-App Engines
        item {
            SectionHeader(title = strings.coreIntelligenceHeader)
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
                        title = strings.automationsTitle,
                        subtitle = strings.automationsSubtitle,
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
                        title = strings.aiCopilotTitle,
                        subtitle = strings.aiCopilotSubtitle,
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
                        title = strings.dailyBriefingTitle,
                        subtitle = strings.dailyBriefingSubtitle,
                        badge = "Scheduled",
                        badgeColor = Tertiary,
                        icon = Icons.Default.WbTwilight,
                        iconTint = Tertiary,
                        iconBg = TertiaryFixed,
                        onClick = { viewModel.openDailyBriefing() },
                        testTag = "module_briefing"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Life Inbox",
                        subtitle = "Universal capture: Text, voice, photos, links & ideas",
                        badge = if (lifeInboxItems.isNotEmpty()) "${lifeInboxItems.size} Unfiled" else "Inbox Clear",
                        badgeColor = if (lifeInboxItems.isNotEmpty()) AmberWarning else EmeraldSuccess,
                        icon = Icons.Default.Inbox,
                        iconTint = if (lifeInboxItems.isNotEmpty()) AmberWarning else Primary,
                        iconBg = if (lifeInboxItems.isNotEmpty()) Color(0xFFFFF3E0) else PrimaryFixed,
                        onClick = { viewModel.openSubScreen("life_inbox") },
                        testTag = "module_life_inbox"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Morning Kickoff",
                        subtitle = "Daily capacity budget, energy gauge & top 3 highlights",
                        badge = "Plan Day",
                        badgeColor = Color(0xFFD97706),
                        icon = Icons.Default.WbSunny,
                        iconTint = Color(0xFFD97706),
                        iconBg = Color(0xFFFEF3C7),
                        onClick = { viewModel.openMorningKickoff() },
                        testTag = "module_morning_kickoff"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Evening Wind-Down",
                        subtitle = "Day retrospective, guilt-free rollover & gratitude journal",
                        badge = "Reflect",
                        badgeColor = Color(0xFF7C3AED),
                        icon = Icons.Default.Bedtime,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFEDE9FE),
                        onClick = { viewModel.openEveningWindDown() },
                        testTag = "module_evening_wind_down"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Weekly Review & Retrospective",
                        subtitle = "Week 38 score, wins, lessons & 5 pillars delta",
                        badge = "Sunday Ritual",
                        badgeColor = Color(0xFF6D28D9),
                        icon = Icons.Default.Assessment,
                        iconTint = Color(0xFF6D28D9),
                        iconBg = Color(0xFFEDE9FE),
                        onClick = { viewModel.openWeeklyReview() },
                        testTag = "module_weekly_review"
                    )
                }
            }
        }

        // 3. Productivity & Workspaces
        item {
            SectionHeader(title = strings.productivityWorkspacesHeader)
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
                        title = "Meeting Pre-Briefing",
                        subtitle = "15m automated context: Attendees, past decisions & action items",
                        badge = "15m Prep",
                        badgeColor = Color(0xFF673AB7),
                        icon = Icons.Default.Timer,
                        iconTint = Color(0xFF673AB7),
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openPreMeetingBrief() },
                        testTag = "module_pre_meeting_brief"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Smart Audio Voice Memo",
                        subtitle = "Voice transcription with automatic action item extraction to Tasks",
                        badge = "Voice AI",
                        badgeColor = Color(0xFFDC2626),
                        icon = Icons.Default.Mic,
                        iconTint = Color(0xFFDC2626),
                        iconBg = Color(0xFFFEE2E2),
                        onClick = { viewModel.openAudioMemo() },
                        testTag = "module_audio_memo"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = strings.meetingsTitle,
                        subtitle = strings.meetingsSubtitle,
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
                        title = strings.meetingMinutesTitle,
                        subtitle = strings.meetingMinutesSubtitle,
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
                        title = strings.pomodoroSanctuaryTitle,
                        subtitle = strings.pomodoroSanctuarySubtitle,
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
                        title = strings.notesTitle,
                        subtitle = strings.notesSubtitle,
                        badge = "3 Notes",
                        badgeColor = SkyBlue,
                        icon = Icons.Default.EditNote,
                        iconTint = SkyBlue,
                        iconBg = SkyLight,
                        onClick = { viewModel.openSubScreen("notes") },
                        testTag = "module_notes"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Projects & Initiatives",
                        subtitle = "Milestones, deliverables, task boards & budgets",
                        badge = "2 Active",
                        badgeColor = Primary,
                        icon = Icons.Default.Folder,
                        iconTint = Primary,
                        iconBg = PrimaryFixed,
                        onClick = { viewModel.openSubScreen("projects") },
                        testTag = "module_projects"
                    )
                }
            }
        }

        // 4. Personal Growth & Habits
        item {
            SectionHeader(title = strings.habitsGoalsWellnessHeader)
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
                        title = strings.habitsTitle,
                        subtitle = strings.habitsSubtitle,
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
                        title = strings.goalsTitle,
                        subtitle = strings.goalsSubtitle,
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
                        title = strings.remindersTitle,
                        subtitle = strings.remindersSubtitle,
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
            SectionHeader(title = strings.financeBillsCommerceHeader)
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
                        title = strings.financeDashboardTitle,
                        subtitle = strings.financeDashboardSubtitle,
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
                        title = strings.subscriptionsTitle,
                        subtitle = strings.subscriptionsSubtitle,
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
                        title = strings.shoppingListsTitle,
                        subtitle = strings.shoppingListsSubtitle,
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
            SectionHeader(title = strings.travelSecurityHeader)
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
                        title = strings.travelItineraryTitle,
                        subtitle = strings.travelItinerarySubtitle,
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
                        title = strings.secureVaultTitle,
                        subtitle = strings.secureVaultSubtitle,
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
                        title = strings.contactsTitle,
                        subtitle = strings.contactsSubtitle,
                        badge = "2 Scheduled",
                        badgeColor = Primary,
                        icon = Icons.Default.People,
                        iconTint = Primary,
                        iconBg = Color(0xFFEDE7F6),
                        onClick = { viewModel.openSubScreen("contacts") },
                        testTag = "module_contacts"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Appointments & Bookings",
                        subtitle = "Doctor, dentist, salon, services & clinic visits",
                        badge = "3 Upcoming",
                        badgeColor = Color(0xFF00897B),
                        icon = Icons.Default.EventAvailable,
                        iconTint = Color(0xFF00897B),
                        iconBg = Color(0xFFE0F2F1),
                        onClick = { viewModel.openSubScreen("appointments") },
                        testTag = "module_appointments"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Home & Vehicle Care",
                        subtitle = "Maintenance schedules, vehicle service, PUC & renewals",
                        badge = "1 Overdue",
                        badgeColor = AmberWarning,
                        icon = Icons.Default.Build,
                        iconTint = AmberWarning,
                        iconBg = Color(0xFFFFF3E0),
                        onClick = { viewModel.openSubScreen("home_vehicle") },
                        testTag = "module_home_vehicle"
                    )
                }
            }
        }

        // 7. System Appearance & Tailwind Theme Configuration
        item {
            SectionHeader(title = strings.themeSectionTitle)
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

        // Phase 5: Android Home Widgets & System HUD Controls
        item {
            SectionHeader("ANDROID INTEGRATIONS & SYSTEM HUD")
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
                        title = "Home Screen Widgets & Tile HUD",
                        subtitle = "Live RemoteViews (4x2, 2x2), Quick Settings tile & Ongoing HUD",
                        badge = "Android 14",
                        badgeColor = Color(0xFF4F46E5),
                        icon = Icons.Default.Widgets,
                        iconTint = Color(0xFF4F46E5),
                        iconBg = Color(0xFFE0E7FF),
                        onClick = { viewModel.openWidgetPreview() },
                        testTag = "module_widgets_hud"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Sedentary Micro-Stretch Routine",
                        subtitle = "30-second optic nerve & shoulder recharge for desk focus",
                        badge = "Recovery",
                        badgeColor = Color(0xFFD97706),
                        icon = Icons.Default.SelfImprovement,
                        iconTint = Color(0xFFD97706),
                        iconBg = Color(0xFFFEF3C7),
                        onClick = { viewModel.openSedentaryStretch() },
                        testTag = "module_sedentary_stretch"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "Autonomous Delegation Hub",
                        subtitle = "Cross-functional ownership & automated check-in pings",
                        badge = "Ownership",
                        badgeColor = Color(0xFF7C3AED),
                        icon = Icons.Default.Groups,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFEDE9FE),
                        onClick = { viewModel.openDelegationHub() },
                        testTag = "module_delegation_hub"
                    )

                    HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

                    ModuleListRow(
                        title = "E2EE Ecosystem Vault",
                        subtitle = "Zero-knowledge AES-256-GCM hardware keystore snapshot sync",
                        badge = "Encrypted",
                        badgeColor = Color(0xFF16A34A),
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF16A34A),
                        iconBg = Color(0xFFDCFCE7),
                        onClick = { viewModel.openEcosystemVault() },
                        testTag = "module_ecosystem_vault"
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

        // 9. System Resiliency, Security & Diagnostics (Master UX Flow)
        item {
            SectionHeader("SYSTEM RESILIENCY, SECURITY & UX FLOWS")
        }

        item {
            SystemDiagnosticsAndSecurityCard(viewModel = viewModel)
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
private fun SystemDiagnosticsAndSecurityCard(viewModel: DayMeetViewModel) {
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val hasSyncIssue by viewModel.hasSyncIssue.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedChangesCount.collectAsStateWithLifecycle()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("system_diagnostics_security_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "System Resiliency & UX Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Real-time state transitions, offline cache & security flows",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)

            // 1. Offline Mode Simulation Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHigh.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Simulate Offline Mode",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                        )
                        if (isOffline) {
                            Text(
                                text = "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isOffline) "All mutations queued locally ($unsyncedCount pending). Tap to reconnect." else "Device online. Operations instantly synced to cloud.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
                Switch(
                    checked = isOffline,
                    onCheckedChange = { viewModel.toggleOfflineMode() },
                    modifier = Modifier.testTag("toggle_offline_mode")
                )
            }

            // 2. Action buttons grid for testing master flows
            Text(
                text = "INTERACTIVE SYSTEM FLOWS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
            )

            // Flow Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Row 1: App Launch & Permissions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetLaunchSequence() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_splash_launch"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Replay Splash", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.openPermissionRequest("notifications") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_permissions"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Permission Flow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Row 2: Errors & Conflicts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openNetworkError() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_network_error"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Network Error", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.simulateConflict() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_schedule_conflict"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Schedule Conflict", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Row 3: Vault Lock & Session Expire
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.lockVault() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_lock_vault"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Biometric Lock", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.simulateSessionExpiry() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_test_session_expire"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Expire Session", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Row 4: Connected Apps & Data Backup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.openIntegrationCenter() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_integrations"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Integrations", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.openImportExport(isExport = true) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_import_export"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Data Backup", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Row 5: Feedback & Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openFeedback() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_feedback"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Give Feedback", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.openLogoutDialog() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_trigger_logout"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out Flow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
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

@Composable
fun CommandCenterLanguageCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("command_center_language_card")
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strings.languageSectionTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = strings.languageCardSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Primary.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { viewModel.openLanguageSelector() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currentLanguage.flagEmoji,
                            fontSize = 14.sp
                        )
                        Text(
                            text = currentLanguage.nativeName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }
                }
            }

            // Quick language selector pills
            val quickLanguages = remember {
                listOf(
                    AppLanguage.ENGLISH,
                    AppLanguage.SPANISH,
                    AppLanguage.HINDI,
                    AppLanguage.FRENCH,
                    AppLanguage.GERMAN,
                    AppLanguage.CHINESE,
                    AppLanguage.JAPANESE,
                    AppLanguage.ARABIC
                )
            }

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickLanguages.size) { index ->
                    val lang = quickLanguages[index]
                    val isSelected = lang == currentLanguage
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Primary else SurfaceContainerHigh,
                        border = if (isSelected) null else BorderStroke(1.dp, SurfaceContainerHighest),
                        modifier = Modifier
                            .clickable {
                                viewModel.setLanguage(lang)
                            }
                            .testTag("quick_lang_${lang.code}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(lang.flagEmoji, fontSize = 14.sp)
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else OnSurface
                                )
                            )
                        }
                    }
                }
            }

            // All 13 languages button
            OutlinedButton(
                onClick = { viewModel.openLanguageSelector() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_languages_selector_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "View All 13 Supported Languages",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }
    }
}
