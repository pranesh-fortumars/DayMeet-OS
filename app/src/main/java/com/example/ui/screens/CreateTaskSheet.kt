package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Priority
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskSheet(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    val initialTab by viewModel.quickAddInitialTab.collectAsState()
    var selectedType by remember { mutableStateOf(initialTab) }
    var nlpInput by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var extraValue by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.HIGH) }
    var selectedCategory by remember { mutableStateOf("Work") }
    var selectedReminderTime by remember { mutableStateOf<String?>("Today 05:00 PM") }
    var paymentMethod by remember { mutableStateOf("UPI") }
    var hasReceiptAttached by remember { mutableStateOf(false) }
    var isDebtOwedToMe by remember { mutableStateOf(true) }

    val types = listOf(
        "Task", "Meeting", "Reminder", "Expense", "Income", "Bill", "Project",
        "Appointment", "Home & Vehicle", "Note", "Shopping", "Habit", "Goal",
        "Subscription", "Debt", "EMI", "Message"
    )

    LaunchedEffect(initialTab) {
        selectedType = initialTab
        when (initialTab) {
            "Meeting" -> {
                title = "Design Sync"
                detail = "Google Meet • 30 mins"
                extraValue = "Today, 03:00 PM"
            }
            "Expense" -> {
                title = "Coffee & Bakery"
                detail = "Food"
                extraValue = "240"
                selectedCategory = "Food"
            }
            "Income" -> {
                title = "Client Consulting Retainer"
                detail = "Bank"
                extraValue = "45000"
                selectedCategory = "Salary"
            }
            "EMI" -> {
                title = "MacBook Pro EMI"
                detail = "HDFC Bank"
                extraValue = "4250"
                selectedCategory = "Gadgets"
            }
            "Debt" -> {
                title = "Rahul Verma"
                detail = "Dinner split share"
                extraValue = "850"
                isDebtOwedToMe = true
            }
            "Subscription" -> {
                title = "Netflix Premium 4K"
                detail = "Entertainment"
                extraValue = "649"
                selectedCategory = "Entertainment"
            }
            "Reminder" -> {
                title = "Review quarterly presentation"
                detail = "Time-based"
                extraValue = "Today, 06:00 PM"
            }
            "Note" -> {
                title = "Sprint Retrospective Notes"
                detail = "Key action items: refactor state management and streamline navigation."
                extraValue = ""
            }
            "Habit" -> {
                title = "Read 15 mins before bed"
                detail = "Daily habit"
                extraValue = ""
            }
            "Goal" -> {
                title = "Save ₹25,000 for travel fund"
                detail = "Financial goal"
                extraValue = "₹25,000"
            }
            "Bill" -> {
                title = "Internet Fiber Bill"
                detail = "Airtel Broadband"
                extraValue = "1179"
            }
            "Shopping" -> {
                title = "Organic Almond Milk"
                detail = "2 cartons"
                extraValue = "360"
            }
            "Message" -> {
                title = "Alex Chen"
                detail = "Hey, let's sync up before the sprint review tomorrow!"
                extraValue = "Tomorrow, 09:00 AM"
            }
            "Project" -> {
                title = "Website Launch"
                detail = "Deliverables, task board and budget tracker"
                extraValue = "₹42,000"
                selectedCategory = "Work"
            }
            "Appointment" -> {
                title = "Dr. Mehta Dental Consultation"
                detail = "SmileCare Clinic"
                extraValue = "22 Sep, 04:30 PM"
                selectedCategory = "Doctor"
            }
            "Home & Vehicle" -> {
                title = "Royal Enfield Periodic Maintenance"
                detail = "Scheduled service and brake inspection"
                extraValue = "12 November"
                selectedCategory = "Vehicle"
            }
            else -> {
                title = "Finalize Mobile Design Tokens"
                detail = "Review token architecture with mobile engineering squad."
                extraValue = ""
            }
        }
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("create_task_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                            .clip(CircleShape)
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Universal Quick Add",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Smart NLP Universal Quick Capture Box (Section 5 Spec)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Primary.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.22f)),
                modifier = Modifier.fillMaxWidth().testTag("nlp_quick_capture_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            text = "Natural Language Quick Capture (Type anything)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = nlpInput,
                        onValueChange = { nlpInput = it },
                        placeholder = {
                            Text(
                                "e.g. 'Call Arun tomorrow at 5', 'Buy milk', 'Pay bill ₹1,500'...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("nlp_quick_capture_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (nlpInput.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.executeNaturalLanguageCapture(nlpInput)
                                    onDismiss()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Parse & Save",
                                        tint = Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )

                    // Suggested quick chips (Directly from section 5 specification!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Call Arun tomorrow at 5",
                            "Buy milk when I reach home",
                            "Pay electricity bill ₹1,500 on 25th",
                            "Meet John next Tuesday afternoon",
                            "Car service 25 Sep 10:30 AM"
                        ).forEach { samplePrompt ->
                            SuggestionChip(
                                onClick = { nlpInput = samplePrompt },
                                label = {
                                    Text(
                                        samplePrompt,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    if (nlpInput.isNotBlank()) {
                        val parsed = remember(nlpInput) { viewModel.parseNaturalLanguage(nlpInput) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "✨ Detected: ${parsed.type}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                )
                                Text(
                                    text = parsed.explanation,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = OnSurfaceVariant
                                    )
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.executeNaturalLanguageCapture(nlpInput)
                                    onDismiss()
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                modifier = Modifier.testTag("nlp_quick_capture_execute_btn")
                            ) {
                                Text("Capture", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // Type Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedType = type
                            // Provide quick defaults
                            when (type) {
                                "Expense" -> {
                                    title = "Coffee & Bakery"
                                    detail = "Food & Dining"
                                    extraValue = "240"
                                }
                                "Meeting" -> {
                                    title = "Design Sync"
                                    detail = "Google Meet"
                                    extraValue = "Today, 03:00 PM"
                                }
                                "Bill" -> {
                                    title = "Internet Bill"
                                    detail = "Broadband"
                                    extraValue = "1179"
                                }
                                "Project" -> {
                                    title = "Website Launch"
                                    detail = "Deliverables, task board and budget"
                                    extraValue = "₹42,000"
                                }
                                "Appointment" -> {
                                    title = "Dr. Mehta Dental Consultation"
                                    detail = "SmileCare Clinic"
                                    extraValue = "22 Sep, 04:30 PM"
                                }
                                "Home & Vehicle" -> {
                                    title = "Air Conditioner Annual Servicing"
                                    detail = "Filter deep cleaning & gas check"
                                    extraValue = "15 October"
                                }
                                else -> {
                                    if (title.isBlank()) title = "New $type"
                                }
                            }
                        },
                        label = {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        shape = RoundedCornerShape(99.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceContainerLow,
                            labelColor = OnSurfaceVariant
                        )
                    )
                }
            }

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        when (selectedType) {
                            "Expense" -> "Expense Description"
                            "Meeting" -> "Meeting Title"
                            "Message" -> "Recipient Name"
                            "Bill" -> "Biller / Provider Name"
                            "Goal" -> "Goal Name"
                            "Project" -> "Project Title"
                            "Appointment" -> "Appointment / Specialist"
                            "Home & Vehicle" -> "Maintenance Item / Vehicle"
                            else -> "Title"
                        }
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth().testTag("quick_add_title_input")
            )

            // Detail / Notes input
            OutlinedTextField(
                value = detail,
                onValueChange = { detail = it },
                label = {
                    Text(
                        when (selectedType) {
                            "Expense" -> "Category (e.g. Dining, Transit)"
                            "Meeting" -> "Agenda / Platform"
                            "Message" -> "Message Content"
                            "Bill" -> "Department / Utility"
                            "Goal" -> "Category / Target Description"
                            "Project" -> "Deliverables & Scope"
                            "Appointment" -> "Clinic / Provider Location"
                            "Home & Vehicle" -> "Service Scope / Warranty Details"
                            else -> "Notes & Context"
                        }
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth().testTag("quick_add_detail_input")
            )

            // Optional 3rd parameter input (e.g., Amount, Time, Target Value)
            if (selectedType in listOf("Expense", "Income", "Meeting", "Reminder", "Bill", "Goal", "Message", "EMI", "Debt", "Subscription", "Shopping", "Project", "Appointment", "Home & Vehicle")) {
                OutlinedTextField(
                    value = extraValue,
                    onValueChange = { extraValue = it },
                    label = {
                        Text(
                            when (selectedType) {
                                "Expense", "Income", "Bill", "Shopping" -> "Amount (₹)"
                                "EMI" -> "Monthly EMI Installment (₹)"
                                "Debt" -> "Debt / Share Amount (₹)"
                                "Subscription" -> "Monthly Subscription Fee (₹)"
                                "Meeting", "Reminder", "Message", "Appointment" -> "Scheduled Date & Time"
                                "Project" -> "Budget / Target (e.g. ₹42,000)"
                                "Home & Vehicle" -> "Due Date (e.g. 15 October)"
                                "Goal" -> "Target Metric"
                                else -> "Value"
                            }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("quick_add_amount_input")
                )
            }


            // Debt Direction Selector
            if (selectedType == "Debt") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Debt Type",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDebtOwedToMe) Color(0xFFE8F5E9) else SurfaceContainerLow,
                            border = if (isDebtOwedToMe) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32)) else null,
                            modifier = Modifier.weight(1f).clickable { isDebtOwedToMe = true }
                        ) {
                            Text(
                                text = "They Owe Me (To Receive)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isDebtOwedToMe) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDebtOwedToMe) Color(0xFF2E7D32) else OnSurfaceVariant
                                ),
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isDebtOwedToMe) Color(0xFFFFEBEE) else SurfaceContainerLow,
                            border = if (!isDebtOwedToMe) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F)) else null,
                            modifier = Modifier.weight(1f).clickable { isDebtOwedToMe = false }
                        ) {
                            Text(
                                text = "I Owe Them (To Pay)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (!isDebtOwedToMe) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isDebtOwedToMe) Color(0xFFD32F2F) else OnSurfaceVariant
                                ),
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            // Payment Mode Selector for Expense & Income
            if (selectedType in listOf("Expense", "Income")) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Payment Mode",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("UPI", "Credit / Debit Card", "Cash", "Bank Transfer").forEach { mode ->
                            val isSelected = paymentMethod == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { paymentMethod = mode },
                                label = { Text(mode, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)) },
                                shape = RoundedCornerShape(99.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceContainerLow,
                                    labelColor = OnSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Receipt Attachment Action
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasReceiptAttached) PrimaryFixed else SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth().clickable {
                        hasReceiptAttached = !hasReceiptAttached
                        viewModel.showToast(if (hasReceiptAttached) "Receipt photo attached 📎" else "Receipt removed")
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (hasReceiptAttached) Icons.Default.ReceiptLong else Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = if (hasReceiptAttached) Primary else OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (hasReceiptAttached) "Receipt Attached (receipt_scan_01.jpg)" else "Attach Receipt / Bill Photo (Camera)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (hasReceiptAttached) FontWeight.Bold else FontWeight.Medium,
                                    color = if (hasReceiptAttached) Primary else OnSurfaceVariant
                                )
                            )
                        }

                        if (hasReceiptAttached) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Category Selector for Expenses & Incomes
            if (selectedType == "Expense") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Expense Category",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Food" to "🍔",
                            "Travel" to "🚗",
                            "Shopping" to "🛍️",
                            "Bills" to "💡",
                            "Rent" to "🏠",
                            "Education" to "📚",
                            "Healthcare" to "💊",
                            "Entertainment" to "🎬",
                            "Fuel" to "⛽",
                            "Other" to "🏷️"
                        ).forEach { (cat, emoji) ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategory = cat
                                    detail = cat
                                },
                                label = { Text("$emoji $cat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)) },
                                shape = RoundedCornerShape(99.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceContainerLow,
                                    labelColor = OnSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Priority Selector for Tasks
            if (selectedType == "Task") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Priority Level",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(Priority.HIGH, "High", Color(0xFFD32F2F)),
                            Triple(Priority.MEDIUM, "Medium", Color(0xFFF57C00)),
                            Triple(Priority.LOW, "Low", Color(0xFF1976D2))
                        ).forEach { (p, label, accentColor) ->
                            val isSelected = selectedPriority == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) accentColor.copy(alpha = 0.15f) else SurfaceContainerLow,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, accentColor) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPriority = p }
                                    .testTag("priority_selector_${label.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accentColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) accentColor else OnSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Selector for Tasks
            if (selectedType == "Task") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_add_category_selector"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Work" to "💼",
                            "Personal" to "👤",
                            "Shopping" to "🛒",
                            "Urgent" to "⚡",
                            "Finance" to "💰",
                            "Health" to "🏥"
                        ).forEach { (cat, emoji) ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = {
                                    Text(
                                        text = "$emoji $cat",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(99.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceContainerLow,
                                    labelColor = OnSurfaceVariant
                                ),
                                modifier = Modifier.testTag("quick_add_category_${cat.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Set Reminder Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_add_reminder_selector"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Set Reminder",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "None" to null,
                        "In 15 Mins" to "In 15 Mins",
                        "In 1 Hour" to "In 1 Hour",
                        "Today 05:00 PM" to "Today 05:00 PM",
                        "Tomorrow 09:00 AM" to "Tomorrow 09:00 AM"
                    ).forEach { (label, preset) ->
                        val isSelected = selectedReminderTime == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedReminderTime = preset },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            shape = RoundedCornerShape(99.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceContainerLow,
                                labelColor = OnSurfaceVariant
                            ),
                            modifier = Modifier.testTag("quick_add_reminder_${label.lowercase().replace(' ', '_')}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Cancel", color = OnSurfaceVariant)
                }

                Button(
                    onClick = {
                        when (selectedType) {
                            "Expense" -> {
                                val amt = extraValue.toDoubleOrNull() ?: 150.0
                                viewModel.checkAndLogExpense(
                                    title = title.ifBlank { "Expense" },
                                    amount = amt,
                                    category = selectedCategory,
                                    method = paymentMethod,
                                    receiptNote = if (hasReceiptAttached) "receipt_scan_01.jpg" else null
                                )
                                onDismiss()
                            }
                            "Income" -> {
                                val amt = extraValue.toDoubleOrNull() ?: 1000.0
                                viewModel.logIncome(title.ifBlank { "Income" }, amt, paymentMethod)
                                onDismiss()
                            }
                            "Debt" -> {
                                val amt = extraValue.toDoubleOrNull() ?: 500.0
                                viewModel.addDebt(
                                    person = title.ifBlank { "Contact" },
                                    amount = amt,
                                    isOwedToMe = isDebtOwedToMe,
                                    dueDate = "Next Week",
                                    note = detail.ifBlank { "Split share" }
                                )
                                onDismiss()
                            }
                            "EMI" -> {
                                val amt = extraValue.toDoubleOrNull() ?: 2500.0
                                viewModel.addEmi(
                                    title = title.ifBlank { "New EMI" },
                                    amount = amt,
                                    totalMonths = 12,
                                    nextDue = "5th of next month",
                                    category = selectedCategory.ifBlank { "Personal" }
                                )
                                onDismiss()
                            }
                            "Subscription" -> {
                                val cost = extraValue.toDoubleOrNull() ?: 499.0
                                viewModel.addSubscription(
                                    name = title.ifBlank { "Subscription" },
                                    cost = cost,
                                    date = "End of Month",
                                    category = selectedCategory.ifBlank { "Entertainment" },
                                    autoPay = true
                                )
                                onDismiss()
                            }
                            else -> {
                                viewModel.universalQuickAdd(
                                    type = selectedType,
                                    title = title,
                                    detail = detail,
                                    extraValue = extraValue,
                                    priority = selectedPriority,
                                    category = selectedCategory,
                                    reminderTime = selectedReminderTime
                                )
                                onDismiss()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.weight(2f).height(48.dp).testTag("quick_add_submit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save $selectedType",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
