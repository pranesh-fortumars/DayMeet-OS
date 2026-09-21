package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.*
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
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeInboxScreen(
    viewModel: DayMeetViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inboxItems by viewModel.lifeInboxItems.collectAsState()
    var quickTextInput by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<LifeInboxType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredItems = remember(inboxItems, selectedFilter) {
        if (selectedFilter == null) inboxItems
        else inboxItems.filter { it.type == selectedFilter }
    }

    SubModuleContainer(
        title = "Life Inbox",
        subtitle = "Universal capture: Dump anything, organize when ready",
        onBack = onBack
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick Dump Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("life_inbox_dump_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quickTextInput,
                            onValueChange = { quickTextInput = it },
                            placeholder = {
                                Text(
                                    "Dump text, thought, link, or note...",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("life_inbox_text_field"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (quickTextInput.isNotBlank()) {
                                    viewModel.addToLifeInbox(
                                        content = quickTextInput,
                                        type = if (quickTextInput.startsWith("http")) LifeInboxType.LINK else LifeInboxType.TEXT,
                                        source = "Quick Dump"
                                    )
                                    quickTextInput = ""
                                }
                            },
                            enabled = quickTextInput.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (quickTextInput.isNotBlank()) Primary else SurfaceContainerHigh)
                                .testTag("life_inbox_submit_dump_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Dump to Inbox",
                                tint = if (quickTextInput.isNotBlank()) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Capture Quick Buttons (Text, Voice, Photo, Link, Idea)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickDumpTypePill(
                            label = "Voice",
                            icon = Icons.Default.Mic,
                            color = Color(0xFF673AB7),
                            onClick = { viewModel.dumpSimulatedMediaToInbox(LifeInboxType.VOICE) }
                        )
                        QuickDumpTypePill(
                            label = "Photo",
                            icon = Icons.Default.CameraAlt,
                            color = Color(0xFF2E7D32),
                            onClick = { viewModel.dumpSimulatedMediaToInbox(LifeInboxType.PHOTO) }
                        )
                        QuickDumpTypePill(
                            label = "Link",
                            icon = Icons.Default.Link,
                            color = Color(0xFF1976D2),
                            onClick = { viewModel.dumpSimulatedMediaToInbox(LifeInboxType.LINK) }
                        )
                        QuickDumpTypePill(
                            label = "Idea",
                            icon = Icons.Default.Lightbulb,
                            color = Color(0xFFF57C00),
                            onClick = { viewModel.dumpSimulatedMediaToInbox(LifeInboxType.IDEA) }
                        )
                        QuickDumpTypePill(
                            label = "Clipboard",
                            icon = Icons.Default.ContentPaste,
                            color = Color(0xFF00897B),
                            onClick = { viewModel.captureFromClipboard() }
                        )
                    }
                }
            }

            // Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${inboxItems.size})", fontSize = 11.sp) },
                    shape = RoundedCornerShape(8.dp)
                )

                LifeInboxType.values().forEach { type ->
                    val count = inboxItems.count { it.type == type }
                    if (count > 0 || selectedFilter == type) {
                        FilterChip(
                            selected = selectedFilter == type,
                            onClick = {
                                selectedFilter = if (selectedFilter == type) null else type
                            },
                            label = { Text("${type.label} ($count)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Items List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = OutlineVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Life Inbox is clear",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant
                            )
                        )
                        Text(
                            text = "Dump voice notes, photos, links, or tasks without filing them yet.",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        LifeInboxItemCard(
                            item = item,
                            onCommitSuggestion = { suggestion ->
                                viewModel.commitLifeInboxSuggestion(item.id, suggestion)
                            },
                            onDismissItem = { viewModel.dismissLifeInboxItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDumpTypePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun LifeInboxItemCard(
    item: LifeInboxItem,
    onCommitSuggestion: (LifeInboxSuggestion) -> Unit,
    onDismissItem: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(0.8.dp, SurfaceContainerHigh),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inbox_item_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Type Badge, Source & Time, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (item.type) {
                            LifeInboxType.VOICE -> Color(0xFFEDE7F6)
                            LifeInboxType.PHOTO, LifeInboxType.SCREENSHOT -> Color(0xFFE8F5E9)
                            LifeInboxType.LINK -> Color(0xFFE3F2FD)
                            LifeInboxType.EXPENSE -> Color(0xFFFFF3E0)
                            else -> PrimaryFixed
                        }
                    ) {
                        Text(
                            text = item.type.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (item.type) {
                                    LifeInboxType.VOICE -> Color(0xFF673AB7)
                                    LifeInboxType.PHOTO, LifeInboxType.SCREENSHOT -> Color(0xFF2E7D32)
                                    LifeInboxType.LINK -> Color(0xFF1976D2)
                                    LifeInboxType.EXPENSE -> Color(0xFFF57C00)
                                    else -> Primary
                                },
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "• ${item.source} • ${item.timestamp}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismissItem,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Body text
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                    fontSize = 13.sp
                )
            )

            // AI Categorization / Conversion Suggestions
            if (item.suggestions.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Primary.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "AI Suggested Actions (Nothing committed without you)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        item.suggestions.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                    Text(
                                        text = "${suggestion.targetModule} → ${suggestion.title}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = suggestion.detail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = { onCommitSuggestion(suggestion) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = "+ Confirm",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
