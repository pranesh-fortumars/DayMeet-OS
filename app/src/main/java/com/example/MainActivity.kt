package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DayMeetBottomDock
import com.example.ui.components.DayMeetHeader
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.screens.*
import com.example.ui.theme.InverseOnSurface
import com.example.ui.theme.InverseSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DayMeetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DayMeetApp()
            }
        }
    }
}

@Composable
fun DayMeetApp(
    viewModel: DayMeetViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val subScreen by viewModel.subScreen.collectAsStateWithLifecycle()
    val showMeetingMinutes by viewModel.showMeetingMinutes.collectAsStateWithLifecycle()
    val showAiAssistant by viewModel.showAiAssistant.collectAsStateWithLifecycle()
    val showCreateSheet by viewModel.showCreateSheet.collectAsStateWithLifecycle()
    val showDailyBriefing by viewModel.showDailyBriefing.collectAsStateWithLifecycle()
    val showSearchOverlay by viewModel.showSearchOverlay.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val appUpdateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()

    // Handle back button on sub-screens
    BackHandler(enabled = subScreen != null || showMeetingMinutes || showAiAssistant || showSearchOverlay || showDailyBriefing || showUpdateDialog) {
        if (showUpdateDialog) viewModel.dismissUpdateDialog()
        else if (showSearchOverlay) viewModel.closeSearch()
        else if (showDailyBriefing) viewModel.closeDailyBriefing()
        else if (showMeetingMinutes) viewModel.closeMeetingMinutes()
        else if (showAiAssistant) viewModel.closeAiAssistant()
        else if (subScreen != null) viewModel.closeSubScreen()
    }

    val isFullscreenOverlay = showMeetingMinutes || showAiAssistant || subScreen != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isFullscreenOverlay) {
                DayMeetHeader(
                    onSearchClick = {
                        viewModel.openSearch()
                    },
                    onNotificationsClick = {
                        viewModel.showToast("All systems synced: Calendar, Health, Tasks & Budget")
                    },
                    onProfileClick = {
                        viewModel.showToast("Alex Chen • Product Lead (DayMeet Pro)")
                    },
                    onAiClick = {
                        viewModel.openAiAssistant()
                    }
                )
            }
        },
        bottomBar = {
            if (!isFullscreenOverlay) {
                DayMeetBottomDock(
                    currentScreen = currentScreen,
                    onTabSelected = { screen -> viewModel.navigateTo(screen) },
                    onCreateClick = { viewModel.openCreateTask() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (!isFullscreenOverlay) innerPadding.calculateTopPadding() else 0.dp)
        ) {
            // Main tabs transition
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "calendar" -> CalendarScreen(viewModel = viewModel)
                    "tasks" -> TasksScreen(viewModel = viewModel)
                    "insights" -> HealthScreen(viewModel = viewModel)
                    "more" -> MoreScreen(viewModel = viewModel)
                    "meetings" -> MeetingsScreen(viewModel = viewModel)
                    "finance" -> FinanceScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
            }

            // Dedicated Subscreens
            subScreen?.let { sub ->
                when (sub) {
                    "automations" -> {
                        SubModuleContainer(
                            title = "Rules & Automations",
                            subtitle = "When → If → Then workflow engine",
                            onBack = { viewModel.closeSubScreen() }
                        ) {
                            AutomationsScreen(viewModel = viewModel)
                        }
                    }
                    "habits", "goals" -> HabitsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "notes" -> NotesSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "shopping" -> ShoppingSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "travel" -> TravelSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "documents" -> DocumentsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "contacts" -> ContactsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "subscriptions" -> SubscriptionsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                }
            }

            // Meeting Minutes Subscreen Overlay
            AnimatedVisibility(
                visible = showMeetingMinutes,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut()
            ) {
                MeetingMinutesScreen(viewModel = viewModel)
            }

            // AI Assistant Subscreen Overlay
            AnimatedVisibility(
                visible = showAiAssistant,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                AiAssistantScreen(viewModel = viewModel)
            }

            // Toast Alert Banner
            toastMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = InverseSurface.copy(alpha = 0.92f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .testTag("app_toast_banner")
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InverseOnSurface,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }

            // Create Task Bottom Sheet Modal
            if (showCreateSheet) {
                CreateTaskSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeCreateTask() }
                )
            }

            // Daily Briefing Dialog
            if (showDailyBriefing) {
                DailyBriefingDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeDailyBriefing() }
                )
            }

            // Global Search Dialog
            if (showSearchOverlay) {
                GlobalSearchDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeSearch() }
                )
            }

            // In-App Production Update Dialog
            if (showUpdateDialog && appUpdateInfo != null) {
                val context = androidx.compose.ui.platform.LocalContext.current
                InAppUpdateDialog(
                    updateInfo = appUpdateInfo!!,
                    onStartDownload = { viewModel.startAppUpdateDownload(context) },
                    onInstall = { viewModel.installDownloadedUpdate(context) },
                    onDismiss = { viewModel.dismissUpdateDialog() }
                )
            }
        }
    }
}
