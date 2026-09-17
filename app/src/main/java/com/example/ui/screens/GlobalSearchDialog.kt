package com.example.ui.screens

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

data class SearchResultItem(
    val type: String,
    val title: String,
    val subtitle: String,
    val categoryGroup: String,
    val actionRoute: String
)

@Composable
fun GlobalSearchDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val tasks by viewModel.feedItems.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val automations by viewModel.automations.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val bills by viewModel.upcomingBills.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val appointments by viewModel.appointments.collectAsState()

    val categories = listOf("All", "People", "Tasks", "Meetings", "Notes", "Finance & Bills", "Projects", "Appointments", "Automations")

    val allResults = remember(query, tasks, meetings, notes, automations, contacts, transactions, bills, projects, appointments) {
        if (query.isBlank()) emptyList()
        else {
            val q = query.trim().lowercase()
            val list = mutableListOf<SearchResultItem>()

            // 1. Contacts / People
            contacts.filter { it.name.lowercase().contains(q) || it.role.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Person", it.name, "${it.role} • ${it.phone}", "People", "contacts"))
                }

            // 2. Tasks
            tasks.filter { it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q) || (it.notes?.lowercase()?.contains(q) == true) }
                .forEach {
                    list.add(SearchResultItem("Task", it.title, "${it.time} • ${it.statusTag ?: "Priority"}", "Tasks", "tasks"))
                }

            // 3. Meetings
            meetings.filter { it.title.lowercase().contains(q) || it.platform.lowercase().contains(q) || it.attendees.any { a -> a.name.lowercase().contains(q) } }
                .forEach {
                    list.add(SearchResultItem("Meeting", it.title, "${it.time} • ${it.platform} • ${it.duration}", "Meetings", "meetings"))
                }

            // 4. Notes
            notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Note", it.title, it.content.take(60), "Notes", "notes"))
                }

            // 5. Finance & Bills
            transactions.filter { it.title.lowercase().contains(q) || it.category.lowercase().contains(q) }
                .forEach {
                    val sign = if (it.amount < 0) "-₹" else "+₹"
                    list.add(SearchResultItem("Expense", it.title, "$sign${Math.abs(it.amount).toInt()} • ${it.category}", "Finance & Bills", "finance"))
                }
            bills.filter { it.name.lowercase().contains(q) || it.department.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Bill", it.name, "₹${it.amount.toInt()} • ${it.daysLeft} • ${it.department}", "Finance & Bills", "finance"))
                }

            // 6. Projects
            projects.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Project", it.title, "${it.progressPercent}% complete • ${it.tasksCount} tasks • ${it.budget}", "Projects", "projects"))
                }

            // 7. Appointments
            appointments.filter { it.title.lowercase().contains(q) || it.category.lowercase().contains(q) || it.locationOrProvider.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Appointment", it.title, "${it.date} ${it.time} • ${it.locationOrProvider}", "Appointments", "appointments"))
                }

            // 8. Automations
            automations.filter { it.title.lowercase().contains(q) || it.thenAction.lowercase().contains(q) || it.whenTrigger.lowercase().contains(q) }
                .forEach {
                    list.add(SearchResultItem("Automation", it.title, "When: ${it.whenTrigger} → ${it.thenAction}", "Automations", "automations"))
                }

            list
        }
    }

    val filteredResults = remember(allResults, selectedCategory) {
        if (selectedCategory == "All") allResults
        else allResults.filter { it.categoryGroup == selectedCategory }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
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
                    placeholder = { Text("Search people, tasks, meetings, bills, notes...", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)) },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips (Section 6)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
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

                Spacer(modifier = Modifier.height(10.dp))

                if (query.isBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Primary, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Universal Personal Operating System Search",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Instant queries across Contacts, Tasks, Meetings, Finance, Projects & Rules",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Try searching for:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Alex", "Website Launch", "Electricity", "Car Service", "Design").forEach { sample ->
                                SuggestionChip(
                                    onClick = { query = sample },
                                    label = { Text(sample, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                } else if (filteredResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$query\" in $selectedCategory",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                        )
                    }
                } else {
                    Text(
                        text = "${filteredResults.size} results found",
                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredResults) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        when (item.actionRoute) {
                                            "meetings" -> viewModel.navigateTo("meetings")
                                            "tasks" -> viewModel.navigateTo("tasks")
                                            "notes" -> viewModel.openSubScreen("notes")
                                            "finance" -> viewModel.navigateTo("finance")
                                            "contacts" -> viewModel.openSubScreen("contacts")
                                            "projects" -> viewModel.openSubScreen("projects")
                                            "appointments" -> viewModel.openSubScreen("appointments")
                                            "automations" -> viewModel.openSubScreen("automations")
                                            else -> viewModel.showToast("Opening ${item.title}")
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
                                        text = item.type,
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
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = OnSurface
                                            )
                                        )
                                        Text(
                                            text = item.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnSurfaceVariant,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 2
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
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

