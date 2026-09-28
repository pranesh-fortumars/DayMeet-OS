package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import com.example.model.FinanceTransaction
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Mathematical model representing a calculated SVG Arc path command.
 * Conforms strictly to W3C SVG 1.1 / 2.0 path grammar:
 * "M {startX} {startY} A {rx} {ry} 0 {largeArcFlag} 1 {endX} {endY}"
 */
data class SvgArcData(
    val pathString: String,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val startAngleDeg: Float,
    val sweepAngleDeg: Float,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val isLargeArc: Boolean
)

/**
 * Builds a valid SVG Arc path data string conforming to SVG specification.
 * Starts with MoveTo (M startX startY) followed by ArcTo (A rx ry 0 largeArcFlag sweepFlag endX endY).
 */
fun buildSvgArcPathData(
    cx: Float,
    cy: Float,
    radius: Float,
    startAngleDeg: Float,
    sweepAngleDeg: Float
): SvgArcData {
    val clampedSweep = sweepAngleDeg.coerceIn(0.1f, 359.99f)
    val startRad = Math.toRadians(startAngleDeg.toDouble())
    val endRad = Math.toRadians((startAngleDeg + clampedSweep).toDouble())

    val x1 = cx + radius * cos(startRad).toFloat()
    val y1 = cy + radius * sin(startRad).toFloat()
    val x2 = cx + radius * cos(endRad).toFloat()
    val y2 = cy + radius * sin(endRad).toFloat()

    val largeArc = clampedSweep > 180f
    val largeArcFlag = if (largeArc) 1 else 0
    val sweepFlag = 1 // Clockwise direction

    // Precise SVG Path string: M x1 y1 A rx ry x-axis-rotation large-arc sweep endX endY
    val pathString = String.format(
        Locale.US,
        "M %.2f %.2f A %.2f %.2f 0 %d %d %.2f %.2f",
        x1, y1, radius, radius, largeArcFlag, sweepFlag, x2, y2
    )

    return SvgArcData(
        pathString = pathString,
        centerX = cx,
        centerY = cy,
        radius = radius,
        startAngleDeg = startAngleDeg,
        sweepAngleDeg = clampedSweep,
        startX = x1,
        startY = y1,
        endX = x2,
        endY = y2,
        isLargeArc = largeArc
    )
}

/**
 * Safely parses SVG path data string into an Android Compose Path.
 * Uses AndroidX PathParser with graceful Compose Path fallback.
 */
fun parseSvgArcToComposePath(svgPathData: String, fallbackBlock: () -> Path): Path {
    return try {
        val androidPath = PathParser.createPathFromPathData(svgPathData)
        if (androidPath != null && !androidPath.isEmpty) {
            androidPath.asComposePath()
        } else {
            fallbackBlock()
        }
    } catch (_: Throwable) {
        fallbackBlock()
    }
}

/**
 * Financial Health Gauge Status Descriptor
 */
data class FinancialHealthGaugeStatus(
    val label: String,
    val subtitle: String,
    val color: Color,
    val containerColor: Color,
    val icon: ImageVector,
    val efficiencyScore: Int
)

/**
 * Circular 'Financial Health' Gauge Widget
 *
 * Uses an SVG arc path to visualize the current month's spending against the target budget.
 * Provides real-time percentage progress, surplus/deficit indicators, time-elapsed benchmark,
 * interactive simulation mode, and an expandable SVG Arc Inspector showing the underlying
 * vector path mathematics.
 */
@Composable
fun CircularFinancialHealthGaugeCard(
    monthlySpent: Double,
    monthlyBudgetTarget: Double,
    transactions: List<FinanceTransaction> = emptyList(),
    onCustomizeBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showInspector by remember { mutableStateOf(false) }
    var isSimulationMode by remember { mutableStateOf(false) }
    var simulatedSpent by remember(monthlySpent) { mutableDoubleStateOf(monthlySpent) }
    var copyNotice by remember { mutableStateOf<String?>(null) }

    // Dismiss copy notice after a brief delay
    LaunchedEffect(copyNotice) {
        if (copyNotice != null) {
            kotlinx.coroutines.delay(2200)
            copyNotice = null
        }
    }

    val activeSpent = if (isSimulationMode) simulatedSpent else monthlySpent
    val budgetTarget = if (monthlyBudgetTarget > 0.0) monthlyBudgetTarget else 48000.0

    val spentRatio = (activeSpent / budgetTarget).toFloat()
    val spentPercentage = (spentRatio * 100).roundToInt()
    val remainingAmount = budgetTarget - activeSpent
    val isOverBudget = activeSpent > budgetTarget
    val deficitAmount = (activeSpent - budgetTarget).coerceAtLeast(0.0)

    // Month calendar timing (e.g. Day 14 of 31)
    val totalDaysInMonth = 31
    val elapsedDays = 14
    val remainingDays = (totalDaysInMonth - elapsedDays).coerceAtLeast(1)
    val timeElapsedRatio = elapsedDays.toFloat() / totalDaysInMonth.toFloat()
    val safeDailySpend = if (remainingDays > 0) (budgetTarget - activeSpent).coerceAtLeast(0.0) / remainingDays else 0.0

    // Arc Geometry Configuration: 280-degree circular ring from 130° to 410°
    val arcStartAngle = 130f
    val totalArcSweep = 280f

    // Animated sweep angle for smooth transitions
    val targetSweep = (totalArcSweep * spentRatio.coerceIn(0f, 1.25f))
    val animatedSweep by animateFloatAsState(
        targetValue = targetSweep.coerceAtLeast(0.1f),
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "svg_arc_sweep"
    )

    // Dynamic Health Status Evaluation
    val healthStatus = when {
        spentRatio <= 0.70f -> FinancialHealthGaugeStatus(
            label = "Excellent Surplus",
            subtitle = "Spending well below target ceiling",
            color = Color(0xFF10B981),
            containerColor = Color(0xFFECFDF5),
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            efficiencyScore = 96
        )
        spentRatio <= 0.88f -> FinancialHealthGaugeStatus(
            label = "Sustainable Pace",
            subtitle = "Right on track with monthly plan",
            color = Color(0xFF0288D1),
            containerColor = Color(0xFFE0F2FE),
            icon = Icons.Default.CheckCircle,
            efficiencyScore = 88
        )
        spentRatio <= 1.0f -> FinancialHealthGaugeStatus(
            label = "Caution Limit",
            subtitle = "Approaching maximum budget ceiling",
            color = Color(0xFFF59E0B),
            containerColor = Color(0xFFFFFBEB),
            icon = Icons.Default.WarningAmber,
            efficiencyScore = 71
        )
        else -> FinancialHealthGaugeStatus(
            label = "Budget Exceeded",
            subtitle = "Spending has surpassed monthly ceiling",
            color = Color(0xFFEF4444),
            containerColor = Color(0xFFFEF2F2),
            icon = Icons.Default.ErrorOutline,
            efficiencyScore = 42
        )
    }

    // Over-budget pulsing alert animation
    val infiniteTransition = rememberInfiniteTransition(label = "deficit_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Store calculated SVG Arc strings for Inspector
    var currentTrackSvgString by remember { mutableStateOf("") }
    var currentProgressSvgString by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isOverBudget) {
            BorderStroke(1.5.dp, Color(0xFFEF4444).copy(alpha = pulseAlpha * 0.85f))
        } else {
            BorderStroke(1.dp, Primary.copy(alpha = 0.12f))
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag("circular_financial_health_gauge")
            .testTag("financial_health_gauge")
            .testTag("financial_health_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(healthStatus.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOverBudget) Icons.Default.Warning else Icons.Default.PieChart,
                            contentDescription = "Financial Health",
                            tint = healthStatus.color,
                            modifier = Modifier.size(22.dp)
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
                                    color = OnSurface,
                                    fontSize = 17.sp
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(healthStatus.containerColor)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = healthStatus.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = healthStatus.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Circular SVG Arc • Month Outflow vs Target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Customize Budget Button
                IconButton(
                    onClick = onCustomizeBudget,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .testTag("customize_budget_target_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize Budget Target",
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==============================================================
            // CIRCULAR SVG ARC GAUGE CANVAS
            // ==============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                val trackColor = SurfaceContainerHighest
                val progressBrush = Brush.sweepGradient(
                    colors = if (spentRatio <= 0.70f) {
                        listOf(Color(0xFF06B6D4), Color(0xFF10B981), Color(0xFF10B981))
                    } else if (spentRatio <= 1.0f) {
                        listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFF59E0B))
                    } else {
                        listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFDC2626))
                    }
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .testTag("svg_arc_canvas")
                ) {
                    val strokeWidth = 16.dp.toPx()
                    val center = Offset(size.width / 2f, size.height * 0.48f)
                    val arcRadius = min(size.width * 0.38f, size.height * 0.38f)

                    // 1. Build Background Track SVG Arc (full 280 deg circular arc)
                    val trackArcData = buildSvgArcPathData(
                        cx = center.x,
                        cy = center.y,
                        radius = arcRadius,
                        startAngleDeg = arcStartAngle,
                        sweepAngleDeg = totalArcSweep
                    )
                    currentTrackSvgString = trackArcData.pathString

                    val trackComposePath = parseSvgArcToComposePath(trackArcData.pathString) {
                        Path().apply {
                            arcTo(
                                rect = Rect(
                                    center.x - arcRadius,
                                    center.y - arcRadius,
                                    center.x + arcRadius,
                                    center.y + arcRadius
                                ),
                                startAngleDegrees = arcStartAngle,
                                sweepAngleDegrees = totalArcSweep,
                                forceMoveTo = false
                            )
                        }
                    }

                    drawPath(
                        path = trackComposePath,
                        color = trackColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Build Spending Progress SVG Arc
                    val progressSweep = animatedSweep.coerceIn(0.5f, totalArcSweep * 1.2f)
                    val progressArcData = buildSvgArcPathData(
                        cx = center.x,
                        cy = center.y,
                        radius = arcRadius,
                        startAngleDeg = arcStartAngle,
                        sweepAngleDeg = progressSweep
                    )
                    currentProgressSvgString = progressArcData.pathString

                    val progressComposePath = parseSvgArcToComposePath(progressArcData.pathString) {
                        Path().apply {
                            arcTo(
                                rect = Rect(
                                    center.x - arcRadius,
                                    center.y - arcRadius,
                                    center.x + arcRadius,
                                    center.y + arcRadius
                                ),
                                startAngleDegrees = arcStartAngle,
                                sweepAngleDegrees = progressSweep,
                                forceMoveTo = false
                            )
                        }
                    }

                    drawPath(
                        path = progressComposePath,
                        brush = progressBrush,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 3. Target Limit Tick (100% Budget mark at totalArcSweep)
                    val targetAngleRad = Math.toRadians((arcStartAngle + totalArcSweep).toDouble())
                    val tickInner = center + Offset(
                        cos(targetAngleRad).toFloat() * (arcRadius - strokeWidth * 0.7f),
                        sin(targetAngleRad).toFloat() * (arcRadius - strokeWidth * 0.7f)
                    )
                    val tickOuter = center + Offset(
                        cos(targetAngleRad).toFloat() * (arcRadius + strokeWidth * 0.7f),
                        sin(targetAngleRad).toFloat() * (arcRadius + strokeWidth * 0.7f)
                    )
                    drawLine(
                        color = if (isOverBudget) Color(0xFFEF4444) else Primary,
                        start = tickInner,
                        end = tickOuter,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // 4. Calendar Month Time Elapsed Marker (Day 14/31 pace)
                    val timeAngleRad = Math.toRadians((arcStartAngle + totalArcSweep * timeElapsedRatio).toDouble())
                    val timeInner = center + Offset(
                        cos(timeAngleRad).toFloat() * (arcRadius - strokeWidth * 0.85f),
                        sin(timeAngleRad).toFloat() * (arcRadius - strokeWidth * 0.85f)
                    )
                    val timeOuter = center + Offset(
                        cos(timeAngleRad).toFloat() * (arcRadius + strokeWidth * 0.85f),
                        sin(timeAngleRad).toFloat() * (arcRadius + strokeWidth * 0.85f)
                    )
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = timeInner,
                        end = timeOuter,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // 5. Glowing Head Indicator on the SVG Arc
                    val headX = progressArcData.endX
                    val headY = progressArcData.endY

                    // Outer halo
                    drawCircle(
                        color = healthStatus.color.copy(alpha = 0.28f),
                        radius = strokeWidth * 0.85f,
                        center = Offset(headX, headY)
                    )
                    // Inner solid dot
                    drawCircle(
                        color = Color.White,
                        radius = strokeWidth * 0.35f,
                        center = Offset(headX, headY)
                    )
                }

                // ==============================================================
                // CENTER READOUT: Current Month's Spending vs Target Budget
                // ==============================================================
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$spentPercentage%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isOverBudget) Color(0xFFDC2626) else OnSurface,
                            fontSize = 36.sp,
                            letterSpacing = (-1).sp
                        ),
                        modifier = Modifier.testTag("gauge_percentage_text")
                    )

                    Text(
                        text = "SPENT THIS MONTH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp
                        )
                    )

                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", activeSpent)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = healthStatus.color,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.testTag("monthly_spent_text")
                    )

                    Text(
                        text = "Target: ₹${String.format(Locale.getDefault(), "%,.0f", budgetTarget)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.testTag("monthly_budget_target_text")
                    )
                }

                // Bottom Anchor Labels (0% Start and 100% Target)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹0",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )

                    // Month elapsed indicator pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceContainerHigh,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                            )
                            Text(
                                text = "Day $elapsedDays/$totalDaysInMonth (${(timeElapsedRatio * 100).roundToInt()}%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", budgetTarget)} Limit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isOverBudget) Color(0xFFDC2626) else Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==============================================================
            // STATUS BANNER: Surplus Buffer or Over-budget Deficit
            // ==============================================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = healthStatus.containerColor,
                border = BorderStroke(1.dp, healthStatus.color.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("financial_health_status_banner")
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = healthStatus.icon,
                            contentDescription = null,
                            tint = healthStatus.color,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = if (isOverBudget) {
                                    "Over Budget by ₹${String.format(Locale.getDefault(), "%,.0f", deficitAmount)}"
                                } else {
                                    "Buffer Remaining: ₹${String.format(Locale.getDefault(), "%,.0f", remainingAmount)}"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = healthStatus.color,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = if (isOverBudget) {
                                    "Deficit pace: Reduce discretionary spending to balance"
                                } else {
                                    "Safe daily cap: ₹${String.format(Locale.getDefault(), "%,.0f", safeDailySpend)}/day for $remainingDays days"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Score pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = healthStatus.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${healthStatus.efficiencyScore} Score",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = healthStatus.color,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==============================================================
            // ACTION TOOLBAR: Simulation Slider & SVG Arc Inspector Toggle
            // ==============================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Simulation Toggle
                TextButton(
                    onClick = {
                        isSimulationMode = !isSimulationMode
                        if (!isSimulationMode) simulatedSpent = monthlySpent
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("toggle_spending_simulation_btn")
                ) {
                    Icon(
                        imageVector = if (isSimulationMode) Icons.Default.Close else Icons.Default.Tune,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSimulationMode) "Reset Actual (₹${String.format(Locale.getDefault(), "%,.0f", monthlySpent)})" else "Simulate Outflow",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 11.sp
                        )
                    )
                }

                // SVG Arc Code Inspector Toggle
                OutlinedButton(
                    onClick = { showInspector = !showInspector },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.35f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("svg_arc_inspector_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Inspect SVG Arc",
                        tint = Primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showInspector) "Hide SVG Arc" else "Inspect SVG Arc",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Interactive Simulation Slider
            AnimatedVisibility(
                visible = isSimulationMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate Monthly Spend",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", simulatedSpent)} (${((simulatedSpent / budgetTarget) * 100).roundToInt()}%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary
                            )
                        )
                    }

                    Slider(
                        value = simulatedSpent.toFloat(),
                        onValueChange = { simulatedSpent = it.toDouble() },
                        valueRange = 5000f..(budgetTarget.toFloat() * 1.4f),
                        modifier = Modifier.fillMaxWidth().testTag("spending_simulation_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("₹5,000", fontSize = 9.sp, color = OnSurfaceVariant)
                        Text("Target: ₹${String.format(Locale.getDefault(), "%,.0f", budgetTarget)}", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold)
                        Text("+40% Over", fontSize = 9.sp, color = Color(0xFFEF4444))
                    }
                }
            }

            // ==============================================================
            // EXPANDABLE SVG ARC INSPECTOR
            // ==============================================================
            AnimatedVisibility(
                visible = showInspector,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
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
                                imageVector = Icons.Default.DataObject,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SVG Arc Vector Specification",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        // Copy SVG Path Button
                        TextButton(
                            onClick = {
                                val fullSvgMarkup = """
                                    <svg viewBox="0 0 300 300" xmlns="http://www.w3.org/2000/svg">
                                      <!-- Track Arc -->
                                      <path d="$currentTrackSvgString" fill="none" stroke="#E2E8F0" stroke-width="16" stroke-linecap="round"/>
                                      <!-- Spending Progress Arc ($spentPercentage%) -->
                                      <path d="$currentProgressSvgString" fill="none" stroke="${if (isOverBudget) "#EF4444" else "#10B981"}" stroke-width="16" stroke-linecap="round"/>
                                    </svg>
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(fullSvgMarkup))
                                copyNotice = "SVG Arc path copied!"
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy SVG Arc",
                                tint = Primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = copyNotice ?: "Copy SVG",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Calculated from center (cx, cy) and radius r with W3C arc formula:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "// Active Spending Progress Arc Path:",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = currentProgressSvgString.ifEmpty { "M x y A r r 0 largeArc 1 x y" },
                                color = Color(0xFF38BDF8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("svg_arc_path_text")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "// Background Track Arc Path:",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = currentTrackSvgString.ifEmpty { "M x y A r r 0 1 1 x y" },
                                color = Color(0xFFA7F3D0),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
