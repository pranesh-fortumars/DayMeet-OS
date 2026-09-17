package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AutomationWorkflow
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun AutomationsScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val automations by viewModel.automations.collectAsState()
    val logs by viewModel.automationLogs.collectAsState()
    var selectedCategory by remember { mutableStateOf("All Rules") }
    var showCreateRuleDialog by remember { mutableStateOf(false) }
    var editingWorkflow by remember { mutableStateOf<AutomationWorkflow?>(null) }

    val filteredList = remember(selectedCategory, automations) {
        if (selectedCategory == "All Rules") automations
        else automations.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE7F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "AUTOMATIONS ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${automations.count { it.isEnabled }} Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )

                            Button(
                                onClick = { showCreateRuleDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp).testTag("new_rule_header_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "New Rule",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rules & Automations",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 24.sp
                        )
                    )

                    Text(
                        text = "Cross-module intelligence connecting meetings, tasks, expenses, health & daily routines",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // 2. Filter Chips Row
            item {
                val filterOptions = listOf("All Rules", "Productivity", "Finance", "Health", "Deadlines")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        val isSelected = selectedCategory == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = filter },
                            label = {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            shape = RoundedCornerShape(99.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OnSurface,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceContainerLowest,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color.Transparent else OutlineVariant
                            )
                        )
                    }
                }
            }

            // 3. Workflow Cards
            items(filteredList, key = { it.id }) { workflow ->
                AutomationWorkflowCard(
                    workflow = workflow,
                    onToggle = { viewModel.toggleAutomation(workflow.id) },
                    onTestTrigger = { viewModel.testAutomation(workflow.id) },
                    onEditRule = { editingWorkflow = workflow }
                )
            }

        // 4. Automation Activity Log
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
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
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Automation Activity Log",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                        }

                        Text(
                            text = "Live Audit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryFixed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    logs.forEachIndexed { index, log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (log.isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (log.isSuccess) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (log.isSuccess) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = log.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = OnSurface
                                        )
                                    )
                                    Text(
                                        text = log.detail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Text(
                                text = log.time,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        if (index < logs.size - 1) {
                            HorizontalDivider(color = SurfaceContainerHigh, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }

    if (showCreateRuleDialog) {
        AutomationRuleEditorDialog(
            workflow = null,
            onDismiss = { showCreateRuleDialog = false },
            onSave = { title, category, whenTrigger, ifCondition, thenAction ->
                viewModel.addCustomAutomation(title, category, whenTrigger, ifCondition, thenAction)
                showCreateRuleDialog = false
            },
            onDelete = null
        )
    }

    editingWorkflow?.let { workflow ->
        AutomationRuleEditorDialog(
            workflow = workflow,
            onDismiss = { editingWorkflow = null },
            onSave = { title, category, whenTrigger, ifCondition, thenAction ->
                viewModel.updateAutomation(workflow.id, title, category, whenTrigger, ifCondition, thenAction)
                editingWorkflow = null
            },
            onDelete = {
                viewModel.deleteAutomation(workflow.id)
                editingWorkflow = null
            }
        )
    }
}
}

@Composable
private fun AutomationWorkflowCard(
    workflow: AutomationWorkflow,
    onToggle: () -> Unit,
    onTestTrigger: () -> Unit,
    onEditRule: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("automation_card_${workflow.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Status + Switch
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
                    val (catBg, catColor) = when (workflow.category.lowercase()) {
                        "productivity" -> Color(0xFFE8EAF6) to Color(0xFF3949AB)
                        "finance" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        "health" -> Color(0xFFE0F2F1) to Color(0xFF00897B)
                        "deadlines" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                        else -> Color(0xFFEDE7F6) to Color(0xFF673AB7)
                    }

                    Text(
                        text = workflow.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = catColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(catBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    Text(
                        text = workflow.statusTag,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Switch(
                    checked = workflow.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary
                    ),
                    modifier = Modifier.testTag("automation_switch_${workflow.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = workflow.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // When -> If -> Then box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
                    .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WHEN
                RuleStepRow(
                    tag = "WHEN",
                    tagBg = Color(0xFFE3F2FD),
                    tagColor = Color(0xFF1565C0),
                    text = workflow.whenTrigger
                )

                // IF
                RuleStepRow(
                    tag = "IF",
                    tagBg = Color(0xFFFFF3E0),
                    tagColor = Color(0xFFE65100),
                    text = workflow.ifCondition
                )

                // THEN
                RuleStepRow(
                    tag = "THEN",
                    tagBg = Color(0xFFE8F5E9),
                    tagColor = Color(0xFF2E7D32),
                    text = workflow.thenAction
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Stats text + Test Trigger + Edit Rule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workflow.statsText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onTestTrigger,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("test_trigger_${workflow.id}")
                    ) {
                        Text(
                            text = "Test Trigger",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    FilledTonalButton(
                        onClick = onEditRule,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = "Edit Rule",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleStepRow(
    tag: String,
    tagBg: Color,
    tagColor: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = tagColor,
                fontSize = 10.sp
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(tagBg)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = OnSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomationRuleEditorDialog(
    workflow: AutomationWorkflow?,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, whenTrigger: String, ifCondition: String, thenAction: String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var title by remember { mutableStateOf(workflow?.title ?: "") }
    var category by remember { mutableStateOf(workflow?.category ?: "Productivity") }
    var whenTrigger by remember { mutableStateOf(workflow?.whenTrigger ?: "When meeting is within 5 minutes") }
    var ifCondition by remember { mutableStateOf(workflow?.ifCondition ?: "Always unconditional") }
    var thenAction by remember { mutableStateOf(workflow?.thenAction ?: "Mute notifications & enable Focus DND") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("automation_rule_editor_sheet")
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
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = if (workflow != null) "Edit Automation Rule" else "Create Automation Rule",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = OnSurface)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariant)
                }
            }

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Productivity", "Finance", "Health", "Deadlines", "Communications").forEach { cat ->
                    val isSelected = category == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { category = cat },
                        label = { Text(cat, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)) },
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

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Workflow Name") },
                placeholder = { Text("e.g., Auto Focus DND for Meetings") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth().testTag("automation_rule_name_input")
            )

            // WHEN Trigger Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "WHEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text("Trigger Condition", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Meeting within 5 mins",
                        "Expense > ₹2,000 logged",
                        "Bedtime 10:30 PM reached",
                        "Task marked completed",
                        "Monthly budget hits 80%"
                    ).forEach { preset ->
                        AssistChip(
                            onClick = { whenTrigger = preset },
                            label = { Text(preset, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = whenTrigger,
                    onValueChange = { whenTrigger = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = SurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // IF Condition Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "IF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text("Rule Condition", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Always unconditional",
                        "Meeting has >2 participants",
                        "Category is Dining or Shopping",
                        "Today is a weekday",
                        "Hydration below daily target"
                    ).forEach { preset ->
                        AssistChip(
                            onClick = { ifCondition = preset },
                            label = { Text(preset, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = ifCondition,
                    onValueChange = { ifCondition = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = SurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // THEN Action Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text("Automated Action", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceVariant))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Mute notifications & enable Focus DND",
                        "Prompt receipt photo & expense alert",
                        "Trigger confetti & update streak",
                        "Auto-add ₹500 to savings goal",
                        "Log +250ml water automatically"
                    ).forEach { preset ->
                        AssistChip(
                            onClick = { thenAction = preset },
                            label = { Text(preset, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = thenAction,
                    onValueChange = { thenAction = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = SurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onDelete != null) {
                    OutlinedButton(
                        onClick = onDelete,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Delete")
                    }
                } else {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Cancel", color = OnSurfaceVariant)
                    }
                }

                Button(
                    onClick = {
                        val finalTitle = title.ifBlank { "Smart ${category} Rule" }
                        onSave(finalTitle, category, whenTrigger, ifCondition, thenAction)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.weight(2f).height(48.dp).testTag("save_automation_rule_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (workflow != null) "Update Rule" else "Activate Rule",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
