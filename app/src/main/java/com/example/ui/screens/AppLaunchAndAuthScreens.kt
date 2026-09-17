package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 1. APP LAUNCH / SPLASH SCREEN
 * Displays DayMeet logo, tagline "Your day. Your life. One place." with subtle animation.
 */
@Composable
fun DayMeetSplashScreen(
    onSplashFinished: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.82f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run smooth entrance animation
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        alphaAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(400)
        )
        // Brief splash duration before entering initialization
        delay(950)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        Primary.copy(alpha = 0.05f)
                    )
                )
            )
            .testTag("app_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.scale(scaleAnim.value)
        ) {
            // DayMeet Premium App Logo Box
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Primary)
                    .border(2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "DayMeet Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(80.dp)
                )
            }

            // Title
            Text(
                text = "DayMeet",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
            )

            // Tagline
            Text(
                text = "Your day. Your life. One place.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }

        // Subtext bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "PERSONAL OPERATING SYSTEM",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

/**
 * 2. APP INITIALIZATION LOADING SCREEN
 * Displays progress indicators: Account verified, Local data loaded, Synchronizing data, Preparing notifications
 * Includes timeout recovery so the user is never stranded on an infinite spinner.
 */
@Composable
fun DayMeetInitializationScreen(
    onInitializationComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var isTakingLonger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        step = 2 // Local data loaded
        delay(450)
        step = 3 // Synchronizing data
        delay(500)
        step = 4 // Preparing notifications
        delay(350)
        onInitializationComplete()
    }

    // Safety timeout fallback: If takes > 2.5s, offer manual skip
    LaunchedEffect(Unit) {
        delay(2200)
        isTakingLonger = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)
            .testTag("app_initialization_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(54.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Preparing your workspace…",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Setting up your personal operating system",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            // Progressive check states
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProgressStepRow(title = "Account verified", state = if (step >= 1) StepState.DONE else StepState.ACTIVE)
                    ProgressStepRow(title = "Local data loaded", state = if (step >= 2) StepState.DONE else if (step == 1) StepState.ACTIVE else StepState.PENDING)
                    ProgressStepRow(title = "Synchronizing data", state = if (step >= 3) StepState.DONE else if (step == 2) StepState.ACTIVE else StepState.PENDING)
                    ProgressStepRow(title = "Preparing notifications", state = if (step >= 4) StepState.DONE else if (step == 3) StepState.ACTIVE else StepState.PENDING)
                }
            }

            if (isTakingLonger) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Still preparing your workspace…",
                        style = MaterialTheme.typography.bodySmall.copy(color = AmberWarning, fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onInitializationComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue to Workspace")
                    }
                }
            }
        }
    }
}

private enum class StepState { DONE, ACTIVE, PENDING }

@Composable
private fun ProgressStepRow(title: String, state: StepState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (state) {
            StepState.DONE -> {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            StepState.ACTIVE -> {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Primary)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
            StepState.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}

/**
 * 11 & 12. PERMISSION ONBOARDING & CONTEXTUAL FLOW MODAL
 * Contextual explanation before system prompt: Notifications, Calendar, Contacts, Location, Health
 */
@Composable
fun ContextualPermissionDialog(
    permissionType: PermissionFlowType,
    onAllow: () -> Unit,
    onNotNow: () -> Unit
) {
    val (icon, title, explanation) = when (permissionType) {
        PermissionFlowType.NOTIFICATIONS -> Triple(
            Icons.Default.NotificationsActive,
            "Enable Notifications",
            "Allow notifications so DayMeet can remind you about upcoming meetings, tasks, bills and important life events."
        )
        PermissionFlowType.CALENDAR -> Triple(
            Icons.Default.Event,
            "Connect System Calendar",
            "Allow DayMeet to synchronize with your device calendar to prevent overlapping appointments and auto-schedule meetings."
        )
        PermissionFlowType.LOCATION -> Triple(
            Icons.Default.LocationOn,
            "Location Reminders",
            "DayMeet can trigger smart reminders when you arrive at or leave home, office, or specific meeting venues."
        )
        PermissionFlowType.HEALTH -> Triple(
            Icons.Default.Favorite,
            "Connect Health Data",
            "Track your daily step goals, hydration, sleep quality, and active minutes alongside your daily schedule."
        )
    }

    AlertDialog(
        onDismissRequest = onNotNow,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
            }
        },
        title = {
            Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(explanation, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), textAlign = TextAlign.Center)

                if (permissionType == PermissionFlowType.HEALTH) {
                    // Granular categories
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Requested categories:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Steps", "Sleep", "Exercise", "Heart Rate").forEach { cat ->
                                    Surface(shape = RoundedCornerShape(6.dp), color = Primary.copy(alpha = 0.1f)) {
                                        Text(cat, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Primary))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAllow,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("permission_allow_btn")
            ) {
                Text(if (permissionType == PermissionFlowType.LOCATION) "Allow while using" else "Allow", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNotNow,
                modifier = Modifier.testTag("permission_not_now_btn")
            ) {
                Text("Not now")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

enum class PermissionFlowType {
    NOTIFICATIONS,
    CALENDAR,
    LOCATION,
    HEALTH
}

/**
 * 12. PERMISSION DENIED STATE BANNER
 */
@Composable
fun PermissionDeniedCard(
    featureName: String = "Notifications",
    impactDescription: String = "Automated alerts for meetings and due tasks are paused.",
    onOpenSettings: () -> Unit,
    onContinueWithout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                Text("$featureName are turned off", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE65100)))
            }
            Text(impactDescription, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF5D4037)))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Enable in Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = onContinueWithout,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Continue without it", fontSize = 11.sp, color = Color(0xFF5D4037))
                }
            }
        }
    }
}

/**
 * 10. FIRST DATA EXPERIENCE / GUIDED SAMPLE DAY BANNER
 */
@Composable
fun FirstDataExperienceBanner(
    onUseSampleData: () -> Unit,
    onStartScratch: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth().testTag("first_data_experience_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(PrimaryFixed), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                    }
                    Text("Welcome to DayMeet!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                }
            }

            Text(
                text = "Would you like to explore DayMeet with a guided sample day? (Morning Routine, Focus Session, Team Meeting, Lunch & Project Tasks) You can clear it anytime with one tap.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onUseSampleData,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("use_sample_day_btn")
                ) {
                    Text("Use Sample Day", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onStartScratch,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("start_scratch_btn")
                ) {
                    Text("Start from Scratch", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * 38. INTEGRATION CENTER: Connected Apps Overview
 */
@Composable
fun IntegrationCenterDialog(
    onDismiss: () -> Unit,
    onToggleIntegration: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Connected Apps & Services", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Manage integrations with DayMeet Personal OS", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                data class IntegrationItem(
                    val name: String,
                    val description: String,
                    val status: String,
                    val icon: ImageVector
                )

                val integrations = listOf(
                    IntegrationItem("Google Calendar", "Calendar & Meet synchronization", "Connected", Icons.Default.CalendarMonth),
                    IntegrationItem("Health Connect", "Steps, sleep & active calories", "Connected", Icons.Default.Favorite),
                    IntegrationItem("Slack & WhatsApp", "Scheduled automated messages", "Connected", Icons.Default.Chat),
                    IntegrationItem("Google Drive Vault", "Encrypted document backups", "Needs Attention", Icons.Default.CloudQueue),
                    IntegrationItem("Outlook Calendar", "Secondary work schedule", "Disconnected", Icons.Default.Mail)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(integrations) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Primary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                                        Icon(item.icon, contentDescription = null, tint = Primary)
                                    }
                                    Column {
                                        Text(item.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(item.description, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (item.status) {
                                        "Connected" -> EmeraldSuccess.copy(alpha = 0.15f)
                                        "Needs Attention" -> AmberWarning.copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Text(
                                        text = item.status,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.status) {
                                                "Connected" -> EmeraldSuccess
                                                "Needs Attention" -> AmberWarning
                                                else -> MaterialTheme.colorScheme.outline
                                            }
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

/**
 * 39 & 40. DATA IMPORT & EXPORT DIALOG
 */
@Composable
fun DataImportExportDialog(
    isExport: Boolean = true,
    onDismiss: () -> Unit,
    onExecute: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isExport) "Export Workspace Data" else "Import Workspace Data", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isFinished) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                        Text(if (isExport) "142 items securely packaged to DayMeet_Backup.json" else "142 items imported into workspace!", fontWeight = FontWeight.Bold)
                    }
                } else if (isProcessing) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        CircularProgressIndicator(color = Primary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(if (isExport) "Preparing encrypted export package…" else "Verifying & importing entries…", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Text(
                        text = if (isExport) "Export your tasks, meetings, calendar events, finance logs, notes, habits and goals into a secure JSON backup."
                        else "Restore your DayMeet workspace from a previously exported backup file.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        },
        confirmButton = {
            if (!isFinished) {
                Button(
                    onClick = {
                        isProcessing = true
                        // Simulate operation
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !isProcessing
                ) {
                    Text(if (isExport) "Export Data" else "Select File & Import")
                }
            } else {
                Button(onClick = onDismiss) { Text("Done") }
            }
        },
        dismissButton = {
            if (!isFinished) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(1500)
            isProcessing = false
            isFinished = true
            onExecute()
        }
    }
}
