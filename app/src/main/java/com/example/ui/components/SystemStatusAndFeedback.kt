package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

/**
 * Global persistent synchronization and offline banner that docks cleanly below the top app bar.
 */
@Composable
fun GlobalSyncOfflineBanner(
    isOffline: Boolean,
    isSyncing: Boolean,
    hasSyncIssue: Boolean,
    unsyncedCount: Int = 0,
    onRetrySync: () -> Unit = {},
    onViewSyncDetails: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOffline || isSyncing || hasSyncIssue,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        val (bgColor, contentColor, icon, text) = when {
            isOffline -> Quad(
                Color(0xFF37474F),
                Color.White,
                Icons.Default.CloudOff,
                if (unsyncedCount > 0) "Offline — $unsyncedCount changes queued to sync"
                else "Offline — changes will sync when you're back online."
            )
            hasSyncIssue -> Quad(
                Color(0xFFD32F2F),
                Color.White,
                Icons.Default.SyncProblem,
                "Some changes couldn't be synced."
            )
            isSyncing -> Quad(
                Primary,
                Color.White,
                Icons.Default.Sync,
                "Syncing your changes…"
            )
            else -> Quad(
                EmeraldSuccess,
                Color.White,
                Icons.Default.CheckCircle,
                "Everything is up to date."
            )
        }

        Surface(
            color = bgColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth().testTag("global_sync_offline_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSyncing) {
                        val infiniteTransition = rememberInfiniteTransition(label = "banner_sync_spin")
                        val angle by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "banner_sync_angle"
                        )
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp).rotate(angle)
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (hasSyncIssue) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = onRetrySync,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("Retry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        TextButton(
                            onClick = onViewSyncDetails,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("Details", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Friendly Network Error Dialog (No technical jargon)
 */
@Composable
fun NetworkErrorDialog(
    onRetry: () -> Unit,
    onContinueOffline: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = AmberWarning,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Unable to connect",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Check your internet connection and try again. You can continue offline — your tasks, notes, calendar and finance changes will sync automatically when your connection returns.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("network_error_retry_btn")
            ) {
                Text("Retry Connection", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onContinueOffline,
                modifier = Modifier.testTag("network_error_offline_btn")
            ) {
                Text("Continue Offline")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("network_error_dialog")
    )
}

/**
 * Server Error Dialog with human-friendly explanation and reference code
 */
@Composable
fun ServerErrorDialog(
    referenceId: String = "ERR-7492",
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onGoBack,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "We couldn't complete that request right now. Your data is safe locally. Please try again in a moment.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Support Ref: $referenceId",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("server_error_retry_btn")
            ) {
                Text("Retry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onGoBack,
                modifier = Modifier.testTag("server_error_back_btn")
            ) {
                Text("Go Back")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("server_error_dialog")
    )
}

/**
 * Delete Confirmation Dialog supporting single and bulk deletion with clear count
 */
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String = "Delete",
    isDestructive: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) Color(0xFFD32F2F) else Primary
                ),
                modifier = Modifier.testTag("dialog_confirm_delete_btn")
            ) {
                Text(confirmLabel, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_cancel_delete_btn")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("delete_confirmation_dialog")
    )
}

/**
 * Undo Action floating pill / snackbar
 */
@Composable
fun UndoActionSnackbar(
    message: String,
    onUndo: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("undo_action_snackbar")
    ) {
        Row(
            modifier = Modifier.padding(start = 18.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onUndo,
                    modifier = Modifier.testTag("undo_btn")
                ) {
                    Text(
                        text = "UNDO",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = PrimaryFixed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Schedule Conflict Detection Dialog
 */
@Composable
fun ScheduleConflictDialog(
    existingMeetingTitle: String,
    existingMeetingTime: String,
    newMeetingTitle: String,
    newMeetingTime: String,
    conflictDuration: String = "30 mins",
    onChooseAnotherTime: () -> Unit,
    onScheduleAnyway: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = AmberWarning,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = "Schedule conflict detected",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "The selected time overlaps by $conflictDuration with another scheduled event on your calendar.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                // Existing event card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Existing Event", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline))
                        Text(existingMeetingTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(existingMeetingTime, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                    }
                }

                // New event card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("New Event", style = MaterialTheme.typography.labelSmall.copy(color = Primary))
                        Text(newMeetingTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(newMeetingTime, style = MaterialTheme.typography.labelSmall.copy(color = Primary))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onChooseAnotherTime,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("conflict_choose_time_btn")
            ) {
                Text("Find Free Slot", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onScheduleAnyway,
                modifier = Modifier.testTag("conflict_schedule_anyway_btn")
            ) {
                Text("Schedule Anyway")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("schedule_conflict_dialog")
    )
}

/**
 * Reschedule Flow Sheet / Dialog (Tomorrow, Next Week, Custom)
 */
@Composable
fun RescheduleSheet(
    itemTitle: String,
    onReschedule: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().testTag("reschedule_sheet_card")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reschedule Item",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Text(
                    text = "Move \"$itemTitle\" to:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                val options = listOf(
                    Triple("Tomorrow", "Tomorrow, 09:00 AM", Icons.Default.CalendarToday),
                    Triple("This Weekend", "Saturday, 10:00 AM", Icons.Default.Weekend),
                    Triple("Next Week", "Next Monday, 09:00 AM", Icons.Default.DateRange),
                    Triple("Someday / Later", "No specific due date", Icons.Default.Inbox)
                )

                options.forEach { (label, sublabel, icon) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReschedule(sublabel) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(sublabel, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Biometric / PIN App Lock Screen for sensitive data (Finance, Vault, Documents, Health)
 */
@Composable
fun BiometricLockScreen(
    lockTitle: String = "DayMeet Vault Locked",
    lockSubtitle: String = "Unlock with Biometrics or PIN to access sensitive financial & personal data",
    onUnlockSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var hasPinError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top close button
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }

                // Lock header & Icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = lockTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = lockSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )

                    // PIN Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        repeat(4) { index ->
                            val isFilled = index < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            hasPinError -> Color(0xFFD32F2F)
                                            isFilled -> Primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                            )
                        }
                    }

                    if (hasPinError) {
                        Text(
                            text = "Incorrect PIN. Try 1234 or use fingerprint.",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD32F2F))
                        )
                    }
                }

                // Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val keyRows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("BIO", "0", "DEL")
                    )

                    keyRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                Surface(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            when (key) {
                                                "BIO" -> {
                                                    // Simulate biometric unlock (fingerprint / face ID)
                                                    onUnlockSuccess()
                                                }
                                                "DEL" -> {
                                                    if (enteredPin.isNotEmpty()) {
                                                        enteredPin = enteredPin.dropLast(1)
                                                        hasPinError = false
                                                    }
                                                }
                                                else -> {
                                                    if (enteredPin.length < 4) {
                                                        enteredPin += key
                                                        if (enteredPin.length == 4) {
                                                            if (enteredPin == "1234" || enteredPin == "0000") {
                                                                onUnlockSuccess()
                                                            } else {
                                                                hasPinError = true
                                                                enteredPin = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        when (key) {
                                            "BIO" -> Icon(Icons.Default.Fingerprint, contentDescription = "Biometric", tint = Primary)
                                            "DEL" -> Icon(Icons.Default.ArrowBack, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface)
                                            else -> Text(key, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom help
                TextButton(onClick = { onUnlockSuccess() }) {
                    Text("Bypass with Master Key (Demo)", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline))
                }
            }
        }
    }
}

/**
 * Session Expiration Dialog with data preservation note
 */
@Composable
fun SessionExpiredDialog(
    onSignInAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSignInAgain,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(26.dp))
            }
        },
        title = {
            Text(
                text = "Your session has expired",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "For your security, DayMeet sessions time out periodically. Any unsaved drafts have been safely preserved locally on this device.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onSignInAgain,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.fillMaxWidth().testTag("sign_in_again_btn")
            ) {
                Text("Sign In Again", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("session_expired_dialog")
    )
}

/**
 * Logout Confirmation Dialog checking for unsynced changes
 */
@Composable
fun LogoutConfirmationDialog(
    unsyncedCount: Int = 0,
    onSyncAndLogout: () -> Unit,
    onLogoutAnyway: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text("Log out of DayMeet?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            if (unsyncedCount > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "You have $unsyncedCount changes waiting to sync with the cloud.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Logging out before syncing might cause changes on this device to be delayed until next sign-in.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            } else {
                Text("Are you sure you want to log out? Your local data is securely encrypted.", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            if (unsyncedCount > 0) {
                Button(
                    onClick = onSyncAndLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.testTag("sync_and_logout_btn")
                ) {
                    Text("Sync & Logout")
                }
            } else {
                Button(
                    onClick = onLogoutAnyway,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.testTag("logout_confirm_btn")
                ) {
                    Text("Log Out")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Draft Recovery Dialog: "Continue your draft?"
 */
@Composable
fun DraftRecoveryDialog(
    draftType: String = "Task",
    draftTitle: String,
    onContinueDraft: () -> Unit,
    onDiscardDraft: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDiscardDraft,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.RestorePage, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Text(
                text = "Continue your draft?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You were working on an unfinished $draftType: \"$draftTitle\". Would you like to pick up where you left off?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinueDraft,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("continue_draft_btn")
            ) {
                Text("Continue Draft", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscardDraft,
                modifier = Modifier.testTag("discard_draft_btn")
            ) {
                Text("Discard Draft", color = Color(0xFFD32F2F))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Experience Feedback Dialog
 */
@Composable
fun ExperienceFeedbackDialog(
    onSubmit: (rating: String, feedback: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRating by remember { mutableStateOf("Very easy") }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("How was your experience?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your feedback helps shape DayMeet's personal operating system.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

                val ratings = listOf("Very easy", "Easy", "Okay", "Difficult")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ratings.forEach { r ->
                        val isSelected = selectedRating == r
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f).clickable { selectedRating = r }
                        ) {
                            Text(
                                text = r,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Any suggestions? (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedRating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Submit Feedback")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not Now") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Unobtrusive Auto-Save Indicator for long-form editors
 */
@Composable
fun AutoSaveIndicator(
    status: AutoSaveStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (status) {
                AutoSaveStatus.SAVING -> {
                    CircularProgressIndicator(modifier = Modifier.size(10.dp), strokeWidth = 1.5.dp, color = Primary)
                    Text("Saving…", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Primary))
                }
                AutoSaveStatus.SAVED -> {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(10.dp))
                    Text("Saved", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = EmeraldSuccess))
                }
                AutoSaveStatus.OFFLINE_SAVED -> {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(10.dp))
                    Text("Saved locally", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AmberWarning))
                }
            }
        }
    }
}

enum class AutoSaveStatus {
    SAVING,
    SAVED,
    OFFLINE_SAVED
}
