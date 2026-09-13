package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import android.view.SoundEffectConstants
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayMeetRepository
import com.example.model.CrossStreamItem
import com.example.model.HabitItem
import com.example.model.NonRoutineTask
import com.example.ui.theme.*
import com.example.util.TimeUtils
import com.example.viewmodel.DayMeetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val crossStreamItems by viewModel.crossStreamItems.collectAsState()
    val healthMetrics by viewModel.healthMetrics.collectAsState()
    val upcomingBills by viewModel.upcomingBills.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val nonRoutineTasks by viewModel.nonRoutineTasks.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncedTime by viewModel.lastSyncedTime.collectAsState()
    val syncPulseKey by viewModel.syncPulseKey.collectAsState()

    val electricityBill = upcomingBills.firstOrNull { it.id == "b1" }
    val nextMeeting = meetings.firstOrNull()

    val todaySpend = remember(transactions) {
        transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    }
    val dailyLimit = 5000.0
    val spendProgress = remember(todaySpend) { (todaySpend / dailyLimit).toFloat().coerceIn(0f, 1f) }
    val spendStatus = remember(todaySpend) { if (todaySpend <= dailyLimit) "Under Budget" else "Over Budget" }

    val completedTasks = remember(feedItems) { feedItems.count { it.isCompleted } }
    val totalTasks = feedItems.size
    val taskProgress = remember(completedTasks, totalTasks) { if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f }

    val completedHabits = remember(habits) { habits.count { it.isCompletedToday } }
    val totalHabits = habits.size
    val habitProgress = remember(completedHabits, totalHabits) { if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0f }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Manual Dashboard Sync Refresh Banner
        item {
            AnimatedVisibility(
                visible = isSyncing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Primary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("dashboard_sync_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Primary
                        )
                        Column {
                            Text(
                                text = "Synchronizing All Widgets...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "Refreshing live Calendar, Tasks, Finance & Bio-Sync streams",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 1. Good Morning Greeting & Daily Conditions
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Good Morning, Alex 👋",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = OnSurface
                        )
                    )

                    // High Focus pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(TertiaryFixed)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = OnTertiaryFixed,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "High Focus",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnTertiaryFixed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Weather, date conditions, and live sync status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Thursday, Oct 24",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                    )
                    Text(text = "•", color = OutlineVariant)
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = AmberWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "72°F Sunny",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(text = "•", color = OutlineVariant)
                    // Live Sync status indicator chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clickable { viewModel.triggerManualSync() }
                            .testTag("dashboard_sync_status_badge")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isSyncing) AmberWarning else EmeraldSuccess)
                        )
                        Text(
                            text = if (isSyncing) "Syncing..." else "Synced $lastSyncedTime",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSyncing) Primary else OnSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. Daily Briefing Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openDailyBriefing() }
                    .testTag("daily_briefing_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Daily Briefing",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        Text(
                            text = "Auto-Synced",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(PrimaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BriefingMetricItem(number = "${meetings.size}", label = "Meetings", modifier = Modifier.weight(1f))
                        BriefingMetricItem(number = "$totalTasks", label = "Tasks", modifier = Modifier.weight(1f))
                        BriefingMetricItem(number = "₹${String.format("%.0f", todaySpend)}", label = "Spent", modifier = Modifier.weight(1f))
                        BriefingMetricItem(
                            number = "${healthMetrics.steps / 1000}.${(healthMetrics.steps % 1000) / 100}k",
                            label = "Steps",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar & Start My Day button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).padding(end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { taskProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = Primary,
                                trackColor = SurfaceContainerHigh
                            )
                            Text(
                                text = "$completedTasks/$totalTasks Done",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.openDailyBriefing() },
                            shape = RoundedCornerShape(99.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("start_my_day_btn")
                        ) {
                            Text(
                                text = "Start My Day →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. My Day Widgets Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "My Day Widgets",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Live",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TertiaryFixed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .clickable { viewModel.showToast("Widget customization: Reorder & toggles") }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Customize & Reorder",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // 4. Hero Next Meeting Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openMeetingMinutes() }
                    .testTag("hero_meeting_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "In 20m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFD32F2F),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = "09:30 AM - 10:15 AM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = OnSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Google Meet",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = nextMeeting?.title ?: "Product Strategy Review",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 17.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Attendees & Join Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Overlapping Avatars
                        val sampleAttendees = remember {
                            listOf(
                                Triple("AC", PrimaryContainer, OnPrimaryContainer),
                                Triple("ML", SecondaryContainer, OnSecondaryContainer),
                                Triple("DK", TertiaryContainer, OnTertiaryContainer)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            sampleAttendees.forEachIndexed { index, (initials, bgColor, textColor) ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (-index * 8).dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(bgColor)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .offset(x = (-24).dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+4",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariant
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.showToast("Connecting to Google Meet room...") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(38.dp).testTag("join_meeting_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Join Meeting",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // 5. Electricity Bill Due Banner
        if (electricityBill != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5FD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().testTag("bill_alert_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(SkyLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = SkyBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Electricity Bill Due Tomorrow",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                )
                                Text(
                                    text = "Tata Power • ₹2,400",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "View",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                ),
                                modifier = Modifier.clickable { viewModel.navigateTo("finance") }
                            )

                            Button(
                                onClick = { viewModel.payBill("b1") },
                                shape = RoundedCornerShape(99.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OnSurface),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("pay_bill_btn")
                            ) {
                                Text(
                                    text = "Pay Now",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Daily Vitals (4 Streams) 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Vitals",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "4 Streams",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                    )
                }

                // Row 1: Focus & Work + Finance (Daily)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Focus & Work Card
                    VitalsBentoCard(
                        icon = Icons.Default.FilterCenterFocus,
                        badge = "${(taskProgress * 100).toInt()}%",
                        badgeColor = Primary,
                        title = "Focus & Work",
                        mainValue = "$completedTasks/$totalTasks Tasks",
                        progress = taskProgress,
                        progressColor = Primary,
                        subtext = "${meetings.size} meetings scheduled",
                        modifier = Modifier.weight(1f).clickable { viewModel.navigateTo("tasks") }
                    )

                    // Finance Card
                    VitalsBentoCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        badge = spendStatus,
                        badgeColor = if (todaySpend <= dailyLimit) Tertiary else Color(0xFFD32F2F),
                        title = "Finance (Daily)",
                        mainValue = "₹${String.format("%.0f", todaySpend)} / 5k",
                        progress = spendProgress,
                        progressColor = if (todaySpend <= dailyLimit) Tertiary else Color(0xFFD32F2F),
                        subtext = "${(spendProgress * 100).toInt()}% of daily ceiling",
                        modifier = Modifier.weight(1f).clickable { viewModel.navigateTo("finance") }
                    )
                }

                // Row 2: Health & Vitality + Habits & Goals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Health Card
                    VitalsBentoCard(
                        icon = Icons.Default.FavoriteBorder,
                        badge = "${healthMetrics.score} Score",
                        badgeColor = SkyBlue,
                        title = "Health & Vitality",
                        mainValue = "${healthMetrics.steps} Steps",
                        progress = (healthMetrics.hydration / healthMetrics.hydrationTarget).coerceIn(0f, 1f),
                        progressColor = SkyBlue,
                        subtext = "Hydration: ${healthMetrics.hydration}L / ${healthMetrics.hydrationTarget}L",
                        modifier = Modifier.weight(1f).clickable { viewModel.navigateTo("insights") }
                    )

                    // Habits Card
                    VitalsBentoCard(
                        icon = Icons.Default.LocalFireDepartment,
                        badge = "${habits.maxOfOrNull { it.streakDays } ?: 18}d streak",
                        badgeColor = AmberWarning,
                        title = "Habits & Goals",
                        mainValue = "$completedHabits / $totalHabits Done",
                        progress = habitProgress,
                        progressColor = AmberWarning,
                        subtext = "${(habitProgress * 100).toInt()}% consistency",
                        modifier = Modifier.weight(1f).clickable { viewModel.openSubScreen("habits") }
                    )
                }

                // Daily Habit Check-in Bento Widget (Single Tap Logging with Categorization & Custom Habits)
                DailyHabitCheckInCard(
                    habits = habits,
                    nonRoutineTasks = nonRoutineTasks,
                    onToggleHabit = { viewModel.toggleHabit(it) },
                    onToggleNonRoutineTask = { viewModel.toggleNonRoutineTask(it) },
                    onIncrementNonRoutineProgress = { viewModel.incrementNonRoutineTaskProgress(it) },
                    onAddNonRoutineTask = { title, cat, mins, steps, desc ->
                        viewModel.addNonRoutineTask(title, cat, mins, steps, desc)
                    },
                    onAddCustomHabit = { name, cat, icon, color ->
                        viewModel.addCustomHabit(name, cat, icon, color)
                    },
                    onOpenHabits = { viewModel.openSubScreen("habits") }
                )
            }
        }

        // 7. Cross-Module Stream (Full View)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Cross-Module Stream",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess)
                    )
                }

                Text(
                    text = "Full View",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.navigateTo("calendar") }
                )
            }
        }

        // Stream Items with animateItem for smooth entrance, exit, and re-ordering animations
        items(crossStreamItems, key = { it.id }) { streamItem ->
            CrossStreamRowItem(
                item = streamItem,
                onToggleDone = { viewModel.toggleCrossStreamDone(streamItem.id) },
                onRemove = { viewModel.removeCrossStreamItem(streamItem.id) },
                onItemClick = {
                    when (streamItem.tagType) {
                        "meeting" -> viewModel.navigateTo("meetings")
                        "expense" -> viewModel.navigateTo("finance")
                        "wellness" -> viewModel.navigateTo("insights")
                        "autopay" -> viewModel.navigateTo("finance")
                        "travel" -> viewModel.openSubScreen("travel")
                        else -> viewModel.navigateTo("tasks")
                    }
                },
                modifier = Modifier.animateItem(
                    fadeInSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    fadeOutSpec = spring(
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            )
        }

        // 8. Quick Capture Hub (6 Circular Icons)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Capture Hub",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Tap to create",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickCaptureIconItem(
                            label = "Meeting",
                            icon = Icons.Default.CalendarToday,
                            bgColor = Color(0xFFEDE7F6),
                            tintColor = Color(0xFF673AB7),
                            onClick = { viewModel.openQuickScheduleMeeting() }
                        )
                        QuickCaptureIconItem(
                            label = "Task",
                            icon = Icons.Default.CheckCircleOutline,
                            bgColor = Color(0xFFFFEBEE),
                            tintColor = Color(0xFFE53935),
                            onClick = { viewModel.openCreateTask("Task") }
                        )
                        QuickCaptureIconItem(
                            label = "Expense",
                            icon = Icons.Default.AccountBalanceWallet,
                            bgColor = Color(0xFFE8F5E9),
                            tintColor = Color(0xFF2E7D32),
                            onClick = { viewModel.openCreateTask("Expense") }
                        )
                        QuickCaptureIconItem(
                            label = "Note",
                            icon = Icons.Default.EditNote,
                            bgColor = Color(0xFFE3F2FD),
                            tintColor = Color(0xFF1976D2),
                            onClick = { viewModel.openCreateTask("Note") }
                        )
                        QuickCaptureIconItem(
                            label = "Habit",
                            icon = Icons.Default.LocalFireDepartment,
                            bgColor = Color(0xFFFFF3E0),
                            tintColor = Color(0xFFF57C00),
                            onClick = { viewModel.openCreateTask("Habit") }
                        )
                        QuickCaptureIconItem(
                            label = "Health",
                            icon = Icons.Default.FavoriteBorder,
                            bgColor = Color(0xFFE0F2F1),
                            tintColor = Color(0xFF00897B),
                            onClick = { viewModel.openCreateTask("Health Entry") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BriefingMetricItem(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                fontSize = 18.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = OnSurfaceVariant,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun VitalsBentoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    badgeColor: Color,
    title: String,
    mainValue: String,
    progress: Float,
    progressColor: Color,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Text(
                text = mainValue,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = progressColor,
                trackColor = SurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CrossStreamRowItem(
    item: CrossStreamItem,
    onToggleDone: () -> Unit,
    onRemove: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isToggledState by remember(item.isCompleted) { mutableStateOf(item.isCompleted) }
    var isVisible by remember { mutableStateOf(true) }
    var isAnimatingOut by remember { mutableStateOf(false) }

    // Entrance animation for newly added/mounted items
    val enterAlpha = remember { Animatable(0f) }
    val enterScale = remember { Animatable(0.92f) }
    val enterSlideY = remember { Animatable(-20f) }

    LaunchedEffect(item.id) {
        launch {
            enterAlpha.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
        }
        launch {
            enterScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            enterSlideY.animateTo(
                0f,
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

    val swipeOffset = remember { Animatable(0f) }
    val collapseAnim = remember { Animatable(1f) }
    val hapticBounce = remember { Animatable(1f) }
    val density = LocalDensity.current
    val thresholdPx = with(density) { 85.dp.toPx() }
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    var hasPassedThresholdHaptic by remember { mutableStateOf(false) }

    val strikeProgress = remember { Animatable(if (item.isCompleted) 1f else 0f) }
    val isDueSoon = remember(item.time) { TimeUtils.isDueWithinNextTwoHours(item.time) }
    val isWarning = isDueSoon && !item.isCompleted && !isToggledState && (item.tagType == "priority" || item.tagType == "task")

    fun triggerToggle(swipedOut: Boolean = false) {
        if (isAnimatingOut) return
        if (!isToggledState) {
            isAnimatingOut = true
            isToggledState = true

            // Trigger physical haptic feedback & sound effect
            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (_: Exception) {}
            try {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            } catch (_: Exception) {}

            coroutineScope.launch {
                // Haptic-like visual pulse bounce animation
                launch {
                    hapticBounce.animateTo(1.08f, tween(70, easing = FastOutSlowInEasing))
                    hapticBounce.animateTo(0.95f, tween(60, easing = FastOutSlowInEasing))
                    hapticBounce.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }

                if (swipedOut) {
                    // 1. Accelerate card swipe fling off screen
                    launch {
                        swipeOffset.animateTo(1400f, tween(durationMillis = 240, easing = FastOutSlowInEasing))
                    }
                    delay(90)
                    // 2. Smoothly collapse vertical height and space of the card
                    collapseAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                    )
                    isVisible = false
                    onRemove()
                } else {
                    // Phase 1: Smooth CSS strike-through transition across title
                    launch {
                        strikeProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                        )
                    }
                    // Refined pause giving user crisp visual confirmation of task completion
                    delay(280)
                    // Phase 2: Slide-out horizontally & collapse vertically
                    launch {
                        swipeOffset.animateTo(1200f, tween(260, easing = FastOutSlowInEasing))
                    }
                    collapseAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                    isVisible = false
                    onRemove()
                }
            }
        } else {
            onToggleDone()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * 1.3f).toInt() },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 240)
        )
    ) {
        val currentOffset = swipeOffset.value
        val progressFraction = (currentOffset / thresholdPx).coerceIn(0f, 1f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = collapseAnim.value.coerceIn(0f, 1f) * enterAlpha.value
                    scaleX = enterScale.value
                    scaleY = enterScale.value
                    translationY = enterSlideY.value
                }
                .then(
                    if (collapseAnim.value < 1f) {
                        Modifier.layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val currentHeight = (placeable.height * collapseAnim.value).roundToInt()
                            layout(placeable.width, currentHeight) {
                                placeable.place(0, 0)
                            }
                        }
                    } else Modifier
                )
                .testTag("cross_stream_${item.id}")
                .testTag("swipe_to_complete_${item.id}")
        ) {
            // Background Reveal: Swipe-to-Complete Emerald Layer
            if (currentOffset > 2f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldSuccess)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.graphicsLayer {
                            alpha = progressFraction
                            scaleX = 0.85f + (0.15f * progressFraction)
                            scaleY = 0.85f + (0.15f * progressFraction)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (currentOffset >= thresholdPx) "Release to Complete ✓" else "Swipe to Complete",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Foreground Card with Horizontal Drag Gesture
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWarning) Color(0xFFFFF8F8) else SurfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isWarning) 2.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(swipeOffset.value.roundToInt(), 0) }
                    .graphicsLayer {
                        scaleX = hapticBounce.value
                        scaleY = hapticBounce.value
                    }
                    .then(
                        if (isWarning) Modifier.border(1.5.dp, Color(0xFFE53935), RoundedCornerShape(14.dp)) else Modifier
                    )
                    .pointerInput(item.id, isAnimatingOut) {
                        if (isAnimatingOut) return@pointerInput
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                hasPassedThresholdHaptic = false
                                if (swipeOffset.value >= thresholdPx) {
                                    triggerToggle(swipedOut = true)
                                } else {
                                    coroutineScope.launch {
                                        swipeOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                    }
                                }
                            },
                            onDragCancel = {
                                hasPassedThresholdHaptic = false
                                coroutineScope.launch {
                                    swipeOffset.animateTo(0f, spring())
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                if (dragAmount > 0 || swipeOffset.value > 0) {
                                    change.consume()
                                    val nextVal = (swipeOffset.value + dragAmount).coerceAtLeast(0f)
                                    // Trigger sensory tick when passing swipe threshold
                                    if (nextVal >= thresholdPx && !hasPassedThresholdHaptic) {
                                        hasPassedThresholdHaptic = true
                                        try {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            view.playSoundEffect(SoundEffectConstants.CLICK)
                                        } catch (_: Exception) {}
                                    } else if (nextVal < thresholdPx && hasPassedThresholdHaptic) {
                                        hasPassedThresholdHaptic = false
                                    }

                                    coroutineScope.launch {
                                        swipeOffset.snapTo(nextVal)
                                    }
                                }
                            }
                        )
                    }
                    .clickable { onItemClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Time column
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.width(54.dp)
                        )

                        // Vertical indicator line
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(
                                    when (item.tagType) {
                                        "priority" -> Color(0xFFE53935)
                                        "expense" -> Color(0xFF2E7D32)
                                        "focus" -> Primary
                                        "wellness" -> SkyBlue
                                        "autopay" -> Tertiary
                                        "travel" -> Color(0xFF5C6BC0)
                                        else -> Primary
                                    }
                                )
                        )

                        // Interactive check button
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isToggledState) EmeraldSuccess else SurfaceContainerHigh)
                                .border(
                                    width = 1.dp,
                                    color = if (isToggledState) EmeraldSuccess else OutlineVariant,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { triggerToggle() }
                                .testTag("cross_stream_check_${item.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isToggledState) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Text details
                        Column(modifier = Modifier.weight(1f)) {
                            val textColor = if (isToggledState) OnSurfaceVariant.copy(alpha = 0.55f) else OnSurface
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.drawWithContent {
                                    drawContent()
                                    if (strikeProgress.value > 0f) {
                                        val strokeW = 1.8.dp.toPx()
                                        val y = size.height * 0.54f
                                        drawLine(
                                            color = OnSurfaceVariant,
                                            start = Offset(0f, y),
                                            end = Offset(size.width * strikeProgress.value, y),
                                            strokeWidth = strokeW,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isToggledState) OnSurfaceVariant.copy(alpha = 0.45f) else OnSurfaceVariant,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tag pill
                    val (tagBg, tagColor) = when (item.tagType) {
                        "meeting" -> Color(0xFFEDE7F6) to Color(0xFF673AB7)
                        "priority" -> Color(0xFFFFEBEE) to Color(0xFFE53935)
                        "expense" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        "focus" -> Color(0xFFEDE7F6) to Primary
                        "wellness" -> Color(0xFFE1F5FE) to Color(0xFF0288D1)
                        "autopay" -> Color(0xFFECEFF1) to Color(0xFF455A64)
                        "travel" -> Color(0xFFE8EAF6) to Color(0xFF3949AB)
                        else -> SurfaceContainerHigh to OnSurfaceVariant
                    }

                    Text(
                        text = item.tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = tagColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tagBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun resolveHabitIcon(iconKey: String, name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        iconKey.contains("book", ignoreCase = true) || name.contains("Read", ignoreCase = true) -> Icons.Default.MenuBook
        iconKey.contains("school", ignoreCase = true) || iconKey.contains("learn", ignoreCase = true) || name.contains("Learn", ignoreCase = true) -> Icons.Default.School
        iconKey.contains("fitness", ignoreCase = true) || name.contains("Exercise", ignoreCase = true) || name.contains("Gym", ignoreCase = true) -> Icons.Default.FitnessCenter
        iconKey.contains("fire", ignoreCase = true) || iconKey.contains("streak", ignoreCase = true) -> Icons.Default.LocalFireDepartment
        iconKey.contains("water", ignoreCase = true) || name.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
        iconKey.contains("laptop", ignoreCase = true) || iconKey.contains("code", ignoreCase = true) -> Icons.Default.Laptop
        iconKey.contains("psychology", ignoreCase = true) || name.contains("Breath", ignoreCase = true) -> Icons.Default.Psychology
        else -> Icons.Default.SelfImprovement
    }
}

private fun resolveHabitColor(colorHex: String, category: String): Color {
    return try {
        if (colorHex.startsWith("#")) Color(android.graphics.Color.parseColor(colorHex))
        else when (category.lowercase()) {
            "fitness" -> Color(0xFF2E7D32)
            "reading" -> Color(0xFF0288D1)
            "learning" -> Color(0xFFF57C00)
            "wellness" -> Color(0xFFE91E63)
            "health" -> Color(0xFF00897B)
            else -> Color(0xFF673AB7)
        }
    } catch (_: Exception) {
        Color(0xFF673AB7)
    }
}

@Composable
private fun DailyHabitCheckInCard(
    habits: List<HabitItem>,
    nonRoutineTasks: List<NonRoutineTask> = emptyList(),
    onToggleHabit: (String) -> Unit,
    onToggleNonRoutineTask: (String) -> Unit = {},
    onIncrementNonRoutineProgress: (String) -> Unit = {},
    onAddNonRoutineTask: (title: String, category: String, estimatedMinutes: Int, totalSteps: Int, targetDesc: String) -> Unit = { _, _, _, _, _ -> },
    onAddCustomHabit: (name: String, category: String, iconKey: String, colorHex: String) -> Unit,
    onOpenHabits: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Mindfulness", "Fitness", "Reading", "Learning", "Wellness")
    val filteredHabits = remember(habits, selectedCategory) {
        if (selectedCategory == "All") habits.take(4)
        else habits.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    if (showAddDialog) {
        AddCustomHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, iconKey, colorHex ->
                onAddCustomHabit(name, category, iconKey, colorHex)
            }
        )
    }

    if (showAddGoalDialog) {
        AddNonRoutineGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, cat, mins, steps, desc ->
                onAddNonRoutineTask(title, cat, mins, steps, desc)
                showAddGoalDialog = false
            }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_habit_checkin_widget")
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
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Daily Habit Check-in",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Single-tap logging • Streak tracking",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // + Custom Habit button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryFixed)
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("add_custom_habit_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Custom",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenHabits() }
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Primary else SurfaceContainerLow)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else OnSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Habits Grid Display
            if (filteredHabits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits in $selectedCategory yet. Tap '+ Custom' to add one!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            } else {
                val chunks = filteredHabits.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chunks.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { habit ->
                                val activeColor = resolveHabitColor(habit.colorHex, habit.category)
                                val iconVector = resolveHabitIcon(habit.iconKey, habit.name)

                                HabitSingleTapItem(
                                    title = habit.name,
                                    subtitle = "${habit.streakDays}d streak • ${habit.category}",
                                    isDone = habit.isCompletedToday,
                                    icon = iconVector,
                                    activeColor = activeColor,
                                    activeBg = activeColor.copy(alpha = 0.12f),
                                    categoryTag = habit.category,
                                    testTag = "habit_checkin_${habit.id}",
                                    onClick = { onToggleHabit(habit.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Divider between Routine Habits & Daily Habit Goals (Non-Routine)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = SurfaceContainerHigh
            )

            // Section Header: Daily Habit Goals (Non-Routine Tasks)
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
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Daily Habit Goals",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Non-routine targets & progress toggle",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // "+ Set Goal" Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .clickable { showAddGoalDialog = true }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("set_daily_goal_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "+ Set Goal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Non-routine Tasks List with Progress Toggles
            if (nonRoutineTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No daily non-routine goals set yet. Tap '+ Set Goal' above!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    nonRoutineTasks.forEach { task ->
                        NonRoutineTaskItemCard(
                            task = task,
                            onToggleComplete = { onToggleNonRoutineTask(task.id) },
                            onAdvanceStep = { onIncrementNonRoutineProgress(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NonRoutineTaskItemCard(
    task: NonRoutineTask,
    onToggleComplete: () -> Unit,
    onAdvanceStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (task.totalSteps > 0) {
        (task.progressSteps.toFloat() / task.totalSteps.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val categoryColor = when (task.category.lowercase()) {
        "learning" -> Color(0xFF0288D1)
        "focus deep work" -> Color(0xFF7C3AED)
        "personal errand" -> Color(0xFFE91E63)
        else -> Color(0xFF2E7D32)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (task.isCompleted) SurfaceContainerHigh.copy(alpha = 0.6f) else SurfaceContainerLow)
            .border(
                1.dp,
                if (task.isCompleted) Color(0xFF2E7D32).copy(alpha = 0.3f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggleComplete() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("non_routine_task_${task.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle Checkbox / Circular Button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) Color(0xFF2E7D32) else SurfaceContainerLowest)
                        .border(
                            1.5.dp,
                            if (task.isCompleted) Color(0xFF2E7D32) else Outline,
                            CircleShape
                        )
                        .clickable { onToggleComplete() }
                        .testTag("toggle_goal_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (task.progressSteps > 0) {
                        Text(
                            text = "${task.progressSteps}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Primary
                            )
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isCompleted) OnSurfaceVariant else OnSurface,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category tag
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = categoryColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        )

                        // Duration tag
                        Text(
                            text = "${task.estimatedMinutes}m target",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            )
                        )

                        // Steps description
                        Text(
                            text = "• ${task.progressSteps}/${task.totalSteps} steps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if (task.isCompleted) Color(0xFF2E7D32) else OnSurfaceVariant,
                                fontWeight = if (task.isCompleted) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    if (task.totalSteps > 1) {
                        Spacer(modifier = Modifier.height(5.dp))
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (task.isCompleted) Color(0xFF2E7D32) else Primary,
                            trackColor = SurfaceContainerHigh
                        )
                    }
                }
            }

            // Quick "+ Step" button if multi-step and not yet completed
            if (!task.isCompleted && task.totalSteps > 1) {
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryFixed)
                        .clickable { onAdvanceStep() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("advance_step_${task.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Step",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AddNonRoutineGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, estimatedMinutes: Int, totalSteps: Int, targetDesc: String) -> Unit
) {
    var goalTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Sprint Goal") }
    var selectedMinutes by remember { mutableIntStateOf(45) }
    var selectedSteps by remember { mutableIntStateOf(1) }

    val categories = listOf("Sprint Goal", "Focus Deep Work", "Learning", "Personal Errand")
    val durationOptions = listOf(15, 30, 45, 60, 90)
    val stepOptions = listOf(1, 2, 3, 4, 5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "Set Daily Habit Goal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Define a non-routine task or milestone for today's focus.",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )

                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text("Goal / Task Title") },
                    placeholder = { Text("e.g. Draft Q3 Strategy, Review PR") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_goal_title")
                )

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Estimated Duration
                Text(
                    text = "Target Focus Duration",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    durationOptions.forEach { mins ->
                        FilterChip(
                            selected = selectedMinutes == mins,
                            onClick = { selectedMinutes = mins },
                            label = { Text("${mins}m", fontSize = 11.sp) }
                        )
                    }
                }

                // Total Steps
                Text(
                    text = "Milestone Steps ($selectedSteps step${if (selectedSteps > 1) "s" else ""})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stepOptions.forEach { steps ->
                        FilterChip(
                            selected = selectedSteps == steps,
                            onClick = { selectedSteps = steps },
                            label = { Text("$steps", fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (goalTitle.isNotBlank()) {
                        onConfirm(
                            goalTitle.trim(),
                            selectedCategory,
                            selectedMinutes,
                            selectedSteps,
                            "$selectedSteps Target Milestone${if (selectedSteps > 1) "s" else ""}"
                        )
                    }
                },
                enabled = goalTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.testTag("confirm_set_goal_btn")
            ) {
                Text("Set Goal", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddCustomHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, iconKey: String, colorHex: String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Reading") }
    var selectedIcon by remember { mutableStateOf("menu_book") }
    var selectedColor by remember { mutableStateOf("#0288D1") }

    val categories = listOf("Reading", "Learning", "Mindfulness", "Fitness", "Wellness", "Productivity")
    val icons = listOf(
        Pair("menu_book", Icons.Default.MenuBook),
        Pair("school", Icons.Default.School),
        Pair("self_improvement", Icons.Default.SelfImprovement),
        Pair("fitness_center", Icons.Default.FitnessCenter),
        Pair("laptop", Icons.Default.Laptop),
        Pair("local_fire_department", Icons.Default.LocalFireDepartment),
        Pair("water_drop", Icons.Default.WaterDrop),
        Pair("psychology", Icons.Default.Psychology)
    )
    val colors = listOf(
        Pair("#0288D1", Color(0xFF0288D1)),
        Pair("#F57C00", Color(0xFFF57C00)),
        Pair("#673AB7", Color(0xFF673AB7)),
        Pair("#2E7D32", Color(0xFF2E7D32)),
        Pair("#E91E63", Color(0xFFE91E63)),
        Pair("#00897B", Color(0xFF00897B)),
        Pair("#3525CD", Color(0xFF3525CD))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "New Custom Habit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "e.g. Reading, Learning, Deep Work",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Daily Book Reading") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_habit_name_input")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLow)
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else OnSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(3).forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLow)
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else OnSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Choose Icon",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        icons.take(4).forEach { (key, vector) ->
                            val isSelected = selectedIcon == key
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceContainerLow)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedIcon = key },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = key,
                                    tint = if (isSelected) Primary else OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        icons.drop(4).forEach { (key, vector) ->
                            val isSelected = selectedIcon == key
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceContainerLow)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedIcon = key },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = key,
                                    tint = if (isSelected) Primary else OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Accent Color",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.forEach { (hex, col) ->
                            val isSelected = selectedColor == hex
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .clickable { selectedColor = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (habitName.isNotBlank()) habitName else "$selectedCategory Routine"
                    onConfirm(finalName, selectedCategory, selectedIcon, selectedColor)
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("save_custom_habit_btn")
            ) {
                Text("Create Habit", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelMedium)
            }
        }
    )
}

@Composable
private fun HabitSingleTapItem(
    title: String,
    subtitle: String,
    isDone: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    activeBg: Color,
    categoryTag: String? = null,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) activeBg else SurfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isDone) activeColor.copy(alpha = 0.3f) else OutlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDone) activeColor else SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.Check else icon,
                        contentDescription = null,
                        tint = if (isDone) Color.White else OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = if (isDone) "Logged ✓" else "Tap to Log",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) activeColor else Primary,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDone) activeColor.copy(alpha = 0.12f) else PrimaryFixed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickCaptureIconItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    tintColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant
            )
        )
    }
}
