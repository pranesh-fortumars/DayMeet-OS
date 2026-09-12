package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GoalItem
import com.example.model.HabitItem
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * Monthly habit consistency data model for calendar heatmap visualization.
 */
data class MonthDayHabitStatus(
    val dayNumber: Int,
    val completedCount: Int,
    val totalCount: Int,
    val isToday: Boolean = false,
    val isFuture: Boolean = false
) {
    val completionRatio: Float
        get() = if (totalCount > 0) (completedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f
}

@Composable
fun HabitsSubScreen(
    viewModel: DayMeetViewModel,
    onBack: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val goals by viewModel.goals.collectAsState()

    var selectedDayNumber by remember { mutableIntStateOf(12) } // Default to Today (Sep 12)
    var selectedFilterMode by remember { mutableStateOf("both") } // "heatmap", "charts", or "both"

    // Dynamic calculation for today (Day 12)
    val todayCompletedCount = habits.count { it.isCompletedToday }
    val totalHabitsCount = habits.size.coerceAtLeast(1)
    val maxStreak = habits.maxOfOrNull { it.streakDays } ?: 18

    // September 2026 has 30 days. Day 1 is Tuesday (offset = 1 if week starts Monday).
    val daysInMonth = 30
    val todayDay = 12

    // Baseline historical completions for days 1..11 (preserving the 18-day streak)
    val historicalDailyCompletions = remember {
        mapOf(
            1 to 5, 2 to 4, 3 to 5, 4 to 5, 5 to 4,
            6 to 5, 7 to 4, 8 to 5, 9 to 5, 10 to 4, 11 to 5
        )
    }

    val monthDays = remember(todayCompletedCount, totalHabitsCount) {
        (1..daysInMonth).map { day ->
            when {
                day == todayDay -> MonthDayHabitStatus(
                    dayNumber = day,
                    completedCount = todayCompletedCount,
                    totalCount = totalHabitsCount,
                    isToday = true,
                    isFuture = false
                )
                day < todayDay -> {
                    val count = historicalDailyCompletions[day] ?: (totalHabitsCount - 1)
                    MonthDayHabitStatus(
                        dayNumber = day,
                        completedCount = count.coerceAtMost(totalHabitsCount),
                        totalCount = totalHabitsCount,
                        isToday = false,
                        isFuture = false
                    )
                }
                else -> MonthDayHabitStatus(
                    dayNumber = day,
                    completedCount = 0,
                    totalCount = totalHabitsCount,
                    isToday = false,
                    isFuture = true
                )
            }
        }
    }

    // Monthly consistency stats up to today
    val elapsedDaysCount = todayDay
    val totalCompletedPastDays = monthDays.take(elapsedDaysCount).sumOf { it.completedCount }
    val totalPossibleChecks = elapsedDaysCount * totalHabitsCount
    val monthlyConsistencyPercent = if (totalPossibleChecks > 0) {
        ((totalCompletedPastDays.toFloat() / totalPossibleChecks) * 100).toInt()
    } else 88

    SubModuleContainer(
        title = "Habits & Milestones",
        subtitle = "Daily streak preservation & visual heatmap",
        onBack = onBack
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Month Consistency Highlights Bento
            item {
                ConsistencyHighlightsCard(
                    monthTitle = "September 2026",
                    maxStreak = maxStreak,
                    consistencyRate = monthlyConsistencyPercent,
                    completedToday = todayCompletedCount,
                    totalToday = totalHabitsCount
                )
            }

            // 2. View Mode Tabs (Heatmap vs Progress Chart vs All)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilterMode == "both",
                        onClick = { selectedFilterMode = "both" },
                        label = { Text("Overview & Heatmap") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarViewMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryFixed,
                            selectedLabelColor = Primary
                        )
                    )
                    FilterChip(
                        selected = selectedFilterMode == "charts",
                        onClick = { selectedFilterMode = "charts" },
                        label = { Text("Habit Charts") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryFixed,
                            selectedLabelColor = Primary
                        )
                    )
                }
            }

            // 3. Calendar Heatmap Card
            if (selectedFilterMode == "both" || selectedFilterMode == "heatmap") {
                item {
                    CalendarHeatmapCard(
                        monthDays = monthDays,
                        selectedDay = selectedDayNumber,
                        onSelectDay = { selectedDayNumber = it },
                        habits = habits
                    )
                }
            }

            // 4. Visual Progress Chart by Habit
            item {
                Text(
                    text = "HABIT CONSISTENCY PROGRESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.6.sp
                    )
                )
            }

            items(habits, key = { it.id }) { habit ->
                HabitProgressCard(
                    habit = habit,
                    onToggleDone = { viewModel.toggleHabit(habit.id) }
                )
            }

            // 5. Active Goals & Targets Section
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ACTIVE GOALS & TARGETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.6.sp
                    )
                )
            }

            items(goals, key = { it.id }) { goal ->
                GoalProgressCard(goal = goal)
            }
        }
    }
}

/**
 * Top consistency summary card with streak and completion rate metrics.
 */
@Composable
private fun ConsistencyHighlightsCard(
    monthTitle: String,
    maxStreak: Int,
    consistencyRate: Int,
    completedToday: Int,
    totalToday: Int
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("consistency_highlights_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = monthTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Text(
                        text = "Streak Consistency",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberWarning.copy(alpha = 0.12f),
                    modifier = Modifier.testTag("consistency_metric_streak")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = AmberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${maxStreak}d Streak",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Stat Columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Monthly Consistency Rate
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("consistency_metric_rate")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Monthly",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$consistencyRate%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        )
                        Text(
                            text = "Consistency",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Today Check-ins
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedToday / $totalToday",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                        Text(
                            text = if (completedToday == totalToday) "All Done! 🎉" else "Habits checked",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Best Streak Focus
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "On Track",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "12 / 12",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Tertiary
                            )
                        )
                        Text(
                            text = "Days active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calendar Heatmap showing daily streak consistency for the current month.
 */
@Composable
private fun CalendarHeatmapCard(
    monthDays: List<MonthDayHabitStatus>,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    habits: List<HabitItem>
) {
    val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
    // September 1, 2026 starts on Tuesday, so offset is 1 empty slot for Monday
    val firstDayOffset = 1

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_heatmap_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Monthly Streak Heatmap",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }

                Text(
                    text = "Sep 2026",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekday Headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdays.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Heatmap Grid: 5 weeks
            val totalCells = firstDayOffset + monthDays.size
            val rows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (rowIndex in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (colIndex in 0..6) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val dayNumber = cellIndex - firstDayOffset + 1

                            if (cellIndex < firstDayOffset || dayNumber > monthDays.size) {
                                // Empty spacer cell
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                val dayStatus = monthDays[dayNumber - 1]
                                val isSelected = dayNumber == selectedDay

                                HeatmapDayCell(
                                    status = dayStatus,
                                    isSelected = isSelected,
                                    onClick = { onSelectDay(dayNumber) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Heatmap Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heatmap_legend"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendBox(color = SurfaceContainerHigh)
                    LegendBox(color = Color(0xFFC8E6C9))
                    LegendBox(color = Color(0xFF81C784))
                    LegendBox(color = Color(0xFF4CAF50))
                    LegendBox(color = Color(0xFF2E7D32))
                }

                Text(
                    text = "More (100%)",
                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                )
            }

            // Selected Day Detailed Breakdown Banner
            val selectedStatus = monthDays.firstOrNull { it.dayNumber == selectedDay }
            if (selectedStatus != null) {
                Spacer(modifier = Modifier.height(14.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    DayDetailSummaryCard(
                        status = selectedStatus,
                        habits = habits
                    )
                }
            }
        }
    }
}

/**
 * Individual day cell in the heatmap matrix.
 */
@Composable
private fun HeatmapDayCell(
    status: MonthDayHabitStatus,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        status.isFuture -> SurfaceContainerHigh
        status.completedCount == 0 -> SurfaceContainerHigh
        status.completionRatio < 0.35f -> Color(0xFFC8E6C9) // Level 1 (mint)
        status.completionRatio < 0.65f -> Color(0xFF81C784) // Level 2 (green)
        status.completionRatio < 0.90f -> Color(0xFF4CAF50) // Level 3 (vibrant green)
        else -> Color(0xFF2E7D32) // Level 4 (deep emerald)
    }

    val textColor = when {
        status.isFuture -> OnSurfaceVariant.copy(alpha = 0.4f)
        status.completedCount == 0 -> OnSurfaceVariant
        status.completionRatio >= 0.65f -> Color.White
        else -> Color(0xFF1B5E20)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (status.isToday) {
                    Modifier.border(2.dp, AmberWarning, RoundedCornerShape(8.dp))
                } else if (isSelected) {
                    Modifier.border(1.5.dp, Primary, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .testTag("heatmap_day_${status.dayNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${status.dayNumber}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (status.isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    fontSize = 11.sp
                )
            )

            if (status.isToday) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(AmberWarning)
                )
            }
        }
    }
}

@Composable
private fun LegendBox(color: Color) {
    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
    )
}

/**
 * Breakdown banner when tapping any day in the heatmap.
 */
@Composable
private fun DayDetailSummaryCard(
    status: MonthDayHabitStatus,
    habits: List<HabitItem>
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerHigh,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_day_detail")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (status.isToday) "Sep ${status.dayNumber} (Today)" else "September ${status.dayNumber}, 2026",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    if (status.isToday) {
                        Text(
                            text = "TODAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberWarning.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = when {
                        status.isFuture -> "Upcoming day • Keep the momentum rolling!"
                        status.completedCount == status.totalCount -> "100% Completed • All habits nailed! 🔥"
                        status.completedCount > 0 -> "${status.completedCount}/${status.totalCount} habits completed (${(status.completionRatio * 100).toInt()}%)"
                        else -> "No check-ins logged for this date"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
            }

            if (!status.isFuture && status.completedCount > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${(status.completionRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Habit Progress Card replacing the static list with visual consistency progress bars.
 */
@Composable
private fun HabitProgressCard(
    habit: HabitItem,
    onToggleDone: () -> Unit
) {
    // Calculate an estimated consistency rate based on streak days vs 30 days
    val monthlyConsistency = remember(habit.streakDays) {
        when {
            habit.streakDays >= 15 -> 0.94f
            habit.streakDays >= 10 -> 0.85f
            habit.streakDays >= 7 -> 0.75f
            else -> 0.65f
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_progress_card_${habit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Habit Name, Category, Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = habit.isCompletedToday,
                        onCheckedChange = { onToggleDone() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = OutlineVariant
                        ),
                        modifier = Modifier.testTag("toggle_habit_${habit.id}")
                    )

                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${habit.category} • ${habit.targetFrequency}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Flame streak badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberWarning.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AmberWarning,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${habit.streakDays}d streak",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Progress Consistency Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Month Consistency",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${(monthlyConsistency * 100).toInt()}% on track",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 11.sp
                        )
                    )
                }

                LinearProgressIndicator(
                    progress = { monthlyConsistency },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (monthlyConsistency >= 0.85f) EmeraldSuccess else Primary,
                    trackColor = SurfaceContainerHigh
                )
            }
        }
    }
}

/**
 * Long-term Goal Progress Card.
 */
@Composable
private fun GoalProgressCard(goal: GoalItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Tertiary
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TertiaryFixed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = "Target: ${goal.deadline}",
                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Primary,
                trackColor = SurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: ${goal.current}",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
                Text(
                    text = "${(goal.progressPercent * 100).toInt()}% Done",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }
    }
}
