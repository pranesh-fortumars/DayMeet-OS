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
import androidx.compose.foundation.shape.CornerSize
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
import com.example.model.ChatMessage
import com.example.model.ProposalItem
import com.example.model.ScheduleProposal
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

@Composable
fun AiAssistantScreen(
    viewModel: DayMeetViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    var inputQuery by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface.copy(alpha = 0.95f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.closeAiAssistant() },
                            modifier = Modifier.testTag("back_button_ai")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = OnSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(SurfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess)
                        )
                        Text(
                            text = "Calendar, Tasks & Email",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = OnSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Quick Prompts Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                QuickPromptChip("Plan my afternoon") {
                    viewModel.sendChatMessage("Plan my afternoon schedule for maximum productivity")
                }
                QuickPromptChip("Reschedule 3 PM meeting") {
                    viewModel.sendChatMessage("Can you reschedule my 3:00 PM meeting to tomorrow?")
                }
                QuickPromptChip("Summarize Q4 sync") {
                    viewModel.sendChatMessage("Please summarize the key decisions from the Q4 sync")
                }
                QuickPromptChip("Budget status today") {
                    viewModel.sendChatMessage("What is my budget status and daily allowance left?")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        msg = msg,
                        onApply = { viewModel.applyAiProposal() },
                        onModify = { viewModel.showToast("Proposal modification options") }
                    )
                }
            }

            // Follow-up suggestion pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FollowUpPill("What was postponed?") {
                    viewModel.sendChatMessage("What tasks or meetings got postponed?")
                }
                FollowUpPill("Show focus blocks") {
                    viewModel.sendChatMessage("Show my active focus blocks for today")
                }
                FollowUpPill("Send team update") {
                    viewModel.sendChatMessage("Draft a quick team update on Slack")
                }
            }

            // Bottom Input Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = SurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.showToast("Voice input listening...") },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice dictation",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    TextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = {
                            Text(
                                text = "Ask DayMeet to plan, draft or reschedule...",
                                style = MaterialTheme.typography.bodySmall.copy(color = Outline)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_input_field")
                    )

                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                viewModel.sendChatMessage(inputQuery)
                                inputQuery = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(99.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        )
    }
}

@Composable
private fun FollowUpPill(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            color = Primary,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(PrimaryFixed.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun ChatBubbleItem(
    msg: ChatMessage,
    onApply: () -> Unit,
    onModify: () -> Unit
) {
    if (msg.isUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(18.dp).copy(bottomEnd = CornerSize(4.dp)))
                    .background(Primary)
                    .padding(14.dp)
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                )
                Text(
                    text = msg.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp).copy(bottomStart = CornerSize(4.dp)))
                    .background(SurfaceContainerLowest)
                    .padding(14.dp)
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurface,
                        lineHeight = 20.sp
                    )
                )

                // Render Interactive Proposal Card if available
                if (msg.proposal != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    ProposalWidget(
                        proposal = msg.proposal,
                        onApply = onApply,
                        onModify = onModify
                    )
                }

                Text(
                    text = msg.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = OnSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ProposalWidget(
    proposal: ScheduleProposal,
    onApply: () -> Unit,
    onModify: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            proposal.rescheduled?.let { item ->
                ProposalLineItem(
                    tag = item.typeTag,
                    tagColor = AmberWarning,
                    title = item.title,
                    detail = item.detail,
                    status = item.statusTag
                )
            }

            proposal.addedBlock?.let { item ->
                ProposalLineItem(
                    tag = item.typeTag,
                    tagColor = Primary,
                    title = item.title,
                    detail = item.detail,
                    status = item.statusTag
                )
            }

            proposal.autoReminder?.let { item ->
                ProposalLineItem(
                    tag = item.typeTag,
                    tagColor = Tertiary,
                    title = item.title,
                    detail = item.detail,
                    status = item.statusTag
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Buttons
            if (proposal.isApplied) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TertiaryFixed)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = OnTertiaryFixed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Applied to Calendar & Tasks",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnTertiaryFixed
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("proposal_apply_btn")
                    ) {
                        Text(
                            text = "Apply Changes",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onModify,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerHighest,
                            contentColor = OnSurface
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "Modify",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposalLineItem(
    tag: String,
    tagColor: Color,
    title: String,
    detail: String,
    status: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLowest)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )
            )
            if (status != null) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
        )
    }
}
