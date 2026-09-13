package com.example.ui.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinanceTransaction
import com.example.model.UpcomingBill
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel
import kotlin.math.roundToInt

@Composable
fun FinanceScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val upcomingBills by viewModel.upcomingBills.collectAsState()

    val dailyLimit = 5000.0
    val spentToday = remember(transactions) {
        transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    }
    val leftToday = (dailyLimit - spentToday).coerceAtLeast(0.0)
    val spentPercent = ((spentToday / dailyLimit) * 100).coerceIn(0.0, 100.0).toInt()
    val sweepAngle = (360f * (spentToday / dailyLimit).toFloat()).coerceIn(0f, 360f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Tertiary)
                        )
                        Text(
                            text = "PRODUCTIVITY SYNC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Tertiary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                    Text(
                        text = "Daily Finance",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.downloadWeeklyFinanceReport(context) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest)
                            .testTag("download_finance_report_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Download Report",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(TertiaryFixed)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = OnTertiaryFixed,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Under Budget",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnTertiaryFixed
                            )
                        )
                    }
                }
            }
        }

        // Hero Card: Available Daily Allowance
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("daily_allowance_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE DAILY ALLOWANCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                        )
                        Text(
                            text = "+12.4% vs last week",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Tertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "₹${String.format("%.0f", leftToday)} left",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = OnSurface
                                )
                            )
                            Text(
                                text = "of ₹${String.format("%.0f", dailyLimit)} daily budget limit",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                            )
                        }

                        // Circular Spent Gauge
                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(54.dp)) {
                                val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                                drawCircle(
                                    color = SurfaceContainerHigh,
                                    radius = size.minDimension / 2 - 2.dp.toPx(),
                                    style = stroke
                                )
                                drawArc(
                                    color = if (spentToday <= dailyLimit) Primary else Color(0xFFD32F2F),
                                    startAngle = -90f,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = stroke
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$spentPercent%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Text(
                                    text = "spent",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        color = OnSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linear breakdown bar
                    val progressRatio = (spentToday / dailyLimit).toFloat().coerceIn(0.01f, 1f)
                    val remainingRatio = (1f - progressRatio).coerceAtLeast(0.01f)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    ) {
                        Box(modifier = Modifier.weight(progressRatio).fillMaxHeight().background(if (spentToday <= dailyLimit) Primary else Color(0xFFD32F2F)))
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.White))
                        Box(modifier = Modifier.weight(remainingRatio).fillMaxHeight().background(SurfaceContainerHigh))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = SurfaceContainer, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Spent Today",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                            Text(
                                text = "₹${String.format("%.0f", spentToday)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }
                        Column {
                            Text(
                                text = "Month-to-Date",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                            Text(
                                text = "$1,420",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }
                        Column {
                            Text(
                                text = "Proj. Savings",
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                            )
                            Text(
                                text = "+$850",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Tertiary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Smart Budget Spark AI Insight Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Smart Budget Spark",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Pro Tip: You are on track to save +$120 extra this week by keeping lunch under $30. Good job keeping to your goals!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.openCreateTask("Expense") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("log_expense_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Log Expense",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = { viewModel.openCreateTask("Income") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerLowest,
                        contentColor = OnSurface
                    ),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = Tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Income",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                IconButton(
                    onClick = { viewModel.showToast("Receipt Scanner activated") },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Scan Receipt",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Spending Analytics: Weekly Bar Chart & 30-Day Interactive Line Trend
        item {
            SpendingTrendsAnalyticsCard(
                todaySpend = spentToday,
                dailyCeiling = dailyLimit,
                onDownloadReport = { viewModel.downloadWeeklyFinanceReport(context) }
            )
        }

        // Today's Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                Text(
                    text = "View All (8)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.showToast("All transactions ledger") }
                )
            }
        }

        items(transactions, key = { it.id }) { tx ->
            TransactionRowItem(tx = tx)
        }

        // Upcoming Bills & Dues
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Bills & Dues",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
                Text(
                    text = "Auto-Pay Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        items(upcomingBills, key = { it.id }) { bill ->
            UpcomingBillRowItem(
                bill = bill,
                onPayEarly = { viewModel.payBill(bill.id) }
            )
        }
    }
}

@Composable
private fun TransactionRowItem(tx: FinanceTransaction) {
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (tx.iconType) {
                                    "restaurant" -> AmberLight
                                    "subway" -> SkyLight
                                    else -> IndigoLight
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (tx.iconType) {
                                "restaurant" -> Icons.Default.Restaurant
                                "subway" -> Icons.Default.DirectionsSubway
                                else -> Icons.Default.Computer
                            },
                            contentDescription = null,
                            tint = when (tx.iconType) {
                                "restaurant" -> AmberWarning
                                "subway" -> SkyBlue
                                else -> Primary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = tx.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "${tx.category} • ${tx.time}",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }
                }

                Text(
                    text = if (tx.amount < 0) "-$${String.format("%.2f", -tx.amount)}" else "+$${String.format("%.2f", tx.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
            }

            // Linked calendar event pill
            if (tx.linkedEvent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Linked: ${tx.linkedEvent}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Tags
            if (tx.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tx.tags.forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingBillRowItem(
    bill: UpcomingBill,
    onPayEarly: () -> Unit
) {
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
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bill.autoPay) Icons.Default.Autorenew else Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = if (bill.autoPay) Tertiary else Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = bill.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "${bill.scheduleDate} • ${bill.department}",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", bill.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )

                if (bill.autoPay) {
                    Text(
                        text = "Auto-Pay On",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                } else {
                    Text(
                        text = "Pay Early",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryFixed)
                            .clickable { onPayEarly() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendsAnalyticsCard(
    todaySpend: Double,
    dailyCeiling: Double = 5000.0,
    onDownloadReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var chartMode by remember { mutableStateOf("line") } // "line", "bar", "both"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Controls Row: Segmented Switcher & Download Report Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented Pills
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerHigh)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    Pair("line", "30D Trend"),
                    Pair("bar", "7D Weekly"),
                    Pair("both", "Both")
                ).forEach { (mode, label) ->
                    val isSelected = chartMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) SurfaceContainerLowest else Color.Transparent)
                            .clickable { chartMode = mode }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("chart_mode_$mode"),
                        contentAlignment = Alignment.Center
                    ) {
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

            // Executive Download Report Action
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryFixed)
                    .clickable { onDownloadReport() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("download_finance_report_btn_card"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download Report",
                    tint = Primary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Export Report",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        when (chartMode) {
            "line" -> {
                InteractiveThirtyDaySpendingLineChart(
                    todaySpend = todaySpend,
                    dailyCeiling = dailyCeiling
                )
            }
            "bar" -> {
                DailySpendingWeeklyBarChart(
                    todaySpend = todaySpend,
                    dailyCeiling = dailyCeiling
                )
            }
            "both" -> {
                InteractiveThirtyDaySpendingLineChart(
                    todaySpend = todaySpend,
                    dailyCeiling = dailyCeiling
                )
                DailySpendingWeeklyBarChart(
                    todaySpend = todaySpend,
                    dailyCeiling = dailyCeiling
                )
            }
        }
    }
}

@Composable
private fun InteractiveThirtyDaySpendingLineChart(
    todaySpend: Double,
    dailyCeiling: Double = 5000.0,
    modifier: Modifier = Modifier
) {
    data class DayTrendPoint(
        val dayIndex: Int,
        val dateLabel: String,
        val amount: Double
    )

    val thirtyDayData = remember(todaySpend) {
        val historical = listOf(
            2100.0, 1850.0, 3200.0, 4100.0, 2400.0, 1950.0, 4800.0, // Days 1-7
            1600.0, 2250.0, 3100.0, 2750.0, 1890.0, 3900.0, 4600.0, // Days 8-14
            2050.0, 2800.0, 3400.0, 2150.0, 1900.0, 4200.0, 5100.0, // Days 15-21
            1750.0, 2300.0, 3050.0, 2600.0, 1950.0, 3600.0, 4400.0, // Days 22-28
            2200.0, todaySpend                                        // Days 29-30
        )
        historical.mapIndexed { index, amt ->
            val dayNum = index + 1
            val label = when (dayNum) {
                30 -> "Day 30 (Today)"
                29 -> "Day 29 (Yesterday)"
                else -> "Day $dayNum • Aug/Sep"
            }
            DayTrendPoint(dayNum, label, amt)
        }
    }

    var selectedIndex by remember { mutableIntStateOf(29) } // Default Day 30
    val selectedPoint = thirtyDayData.getOrElse(selectedIndex) { thirtyDayData.last() }

    val totalSpent30Days = remember(thirtyDayData) { thirtyDayData.sumOf { it.amount } }
    val avgDailySpend = remember(thirtyDayData) { totalSpent30Days / thirtyDayData.size }
    val daysUnderBudget = remember(thirtyDayData) { thirtyDayData.count { it.amount <= dailyCeiling } }
    val maxScale = remember(thirtyDayData) { maxOf(6000.0, (thirtyDayData.maxOfOrNull { it.amount } ?: 5000.0) * 1.12) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("finance_30day_line_chart")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-Day Spending Trajectory",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Drag or tap curve to scrub daily expenditure trends",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${((daysUnderBudget / 30f) * 100).toInt()}% Safe Rate",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tooltip Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
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
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (selectedPoint.amount > dailyCeiling) Color(0xFFD32F2F) else Primary)
                        )
                        Text(
                            text = "${selectedPoint.dateLabel}: ₹${String.format("%,.0f", selectedPoint.amount)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                    }

                    val diff = dailyCeiling - selectedPoint.amount
                    val statusText = if (diff >= 0) "✓ ₹${String.format("%,.0f", diff)} under cap" else "⚠ ₹${String.format("%,.0f", -diff)} over limit"
                    val statusColor = if (diff >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Canvas with Touch Scrubbing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(thirtyDayData) {
                            detectTapGestures { offset ->
                                val frac = (offset.x / size.width).coerceIn(0f, 1f)
                                selectedIndex = (frac * (thirtyDayData.size - 1)).roundToInt().coerceIn(0, thirtyDayData.size - 1)
                            }
                        }
                        .pointerInput(thirtyDayData) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val frac = (change.position.x / size.width).coerceIn(0f, 1f)
                                selectedIndex = (frac * (thirtyDayData.size - 1)).roundToInt().coerceIn(0, thirtyDayData.size - 1)
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val n = thirtyDayData.size
                    val stepX = if (n > 1) w / (n - 1) else w

                    // 1. Budget Ceiling reference line (dashed red)
                    val ceilingY = h * (1f - (dailyCeiling / maxScale).toFloat())
                    drawLine(
                        color = Color(0xFFE53935).copy(alpha = 0.55f),
                        start = Offset(0f, ceilingY),
                        end = Offset(w, ceilingY),
                        strokeWidth = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )

                    // 2. Average Spend reference line (dashed green)
                    val avgY = h * (1f - (avgDailySpend / maxScale).toFloat())
                    drawLine(
                        color = Color(0xFF2E7D32).copy(alpha = 0.45f),
                        start = Offset(0f, avgY),
                        end = Offset(w, avgY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Map points
                    val points = thirtyDayData.mapIndexed { idx, pt ->
                        val px = idx * stepX
                        val py = h * (1f - (pt.amount / maxScale).toFloat().coerceIn(0.05f, 0.95f))
                        Offset(px, py)
                    }

                    if (points.isNotEmpty()) {
                        val strokePath = Path()
                        val fillPath = Path()

                        strokePath.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, h)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cpX1 = p0.x + (p1.x - p0.x) / 2f
                            val cpY1 = p0.y
                            val cpX2 = p0.x + (p1.x - p0.x) / 2f
                            val cpY2 = p1.y

                            strokePath.cubicTo(cpX1, cpY1, cpX2, cpY2, p1.x, p1.y)
                            fillPath.cubicTo(cpX1, cpY1, cpX2, cpY2, p1.x, p1.y)
                        }

                        fillPath.lineTo(points.last().x, h)
                        fillPath.close()

                        // Draw smooth gradient fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.35f),
                                    Primary.copy(alpha = 0.02f)
                                ),
                                startY = 0f,
                                endY = h
                            )
                        )

                        // Draw line stroke
                        drawPath(
                            path = strokePath,
                            color = Primary,
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        // Draw Scrubber Indicator on Selected Day
                        val activePoint = points.getOrNull(selectedIndex) ?: points.last()
                        // Vertical dashed indicator
                        drawLine(
                            color = Primary.copy(alpha = 0.5f),
                            start = Offset(activePoint.x, 0f),
                            end = Offset(activePoint.x, h),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )
                        // Glowing outer circle
                        drawCircle(
                            color = Primary.copy(alpha = 0.25f),
                            radius = 9.dp.toPx(),
                            center = activePoint
                        )
                        // Solid inner circle
                        drawCircle(
                            color = Primary,
                            radius = 5.dp.toPx(),
                            center = activePoint
                        )
                        // Crisp white center dot
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = activePoint
                        )
                    }
                }

                // Reference labels badges inside chart
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ceiling ₹5,000",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFC62828),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Avg ₹${String.format("%,.0f", avgDailySpend)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF2E7D32),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // X-axis Time Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "30 Days Ago",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "15 Days Ago",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Today (Day 30)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = SurfaceContainerHigh
            )

            // Bottom Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-Day Total",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "₹${String.format("%,.0f", totalSpent30Days)}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Daily Average",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "₹${String.format("%,.0f", avgDailySpend)}/day",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Adherence",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "$daysUnderBudget/30 Days Safe",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySpendingWeeklyBarChart(
    todaySpend: Double,
    dailyCeiling: Double = 5000.0,
    modifier: Modifier = Modifier
) {
    data class DaySpendData(
        val dayLabel: String,
        val fullDayName: String,
        val amount: Double,
        val isToday: Boolean = false
    )

    val weekData = remember(todaySpend) {
        listOf(
            DaySpendData("M", "Mon", 2100.0),
            DaySpendData("T", "Tue", 4350.0),
            DaySpendData("W", "Wed", 2800.0),
            DaySpendData("T", "Thu (Today)", todaySpend, isToday = true),
            DaySpendData("F", "Fri", 1800.0),
            DaySpendData("S", "Sat", 4800.0),
            DaySpendData("S", "Sun", 1200.0)
        )
    }

    var selectedDayIndex by remember { mutableStateOf(3) } // default Thu (Today)
    val selectedDay = weekData.getOrNull(selectedDayIndex) ?: weekData[3]

    val maxScale = 6000.0 // ceiling is 5000, max scale 6000
    val totalWeekSpend = weekData.sumOf { it.amount }
    val avgDaily = totalWeekSpend / 7.0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("finance_spending_bar_chart")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header & Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Spending vs Budget Ceiling",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Current week expenditure against daily ₹5,000 ceiling",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Ceiling ₹5k/day",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Tooltip (Recharts-style Callout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
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
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (selectedDay.amount > dailyCeiling) Color(0xFFD32F2F) else Primary)
                        )
                        Text(
                            text = "${selectedDay.fullDayName}: ₹${String.format("%,.0f", selectedDay.amount)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                    }

                    val diff = dailyCeiling - selectedDay.amount
                    val statusText = if (diff >= 0) "₹${String.format("%,.0f", diff)} buffer" else "₹${String.format("%,.0f", -diff)} over limit"
                    val statusColor = if (diff >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Chart Canvas & Bars Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Background reference line at ceiling (₹5,000 / 6,000 = 83.3% height from bottom -> y = 16.7% from top)
                val ceilingRatio = (dailyCeiling / maxScale).toFloat() // ~0.833

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val ceilingY = size.height * (1f - ceilingRatio)
                    val midY = size.height * (1f - (2500f / 6000f))

                    // 2500 guideline
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(0f, midY),
                        end = Offset(size.width, midY),
                        strokeWidth = 1.dp.toPx()
                    )

                    // 5000 ceiling reference line (dashed red/amber)
                    drawLine(
                        color = Color(0xFFE53935).copy(alpha = 0.7f),
                        start = Offset(0f, ceilingY),
                        end = Offset(size.width, ceilingY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // Reference line badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "--- Budget Ceiling ₹5,000",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFC62828),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Bar items Row
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weekData.forEachIndexed { index, item ->
                        val barRatio = (item.amount / maxScale).toFloat().coerceIn(0.04f, 1f)
                        val isSelected = index == selectedDayIndex
                        val isOverCeiling = item.amount > dailyCeiling

                        val barColor = when {
                            isOverCeiling -> Color(0xFFE53935)
                            item.isToday -> Primary
                            isSelected -> Primary.copy(alpha = 0.85f)
                            else -> PrimaryFixedDim
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedDayIndex = index }
                                .padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(barRatio)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(barColor)
                                    .then(
                                        if (isSelected) Modifier.border(
                                            width = 1.5.dp,
                                            color = if (isOverCeiling) Color(0xFFB71C1C) else Primary,
                                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        ) else Modifier
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis Day Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekData.forEachIndexed { index, item ->
                    val isSelected = index == selectedDayIndex
                    Text(
                        text = item.dayLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (item.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.isToday) Primary else if (isSelected) OnSurface else OnSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDayIndex = index },
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Metrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerLow)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Total",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                    )
                    Text(
                        text = "₹${String.format("%,.0f", totalWeekSpend)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OnSurface)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Daily Average",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                    )
                    Text(
                        text = "₹${String.format("%,.0f", avgDaily)}/day",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Primary)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Weekly Budget Cap",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.sp)
                    )
                    Text(
                        text = "₹35,000 (Safe)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    )
                }
            }
        }
    }
}
