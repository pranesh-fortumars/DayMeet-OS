package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun GlobalSearchDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val tasks by viewModel.feedItems.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val automations by viewModel.automations.collectAsState()

    val searchResults = remember(query, tasks, meetings, notes, automations) {
        if (query.isBlank()) emptyList()
        else {
            val q = query.trim().lowercase()
            val list = mutableListOf<Triple<String, String, String>>() // Type, Title, Subtitle
            tasks.filter { it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q) }
                .forEach { list.add(Triple("Task", it.title, it.subtitle)) }
            meetings.filter { it.title.lowercase().contains(q) || it.platform.lowercase().contains(q) }
                .forEach { list.add(Triple("Meeting", it.title, "${it.time} • ${it.platform}")) }
            notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
                .forEach { list.add(Triple("Note", it.title, it.content)) }
            automations.filter { it.title.lowercase().contains(q) || it.thenAction.lowercase().contains(q) }
                .forEach { list.add(Triple("Automation", it.title, it.thenAction)) }
            list
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("global_search_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search meetings, tasks, notes, rules...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Primary)
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        } else {
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = SurfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("global_search_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (query.isBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Universal Cross-Module Search",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Instant results across Calendar, Tasks, Finance, Notes & Rules",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                        )
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$query\"",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(searchResults) { (type, resTitle, resSub) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    when (type) {
                                        "Meeting" -> viewModel.navigateTo("meetings")
                                        "Task" -> viewModel.navigateTo("tasks")
                                        "Note" -> viewModel.openSubScreen("notes")
                                        "Automation" -> viewModel.openSubScreen("automations")
                                        else -> viewModel.showToast("Opening $type: $resTitle")
                                    }
                                    onDismiss()
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Primary
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PrimaryFixed)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = resTitle,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = OnSurface
                                            )
                                        )
                                        Text(
                                            text = resSub,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnSurfaceVariant,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
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
}
