package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinanceTransaction
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

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
                    text = "Insights & Analytics",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Holistic financial health, body vitals & productivity metrics auto-synced",
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

        // 2. Financial Health Gauge Widget (Monthly Budget Progress vs Target Arc Speedometer)
        item {
            FinancialHealthGaugeCard(
                monthlySpent = monthlySpent,
                monthlyBudgetTarget = monthlyBudgetTarget,
                transactions = transactions,
                onCustomizeBudget = { showBudgetCustomizerDialog = true }
            )
        }

        // 3. Big Circular Health Score Card
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
            monthlySpent = monthlySpent,
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

private data class SpendingVelocityStatus(
    val label: String,
    val color: Color,
    val bg: Color,
    val score: String,
    val description: String
)

private data class TopSpendingCategoryItem(
    val id: String,
    val name: String,
    val amount: Double,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color,
    val badge: String,
    val explanation: String
)

@Composable
fun SpendingVelocityGaugeCard(
    monthlySpent: Double,
    monthlyBudgetTarget: Double,
    transactions: List<FinanceTransaction> = emptyList(),
    onCustomizeBudget: () -> Unit,
    onQuickAdjustLimit: (Double) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalDaysInMonth = 31
    val elapsedDays = 14
    val remainingDays = (totalDaysInMonth - elapsedDays).coerceAtLeast(1)

    // Dynamic Categorized Spending for current month
    val allCategories = remember(transactions, monthlySpent, monthlyBudgetTarget) {
        val foodTx = transactions.filter { it.amount < 0 && (it.category.contains("Food", ignoreCase = true) || it.category.contains("Dining", ignoreCase = true) || it.category.contains("Restaurant", ignoreCase = true)) }.sumOf { -it.amount }
        val workTx = transactions.filter { it.amount < 0 && (it.category.contains("Subscription", ignoreCase = true) || it.category.contains("Work", ignoreCase = true) || it.category.contains("Software", ignoreCase = true) || it.category.contains("Cloud", ignoreCase = true)) }.sumOf { -it.amount }
        val transitTx = transactions.filter { it.amount < 0 && (it.category.contains("Transit", ignoreCase = true) || it.category.contains("Commute", ignoreCase = true) || it.category.contains("Transport", ignoreCase = true)) }.sumOf { -it.amount }
        val otherTx = transactions.filter {
            it.amount < 0 &&
            !it.category.contains("Food", ignoreCase = true) &&
            !it.category.contains("Dining", ignoreCase = true) &&
            !it.category.contains("Restaurant", ignoreCase = true) &&
            !it.category.contains("Subscription", ignoreCase = true) &&
            !it.category.contains("Work", ignoreCase = true) &&
            !it.category.contains("Software", ignoreCase = true) &&
            !it.category.contains("Cloud", ignoreCase = true) &&
            !it.category.contains("Transit", ignoreCase = true) &&
            !it.category.contains("Commute", ignoreCase = true) &&
            !it.category.contains("Transport", ignoreCase = true)
        }.sumOf { -it.amount }

        val housingAmount = 21000.0
        val foodAmount = 9000.0 + foodTx
        val workAmount = 3500.0 + workTx
        val transitAmount = 1500.0 + transitTx
        val otherAmount = if (otherTx > 0) otherTx else 460.0

        val categories = mutableListOf(
            TopSpendingCategoryItem(
                id = "cat_housing",
                name = "Housing & Utilities",
                amount = housingAmount,
                icon = Icons.Default.Home,
                iconColor = Color(0xFF6366F1),
                iconBg = Color(0xFFEEF2FF),
                badge = "Fixed Essential",
                explanation = "Scheduled rent and utility bills. Fixed anchor of your monthly budget."
            ),
            TopSpendingCategoryItem(
                id = "cat_food",
                name = "Food & Dining",
                amount = foodAmount,
                icon = Icons.Default.Restaurant,
                iconColor = Color(0xFFF59E0B),
                iconBg = Color(0xFFFFFBEB),
                badge = "Variable Spend",
                explanation = "Groceries, daily lunch, and cafe visits. Primary variable driver of current velocity."
            ),
            TopSpendingCategoryItem(
                id = "cat_subscriptions",
                name = "Work Subscriptions",
                amount = workAmount,
                icon = Icons.Default.Devices,
                iconColor = Color(0xFF0288D1),
                iconBg = Color(0xFFE0F2FE),
                badge = "Recurring Tech",
                explanation = "Cloud infrastructure, creative tools, and workspace subscriptions auto-debited monthly."
            ),
            TopSpendingCategoryItem(
                id = "cat_transit",
                name = "Commute & Transit",
                amount = transitAmount,
                icon = Icons.Default.DirectionsTransit,
                iconColor = Color(0xFF10B981),
                iconBg = Color(0xFFE8F5E9),
                badge = "Daily Commute",
                explanation = "Metro smart card transit, ride shares, and daily mobility expenses."
            ),
            TopSpendingCategoryItem(
                id = "cat_other",
                name = "General Discretionary",
                amount = otherAmount,
                icon = Icons.Default.ShoppingBag,
                iconColor = Color(0xFF8B5CF6),
                iconBg = Color(0xFFF5F3FF),
                badge = "Miscellaneous",
                explanation = "Uncategorized retail and incidental expenses."
            )
        )

        categories.sortedByDescending { it.amount }
    }

    val topCategories = remember(allCategories) { allCategories.take(3) }
    var showSpendingBreakdownModal by remember { mutableStateOf(false) }

    // Base velocities & metrics
    val targetDailySpend = if (totalDaysInMonth > 0) monthlyBudgetTarget / totalDaysInMonth else 1.0
    val actualDailySpend = if (elapsedDays > 0) monthlySpent / elapsedDays else 0.0
    val actualVelocityRatio = if (targetDailySpend > 0) (actualDailySpend / targetDailySpend).toFloat() else 1f
    val safeDailyRemaining = if (remainingDays > 0) (monthlyBudgetTarget - monthlySpent).coerceAtLeast(0.0) / remainingDays else 0.0
    val spendRatio = if (monthlyBudgetTarget > 0) (monthlySpent / monthlyBudgetTarget).toFloat() else 0f
    val timeElapsedRatio = elapsedDays.toFloat() / totalDaysInMonth.toFloat()

    // Interactive states
    var isSimulationActive by remember { mutableStateOf(false) }
    var simulatedDailySpend by remember(actualDailySpend) { mutableDoubleStateOf(actualDailySpend) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Velocity Speedometer, 1: Runway & Limit

    val currentDailySpend = if (isSimulationActive) simulatedDailySpend else actualDailySpend
    val currentVelocityRatio = if (targetDailySpend > 0) (currentDailySpend / targetDailySpend).toFloat() else 1f

    // Projections
    val projectedTotalMonthEnd = monthlySpent + (currentDailySpend * remainingDays)
    val projectedVariance = monthlyBudgetTarget - projectedTotalMonthEnd
    val exhaustionDaysRemaining = if (currentDailySpend > 0) {
        ((monthlyBudgetTarget - monthlySpent).coerceAtLeast(0.0) / currentDailySpend).roundToInt()
    } else totalDaysInMonth
    val exhaustionCalendarDay = (elapsedDays + exhaustionDaysRemaining).coerceAtMost(totalDaysInMonth)

    // Dynamic Health Status
    val status = when {
        currentVelocityRatio <= 0.85f -> SpendingVelocityStatus(
            label = "Frugal Cruise",
            color = Color(0xFF10B981),
            bg = Color(0xFFE8F5E9),
            score = "94/100 • Surplus Pace",
            description = "Spending velocity is 15% below uniform limit. Projected month-end surplus of ₹${String.format(Locale.getDefault(), "%,.0f", projectedVariance.coerceAtLeast(0.0))}."
        )
        currentVelocityRatio <= 1.05f -> SpendingVelocityStatus(
            label = "Target Sustainable",
            color = Color(0xFF0288D1),
            bg = Color(0xFFE0F2FE),
            score = "88/100 • Balanced Pace",
            description = "Spending pace is right on track with the ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)} limit. Expected month-end variance is negligible."
        )
        currentVelocityRatio <= 1.30f -> SpendingVelocityStatus(
            label = "Caution Pace",
            color = Color(0xFFF59E0B),
            bg = Color(0xFFFFF8E1),
            score = "72/100 • Accelerated",
            description = "Pacing ${(currentVelocityRatio * 100 - 100).toInt()}% above target velocity. Reduce daily expenses to ₹${String.format(Locale.getDefault(), "%,.0f", safeDailyRemaining)} to avoid deficit."
        )
        else -> SpendingVelocityStatus(
            label = "Budget Overdrive",
            color = Color(0xFFEF4444),
            bg = Color(0xFFFFEBEE),
            score = "46/100 • Rapid Burn",
            description = "At this burn velocity, the budget limit will be exhausted on Day $exhaustionCalendarDay (${totalDaysInMonth - exhaustionCalendarDay} days early). Projected deficit: ₹${String.format(Locale.getDefault(), "%,.0f", -projectedVariance)}."
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("financial_health_gauge_widget")
            .testTag("financial_health_gauge")
            .testTag("financial_health_card")
            .testTag("spending_velocity_gauge_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
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
                            .clip(CircleShape)
                            .background(status.bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Financial Health Speedometer",
                            tint = status.color,
                            modifier = Modifier.size(20.dp)
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
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = status.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = status.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Spending Velocity • ₹${String.format(Locale.getDefault(), "%,.0f", monthlySpent)} of ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)} limit",
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
                    modifier = Modifier
                        .testTag("customize_budget_target_btn")
                        .testTag("configure_budget_target_btn")
                        .testTag("set_monthly_budget_target_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Set Monthly Budget Target",
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Set Target",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tabs: Budget Progress Arc vs Velocity Speedometer
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceContainerLow,
                contentColor = Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Budget Progress Arc", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Velocity Dial & Sim", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // ==========================================
                // 1. BUDGET PROGRESS ARC & SPEEDOMETER GAUGE
                // ==========================================
                val clampedSpend = spendRatio.coerceIn(0f, 1.25f)
                val targetProgressAngle = 150f + (clampedSpend / 1.0f).coerceIn(0f, 1.15f) * 240f
                val animatedNeedleAngle by animateFloatAsState(
                    targetValue = targetProgressAngle,
                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "budget_needle_angle"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val trackColor = SurfaceContainerHighest
                    val progressBrush = Brush.horizontalGradient(
                        colors = if (spendRatio <= 0.70f) {
                            listOf(Color(0xFF06B6D4), Color(0xFF10B981))
                        } else if (spendRatio <= 0.90f) {
                            listOf(Color(0xFF10B981), Color(0xFFF59E0B))
                        } else {
                            listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                        }
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .testTag("budget_progress_arc_canvas")
                    ) {
                        val strokeWidth = 15.dp.toPx()
                        val arcRadius = min(size.width * 0.42f, size.height * 0.78f)
                        val arcSize = arcRadius * 2f
                        val center = Offset(size.width / 2f, size.height * 0.72f)
                        val topLeft = Offset(center.x - arcRadius, center.y - arcRadius)

                        // 1. Background Arc (240 deg: from 150 to 390)
                        drawArc(
                            color = trackColor,
                            startAngle = 150f,
                            sweepAngle = 240f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 2. Spending Progress Arc (Dynamic gradient fill)
                        val activeSweep = 240f * clampedSpend.coerceAtMost(1f)
                        if (activeSweep > 0.5f) {
                            drawArc(
                                brush = progressBrush,
                                startAngle = 150f,
                                sweepAngle = activeSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        // 3. Ticks at 0%, 25%, 50%, 75%, 100%
                        val progressTicks = listOf(0.0f, 0.25f, 0.50f, 0.75f, 1.0f)
                        for (tick in progressTicks) {
                            val ang = 150f + tick * 240f
                            val r = Math.toRadians(ang.toDouble())
                            val p1 = center + Offset(cos(r).toFloat() * (arcRadius - strokeWidth * 0.65f), sin(r).toFloat() * (arcRadius - strokeWidth * 0.65f))
                            val p2 = center + Offset(cos(r).toFloat() * (arcRadius + strokeWidth * 0.65f), sin(r).toFloat() * (arcRadius + strokeWidth * 0.65f))
                            drawLine(
                                color = if (tick == 1.0f) Color(0xFFEF4444) else Color.White.copy(alpha = 0.9f),
                                start = p1,
                                end = p2,
                                strokeWidth = (if (tick == 1.0f) 3.5f else 2f).dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // 4. Time Elapsed Benchmark Marker (Day 14/31 = 45.2%)
                        val timeAngle = 150f + 240f * timeElapsedRatio.coerceIn(0f, 1f)
                        val timeRad = Math.toRadians(timeAngle.toDouble())
                        val mInner = center + Offset(cos(timeRad).toFloat() * (arcRadius - strokeWidth * 0.95f), sin(timeRad).toFloat() * (arcRadius - strokeWidth * 0.95f))
                        val mOuter = center + Offset(cos(timeRad).toFloat() * (arcRadius + strokeWidth * 0.95f), sin(timeRad).toFloat() * (arcRadius + strokeWidth * 0.95f))
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = mInner,
                            end = mOuter,
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // 5. Speedometer Needle pointing to budget progress
                        val needleRad = Math.toRadians(animatedNeedleAngle.toDouble())
                        val needleLen = arcRadius * 0.82f
                        val tip = center + Offset(cos(needleRad).toFloat() * needleLen, sin(needleRad).toFloat() * needleLen)
                        val perpRad = needleRad + PI / 2.0
                        val baseWidth = 5.dp.toPx()
                        val baseL = center + Offset(cos(perpRad).toFloat() * baseWidth, sin(perpRad).toFloat() * baseWidth)
                        val baseR = center + Offset(-cos(perpRad).toFloat() * baseWidth, -sin(perpRad).toFloat() * baseWidth)

                        val needlePath = Path().apply {
                            moveTo(baseL.x, baseL.y)
                            lineTo(tip.x, tip.y)
                            lineTo(baseR.x, baseR.y)
                            close()
                        }
                        drawPath(needlePath, color = status.color)

                        // 6. Metallic Center Pivot Hub
                        drawCircle(color = Color(0xFF1E293B), radius = 10.dp.toPx(), center = center)
                        drawCircle(color = status.color, radius = 6.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = center)
                    }

                    // Center Digital Readout
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(status.bg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${(spendRatio * 100).toInt()}% USED vs ${(timeElapsedRatio * 100).toInt()}% TIME",
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
                            text = "of ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)} target limit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Dial Legend Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 8.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Needle: ${(spendRatio * 100).toInt()}% • Notch: Day $elapsedDays of $totalDaysInMonth (${(timeElapsedRatio * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            } else {
                // ==========================================
                // 2. INTERACTIVE VELOCITY DIAL & SIMULATOR
                // ==========================================
                val clampedVelocity = currentVelocityRatio.coerceIn(0f, 2.2f)
                val targetAngle = 150f + (clampedVelocity / 2.2f) * 240f
                val animatedAngle by animateFloatAsState(
                    targetValue = targetAngle,
                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "needle_angle"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val trackBg = SurfaceContainerHighest
                    val greenColor = Color(0xFF10B981)
                    val cyanColor = Color(0xFF06B6D4)
                    val amberColor = Color(0xFFF59E0B)
                    val redColor = Color(0xFFEF4444)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("interactive_velocity_canvas")
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val cx = size.width / 2f
                                        val cy = size.height * 0.72f
                                        val dx = offset.x - cx
                                        val dy = offset.y - cy
                                        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (deg < 0) deg += 360f
                                        val fraction = when {
                                            deg >= 150f -> (deg - 150f) / 240f
                                            deg <= 40f -> (deg + 210f) / 240f
                                            deg < 95f -> 1f
                                            else -> 0f
                                        }.coerceIn(0f, 1f)
                                        val newVelocity = fraction * 2.2f
                                        simulatedDailySpend = (newVelocity * targetDailySpend).coerceIn(300.0, 6000.0)
                                        isSimulationActive = true
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val cx = size.width / 2f
                                        val cy = size.height * 0.72f
                                        val dx = change.position.x - cx
                                        val dy = change.position.y - cy
                                        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (deg < 0) deg += 360f
                                        val fraction = when {
                                            deg >= 150f -> (deg - 150f) / 240f
                                            deg <= 40f -> (deg + 210f) / 240f
                                            deg < 95f -> 1f
                                            else -> 0f
                                        }.coerceIn(0f, 1f)
                                        val newVelocity = fraction * 2.2f
                                        simulatedDailySpend = (newVelocity * targetDailySpend).coerceIn(300.0, 6000.0)
                                        isSimulationActive = true
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val cx = size.width / 2f
                                    val cy = size.height * 0.72f
                                    val dx = offset.x - cx
                                    val dy = offset.y - cy
                                    var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (deg < 0) deg += 360f
                                    val fraction = when {
                                        deg >= 150f -> (deg - 150f) / 240f
                                        deg <= 40f -> (deg + 210f) / 240f
                                        deg < 95f -> 1f
                                        else -> 0f
                                    }.coerceIn(0f, 1f)
                                    val newVelocity = fraction * 2.2f
                                    simulatedDailySpend = (newVelocity * targetDailySpend).coerceIn(300.0, 6000.0)
                                    isSimulationActive = true
                                }
                            }
                    ) {
                        val strokeWidth = 14.dp.toPx()
                        val arcRadius = min(size.width * 0.42f, size.height * 0.78f)
                        val arcSize = arcRadius * 2f
                        val center = Offset(size.width / 2f, size.height * 0.72f)
                        val topLeft = Offset(center.x - arcRadius, center.y - arcRadius)

                        // 1. Background full track
                        drawArc(
                            color = trackBg,
                            startAngle = 150f,
                            sweepAngle = 240f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 2. Multi-Zone Colored Velocity Segments
                        // Zone 1: Safe Frugal (0.0x - 0.85x)
                        val z1Sweep = 240f * (0.85f / 2.2f)
                        drawArc(
                            color = greenColor.copy(alpha = 0.75f),
                            startAngle = 150f,
                            sweepAngle = z1Sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        // Zone 2: Target Sustainable (0.85x - 1.05x)
                        val z2Start = 150f + z1Sweep
                        val z2Sweep = 240f * (0.20f / 2.2f)
                        drawArc(
                            color = cyanColor.copy(alpha = 0.85f),
                            startAngle = z2Start,
                            sweepAngle = z2Sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        // Zone 3: Caution Pace (1.05x - 1.30x)
                        val z3Start = z2Start + z2Sweep
                        val z3Sweep = 240f * (0.25f / 2.2f)
                        drawArc(
                            color = amberColor.copy(alpha = 0.85f),
                            startAngle = z3Start,
                            sweepAngle = z3Sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )

                        // Zone 4: Overdrive Danger (1.30x - 2.2x)
                        val z4Start = z3Start + z3Sweep
                        val z4Sweep = 240f - (z1Sweep + z2Sweep + z3Sweep)
                        drawArc(
                            color = redColor.copy(alpha = 0.85f),
                            startAngle = z4Start,
                            sweepAngle = z4Sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 3. Ticks & Benchmark Notch for 1.0x Target Pace
                        val targetTickAngle = 150f + (1.0f / 2.2f) * 240f
                        val targetRad = Math.toRadians(targetTickAngle.toDouble())
                        val tInner = center + Offset(cos(targetRad).toFloat() * (arcRadius - strokeWidth * 0.85f), sin(targetRad).toFloat() * (arcRadius - strokeWidth * 0.85f))
                        val tOuter = center + Offset(cos(targetRad).toFloat() * (arcRadius + strokeWidth * 0.85f), sin(targetRad).toFloat() * (arcRadius + strokeWidth * 0.85f))
                        drawLine(
                            color = Color.White,
                            start = tInner,
                            end = tOuter,
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Minor tick marks (0.0x, 0.5x, 1.5x, 2.0x)
                        val tickMultipliers = listOf(0.0f, 0.5f, 1.5f, 2.0f)
                        for (tm in tickMultipliers) {
                            val ang = 150f + (tm / 2.2f) * 240f
                            val r = Math.toRadians(ang.toDouble())
                            val p1 = center + Offset(cos(r).toFloat() * (arcRadius - strokeWidth * 0.5f), sin(r).toFloat() * (arcRadius - strokeWidth * 0.5f))
                            val p2 = center + Offset(cos(r).toFloat() * (arcRadius + strokeWidth * 0.5f), sin(r).toFloat() * (arcRadius + strokeWidth * 0.5f))
                            drawLine(
                                color = Color.White.copy(alpha = 0.8f),
                                start = p1,
                                end = p2,
                                strokeWidth = 1.8.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // 4. Sleek Tapered Speedometer Needle
                        val needleRad = Math.toRadians(animatedAngle.toDouble())
                        val needleLen = arcRadius * 0.82f
                        val tip = center + Offset(cos(needleRad).toFloat() * needleLen, sin(needleRad).toFloat() * needleLen)
                        val perpRad = needleRad + PI / 2.0
                        val baseWidth = 5.dp.toPx()
                        val baseL = center + Offset(cos(perpRad).toFloat() * baseWidth, sin(perpRad).toFloat() * baseWidth)
                        val baseR = center + Offset(-cos(perpRad).toFloat() * baseWidth, -sin(perpRad).toFloat() * baseWidth)

                        val needlePath = Path().apply {
                            moveTo(baseL.x, baseL.y)
                            lineTo(tip.x, tip.y)
                            lineTo(baseR.x, baseR.y)
                            close()
                        }
                        drawPath(needlePath, color = status.color)

                        // 5. Metallic Pivot Hub
                        drawCircle(color = Color(0xFF1E293B), radius = 10.dp.toPx(), center = center)
                        drawCircle(color = status.color, radius = 6.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = center)
                    }

                    // Speedometer Center Digital Readout
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = 22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.2f", currentVelocityRatio)}x",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = status.color,
                                    fontSize = 26.sp
                                )
                            )
                            if (isSimulationActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "SIM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", currentDailySpend)} / day",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 13.sp
                            )
                        )

                        Text(
                            text = "Target: ₹${String.format(Locale.getDefault(), "%,.0f", targetDailySpend)}/day (1.0x)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Dial Legend Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 8.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isSimulationActive) "Drag dial to simulate • Tap Reset to restore" else "Touch & drag dial to simulate spending velocity",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 3. INTERACTIVE SIMULATION PRESET CHIPS
            // ==========================================
            Text(
                text = "Interactive What-If Simulation",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Actual Pace Preset
                FilterChip(
                    selected = !isSimulationActive,
                    onClick = {
                        isSimulationActive = false
                        simulatedDailySpend = actualDailySpend
                    },
                    label = { Text("⚡ Actual (${String.format(Locale.getDefault(), "%.1f", actualVelocityRatio)}x)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                // Safe Pace Preset
                FilterChip(
                    selected = isSimulationActive && kotlin.math.abs(simulatedDailySpend - safeDailyRemaining) < 50,
                    onClick = {
                        isSimulationActive = true
                        simulatedDailySpend = safeDailyRemaining
                    },
                    label = { Text("🎯 Safe Target", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                // Weekend Surge Preset (+₹3.5k)
                FilterChip(
                    selected = isSimulationActive && kotlin.math.abs(simulatedDailySpend - (actualDailySpend + 700)) < 50,
                    onClick = {
                        isSimulationActive = true
                        simulatedDailySpend = actualDailySpend + 700.0
                    },
                    label = { Text("🏖️ Surge (+₹700)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                // Frugal Week Preset (₹800/d)
                FilterChip(
                    selected = isSimulationActive && kotlin.math.abs(simulatedDailySpend - 800.0) < 50,
                    onClick = {
                        isSimulationActive = true
                        simulatedDailySpend = 800.0
                    },
                    label = { Text("🛡️ Frugal (₹800)", fontSize = 10.sp) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // Interactive Fine-Tuning Burn Slider
            if (isSimulationActive) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulated Daily Burn: ₹${String.format(Locale.getDefault(), "%,.0f", simulatedDailySpend)}/day",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Primary)
                        )
                        TextButton(
                            onClick = {
                                isSimulationActive = false
                                simulatedDailySpend = actualDailySpend
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Reset", style = MaterialTheme.typography.labelSmall.copy(color = Primary, fontSize = 10.sp))
                        }
                    }

                    Slider(
                        value = simulatedDailySpend.toFloat(),
                        onValueChange = {
                            simulatedDailySpend = it.toDouble()
                            isSimulationActive = true
                        },
                        valueRange = 400f..4500f,
                        steps = 40,
                        colors = SliderDefaults.colors(thumbColor = status.color, activeTrackColor = status.color),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 4. THREE-METRIC SUMMARY PILLS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Monthly Limit Card
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
                            text = "Monthly Limit",
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

                // Projected Month-End / Remaining Buffer
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
                            text = if (isSimulationActive) "Projected Spend" else "Remaining",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = if (isSimulationActive) "₹${String.format(Locale.getDefault(), "%,.0f", projectedTotalMonthEnd)}"
                                   else "₹${String.format(Locale.getDefault(), "%,.0f", (monthlyBudgetTarget - monthlySpent).coerceAtLeast(0.0))}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = status.color,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (projectedVariance >= 0) "Surplus +₹${String.format(Locale.getDefault(), "%,.0f", projectedVariance)}"
                                   else "Deficit -₹${String.format(Locale.getDefault(), "%,.0f", -projectedVariance)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = status.color,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Safe Daily Allowance / Runway Days
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
                            text = "Safe Allowance",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", safeDailyRemaining)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1),
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Per day ($remainingDays d left)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 5. AI FINANCIAL HEALTH GUIDANCE CALLOUT
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHighest.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_spending_guidance_card")
                    .testTag("daily_spending_guidance")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(status.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = status.color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Health Score: ${status.score}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = status.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 6. EXPANDABLE TOP SPENDING CATEGORIES SECTION
            // ==========================================
            var isTopCategoriesExpanded by remember { mutableStateOf(false) }
            val topThreeTotal = topCategories.sumOf { it.amount }
            val sharePercent = if (monthlySpent > 0) ((topThreeTotal / monthlySpent) * 100).toInt() else 0

            val chevronRotation by animateFloatAsState(
                targetValue = if (isTopCategoriesExpanded) 180f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "top_categories_chevron_rotation"
            )

            HorizontalDivider(
                color = SurfaceContainerHighest.copy(alpha = 0.8f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Section Header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isTopCategoriesExpanded) SurfaceContainerLow else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isTopCategoriesExpanded = !isTopCategoriesExpanded }
                    .testTag("expandable_top_spending_categories_header")
                    .testTag("financial_health_top_categories_expand_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = "Top Categories Icon",
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Top 3 Spending Categories",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface,
                                        fontSize = 13.sp
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "This Month",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "₹${String.format(Locale.getDefault(), "%,.0f", topThreeTotal)} total ($sharePercent% of spend) • Tap to ${if (isTopCategoriesExpanded) "collapse" else "expand list"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { isTopCategoriesExpanded = !isTopCategoriesExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = if (isTopCategoriesExpanded) "Collapse top categories" else "Expand top categories",
                            tint = OnSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isTopCategoriesExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ),
                exit = shrinkVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showSpendingBreakdownModal = true }
                        .testTag("top_spending_categories_list")
                        .testTag("financial_health_top_categories_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Top spending drivers for the current month explaining budget usage (tap item or button for full modal breakdown):",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    topCategories.forEachIndexed { index, cat ->
                        val percentOfLimit = if (monthlyBudgetTarget > 0) ((cat.amount / monthlyBudgetTarget) * 100).toFloat() else 0f
                        val limitProgress = if (monthlyBudgetTarget > 0) (cat.amount / monthlyBudgetTarget).toFloat().coerceIn(0f, 1f) else 0f

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSpendingBreakdownModal = true }
                                .testTag("top_category_item_$index")
                                .testTag("financial_health_category_item_$index")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Rank indicator
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(cat.iconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = cat.iconColor,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }

                                        // Category Icon
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(cat.iconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = cat.icon,
                                                contentDescription = null,
                                                tint = cat.iconColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = cat.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = OnSurface,
                                                        fontSize = 12.sp
                                                    )
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(cat.iconBg)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = cat.badge,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = cat.iconColor,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 8.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Amount & percentage
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${String.format(Locale.getDefault(), "%,.0f", cat.amount)}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = OnSurface,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${String.format(Locale.getDefault(), "%.1f", percentOfLimit)}% of limit",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = cat.iconColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Progress Bar towards budget
                                LinearProgressIndicator(
                                    progress = { limitProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape),
                                    color = cat.iconColor,
                                    trackColor = SurfaceContainerHighest
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Explanation sentence
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = cat.iconColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = cat.explanation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Explanatory summary footer
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHighest.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSpendingBreakdownModal = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Top 3 categories total ₹${String.format(Locale.getDefault(), "%,.0f", topThreeTotal)} ($sharePercent% of ₹${String.format(Locale.getDefault(), "%,.0f", monthlySpent)} MTD spend).",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Interactive banner to open full modal view
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Primary.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showSpendingBreakdownModal = true }
                            .testTag("open_spending_breakdown_modal_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PieChart,
                                    contentDescription = "Breakdown Icon",
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "View Detailed Monthly Breakdown",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Primary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "All Categories",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Primary.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Open Breakdown",
                                    tint = Primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSpendingBreakdownModal) {
        MonthlySpendingBreakdownModal(
            monthlySpent = monthlySpent,
            monthlyBudgetTarget = monthlyBudgetTarget,
            allCategories = allCategories,
            onDismiss = { showSpendingBreakdownModal = false },
            onCustomizeBudget = {
                showSpendingBreakdownModal = false
                onCustomizeBudget()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthlySpendingBreakdownModal(
    monthlySpent: Double,
    monthlyBudgetTarget: Double,
    allCategories: List<TopSpendingCategoryItem>,
    onDismiss: () -> Unit,
    onCustomizeBudget: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("monthly_spending_breakdown_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Modal Title & Close Button
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
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Spending Breakdown Icon",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Monthly Spending Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Detailed Category Distribution & Limit Progress",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_spending_breakdown_modal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Spending Breakdown Modal",
                        tint = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Card
            val overallSpentRatio = if (monthlyBudgetTarget > 0) (monthlySpent / monthlyBudgetTarget).toFloat().coerceIn(0f, 1f) else 0f
            val remainingBudget = (monthlyBudgetTarget - monthlySpent).coerceAtLeast(0.0)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Month Total Spend",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "₹${String.format(Locale.getDefault(), "%,.0f", monthlySpent)}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "/ ₹${String.format(Locale.getDefault(), "%,.0f", monthlyBudgetTarget)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = OnSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (overallSpentRatio <= 0.85f) Color(0xFFE8F5E9) else Color(0xFFFFF7ED))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${(overallSpentRatio * 100).toInt()}% of limit",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (overallSpentRatio <= 0.85f) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { overallSpentRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (overallSpentRatio <= 0.85f) Color(0xFF10B981) else Color(0xFFF59E0B),
                        trackColor = SurfaceContainerHighest
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Remaining: ₹${String.format(Locale.getDefault(), "%,.0f", remainingBudget)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "Day 14 of 31 • Mid-month pace",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Category Breakdown & Limit Progress",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Category List with Progress Bars
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .testTag("modal_category_breakdown_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allCategories.size) { index ->
                    val cat = allCategories[index]
                    val percentOfLimit = if (monthlyBudgetTarget > 0) ((cat.amount / monthlyBudgetTarget) * 100).toFloat() else 0f
                    val percentOfTotalSpend = if (monthlySpent > 0) ((cat.amount / monthlySpent) * 100).toFloat() else 0f
                    val progress = if (monthlyBudgetTarget > 0) (cat.amount / monthlyBudgetTarget).toFloat().coerceIn(0f, 1f) else 0f

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("modal_category_item_$index")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Row 1: Icon, Title, Badge, Amount
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
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(cat.iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            tint = cat.iconColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = cat.name,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = OnSurface,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(cat.iconBg)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = cat.badge,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = cat.iconColor,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${String.format(Locale.getDefault(), "%.1f", percentOfTotalSpend)}% of month spend",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${String.format(Locale.getDefault(), "%,.0f", cat.amount)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = "${String.format(Locale.getDefault(), "%.1f", percentOfLimit)}% of limit",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = cat.iconColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Individual Progress Bar for this category
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .testTag("modal_category_progress_$index"),
                                color = cat.iconColor,
                                trackColor = SurfaceContainerHighest
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Explanation
                            Text(
                                text = cat.explanation,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCustomizeBudget,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Adjust Target",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Adjust Limit")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun FinancialHealthGaugeCard(
    monthlySpent: Double,
    monthlyBudgetTarget: Double,
    transactions: List<FinanceTransaction> = emptyList(),
    onCustomizeBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    SpendingVelocityGaugeCard(
        monthlySpent = monthlySpent,
        monthlyBudgetTarget = monthlyBudgetTarget,
        transactions = transactions,
        onCustomizeBudget = onCustomizeBudget,
        modifier = modifier
    )
}

@Composable
fun CustomizeMonthlyBudgetDialog(
    currentTarget: Double,
    monthlySpent: Double = 35460.0,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var targetText by remember { mutableStateOf(String.format(Locale.getDefault(), "%.0f", currentTarget)) }
    val quickPresets = listOf(40000.0, 50000.0, 60000.0, 75000.0, 80000.0, 100000.0)

    val parsedTarget = (targetText.toDoubleOrNull() ?: currentTarget).coerceAtLeast(1000.0)
    val remainingDays = 17
    val previewDailyAllowance = if (remainingDays > 0) (parsedTarget - monthlySpent).coerceAtLeast(0.0) / remainingDays else 0.0
    val previewDailyPace = parsedTarget / 31.0
    val previewUtilization = ((monthlySpent / parsedTarget) * 100).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .testTag("budget_target_modal")
            .testTag("monthly_budget_configuration_modal"),
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
                    text = "Set your desired spending ceiling for this month. The Financial Health arc gauge and daily spending guidance will update dynamically.",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )

                // Input field + quick stepper buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cur = targetText.toDoubleOrNull() ?: currentTarget
                            val newVal = (cur - 5000.0).coerceAtLeast(1000.0)
                            targetText = String.format(Locale.getDefault(), "%.0f", newVal)
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("-5k", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Monthly Target") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("budget_target_input")
                    )

                    OutlinedButton(
                        onClick = {
                            val cur = targetText.toDoubleOrNull() ?: currentTarget
                            val newVal = cur + 5000.0
                            targetText = String.format(Locale.getDefault(), "%.0f", newVal)
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("+5k", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Dynamic Live Preview Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Live Guidance Preview",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                fontSize = 10.sp
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Safe Daily Allowance:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = OnSurfaceVariant))
                            Text("₹${String.format(Locale.getDefault(), "%,.0f", previewDailyAllowance)}/day", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0288D1), fontSize = 11.sp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Target Daily Pace:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = OnSurfaceVariant))
                            Text("₹${String.format(Locale.getDefault(), "%,.0f", previewDailyPace)}/day", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 11.sp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Projected Utilization:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = OnSurfaceVariant))
                            Text(
                                text = "$previewUtilization% of target",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (previewUtilization <= 85) Color(0xFF10B981) else if (previewUtilization <= 100) Color(0xFFF59E0B) else Color(0xFFEF4444),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

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
                modifier = Modifier
                    .testTag("save_budget_target_btn")
                    .testTag("confirm_budget_target_btn")
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
