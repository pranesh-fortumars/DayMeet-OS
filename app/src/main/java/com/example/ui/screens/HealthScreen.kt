package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel
import java.util.Locale
import kotlin.math.min

@Composable
fun HealthScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val health by viewModel.healthMetrics.collectAsState()
    val monthlyBudgetTarget by viewModel.monthlyBudgetTarget.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    var showBudgetCustomizerDialog by remember { mutableStateOf(false) }

    val monthlySpent = remember(transactions) {
        35000.0 + transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Live Bio-Sync Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "LIVE BIO-SYNC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Synced 3m ago",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Title & Description
        item {
            Column {
                Text(
                    text = "Health & Wellness",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Holistic vitals, mindfulness & body metrics auto-synced with schedule",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
            }
        }

        // Date Bar Navigator
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Day",
                        tint = OnSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { viewModel.showToast("Yesterday's vitals") }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Today, Thu Oct 24",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Real-time",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryFixed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Day",
                        tint = OnSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { viewModel.showToast("Tomorrow's projection") }
                    )
                }
            }
        }

        // Weekly Summary Card (7-Day Trend of Tasks, Habits & Spending)
        item {
            WeeklySummaryCard(viewModel = viewModel)
        }

        // 2. Big Circular Health Score Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("health_score_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gauge Canvas
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(130.dp)) {
                            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            // Background track
                            drawCircle(
                                color = SurfaceContainerHigh,
                                radius = size.minDimension / 2 - 5.dp.toPx(),
                                style = stroke
                            )
                            // Progress arc
                            drawArc(
                                color = Color(0xFF00897B),
                                startAngle = -90f,
                                sweepAngle = 360f * (health.score / 100f),
                                useCenter = false,
                                style = stroke
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${health.score}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "OF 100 SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(Color(0xFFE0F2F1))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFF00897B),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = health.scoreLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00897B)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Balance across sleep, activity and hydration is peak today.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // 3. 4 Vitals Cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sleep
                    VitalDetailCard(
                        icon = Icons.Default.Bedtime,
                        iconColor = Color(0xFF5C6BC0),
                        badge = health.sleepQuality,
                        badgeColor = Color(0xFF5C6BC0),
                        title = "Sleep Duration",
                        value = health.sleepDuration,
                        sub = "Deep: ${health.sleepDeep}",
                        modifier = Modifier.weight(1f)
                    )

                    // Daily Steps
                    VitalDetailCard(
                        icon = Icons.Default.DirectionsWalk,
                        iconColor = SkyBlue,
                        badge = "78% Goal",
                        badgeColor = SkyBlue,
                        title = "Daily Steps",
                        value = "${health.steps}",
                        sub = "Target: 10,000 (${health.stepsDistance})",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hydration
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SkyLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = SkyBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "+250ml",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SkyBlue
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SkyLight)
                                        .clickable { viewModel.addWater(0.25f) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                        .testTag("water_add_btn")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Hydration",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                            Text(
                                text = "${health.hydration}L",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Goal: ${health.hydrationTarget}L",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Active Burn
                    VitalDetailCard(
                        icon = Icons.Default.LocalFireDepartment,
                        iconColor = Color(0xFFE53935),
                        badge = "80% Burn",
                        badgeColor = Color(0xFFE53935),
                        title = "Active Burn",
                        value = "${health.caloriesBurned} kcal",
                        sub = "Target: ${health.caloriesTarget} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Weekly Consistency Chart
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
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
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Weekly Consistency",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        Text(
                            text = "Consistent +12%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TertiaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf(
                        Triple("F", 0.65f, false),
                        Triple("S", 0.45f, false),
                        Triple("S", 0.70f, false),
                        Triple("M", 0.80f, false),
                        Triple("T", 0.75f, false),
                        Triple("W", 0.70f, false),
                        Triple("T", 0.90f, true)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEach { (label, heightRatio, isToday) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isToday) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((70 * heightRatio).dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isToday) Primary else Color(0xFFD6D7FB))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isToday) Primary else OnSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Primary))
                            Text(
                                text = "Daily Steps (Avg 8,420)",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Tertiary))
                            Text(
                                text = "Sleep Regularity: 92%",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // 4b. Financial Health Gauge
        item {
            FinancialHealthGaugeCard(
                monthlySpent = monthlySpent,
                monthlyBudgetTarget = monthlyBudgetTarget,
                onCustomizeBudget = { showBudgetCustomizerDialog = true }
            )
        }

        // 5. Biometrics & Mindset
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Biometrics & Mindset",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                Text(
                    text = "Live Sensors",
                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                )
            }
        }

        // Heart Rate Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${health.heartRateBpm} BPM",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "Normal",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Resting heart rate • Normal sinus rhythm",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Range",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = health.heartRateRange,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                    }
                }
            }
        }

        // Mental State (Emojis)
        item {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Mental State",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        Text(
                            text = "Energized & Focused ⚡",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryFixed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val moods = listOf(
                        "😊" to "Happy",
                        "😌" to "Calm",
                        "😐" to "Neutral",
                        "🥱" to "Fatigued",
                        "🤯" to "Stressed"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        moods.forEach { (emoji, label) ->
                            val isSelected = health.mentalState == label
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryFixed else Color(0xFFF1F5FD))
                                    .clickable { viewModel.setMentalState(label) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(text = emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
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
                    }
                }
            }
        }

        // 6. Planned Body Routines
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planned Body Routines",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Calendar Sync",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // HIIT Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
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
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFCCBC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color(0xFFD84315),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "5:30 PM",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFD84315),
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFBE9E7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Text(
                                    text = "35 min",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                            Text(
                                text = "Evening HIIT & Outdoor Run",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "Auto-scheduled between 'Client Demo'...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.showToast("HIIT session added to Planner") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Details",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 10 min Breathwork Card
        item {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "10 min Midday Breathwork",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "4-7-8 Parasympathetic reset",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Recommended",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Calming ambient soundscape",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                        }

                        Button(
                            onClick = { viewModel.showToast("Starting 10 min breathwork session...") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Start Session",
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

        // 7. Smart Bio-Automations
        item {
            Text(
                text = "Smart Bio-Automations",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Posture & Stand Cue
        item {
            BioAutomationRow(
                icon = Icons.Default.AccessibilityNew,
                title = "Posture & Stand Cue",
                sub = "Every 90m during deep calendar blocks",
                isChecked = health.postureReminderOn,
                onCheckedChange = { viewModel.togglePostureReminder() }
            )
        }

        // Vitamin D3 & Omega
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F2F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = Color(0xFF00897B),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Vitamin D3 & Omega",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = if (health.vitaminLogged) "✓ Logged at 8:00 AM" else "Pending morning dose",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (health.vitaminLogged) Color(0xFF2E7D32) else AmberWarning,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = if (health.vitaminLogged) "Done" else "Mark Done",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (health.vitaminLogged) Color(0xFF2E7D32) else Primary
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (health.vitaminLogged) Color(0xFFE8F5E9) else PrimaryFixed)
                            .clickable { viewModel.toggleVitaminLogged() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bedtime Guard & DND
        item {
            BioAutomationRow(
                icon = Icons.Default.DoNotDisturbOn,
                title = "Bedtime Guard & DND",
                sub = "10:30 PM • Auto-dims ambient screen",
                isChecked = health.bedtimeDndOn,
                onCheckedChange = { viewModel.toggleBedtimeDnd() }
            )
        }

        // 8. Connected Wearable
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "CONNECTED WEARABLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.6.sp
                                )
                            )
                            Text(
                                text = health.wearableStatus,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Connected",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showBudgetCustomizerDialog) {
        CustomizeMonthlyBudgetDialog(
            currentTarget = monthlyBudgetTarget,
            onDismiss = { showBudgetCustomizerDialog = false },
            onSave = { newTarget ->
                viewModel.updateMonthlyBudgetTarget(newTarget)
                showBudgetCustomizerDialog = false
            }
        )
    }
}

@Composable
private fun VitalDetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    badge: String,
    badgeColor: Color,
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun BioAutomationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    sub: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary)
            )
        }
    }
}

private data class WeeklySummaryDayData(
    val day: String,
    val fullDay: String,
    val tasks: Int,
    val habits: Int,
    val spending: Int
)

@Composable
private fun WeeklySummaryCard(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember {
        listOf(
            WeeklySummaryDayData("Mon", "Monday", 6, 4, 2100),
            WeeklySummaryDayData("Tue", "Tuesday", 8, 5, 1850),
            WeeklySummaryDayData("Wed", "Wednesday", 7, 5, 3200),
            WeeklySummaryDayData("Thu", "Thursday (Today)", 11, 5, 3450),
            WeeklySummaryDayData("Fri", "Friday", 9, 4, 2400),
            WeeklySummaryDayData("Sat", "Saturday", 5, 5, 1950),
            WeeklySummaryDayData("Sun", "Sunday", 7, 4, 4100)
        )
    }

    var selectedIndex by remember { mutableStateOf(3) }
    var selectedStream by remember { mutableStateOf("all") } // all, tasks, habits, spending

    val selectedDay = weeklyData.getOrNull(selectedIndex) ?: weeklyData[3]

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
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
                                text = "Weekly Summary",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "7-Day Correlation",
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
                            text = "Tasks completed, habits maintained & spending trend",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Stream Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "all" to "All Streams",
                    "tasks" to "Tasks",
                    "habits" to "Habits",
                    "spending" to "Spending"
                ).forEach { (id, label) ->
                    val isSelected = selectedStream == id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Primary else SurfaceContainerHigh,
                        modifier = Modifier
                            .clickable { selectedStream = id }
                            .testTag("weekly_summary_stream_$id")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) OnPrimary else OnSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Active Day Tooltip Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerLow,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedDay.fullDay} Focus & Balance",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Day ${selectedIndex + 1} of 7",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                            Text(
                                text = "${selectedDay.tasks} Tasks Done",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Text(
                                text = "${selectedDay.habits}/5 Habits",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0288D1)))
                            Text(
                                text = "₹${selectedDay.spending.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Interactive Multi-Series Canvas Visualization
            val primaryColor = Primary
            val habitsColor = Color(0xFF10B981)
            val spendingColor = Color(0xFF0288D1)
            val gridColor = SurfaceContainerHigh
            val highlightColor = SurfaceContainerHighest

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 22.dp.toPx()
                    val chartH = h - paddingBottom
                    val colWidth = w / 7f

                    // 1. Grid Lines
                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = gridColor,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(w, 0f),
                        pathEffect = dashedEffect
                    )
                    drawLine(
                        color = gridColor,
                        start = androidx.compose.ui.geometry.Offset(0f, chartH * 0.5f),
                        end = androidx.compose.ui.geometry.Offset(w, chartH * 0.5f),
                        pathEffect = dashedEffect
                    )
                    drawLine(
                        color = gridColor,
                        start = androidx.compose.ui.geometry.Offset(0f, chartH),
                        end = androidx.compose.ui.geometry.Offset(w, chartH)
                    )

                    // 2. Selected Column Indicator
                    val selectedCenterX = selectedIndex * colWidth + colWidth / 2f
                    drawRect(
                        color = highlightColor.copy(alpha = 0.45f),
                        topLeft = androidx.compose.ui.geometry.Offset(selectedIndex * colWidth + 4.dp.toPx(), 0f),
                        size = androidx.compose.ui.geometry.Size(colWidth - 8.dp.toPx(), chartH)
                    )

                    // 3. Draw Tasks (Bars)
                    if (selectedStream == "all" || selectedStream == "tasks") {
                        val barW = 16.dp.toPx().coerceAtMost(colWidth * 0.45f)
                        weeklyData.forEachIndexed { i, item ->
                            val cx = i * colWidth + colWidth / 2f
                            val barH = (item.tasks / 14f) * (chartH - 8.dp.toPx())
                            val isSel = i == selectedIndex
                            drawRoundRect(
                                color = if (isSel) primaryColor else primaryColor.copy(alpha = 0.65f),
                                topLeft = androidx.compose.ui.geometry.Offset(cx - barW / 2f, chartH - barH),
                                size = androidx.compose.ui.geometry.Size(barW, barH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }

                    // 4. Draw Spending (Dashed Line with circular points)
                    if (selectedStream == "all" || selectedStream == "spending") {
                        val spendPath = Path()
                        val spendPoints = weeklyData.mapIndexed { i, item ->
                            val cx = i * colWidth + colWidth / 2f
                            val cy = chartH - ((item.spending / 5000f).coerceIn(0f, 1f) * (chartH - 12.dp.toPx()))
                            androidx.compose.ui.geometry.Offset(cx, cy)
                        }
                        spendPath.moveTo(spendPoints[0].x, spendPoints[0].y)
                        for (j in 1 until spendPoints.size) {
                            spendPath.lineTo(spendPoints[j].x, spendPoints[j].y)
                        }
                        drawPath(
                            path = spendPath,
                            color = spendingColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                            )
                        )
                        spendPoints.forEachIndexed { i, pt ->
                            drawCircle(
                                color = spendingColor,
                                radius = if (i == selectedIndex) 5.dp.toPx() else 3.5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = if (i == selectedIndex) 2.5.dp.toPx() else 1.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }

                    // 5. Draw Habits (Solid Line with circular points)
                    if (selectedStream == "all" || selectedStream == "habits") {
                        val habitPath = Path()
                        val habitPoints = weeklyData.mapIndexed { i, item ->
                            val cx = i * colWidth + colWidth / 2f
                            val cy = chartH - ((item.habits / 6f).coerceIn(0f, 1f) * (chartH - 10.dp.toPx()))
                            androidx.compose.ui.geometry.Offset(cx, cy)
                        }
                        habitPath.moveTo(habitPoints[0].x, habitPoints[0].y)
                        for (j in 1 until habitPoints.size) {
                            val prev = habitPoints[j - 1]
                            val curr = habitPoints[j]
                            val midX = (prev.x + curr.x) / 2f
                            habitPath.cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                        }
                        drawPath(
                            path = habitPath,
                            color = habitsColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        habitPoints.forEachIndexed { i, pt ->
                            drawCircle(
                                color = habitsColor,
                                radius = if (i == selectedIndex) 5.5.dp.toPx() else 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = if (i == selectedIndex) 2.5.dp.toPx() else 1.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // Interactive click areas overlay for each day
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    weeklyData.forEachIndexed { idx, d ->
                        val isSel = idx == selectedIndex
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    selectedIndex = idx
                                    viewModel.showToast("${d.fullDay}: ${d.tasks} tasks, ${d.habits}/5 habits, ₹${d.spending}")
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = d.day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Primary else OnSurfaceVariant,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }
            }

            // 3 Bottom Metric Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tasks Velocity",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 9.sp)
                        )
                        Text(
                            text = "53 Done",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                        Text(
                            text = "88% goal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Habits Kept",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 9.sp)
                        )
                        Text(
                            text = "32 / 35",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        )
                        Text(
                            text = "91% streak",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Spending",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 9.sp)
                        )
                        Text(
                            text = "₹19,050",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1)
                            )
                        )
                        Text(
                            text = "₹15.9k buffer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class FinancialHealthStatus(
    val label: String,
    val color: Color,
    val bg: Color,
    val score: String
)

@Composable
fun FinancialHealthGaugeCard(
    monthlySpent: Double,
    monthlyBudgetTarget: Double,
    onCustomizeBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ratio = if (monthlyBudgetTarget > 0) (monthlySpent / monthlyBudgetTarget).toFloat() else 0f
    val clampedProgress = ratio.coerceIn(0f, 1f)
    val percentInt = (ratio * 100).toInt()
    val remaining = (monthlyBudgetTarget - monthlySpent).coerceAtLeast(0.0)

    val status = when {
        ratio <= 0.70f -> FinancialHealthStatus(
            label = "Optimal Pace",
            color = Color(0xFF10B981),
            bg = Color(0xFFE8F5E9),
            score = "92/100 • Excellent"
        )
        ratio <= 0.90f -> FinancialHealthStatus(
            label = "Caution Pace",
            color = Color(0xFFF59E0B),
            bg = Color(0xFFFFF8E1),
            score = "76/100 • Watchful"
        )
        else -> FinancialHealthStatus(
            label = "Budget Alert",
            color = Color(0xFFEF4444),
            bg = Color(0xFFFFEBEE),
            score = "48/100 • Overpace"
        )
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("financial_health_gauge_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Financial Health Icon",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Financial Health",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(status.bg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = status.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = status.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Monthly spending progress vs target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Customize Target Button
                OutlinedButton(
                    onClick = onCustomizeBudget,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("customize_budget_target_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize Budget",
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Speedometer Arc Gauge Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                val trackColor = SurfaceContainerHighest
                val progressBrush = Brush.horizontalGradient(
                    colors = if (ratio <= 0.70f) {
                        listOf(Color(0xFF06B6D4), Color(0xFF10B981))
                    } else if (ratio <= 0.90f) {
                        listOf(Color(0xFF10B981), Color(0xFFF59E0B))
                    } else {
                        listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                    }
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    val strokeWidth = 16.dp.toPx()
                    val arcSize = min(size.width * 0.78f, size.height * 1.5f)
                    val left = (size.width - arcSize) / 2f
                    val top = size.height * 0.12f

                    // 1. Background Arc Track (240 degrees: from 150 to 390)
                    drawArc(
                        color = trackColor,
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(left, top),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Active Spending Progress Arc
                    val activeSweep = 240f * clampedProgress
                    if (activeSweep > 0.5f) {
                        drawArc(
                            brush = progressBrush,
                            startAngle = 150f,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = Offset(left, top),
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Center Readout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(status.bg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$percentInt% USED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = status.color,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", monthlySpent)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )

                    Text(
                        text = "of ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)} target",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Metric Summary Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Budget Target
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCustomizeBudget() }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Target Budget",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Tap to edit ✎",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Remaining Buffer
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", remaining)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = status.color,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (remaining > 0) "Under target ✓" else "Exceeded ⚠",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = status.color,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Daily Safe Allowance
                val safeDaily = (remaining / 17.0).coerceAtLeast(0.0)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Safe Burn Rate",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", safeDaily)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1),
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Per day (17d left)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Financial Health Score & Guidance Callout
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHighest.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(status.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = status.color,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Column {
                        val safeDailyText = (remaining / 17.0).coerceAtLeast(0.0)
                        Text(
                            text = "Health Score: ${status.score}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = if (ratio <= 0.70f)
                                "Spending is 18% below seasonal projection. Keeping daily expenses under ₹${String.format(Locale.getDefault(), "%,.0f", safeDailyText)} will leave a surplus of ₹${String.format(Locale.getDefault(), "%,.0f", remaining)}."
                            else if (ratio <= 0.90f)
                                "Pacing near budget limit. Reduce non-essential discretionary expenses to maintain your month-end savings buffer."
                            else
                                "Current spending has consumed most of the monthly budget. Consider reviewing upcoming bill payments and recurring auto-debits.",
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
}

@Composable
fun CustomizeMonthlyBudgetDialog(
    currentTarget: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var targetText by remember { mutableStateOf(String.format(Locale.getDefault(), "%.0f", currentTarget)) }
    val quickPresets = listOf(40000.0, 50000.0, 60000.0, 75000.0, 100000.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Monthly Budget Target",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Set your desired spending ceiling for this month. The Financial Health gauge dynamically tracks and scores your burn velocity against this goal.",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Budget Target (₹)") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_target_input")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPresets.take(3).forEach { amount ->
                            FilterChip(
                                selected = targetText == String.format(Locale.getDefault(), "%.0f", amount),
                                onClick = { targetText = String.format(Locale.getDefault(), "%.0f", amount) },
                                label = { Text("₹${(amount / 1000).toInt()}k", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPresets.drop(3).forEach { amount ->
                            FilterChip(
                                selected = targetText == String.format(Locale.getDefault(), "%.0f", amount),
                                onClick = { targetText = String.format(Locale.getDefault(), "%.0f", amount) },
                                label = { Text("₹${(amount / 1000).toInt()}k", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = targetText.toDoubleOrNull() ?: currentTarget
                    onSave(parsed)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("save_budget_target_btn")
            ) {
                Text("Save Target", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        }
    )
}
