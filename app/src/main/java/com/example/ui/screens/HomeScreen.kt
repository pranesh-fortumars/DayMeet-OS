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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayMeetRepository
import com.example.model.CrossStreamItem
import com.example.model.HabitItem
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
    val transactions by viewModel.transactions.collectAsState()

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

                // Weather and date conditions
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
                        text = "72°F Sunny (10% rain)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(text = "•", color = OutlineVariant)
                    Text(
                        text = "New York",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                    )
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

                // Daily Habit Check-in Bento Widget (Single Tap Logging for Morning Meditation & Exercise)
                DailyHabitCheckInCard(
                    habits = habits,
                    onLogMeditation = { viewModel.logMorningMeditation() },
                    onLogExercise = { viewModel.logMorningExercise() },
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

        // Stream Items
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
                }
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
                            onClick = { viewModel.openCreateTask("Meeting") }
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
    onItemClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isToggledState by remember(item.isCompleted) { mutableStateOf(item.isCompleted) }
    var isVisible by remember { mutableStateOf(true) }
    var isAnimatingOut by remember { mutableStateOf(false) }

    val swipeOffset = remember { Animatable(0f) }
    val density = LocalDensity.current
    val thresholdPx = with(density) { 85.dp.toPx() }

    val strikeProgress = remember { Animatable(if (item.isCompleted) 1f else 0f) }
    val isDueSoon = remember(item.time) { TimeUtils.isDueWithinNextTwoHours(item.time) }
    val isWarning = isDueSoon && !item.isCompleted && !isToggledState && (item.tagType == "priority" || item.tagType == "task")

    fun triggerToggle(swipedOut: Boolean = false) {
        if (isAnimatingOut) return
        if (!isToggledState) {
            isAnimatingOut = true
            isToggledState = true
            coroutineScope.launch {
                if (swipedOut) {
                    launch { swipeOffset.animateTo(1200f, tween(320)) }
                }
                // Phase 1: Smooth CSS strike-through transition across title
                strikeProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                )
                delay(60)
                // Phase 2: Slide-out horizontally & shrink vertically
                isVisible = false
                delay(340)
                // Phase 3: Remove from list
                onRemove()
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
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 280, delayMillis = 30, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 280)
        )
    ) {
        val currentOffset = swipeOffset.value
        val progressFraction = (currentOffset / thresholdPx).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    .then(
                        if (isWarning) Modifier.border(1.5.dp, Color(0xFFE53935), RoundedCornerShape(14.dp)) else Modifier
                    )
                    .pointerInput(item.id, isAnimatingOut) {
                        if (isAnimatingOut) return@pointerInput
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (swipeOffset.value >= thresholdPx) {
                                    triggerToggle(swipedOut = true)
                                } else {
                                    coroutineScope.launch {
                                        swipeOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    swipeOffset.animateTo(0f, spring())
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                if (dragAmount > 0 || swipeOffset.value > 0) {
                                    change.consume()
                                    val nextVal = (swipeOffset.value + dragAmount).coerceAtLeast(0f)
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

@Composable
private fun DailyHabitCheckInCard(
    habits: List<HabitItem>,
    onLogMeditation: () -> Unit,
    onLogExercise: () -> Unit,
    onOpenHabits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val meditationHabit = habits.find { it.name.contains("Meditation", ignoreCase = true) || it.id == "h_meditation" }
    val exerciseHabit = habits.find { it.name.contains("Exercise", ignoreCase = true) || it.name.contains("Walk", ignoreCase = true) || it.id == "h_exercise" }

    val isMeditationDone = meditationHabit?.isCompletedToday == true
    val isExerciseDone = exerciseHabit?.isCompletedToday == true

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_habit_checkin_widget")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            text = "Single-tap morning routine logging",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Two Quick Tap Habit Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HabitSingleTapItem(
                    title = "Morning Meditation",
                    subtitle = "${meditationHabit?.streakDays ?: 19}d streak • 15m",
                    isDone = isMeditationDone,
                    icon = Icons.Default.SelfImprovement,
                    activeColor = Color(0xFF673AB7),
                    activeBg = Color(0xFFEDE7F6),
                    testTag = "habit_checkin_meditation",
                    onClick = onLogMeditation,
                    modifier = Modifier.weight(1f)
                )

                HabitSingleTapItem(
                    title = "Morning Exercise",
                    subtitle = "${exerciseHabit?.streakDays ?: 14}d streak • 30m",
                    isDone = isExerciseDone,
                    icon = Icons.Default.FitnessCenter,
                    activeColor = Color(0xFF2E7D32),
                    activeBg = Color(0xFFE8F5E9),
                    testTag = "habit_checkin_exercise",
                    onClick = onLogExercise,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HabitSingleTapItem(
    title: String,
    subtitle: String,
    isDone: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    activeBg: Color,
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
