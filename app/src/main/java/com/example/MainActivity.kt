package com.example

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.ui.components.DayMeetBottomDock
import com.example.ui.components.DayMeetHeader
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.LanguageSelectorSheet
import com.example.ui.components.GlobalSyncOfflineBanner
import com.example.ui.components.NetworkErrorDialog
import com.example.ui.components.ServerErrorDialog
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.UndoActionSnackbar
import com.example.ui.components.ScheduleConflictDialog
import com.example.ui.components.RescheduleSheet
import com.example.ui.components.BiometricLockScreen
import com.example.ui.components.SessionExpiredDialog
import com.example.ui.components.LogoutConfirmationDialog
import com.example.ui.components.DraftRecoveryDialog
import com.example.ui.components.ExperienceFeedbackDialog
import com.example.model.AppLaunchStep
import com.example.localization.AppLanguage
import com.example.localization.LocalAppLanguage
import com.example.localization.LocalAppStrings
import com.example.localization.LocalizationManager
import com.example.ui.screens.*
import com.example.ui.theme.InverseOnSurface
import com.example.ui.theme.InverseSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DayMeetViewModel

class MainActivity : ComponentActivity() {
    private var appViewModel: DayMeetViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.util.TaskNotificationScheduler.createNotificationChannel(this)
        enableEdgeToEdge()
        handleIncomingIntent(intent)
        setContent {
            val viewModel: DayMeetViewModel = viewModel()
            appViewModel = viewModel
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                DayMeetApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            val sharedStream = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
            if (sharedText != null || sharedStream != null) {
                appViewModel?.handleSharedIntent(sharedText, sharedStream?.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Execute update check asynchronously to avoid blocking the main UI thread during activity launch
        lifecycleScope.launch(Dispatchers.IO) {
            if (com.example.util.PlayAppUpdateManager.isGooglePlayStoreAvailable(this@MainActivity)) {
                try {
                    val playManager = com.example.util.PlayAppUpdateManager.getOrCreate(this@MainActivity)
                    playManager.appUpdateInfo.addOnSuccessListener { info ->
                        if (info.updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            try {
                                playManager.startUpdateFlowForResult(
                                    info,
                                    this@MainActivity,
                                    com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder(com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE).build(),
                                    com.example.util.PlayAppUpdateManager.PLAY_UPDATE_REQUEST_CODE
                                )
                            } catch (_: Exception) {}
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        com.example.util.PlayAppUpdateManager.unregisterInstallListener()
    }
}

@Composable
fun DayMeetApp(
    viewModel: DayMeetViewModel = viewModel()
) {
    val appLaunchStep by viewModel.appLaunchStep.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val hasSyncIssue by viewModel.hasSyncIssue.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedChangesCount.collectAsStateWithLifecycle()
    val showNetworkError by viewModel.showNetworkError.collectAsStateWithLifecycle()
    val showServerError by viewModel.showServerError.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val isSessionExpired by viewModel.isSessionExpired.collectAsStateWithLifecycle()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsStateWithLifecycle()
    val pendingDeletion by viewModel.pendingDeletion.collectAsStateWithLifecycle()
    val showUndoSnackbar by viewModel.showUndoSnackbar.collectAsStateWithLifecycle()
    val lastDeletedMessage by viewModel.lastDeletedMessage.collectAsStateWithLifecycle()
    val activeConflict by viewModel.activeConflict.collectAsStateWithLifecycle()
    val rescheduleItemTitle by viewModel.rescheduleItemTitle.collectAsStateWithLifecycle()
    val activeDraft by viewModel.activeDraft.collectAsStateWithLifecycle()
    val showIntegrationCenter by viewModel.showIntegrationCenter.collectAsStateWithLifecycle()
    val showImportExport by viewModel.showImportExport.collectAsStateWithLifecycle()
    val isExportMode by viewModel.isExportMode.collectAsStateWithLifecycle()
    val showFeedback by viewModel.showFeedback.collectAsStateWithLifecycle()
    val activePermissionRequest by viewModel.activePermissionRequest.collectAsStateWithLifecycle()

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val subScreen by viewModel.subScreen.collectAsStateWithLifecycle()
    val showMeetingMinutes by viewModel.showMeetingMinutes.collectAsStateWithLifecycle()
    val showAiAssistant by viewModel.showAiAssistant.collectAsStateWithLifecycle()
    val showCreateSheet by viewModel.showCreateSheet.collectAsStateWithLifecycle()
    val showQuickMeetingDialog by viewModel.showQuickMeetingDialog.collectAsStateWithLifecycle()
    val showScheduleMeetingModal by viewModel.showScheduleMeetingModal.collectAsStateWithLifecycle()
    val showDailyBriefing by viewModel.showDailyBriefing.collectAsStateWithLifecycle()
    val showMorningKickoff by viewModel.showMorningKickoff.collectAsStateWithLifecycle()
    val showEveningWindDown by viewModel.showEveningWindDown.collectAsStateWithLifecycle()
    val showWeeklyReviewDialog by viewModel.showWeeklyReviewDialog.collectAsStateWithLifecycle()
    val showSearchOverlay by viewModel.showSearchOverlay.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val appUpdateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()
    val showConfetti by viewModel.showConfetti.collectAsStateWithLifecycle()
    val confettiMilestone by viewModel.confettiMilestone.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncedTime by viewModel.lastSyncedTime.collectAsStateWithLifecycle()
    val isFocusModeActive by viewModel.isFocusModeActive.collectAsStateWithLifecycle()
    val currentLifeMode by viewModel.currentLifeMode.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()
    val appStrings = remember(currentLanguage) { LocalizationManager.getStrings(currentLanguage) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    // Check for updates smoothly in background after initial composition and layout settles
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        viewModel.checkForAppUpdates(context = context, manual = false)
    }

    // Handle App Launch Screens (Splash & Initialization)
    if (appLaunchStep == AppLaunchStep.SPLASH) {
        DayMeetSplashScreen(onSplashFinished = { viewModel.finishSplash() })
        return
    }

    if (appLaunchStep == AppLaunchStep.INITIALIZING) {
        DayMeetInitializationScreen(onInitializationComplete = { viewModel.finishInitialization() })
        return
    }

    // Handle back button on sub-screens
    BackHandler(enabled = showLanguageDialog || showScheduleMeetingModal || isFocusModeActive || subScreen != null || showMeetingMinutes || showAiAssistant || showSearchOverlay || showDailyBriefing || showUpdateDialog) {
        if (showLanguageDialog) viewModel.closeLanguageSelector()
        else if (showScheduleMeetingModal) viewModel.closeScheduleMeeting()
        else if (showUpdateDialog) viewModel.dismissUpdateDialog(context)
        else if (isFocusModeActive) viewModel.toggleFocusMode()
        else if (showSearchOverlay) viewModel.closeSearch()
        else if (showDailyBriefing) viewModel.closeDailyBriefing()
        else if (showMeetingMinutes) viewModel.closeMeetingMinutes()
        else if (showAiAssistant) viewModel.closeAiAssistant()
        else if (subScreen != null) viewModel.closeSubScreen()
    }

    val isFullscreenOverlay = showMeetingMinutes || showAiAssistant || subScreen != null

    CompositionLocalProvider(
        LocalAppStrings provides appStrings,
        LocalAppLanguage provides currentLanguage
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                if (!isFullscreenOverlay) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        DayMeetHeader(
                            isSyncing = isSyncing,
                            lastSyncedText = lastSyncedTime,
                            isFocusModeActive = isFocusModeActive,
                            currentLanguage = currentLanguage,
                            lifeMode = currentLifeMode,
                            onLifeModeClick = {
                                viewModel.cycleLifeMode()
                            },
                            onLanguageClick = {
                                viewModel.openLanguageSelector()
                            },
                            onFocusClick = {
                                viewModel.toggleFocusMode()
                            },
                            onSyncClick = {
                                viewModel.triggerManualSync()
                            },
                            onSearchClick = {
                                viewModel.openSearch()
                            },
                            onNotificationsClick = {
                                if (isFocusModeActive) {
                                    viewModel.showToast("🤫 Notifications muted: 12 non-urgent alerts silenced in Focus Mode")
                                } else {
                                    viewModel.showToast("All systems synced: Calendar, Health, Tasks & Budget")
                                }
                            },
                            onProfileClick = {
                                viewModel.showToast("Alex Chen • Product Lead (DayMeet Pro)")
                            },
                            onAiClick = {
                                viewModel.openAiAssistant()
                            }
                        )

                        GlobalSyncOfflineBanner(
                            isOffline = isOffline,
                            isSyncing = isSyncing,
                            hasSyncIssue = hasSyncIssue,
                            unsyncedCount = unsyncedCount,
                            onRetrySync = { viewModel.resolveSyncIssue() },
                            onViewSyncDetails = { viewModel.showToast("Sync queue: $unsyncedCount offline operations ready") }
                        )
                    }
                }
            },
        bottomBar = {
            if (!isFullscreenOverlay && !isFocusModeActive) {
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
            if (isFocusModeActive) {
                SimplifiedFocusModeView(viewModel = viewModel)
            } else {
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
                    "projects" -> ProjectsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "appointments" -> AppointmentsSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "home_vehicle" -> HomeVehicleSubScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                    "life_inbox" -> LifeInboxScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
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

            // Schedule Meeting Modal (with Participants & Duration selection)
            if (showScheduleMeetingModal) {
                ScheduleMeetingModal(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeScheduleMeeting() }
                )
            }

            // Quick Schedule Meeting Dialog (From Quick Capture Hub)
            if (showQuickMeetingDialog) {
                QuickScheduleMeetingDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeQuickScheduleMeeting() }
                )
            }

            // Daily Briefing Dialog
            if (showDailyBriefing) {
                DailyBriefingDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeDailyBriefing() }
                )
            }

            // Morning Kickoff Dialog (Phase 2 Life OS)
            if (showMorningKickoff) {
                MorningKickoffDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeMorningKickoff() }
                )
            }

            // Evening Wind-Down Dialog (Phase 2 Life OS)
            if (showEveningWindDown) {
                EveningWindDownDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeEveningWindDown() }
                )
            }

            // Weekly Review Dialog (Phase 3 Life OS)
            if (showWeeklyReviewDialog) {
                WeeklyReviewDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeWeeklyReview() }
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
                InAppUpdateDialog(
                    updateInfo = appUpdateInfo!!,
                    onStartDownload = {
                        if (activity != null) {
                            viewModel.launchUpdate(activity)
                        } else {
                            viewModel.startAppUpdateDownload(context)
                        }
                    },
                    onInstall = {
                        if (appUpdateInfo?.updateChannel == com.example.model.UpdateChannel.GOOGLE_PLAY) {
                            viewModel.completePlayUpdate(context)
                        } else {
                            viewModel.installDownloadedUpdate(context)
                        }
                    },
                    onDismiss = { viewModel.dismissUpdateDialog(context) }
                )
            }

            // Confetti Overlay for habit streak records
            ConfettiOverlay(
                visible = showConfetti,
                milestoneText = confettiMilestone,
                onDismiss = { viewModel.dismissConfetti() }
            )

            // Dynamic Multilingual Selector Bottom Sheet
            if (showLanguageDialog) {
                LanguageSelectorSheet(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { lang ->
                        viewModel.setLanguage(lang)
                    },
                    onDismissRequest = {
                        viewModel.closeLanguageSelector()
                    }
                )
            }

            // Undo Action Floating Snackbar
            if (showUndoSnackbar) {
                UndoActionSnackbar(
                    message = lastDeletedMessage,
                    onUndo = { viewModel.performUndo() },
                    onDismiss = { viewModel.dismissUndoSnackbar() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 76.dp)
                )
            }

            // Network Error Dialog
            if (showNetworkError) {
                NetworkErrorDialog(
                    onRetry = {
                        viewModel.closeNetworkError()
                        viewModel.triggerManualSync()
                    },
                    onContinueOffline = {
                        viewModel.closeNetworkError()
                        viewModel.toggleOfflineMode()
                    },
                    onDismiss = { viewModel.closeNetworkError() }
                )
            }

            // Server Error Dialog
            if (showServerError) {
                ServerErrorDialog(
                    referenceId = "ERR-9428",
                    onRetry = {
                        viewModel.closeServerError()
                        viewModel.triggerManualSync()
                    },
                    onGoBack = { viewModel.closeServerError() }
                )
            }

            // Destructive / Bulk Delete Confirmation Dialog
            pendingDeletion?.let { del ->
                DeleteConfirmationDialog(
                    title = "Delete ${del.module}?",
                    message = "Are you sure you want to delete \"${del.title}\"? This action can be undone briefly.",
                    onConfirm = { viewModel.confirmPendingDeletion() },
                    onDismiss = { viewModel.cancelPendingDeletion() }
                )
            }

            // Schedule Conflict Detection Dialog
            activeConflict?.let { conflict ->
                ScheduleConflictDialog(
                    existingMeetingTitle = conflict.existingTitle,
                    existingMeetingTime = conflict.existingTime,
                    newMeetingTitle = conflict.newTitle,
                    newMeetingTime = conflict.newTime,
                    conflictDuration = conflict.conflictDuration,
                    onChooseAnotherTime = {
                        viewModel.closeScheduleConflict()
                        viewModel.openRescheduleSheet(conflict.newTitle)
                    },
                    onScheduleAnyway = {
                        viewModel.closeScheduleConflict()
                        viewModel.showToast("Scheduled anyway (Overlap acknowledged)")
                    },
                    onCancel = { viewModel.closeScheduleConflict() }
                )
            }

            // Reschedule Flow Sheet
            rescheduleItemTitle?.let { title ->
                RescheduleSheet(
                    itemTitle = title,
                    onReschedule = { newSlot -> viewModel.applyReschedule(newSlot) },
                    onDismiss = { viewModel.closeRescheduleSheet() }
                )
            }

            // Biometric / App Vault Lock Screen
            if (isAppLocked) {
                BiometricLockScreen(
                    onUnlockSuccess = { viewModel.unlockVault() },
                    onCancel = { viewModel.unlockVault() }
                )
            }

            // Session Expired Dialog
            if (isSessionExpired) {
                SessionExpiredDialog(
                    onSignInAgain = { viewModel.restoreSession() }
                )
            }

            // Logout with Unsynced Changes Dialog
            if (showLogoutDialog) {
                LogoutConfirmationDialog(
                    unsyncedCount = unsyncedCount,
                    onSyncAndLogout = {
                        viewModel.triggerManualSync()
                        viewModel.performLogout()
                    },
                    onLogoutAnyway = { viewModel.performLogout() },
                    onCancel = { viewModel.closeLogoutDialog() }
                )
            }

            // Draft Recovery Dialog
            activeDraft?.let { draft ->
                DraftRecoveryDialog(
                    draftType = draft.type,
                    draftTitle = draft.title,
                    onContinueDraft = { viewModel.restoreDraft() },
                    onDiscardDraft = { viewModel.dismissDraft() }
                )
            }

            // Experience Feedback Dialog
            if (showFeedback) {
                ExperienceFeedbackDialog(
                    onSubmit = { rating, note -> viewModel.submitFeedback(rating, note) },
                    onDismiss = { viewModel.closeFeedback() }
                )
            }

            // Connected Apps & Integration Center
            if (showIntegrationCenter) {
                IntegrationCenterDialog(
                    onDismiss = { viewModel.closeIntegrationCenter() },
                    onToggleIntegration = { name -> viewModel.showToast("Updated integration: $name") }
                )
            }

            // Data Import & Export Dialog
            if (showImportExport) {
                DataImportExportDialog(
                    isExport = isExportMode,
                    onDismiss = { viewModel.closeImportExport() },
                    onExecute = { viewModel.showToast("Data operation finished successfully.") }
                )
            }

            // Contextual Permission Onboarding Modal
            activePermissionRequest?.let { reqType ->
                ContextualPermissionDialog(
                    permissionType = reqType,
                    onAllow = { viewModel.grantPermission() },
                    onNotNow = { viewModel.denyPermission() }
                )
            }
        }
    }
}
}
