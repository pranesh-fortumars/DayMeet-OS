package com.example.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.DayMeetRepository
import com.example.localization.AppLanguage
import com.example.localization.LocalizationManager
import com.example.model.*
import com.example.util.AppUpdateManager
import com.example.util.PlayAppUpdateManager
import com.example.util.TaskNotificationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MinuteActionItem(
    val id: String,
    val title: String,
    val assignee: String,
    val due: String,
    val priority: String,
    val isDone: Boolean = false,
    val isConverted: Boolean = false
)

class DayMeetViewModel : ViewModel() {

    // Global navigation & modals
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Subscreen stack or overlays
    private val _subScreen = MutableStateFlow<String?>(null)
    val subScreen: StateFlow<String?> = _subScreen.asStateFlow()

    private val _showCreateSheet = MutableStateFlow(false)
    val showCreateSheet: StateFlow<Boolean> = _showCreateSheet.asStateFlow()

    private val _showQuickMeetingDialog = MutableStateFlow(false)
    val showQuickMeetingDialog: StateFlow<Boolean> = _showQuickMeetingDialog.asStateFlow()

    private val _showScheduleMeetingModal = MutableStateFlow(false)
    val showScheduleMeetingModal: StateFlow<Boolean> = _showScheduleMeetingModal.asStateFlow()

    private val _quickAddInitialTab = MutableStateFlow("Task")
    val quickAddInitialTab: StateFlow<String> = _quickAddInitialTab.asStateFlow()

    private val _showMeetingMinutes = MutableStateFlow(false)
    val showMeetingMinutes: StateFlow<Boolean> = _showMeetingMinutes.asStateFlow()

    private val _showAiAssistant = MutableStateFlow(false)
    val showAiAssistant: StateFlow<Boolean> = _showAiAssistant.asStateFlow()

    private val _showDailyBriefing = MutableStateFlow(false)
    val showDailyBriefing: StateFlow<Boolean> = _showDailyBriefing.asStateFlow()

    private val _showSearchOverlay = MutableStateFlow(false)
    val showSearchOverlay: StateFlow<Boolean> = _showSearchOverlay.asStateFlow()

    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    // Home feed & filter
    private val _feedFilter = MutableStateFlow(FeedCategory.ALL)
    val feedFilter: StateFlow<FeedCategory> = _feedFilter.asStateFlow()

    private val _feedItems = MutableStateFlow(DayMeetRepository.getInitialFeedItems())
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

    // Cross-Module Stream items
    private val _crossStreamItems = MutableStateFlow(DayMeetRepository.getInitialCrossStreamItems())
    val crossStreamItems: StateFlow<List<CrossStreamItem>> = _crossStreamItems.asStateFlow()

    // Health & Wellness
    private val _healthMetrics = MutableStateFlow(DayMeetRepository.getInitialHealthMetrics())
    val healthMetrics: StateFlow<HealthMetrics> = _healthMetrics.asStateFlow()

    // Automations & Rules Engine
    private val _automations = MutableStateFlow(DayMeetRepository.getInitialAutomations())
    val automations: StateFlow<List<AutomationWorkflow>> = _automations.asStateFlow()

    private val _automationLogs = MutableStateFlow(DayMeetRepository.getInitialAutomationLogs())
    val automationLogs: StateFlow<List<AutomationLog>> = _automationLogs.asStateFlow()

    // Goals & Habits
    private val _goals = MutableStateFlow(DayMeetRepository.getInitialGoals())
    val goals: StateFlow<List<GoalItem>> = _goals.asStateFlow()

    private val _habits = MutableStateFlow(DayMeetRepository.getInitialHabits())
    val habits: StateFlow<List<HabitItem>> = _habits.asStateFlow()

    // Daily Habit Goal Tracking (Non-Routine Tasks)
    private val _nonRoutineTasks = MutableStateFlow(DayMeetRepository.getInitialNonRoutineTasks())
    val nonRoutineTasks: StateFlow<List<NonRoutineTask>> = _nonRoutineTasks.asStateFlow()

    // System-wide Dark Mode & Tailwind Theme Engine
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _themeMode = MutableStateFlow("light") // "light", "dark", "system"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Confetti Animation State for habit streak records
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _confettiMilestone = MutableStateFlow<String?>(null)
    val confettiMilestone: StateFlow<String?> = _confettiMilestone.asStateFlow()

    // Notes & Knowledge
    private val _notes = MutableStateFlow(DayMeetRepository.getInitialNotes())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    // Shopping
    private val _shoppingItems = MutableStateFlow(DayMeetRepository.getInitialShoppingItems())
    val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

    // Travel
    private val _trip = MutableStateFlow(DayMeetRepository.getInitialTrip())
    val trip: StateFlow<TravelTrip> = _trip.asStateFlow()

    // Communication & Contacts
    private val _contacts = MutableStateFlow(DayMeetRepository.getInitialContacts())
    val contacts: StateFlow<List<ContactItem>> = _contacts.asStateFlow()

    private val _scheduledMessages = MutableStateFlow(DayMeetRepository.getInitialScheduledMessages())
    val scheduledMessages: StateFlow<List<ScheduledMessage>> = _scheduledMessages.asStateFlow()

    // Documents & Subscriptions
    private val _documents = MutableStateFlow(DayMeetRepository.getInitialDocuments())
    val documents: StateFlow<List<DocumentItem>> = _documents.asStateFlow()

    private val _subscriptions = MutableStateFlow(DayMeetRepository.getInitialSubscriptions())
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    // Reminders
    private val _reminders = MutableStateFlow(DayMeetRepository.getInitialReminders())
    val reminders: StateFlow<List<SmartReminder>> = _reminders.asStateFlow()

    // Focus Session state
    private val _focusTimerRemaining = MutableStateFlow(28 * 60 + 40)
    val focusTimerRemaining: StateFlow<Int> = _focusTimerRemaining.asStateFlow()

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private val _isFocusCompleted = MutableStateFlow(false)
    val isFocusCompleted: StateFlow<Boolean> = _isFocusCompleted.asStateFlow()

    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    private val _selectedFocusTaskId = MutableStateFlow<String?>(null)
    val selectedFocusTaskId: StateFlow<String?> = _selectedFocusTaskId.asStateFlow()

    // Multilingual & Dynamic Localization State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    // Calendar state
    private val _selectedDay = MutableStateFlow(24)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _calendarMode = MutableStateFlow("Chronological")
    val calendarMode: StateFlow<String> = _calendarMode.asStateFlow()

    private val _calendarCategoryFilter = MutableStateFlow("All")
    val calendarCategoryFilter: StateFlow<String> = _calendarCategoryFilter.asStateFlow()

    private val _timelineEvents = MutableStateFlow(DayMeetRepository.getInitialTimeline())
    val timelineEvents: StateFlow<List<TimelineEvent>> = _timelineEvents.asStateFlow()

    // Meetings state
    private val _meetingSearch = MutableStateFlow("")
    val meetingSearch: StateFlow<String> = _meetingSearch.asStateFlow()

    private val _meetingTab = MutableStateFlow("Today")
    val meetingTab: StateFlow<String> = _meetingTab.asStateFlow()

    private val _meetings = MutableStateFlow(DayMeetRepository.getInitialMeetings())
    val meetings: StateFlow<List<MeetingItem>> = _meetings.asStateFlow()

    // Finance state
    private val _transactions = MutableStateFlow(DayMeetRepository.getInitialTransactions())
    val transactions: StateFlow<List<FinanceTransaction>> = _transactions.asStateFlow()

    private val _monthlyBudgetTarget = MutableStateFlow(60000.0)
    val monthlyBudgetTarget: StateFlow<Double> = _monthlyBudgetTarget.asStateFlow()

    private val _upcomingBills = MutableStateFlow(DayMeetRepository.getInitialUpcomingBills())
    val upcomingBills: StateFlow<List<UpcomingBill>> = _upcomingBills.asStateFlow()

    private val _emis = MutableStateFlow(DayMeetRepository.getInitialEmis())
    val emis: StateFlow<List<EmiItem>> = _emis.asStateFlow()

    private val _debts = MutableStateFlow(DayMeetRepository.getInitialDebts())
    val debts: StateFlow<List<DebtItem>> = _debts.asStateFlow()

    // Projects Management (Section 9)
    private val _projects = MutableStateFlow(DayMeetRepository.getInitialProjects())
    val projects: StateFlow<List<ProjectItem>> = _projects.asStateFlow()

    // Appointments (Section 11)
    private val _appointments = MutableStateFlow(DayMeetRepository.getInitialAppointments())
    val appointments: StateFlow<List<AppointmentItem>> = _appointments.asStateFlow()

    // Home & Vehicle (Sections 28 & 29)
    private val _homeVehicleItems = MutableStateFlow(DayMeetRepository.getInitialHomeVehicleItems())
    val homeVehicleItems: StateFlow<List<HomeVehicleItem>> = _homeVehicleItems.asStateFlow()


    // Meeting Minutes state
    private val _minutesActions = MutableStateFlow(
        listOf(
            MinuteActionItem(
                id = "m_act1",
                title = "Finalize mobile navigation tokens",
                assignee = "Alex Chen",
                due = "Today, 5:00 PM",
                priority = "High Priority"
            ),
            MinuteActionItem(
                id = "m_act2",
                title = "Send vendor proposal to Enterprise client",
                assignee = "Sarah Lee",
                due = "Oct 26",
                priority = "Medium"
            ),
            MinuteActionItem(
                id = "m_act3",
                title = "Set up MS Teams webhook for sync updates",
                assignee = "Mark D.",
                due = "Oct 28",
                priority = "Routine"
            )
        )
    )
    val minutesActions: StateFlow<List<MinuteActionItem>> = _minutesActions.asStateFlow()

    // Chat AI state
    private val _chatMessages = MutableStateFlow(DayMeetRepository.getInitialChatMessages())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Toast notification
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // App Update & Production Sync state
    private val _appUpdateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val appUpdateInfo: StateFlow<AppUpdateInfo?> = _appUpdateInfo.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    private val _isAutoCheckUpdateEnabled = MutableStateFlow(true)
    val isAutoCheckUpdateEnabled: StateFlow<Boolean> = _isAutoCheckUpdateEnabled.asStateFlow()

    // Local device calendar notification alert sync setting
    private val _syncDeviceCalendarAlerts = MutableStateFlow(true)
    val syncDeviceCalendarAlerts: StateFlow<Boolean> = _syncDeviceCalendarAlerts.asStateFlow()

    // Manual Dashboard & All Widgets Network Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncedTime = MutableStateFlow("Just now")
    val lastSyncedTime: StateFlow<String> = _lastSyncedTime.asStateFlow()

    private val _syncPulseKey = MutableStateFlow(0)
    val syncPulseKey: StateFlow<Int> = _syncPulseKey.asStateFlow()

    fun triggerManualSync() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        viewModelScope.launch {
            // Simulate network refresh latency across Cloud Calendar, Finance, Tasks & Health
            delay(1200)
            _lastSyncedTime.value = "Just now"
            _syncPulseKey.value += 1
            _isSyncing.value = false
            showToast("Dashboard synchronized: 6 widgets updated & current")
        }
    }

    fun addCrossStreamItem(
        title: String,
        subtitle: String,
        tag: String = "Task",
        tagType: String = "task",
        time: String = "Now"
    ) {
        val newItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = time,
            title = title.ifBlank { "New Task" },
            subtitle = subtitle,
            tag = tag,
            tagType = tagType,
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newItem) + _crossStreamItems.value
        showToast("Added to stream: $title")
    }

    fun toggleSyncDeviceCalendarAlerts(enabled: Boolean? = null) {
        val next = enabled ?: !_syncDeviceCalendarAlerts.value
        _syncDeviceCalendarAlerts.value = next
        showToast(
            if (next) "Device calendar notifications synced with DayMeet alert system"
            else "Device calendar notification sync paused"
        )
    }

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            while (true) {
                delay(1000)
                if (_isFocusRunning.value && !_isFocusCompleted.value && _focusTimerRemaining.value > 0) {
                    _focusTimerRemaining.value -= 1
                }
            }
        }
    }

    // Navigation
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
        _subScreen.value = null
        _showMeetingMinutes.value = false
        _showAiAssistant.value = false
        _showSearchOverlay.value = false
    }

    fun openSubScreen(screen: String) {
        _subScreen.value = screen
    }

    fun closeSubScreen() {
        _subScreen.value = null
    }

    fun openMeetingMinutes() {
        _showMeetingMinutes.value = true
    }

    fun closeMeetingMinutes() {
        _showMeetingMinutes.value = false
    }

    fun openAiAssistant() {
        _showAiAssistant.value = true
    }

    fun closeAiAssistant() {
        _showAiAssistant.value = false
    }

    fun openCreateTask(initialTab: String = "Task") {
        _quickAddInitialTab.value = initialTab
        _showCreateSheet.value = true
    }

    fun openQuickAdd(initialTab: String = "Task") = openCreateTask(initialTab)

    fun closeCreateTask() {
        _showCreateSheet.value = false
    }

    fun openQuickScheduleMeeting() {
        _showScheduleMeetingModal.value = true
    }

    fun closeQuickScheduleMeeting() {
        _showQuickMeetingDialog.value = false
        _showScheduleMeetingModal.value = false
    }

    fun openScheduleMeeting() {
        _showScheduleMeetingModal.value = true
    }

    fun closeScheduleMeeting() {
        _showScheduleMeetingModal.value = false
    }

    fun scheduleMeeting(
        title: String,
        participants: List<String>,
        date: String = "Today",
        time: String = "Today, 03:00 PM",
        duration: String = "30 mins",
        platform: String = "Google Meet",
        agenda: String = ""
    ) {
        val attendeesList = if (participants.isEmpty()) {
            listOf(Attendee("Alex Chen (You)", avatarUrl = DayMeetRepository.ALEX_AVATAR))
        } else {
            participants.map { name ->
                Attendee(
                    name = name.trim(),
                    role = "Participant",
                    avatarUrl = if (name.contains("Alex", ignoreCase = true)) DayMeetRepository.ALEX_AVATAR else null,
                    initials = name.trim().take(2).uppercase()
                )
            }
        }
        val newMeeting = MeetingItem(
            id = "m_${System.currentTimeMillis()}",
            title = title.ifBlank { "Team Meeting" },
            time = time.ifBlank { "Today, 03:00 PM" },
            duration = duration,
            platform = platform,
            status = "Scheduled",
            attendees = attendeesList,
            attendeesCount = attendeesList.size
        )
        _meetings.value = listOf(newMeeting) + _meetings.value

        // Also add to Cross-Module Stream
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = if (time.contains(",")) time.substringAfter(",").trim() else time,
            title = title.ifBlank { "Team Meeting" },
            subtitle = "${attendeesList.size} attendees • $duration • $platform",
            tag = "Meeting",
            tagType = "meeting",
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showScheduleMeetingModal.value = false
        _showQuickMeetingDialog.value = false
        val participantsDesc = if (participants.isNotEmpty()) " with ${participants.size} participants" else ""
        showToast("Meeting scheduled for $duration$participantsDesc! 📅")
    }

    fun scheduleMeetingFromHub(
        title: String,
        participants: List<String>,
        time: String,
        platform: String = "Google Meet",
        duration: String = "30 mins"
    ) {
        val attendeesList = if (participants.isEmpty()) {
            listOf(Attendee("Alex Chen (You)", avatarUrl = DayMeetRepository.ALEX_AVATAR))
        } else {
            participants.map { name ->
                Attendee(
                    name = name.trim(),
                    role = "Participant",
                    avatarUrl = if (name.contains("Alex", ignoreCase = true)) DayMeetRepository.ALEX_AVATAR else null,
                    initials = name.trim().take(2).uppercase()
                )
            }
        }
        val newMeeting = MeetingItem(
            id = "m_${System.currentTimeMillis()}",
            title = title.ifBlank { "Quick Meeting" },
            time = time.ifBlank { "Today, 03:00 PM" },
            duration = duration,
            platform = platform,
            status = "Scheduled",
            attendees = attendeesList,
            attendeesCount = attendeesList.size
        )
        _meetings.value = listOf(newMeeting) + _meetings.value

        // Also add to Cross-Module Stream
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = if (time.contains(",")) time.substringAfter(",").trim() else time,
            title = title.ifBlank { "Quick Meeting" },
            subtitle = "${attendeesList.size} attendees • $platform",
            tag = "Meeting",
            tagType = "meeting",
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showQuickMeetingDialog.value = false
        val participantsDesc = if (participants.isNotEmpty()) " with ${participants.joinToString(", ")}" else ""
        showToast("Meeting scheduled: ${title.ifBlank { "Quick Meeting" }}$participantsDesc at $time 📅")
    }

    fun openDailyBriefing() {
        _showDailyBriefing.value = true
    }

    fun closeDailyBriefing() {
        _showDailyBriefing.value = false
    }

    fun openSearch() {
        _showSearchOverlay.value = true
    }

    fun closeSearch() {
        _showSearchOverlay.value = false
        _globalSearchQuery.value = ""
    }

    fun setGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    // Automations Actions
    fun toggleAutomation(id: String) {
        _automations.value = _automations.value.map { auto ->
            if (auto.id == id) auto.copy(isEnabled = !auto.isEnabled) else auto
        }
        val auto = _automations.value.firstOrNull { it.id == id }
        showToast("${auto?.title ?: "Workflow"} is now ${if (auto?.isEnabled == true) "Active" else "Paused"}")
    }

    fun testAutomation(id: String) {
        val auto = _automations.value.firstOrNull { it.id == id }
        showToast("Triggered test: ${auto?.title}. Log entry added!")
        val newLog = AutomationLog(
            id = "log_${System.currentTimeMillis()}",
            title = auto?.title ?: "Custom Trigger",
            detail = "Manual trigger test succeeded with zero errors.",
            time = "Just now",
            isSuccess = true
        )
        _automationLogs.value = listOf(newLog) + _automationLogs.value
    }

    // Health Actions
    fun addWater(amount: Float = 0.25f) {
        val current = _healthMetrics.value.hydration
        val target = _healthMetrics.value.hydrationTarget
        val updated = (current + amount).coerceAtMost(5.0f)
        _healthMetrics.value = _healthMetrics.value.copy(
            hydration = (Math.round(updated * 10) / 10.0).toFloat()
        )
        showToast("Logged +${(amount * 1000).toInt()}ml water! (${_healthMetrics.value.hydration}L / ${target}L)")
    }

    fun setMentalState(state: String) {
        _healthMetrics.value = _healthMetrics.value.copy(mentalState = state)
        showToast("Mental state updated to $state ✨")
    }

    fun togglePostureReminder() {
        val current = _healthMetrics.value.postureReminderOn
        _healthMetrics.value = _healthMetrics.value.copy(postureReminderOn = !current)
        showToast("Posture reminder ${if (!current) "enabled" else "disabled"}")
    }

    fun toggleVitaminLogged() {
        val current = _healthMetrics.value.vitaminLogged
        _healthMetrics.value = _healthMetrics.value.copy(vitaminLogged = !current)
        showToast("Vitamin D3 & Omega ${if (!current) "marked taken" else "unmarked"}")
    }

    fun toggleBedtimeDnd() {
        val current = _healthMetrics.value.bedtimeDndOn
        _healthMetrics.value = _healthMetrics.value.copy(bedtimeDndOn = !current)
        showToast("Bedtime Guard & DND ${if (!current) "armed" else "off"}")
    }

    fun toggleWellnessReminder(type: String) {
        val current = _healthMetrics.value
        val updated = when (type.lowercase()) {
            "water" -> current.copy(waterReminderOn = !current.waterReminderOn)
            "walk" -> current.copy(walkReminderOn = !current.walkReminderOn)
            "exercise" -> current.copy(exerciseReminderOn = !current.exerciseReminderOn)
            "sleep" -> current.copy(sleepReminderOn = !current.sleepReminderOn)
            "meditation" -> current.copy(meditationReminderOn = !current.meditationReminderOn)
            "posture" -> current.copy(postureReminderOn = !current.postureReminderOn)
            else -> current
        }
        _healthMetrics.value = updated
        val state = when (type.lowercase()) {
            "water" -> updated.waterReminderOn
            "walk" -> updated.walkReminderOn
            "exercise" -> updated.exerciseReminderOn
            "sleep" -> updated.sleepReminderOn
            "meditation" -> updated.meditationReminderOn
            "posture" -> updated.postureReminderOn
            else -> true
        }
        showToast("$type reminder ${if (state) "activated 🔔" else "paused 🔕"}")
    }

    fun logWorkoutMinutes(minutes: Int = 15) {
        val current = _healthMetrics.value.exerciseMinutes
        val target = _healthMetrics.value.exerciseTarget
        val updated = current + minutes
        _healthMetrics.value = _healthMetrics.value.copy(
            exerciseMinutes = updated,
            caloriesBurned = _healthMetrics.value.caloriesBurned + (minutes * 8)
        )
        showToast("Logged +${minutes}m workout! (${updated}m / ${target}m target)")
        if (updated >= target) {
            triggerConfetti("🏆 Daily Exercise Target Smashed!")
        }
    }

    fun addCustomAutomation(
        title: String,
        category: String = "Productivity",
        whenTrigger: String,
        ifCondition: String,
        thenAction: String
    ) {
        val newAuto = AutomationWorkflow(
            id = "auto_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom Automation" },
            category = category,
            statusTag = "Active • Just created",
            whenTrigger = whenTrigger.ifBlank { "Trigger criteria met" },
            ifCondition = ifCondition.ifBlank { "Condition verified" },
            thenAction = thenAction.ifBlank { "Execute automated workflow" },
            isEnabled = true,
            statsText = "0 runs"
        )
        _automations.value = listOf(newAuto) + _automations.value
        showToast("Created automation: $title ✨")
    }

    fun updateAutomation(
        id: String,
        title: String,
        category: String,
        whenTrigger: String,
        ifCondition: String,
        thenAction: String
    ) {
        _automations.value = _automations.value.map { auto ->
            if (auto.id == id) {
                auto.copy(
                    title = title.ifBlank { auto.title },
                    category = category.ifBlank { auto.category },
                    whenTrigger = whenTrigger.ifBlank { auto.whenTrigger },
                    ifCondition = ifCondition.ifBlank { auto.ifCondition },
                    thenAction = thenAction.ifBlank { auto.thenAction },
                    statusTag = "Updated"
                )
            } else auto
        }
        showToast("Updated rule: $title ✓")
    }

    fun deleteAutomation(id: String) {
        _automations.value = _automations.value.filterNot { it.id == id }
        showToast("Automation rule removed")
    }

    // Habits & Goals Actions
    fun triggerConfetti(milestone: String? = null) {
        _confettiMilestone.value = milestone
        _showConfetti.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(2600)
            _showConfetti.value = false
            _confettiMilestone.value = null
        }
    }

    fun dismissConfetti() {
        _showConfetti.value = false
        _confettiMilestone.value = null
    }

    fun addCustomHabit(
        name: String,
        category: String = "Learning",
        iconKey: String = "school",
        colorHex: String = "#F57C00"
    ) {
        val newHabit = HabitItem(
            id = "h_${System.currentTimeMillis()}",
            name = name.ifBlank { "Custom Habit" },
            streakDays = 1,
            targetFrequency = "Daily",
            isCompletedToday = true,
            category = category.ifBlank { "Learning" },
            iconKey = iconKey,
            colorHex = colorHex,
            bestStreakDays = 1
        )
        _habits.value = listOf(newHabit) + _habits.value
        showToast("✨ Added habit: $name in $category")
        triggerConfetti("🎉 Started new habit streak: $name!")
    }

    fun toggleHabit(id: String) {
        var streakMilestoneMsg: String? = null
        _habits.value = _habits.value.map { h ->
            if (h.id == id) {
                val next = !h.isCompletedToday
                val newStreak = if (next) h.streakDays + 1 else (h.streakDays - 1).coerceAtLeast(0)
                val newBest = if (newStreak > h.bestStreakDays) newStreak else h.bestStreakDays
                if (next && newStreak >= h.bestStreakDays) {
                    streakMilestoneMsg = "🎉 Streak Record! ${newStreak}d streak for ${h.name} 🔥"
                }
                h.copy(
                    isCompletedToday = next,
                    streakDays = newStreak,
                    bestStreakDays = newBest
                )
            } else h
        }
        val habit = _habits.value.firstOrNull { it.id == id }
        val isDone = habit?.isCompletedToday == true
        showToast("${habit?.name} marked ${if (isDone) "done! 🔥" else "incomplete"}")
        if (streakMilestoneMsg != null && isDone) {
            triggerConfetti(streakMilestoneMsg)
        }
    }

    fun logMorningMeditation() {
        val meditation = _habits.value.find { it.name.contains("Meditation", ignoreCase = true) || it.id == "h_meditation" }
        if (meditation != null) {
            toggleHabit(meditation.id)
        } else {
            val newH = HabitItem("h_meditation", "Morning Meditation", 19, "Daily", true, "Mindfulness", "self_improvement", "#673AB7", 19)
            _habits.value = listOf(newH) + _habits.value
            showToast("🧘 Morning Meditation marked done! 19d streak 🔥")
            triggerConfetti("🎉 Streak Record Maintained: 19d Meditation Streak! 🔥")
        }
    }

    fun logMorningExercise() {
        val exercise = _habits.value.find { it.name.contains("Exercise", ignoreCase = true) || it.name.contains("Walk", ignoreCase = true) || it.id == "h_exercise" }
        if (exercise != null) {
            toggleHabit(exercise.id)
        } else {
            val newH = HabitItem("h_exercise", "Morning Exercise", 14, "Daily", true, "Fitness", "fitness_center", "#2E7D32", 14)
            _habits.value = listOf(newH) + _habits.value
            showToast("🏃 Morning Exercise marked done! 14d streak 🔥")
            triggerConfetti("🎉 Streak Record Maintained: 14d Exercise Streak! 🔥")
        }
    }

    // Daily Habit Goal Tracking (Non-Routine Tasks)
    fun toggleNonRoutineTask(id: String) {
        _nonRoutineTasks.value = _nonRoutineTasks.value.map { task ->
            if (task.id == id) {
                val nextState = !task.isCompleted
                val nextSteps = if (nextState) task.totalSteps else 0
                task.copy(isCompleted = nextState, progressSteps = nextSteps)
            } else task
        }
        val t = _nonRoutineTasks.value.firstOrNull { it.id == id }
        if (t != null) {
            showToast("${t.title} marked ${if (t.isCompleted) "completed! ✓" else "pending"}")
            if (t.isCompleted) {
                triggerConfetti("🎯 Goal Accomplished: ${t.title}")
            }
        }
    }

    fun incrementNonRoutineTaskProgress(id: String) {
        _nonRoutineTasks.value = _nonRoutineTasks.value.map { task ->
            if (task.id == id) {
                val nextStep = (task.progressSteps + 1).coerceAtMost(task.totalSteps)
                val isNowDone = nextStep >= task.totalSteps
                task.copy(progressSteps = nextStep, isCompleted = isNowDone)
            } else task
        }
        val t = _nonRoutineTasks.value.firstOrNull { it.id == id }
        if (t != null) {
            showToast("Progress: ${t.progressSteps}/${t.totalSteps} for ${t.title}")
            if (t.isCompleted) {
                triggerConfetti("🎯 Goal Completed: ${t.title}")
            }
        }
    }

    fun addNonRoutineTask(
        title: String,
        category: String,
        estimatedMinutes: Int,
        totalSteps: Int,
        targetDesc: String = "1 Target"
    ) {
        val newTask = NonRoutineTask(
            id = "nrt_${System.currentTimeMillis()}",
            title = title.ifBlank { "New Daily Goal" },
            category = category.ifBlank { "Sprint Goal" },
            targetDescription = targetDesc,
            isCompleted = false,
            progressSteps = 0,
            totalSteps = totalSteps.coerceAtLeast(1),
            estimatedMinutes = estimatedMinutes.coerceAtLeast(5)
        )
        _nonRoutineTasks.value = listOf(newTask) + _nonRoutineTasks.value
        showToast("Daily Goal set: $title")
    }

    // System-wide Dark Mode & Tailwind Theme Configuration
    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        _themeMode.value = if (next) "dark" else "light"
        showToast("Theme: ${if (next) "Dark Mode (Tailwind Slate-900)" else "Light Mode (DayMeet Clean)"}")
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        _themeMode.value = if (enabled) "dark" else "light"
        showToast("Theme switched to ${if (enabled) "Dark Mode" else "Light Mode"}")
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        when (mode) {
            "dark" -> _isDarkMode.value = true
            "light" -> _isDarkMode.value = false
            else -> _isDarkMode.value = false
        }
        showToast("Theme appearance set to $mode")
    }

    fun downloadWeeklyFinanceReport(context: Context) {
        val reportText = buildString {
            appendLine("==================================================")
            appendLine("DAYMEET LIFE OS • EXECUTIVE WEEKLY SPENDING REPORT")
            appendLine("==================================================")
            appendLine("Period: Current Week (Monday - Sunday)")
            appendLine("Generated: 2026-09-12 10:30 AM")
            appendLine("Budget Status: SAFE & UNDER BUDGET (41.4% Buffer)")
            appendLine("")
            appendLine("EXECUTIVE METRICS:")
            appendLine("• Weekly Budget Ceiling:   ₹35,000.00")
            appendLine("• Total Spent to Date:     ₹20,500.00")
            appendLine("• Remaining Buffer:        ₹14,500.00 (Safe Surplus)")
            appendLine("• Daily Average Spend:     ₹2,928.57 / day")
            appendLine("• Daily Budget Ceiling:    ₹5,000.00 / day")
            appendLine("")
            appendLine("DAILY SPENDING CADENCE vs DAILY CEILING:")
            appendLine("--------------------------------------------------")
            appendLine("• Monday:    ₹2,100.00  | Buffer: ₹2,900.00 (Safe)")
            appendLine("• Tuesday:   ₹4,350.00  | Buffer: ₹650.00   (Safe)")
            appendLine("• Wednesday: ₹2,800.00  | Buffer: ₹2,200.00 (Safe)")
            appendLine("• Thursday:  ₹3,450.00  | Buffer: ₹1,550.00 (Safe - Today)")
            appendLine("• Friday:    ₹1,800.00  | Buffer: ₹3,200.00 (Safe)")
            appendLine("• Saturday:  ₹4,800.00  | Buffer: ₹200.00   (Safe)")
            appendLine("• Sunday:    ₹1,200.00  | Buffer: ₹3,800.00 (Safe)")
            appendLine("")
            appendLine("CUMULATIVE SPENDING TREND vs PROJECTED WEEKLY BUDGET:")
            appendLine("--------------------------------------------------")
            appendLine("• Mon: Spent: ₹2,100.00   | Projected Cap: ₹5,000.00   [+₹2,900 Buffer]")
            appendLine("• Tue: Spent: ₹6,450.00   | Projected Cap: ₹10,000.00  [+₹3,550 Buffer]")
            appendLine("• Wed: Spent: ₹9,250.00   | Projected Cap: ₹15,000.00  [+₹5,750 Buffer]")
            appendLine("• Thu: Spent: ₹12,700.00  | Projected Cap: ₹20,000.00  [+₹7,300 Buffer]")
            appendLine("• Fri: Spent: ₹14,500.00  | Projected Cap: ₹25,000.00  [+₹10,500 Buffer]")
            appendLine("• Sat: Spent: ₹19,300.00  | Projected Cap: ₹30,000.00  [+₹10,700 Buffer]")
            appendLine("• Sun: Spent: ₹20,500.00  | Projected Cap: ₹35,000.00  [+₹14,500 Buffer]")
            appendLine("")
            appendLine("TOP SPENDING CATEGORIES BREAKDOWN:")
            appendLine("• Food & Dining:         ₹6,850.00 (33.4%)")
            appendLine("• Utilities & Bills:     ₹4,200.00 (20.5%)")
            appendLine("• Transit & Commute:     ₹3,150.00 (15.4%)")
            appendLine("• Health & Wellness:     ₹2,500.00 (12.2%)")
            appendLine("• Tech & Subscriptions:  ₹1,800.00 (8.8%)")
            appendLine("• Groceries & Essentials:₹2,000.00 (9.7%)")
            appendLine("")
            appendLine("RECENT TRANSACTIONS AUDIT:")
            _transactions.value.take(8).forEach { tx ->
                appendLine("• ${tx.title} (${tx.category}) - ₹${String.format("%,.2f", tx.amount)} via ${tx.method}")
            }
            appendLine("==================================================")
            appendLine("Verified by DayMeet Super App Financial Intelligence")
        }

        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_SUBJECT, "DayMeet Executive Weekly Spending Report")
                putExtra(android.content.Intent.EXTRA_TEXT, reportText)
            }
            val chooser = android.content.Intent.createChooser(intent, "Download or Share Spending Report")
            chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            showToast("Weekly spending report generated & ready to export! 📊")
        } catch (e: Exception) {
            showToast("Report generated: ₹20,500 spent (₹14,500 buffer)")
        }
    }

    // Shopping Actions
    fun toggleShoppingItem(id: String) {
        _shoppingItems.value = _shoppingItems.value.map { s ->
            if (s.id == id) s.copy(isPurchased = !s.isPurchased) else s
        }
    }

    fun addShoppingItem(name: String, quantity: String, price: Double, category: String) {
        val item = ShoppingItem(
            id = "shop_${System.currentTimeMillis()}",
            name = name,
            quantity = quantity.ifBlank { "1 item" },
            estimatedPrice = price,
            category = category,
            isPurchased = false
        )
        _shoppingItems.value = _shoppingItems.value + item
        showToast("Added $name to Shopping List")
    }

    // Reminders Actions
    fun toggleReminder(id: String) {
        _reminders.value = _reminders.value.map { r ->
            if (r.id == id) r.copy(isCompleted = !r.isCompleted) else r
        }
    }

    // Finance Actions
    fun payBill(id: String) {
        val bill = _upcomingBills.value.firstOrNull { it.id == id }
        _upcomingBills.value = _upcomingBills.value.filter { it.id != id }
        if (bill != null) {
            val newTx = FinanceTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = bill.name,
                category = "Bills & Utilities",
                time = "Just now",
                amount = -bill.amount,
                method = "Direct Pay",
                iconType = "software",
                tags = listOf("Paid", bill.department)
            )
            _transactions.value = listOf(newTx) + _transactions.value
            showToast("Paid ₹${String.format("%.0f", bill.amount)} to ${bill.department}!")
        }
    }

    fun checkAndLogExpense(
        title: String,
        amount: Double,
        category: String = "General",
        method: String = "UPI",
        receiptNote: String? = null,
        isSplit: Boolean = false
    ): Boolean {
        val currentSpent = _transactions.value.filter { it.amount < 0 }.sumOf { -it.amount }
        val dailyCeiling = 5000.0
        val safeAmount = Math.abs(amount)
        val projectedSpent = currentSpent + safeAmount
        val isCeilingExceeded = projectedSpent > dailyCeiling

        val icon = when (category.lowercase()) {
            "food" -> "restaurant"
            "travel" -> "subway"
            "fuel" -> "local_gas_station"
            "shopping" -> "shopping_bag"
            "bills" -> "receipt_long"
            "rent" -> "home"
            "education" -> "school"
            "healthcare" -> "medical_services"
            "entertainment" -> "movie"
            else -> "account_balance_wallet"
        }

        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title.ifBlank { "Expense" },
            category = category.ifBlank { "General" },
            time = "Just now",
            amount = -safeAmount,
            method = method,
            iconType = icon,
            receiptNote = receiptNote,
            isSplit = isSplit
        )
        _transactions.value = listOf(newTx) + _transactions.value

        if (isCeilingExceeded) {
            val overBy = projectedSpent - dailyCeiling
            showToast("⚠️ Budget Alert: Expense of ₹${String.format("%.0f", safeAmount)} exceeds ₹5,000 daily budget! (Projected: ₹${String.format("%.0f", projectedSpent)}, Over by ₹${String.format("%.0f", overBy)})")
        } else {
            val remaining = dailyCeiling - projectedSpent
            showToast("Logged expense: ₹${String.format("%.0f", safeAmount)} for $title ($method)")
        }
        return isCeilingExceeded
    }

    fun logExpense(
        title: String,
        amount: Double,
        category: String = "General",
        method: String = "UPI",
        receiptNote: String? = null,
        isSplit: Boolean = false
    ) {
        checkAndLogExpense(title, amount, category, method, receiptNote, isSplit)
    }

    fun logIncome(title: String, amount: Double, method: String = "Bank") {
        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title,
            category = "Income",
            time = "Just now",
            amount = Math.abs(amount),
            method = method,
            iconType = "account_balance"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        showToast("Added income: +₹${String.format("%.0f", amount)} ($method)")
    }

    fun addEmi(title: String, amount: Double, totalMonths: Int, nextDue: String, category: String = "Loan") {
        val newEmi = EmiItem(
            id = "emi_${System.currentTimeMillis()}",
            title = title,
            monthlyAmount = amount,
            totalMonths = totalMonths,
            remainingMonths = totalMonths,
            nextDueDate = nextDue,
            category = category
        )
        _emis.value = listOf(newEmi) + _emis.value
        showToast("Added EMI plan: $title (₹${String.format("%.0f", amount)}/mo)")
    }

    fun payEmiInstallment(id: String) {
        val item = _emis.value.firstOrNull { it.id == id } ?: return
        if (item.remainingMonths <= 0) {
            showToast("EMI for ${item.title} is already completed! 🎉")
            return
        }
        val updated = item.copy(remainingMonths = item.remainingMonths - 1)
        _emis.value = _emis.value.map { if (it.id == id) updated else it }
        logExpense("EMI: ${item.title}", item.monthlyAmount, "Bills", "Auto-Debit")
        showToast("Paid EMI of ₹${String.format("%.0f", item.monthlyAmount)} (${updated.remainingMonths} months left)")
        if (updated.remainingMonths == 0) {
            triggerConfetti("🎉 EMI Completed: ${item.title} fully paid off!")
        }
    }

    fun addDebt(person: String, amount: Double, isOwedToMe: Boolean, dueDate: String, note: String) {
        val newDebt = DebtItem(
            id = "debt_${System.currentTimeMillis()}",
            personOrSource = person,
            amount = amount,
            isOwedToMe = isOwedToMe,
            dueDate = dueDate.ifBlank { "Flexible" },
            note = note
        )
        _debts.value = listOf(newDebt) + _debts.value
        val label = if (isOwedToMe) "owes you" else "you owe"
        showToast("Logged debt: $person $label ₹${String.format("%.0f", amount)}")
    }

    fun settleDebt(id: String) {
        val item = _debts.value.firstOrNull { it.id == id } ?: return
        _debts.value = _debts.value.filterNot { it.id == id }
        if (item.isOwedToMe) {
            logIncome("Settlement from ${item.personOrSource}", item.amount, "UPI")
            showToast("Received ₹${String.format("%.0f", item.amount)} from ${item.personOrSource} (Settled) ✓")
        } else {
            logExpense("Settlement to ${item.personOrSource}", item.amount, "Other", "UPI")
            showToast("Paid ₹${String.format("%.0f", item.amount)} to ${item.personOrSource} (Settled) ✓")
        }
    }

    fun addSubscription(name: String, cost: Double, date: String, category: String, autoPay: Boolean) {
        val newSub = SubscriptionItem(
            id = "sub_${System.currentTimeMillis()}",
            name = name,
            monthlyCost = cost,
            renewalDate = date,
            iconType = when (category.lowercase()) {
                "ott", "entertainment" -> "movie"
                "internet", "broadband" -> "wifi"
                "electricity", "utility" -> "bolt"
                "gym", "fitness" -> "fitness"
                "insurance" -> "health"
                else -> "receipt"
            },
            category = category,
            autoPay = autoPay
        )
        _subscriptions.value = listOf(newSub) + _subscriptions.value
        showToast("Added subscription: $name (₹${String.format("%.0f", cost)}/mo)")
    }

    fun updateMonthlyBudgetTarget(newTarget: Double) {
        val target = newTarget.coerceAtLeast(1000.0)
        _monthlyBudgetTarget.value = target
        showToast("Monthly budget target updated to ₹${String.format("%,.0f", target)}")
    }

    // Universal Quick Add
    fun universalQuickAdd(
        type: String,
        title: String,
        detail: String,
        extraValue: String = "",
        priority: Priority = Priority.HIGH,
        category: String = "Work",
        reminderTime: String? = null
    ) {
        when (type) {
            "Task" -> {
                saveNewTask(title, detail, priority, category, emptyList(), reminderTime)
            }
            "Meeting" -> {
                val participants = if (detail.isNotBlank()) detail.split(",").map { it.trim() } else listOf("Alex Chen")
                scheduleMeetingFromHub(
                    title = title.ifBlank { "Quick Meeting" },
                    participants = participants,
                    time = if (extraValue.isNotBlank()) extraValue else "Today, 03:00 PM"
                )
            }
            "Reminder" -> {
                val newRem = SmartReminder(
                    id = "rem_${System.currentTimeMillis()}",
                    title = title,
                    triggerType = "Time-based",
                    scheduledTime = if (extraValue.isNotBlank()) extraValue else "Today, 06:00 PM"
                )
                _reminders.value = listOf(newRem) + _reminders.value
                showToast("Reminder created: $title")
            }
            "Expense" -> {
                val amount = extraValue.toDoubleOrNull() ?: 150.0
                val cat = if (category.isNotBlank() && category != "Work") category else (if (detail.isNotBlank()) detail else "Food")
                checkAndLogExpense(title.ifBlank { "Quick Expense" }, amount, cat, "UPI")
            }
            "Income" -> {
                val amount = extraValue.toDoubleOrNull() ?: 1000.0
                logIncome(title.ifBlank { "Payment" }, amount, "Bank")
            }
            "Shopping" -> {
                val price = extraValue.toDoubleOrNull() ?: 250.0
                addShoppingItem(title.ifBlank { "Item" }, detail.ifBlank { "1 unit" }, price, category.ifBlank { "General" })
            }
            "Subscription" -> {
                val cost = extraValue.toDoubleOrNull() ?: 499.0
                addSubscription(title.ifBlank { "Subscription" }, cost, "End of Month", category.ifBlank { "Entertainment" }, true)
            }
            "Debt" -> {
                val amount = extraValue.toDoubleOrNull() ?: 500.0
                addDebt(title.ifBlank { "Contact" }, amount, true, "Next Week", detail)
            }
            "EMI" -> {
                val amount = extraValue.toDoubleOrNull() ?: 2000.0
                addEmi(title.ifBlank { "New EMI" }, amount, 12, "Oct 10", category.ifBlank { "Loan" })
            }
            "Note" -> {
                val newNote = NoteItem(
                    id = "note_${System.currentTimeMillis()}",
                    title = title.ifBlank { "Quick Note" },
                    content = detail,
                    category = "Quick Notes",
                    updatedAt = "Just now"
                )
                _notes.value = listOf(newNote) + _notes.value
                showToast("Note saved: $title")
            }
            "Habit" -> {
                val newHabit = HabitItem(
                    id = "h_${System.currentTimeMillis()}",
                    name = title,
                    streakDays = 0,
                    targetFrequency = "Daily",
                    isCompletedToday = false,
                    category = "Personal"
                )
                _habits.value = _habits.value + newHabit
                showToast("Habit added: $title")
            }
            "Health Entry" -> {
                addWater(0.25f)
            }
            "Goal" -> {
                val newGoal = GoalItem(
                    id = "g_${System.currentTimeMillis()}",
                    title = title,
                    category = "Personal",
                    target = extraValue.ifBlank { "100%" },
                    current = "0%",
                    progressPercent = 0.05f,
                    deadline = "Next Month"
                )
                _goals.value = _goals.value + newGoal
                showToast("Goal created: $title")
            }
            "Bill" -> {
                val amount = extraValue.toDoubleOrNull() ?: 500.0
                val bill = UpcomingBill(
                    id = "b_${System.currentTimeMillis()}",
                    name = title,
                    scheduleDate = "Scheduled for Next Week",
                    daysLeft = "7d left",
                    department = detail.ifBlank { "Utility" },
                    amount = amount
                )
                _upcomingBills.value = _upcomingBills.value + bill
                showToast("Bill reminder logged: $title")
            }
            "Message" -> {
                val msg = ScheduledMessage(
                    id = "sm_${System.currentTimeMillis()}",
                    recipientName = title.ifBlank { "Recipient" },
                    platform = "WhatsApp Integration",
                    messageContent = detail,
                    scheduledTime = if (extraValue.isNotBlank()) extraValue else "Tomorrow, 09:00 AM"
                )
                _scheduledMessages.value = listOf(msg) + _scheduledMessages.value
                showToast("Message scheduled for $title")
            }
            "Project" -> {
                addProject(title, detail, extraValue.ifBlank { "₹50,000" }, "End of Month")
            }
            "Appointment" -> {
                addAppointment(title, category.ifBlank { "General" }, "Upcoming", extraValue.ifBlank { "10:00 AM" }, detail.ifBlank { "Scheduled Location" })
            }
            "Home & Vehicle" -> {
                addHomeVehicleItem(title, category.ifBlank { "Home" }, extraValue.ifBlank { "Next Month" }, detail)
            }
            else -> {
                saveNewTask(title, detail, Priority.MEDIUM, "General", emptyList())
            }
        }
        _showCreateSheet.value = false
    }

    // Projects Management (Section 9)
    fun addProject(title: String, description: String, budget: String = "₹50,000", deadline: String = "30 September") {
        val newProj = ProjectItem(
            id = "proj_${System.currentTimeMillis()}",
            title = title.ifBlank { "New Project" },
            description = description.ifBlank { "Project deliverables, tasks and milestone tracker" },
            progressPercent = 15,
            tasksCount = 6,
            completedTasks = 1,
            meetingsCount = 2,
            filesCount = 3,
            budget = budget,
            deadline = deadline,
            colorHex = "#673AB7",
            status = "In Progress"
        )
        _projects.value = listOf(newProj) + _projects.value
        addCrossStreamItem("Project: ${newProj.title}", "Deadline: $deadline • Budget: $budget", "Project", "priority")
        showToast("Project created: ${newProj.title} 🚀")
    }

    fun toggleProjectProgress(id: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == id) {
                val nextPercent = if (proj.progressPercent >= 100) 25 else (proj.progressPercent + 25).coerceAtMost(100)
                val completed = ((nextPercent / 100f) * proj.tasksCount).toInt()
                proj.copy(progressPercent = nextPercent, completedTasks = completed)
            } else proj
        }
    }

    fun deleteProject(id: String) {
        _projects.value = _projects.value.filterNot { it.id == id }
        showToast("Project removed")
    }

    // Appointments (Section 11)
    fun addAppointment(title: String, category: String, date: String, time: String, location: String) {
        val newApt = AppointmentItem(
            id = "apt_${System.currentTimeMillis()}",
            title = title.ifBlank { "Appointment" },
            category = category,
            date = date.ifBlank { "Upcoming" },
            time = time.ifBlank { "10:00 AM" },
            locationOrProvider = location.ifBlank { "Scheduled Location" },
            reminderNotice = "1 day before & 1 hour before"
        )
        _appointments.value = listOf(newApt) + _appointments.value
        addCrossStreamItem("Appointment: ${newApt.title}", "${newApt.date} $time • $location", "Appointment", "meeting")
        showToast("Appointment scheduled: ${newApt.title} 🩺")
    }

    fun toggleAppointmentCompleted(id: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun deleteAppointment(id: String) {
        _appointments.value = _appointments.value.filterNot { it.id == id }
        showToast("Appointment cancelled")
    }

    // Home & Vehicle (Sections 28 & 29)
    fun addHomeVehicleItem(title: String, category: String, dueDate: String, detail: String, cost: String? = null) {
        val newItem = HomeVehicleItem(
            id = "hv_${System.currentTimeMillis()}",
            title = title.ifBlank { "Maintenance Item" },
            category = category,
            dueDate = dueDate.ifBlank { "Next Month" },
            detail = detail.ifBlank { "Periodic service and inspection" },
            statusTag = "Scheduled",
            costEstimate = cost
        )
        _homeVehicleItems.value = listOf(newItem) + _homeVehicleItems.value
        showToast("Logged $category maintenance: $title 🔧")
    }

    fun toggleHomeVehicleItem(id: String) {
        _homeVehicleItems.value = _homeVehicleItems.value.map {
            if (it.id == id) {
                val nextStatus = if (it.statusTag == "Completed") "Scheduled" else "Completed"
                it.copy(statusTag = nextStatus)
            } else it
        }
    }

    fun deleteHomeVehicleItem(id: String) {
        _homeVehicleItems.value = _homeVehicleItems.value.filterNot { it.id == id }
        showToast("Maintenance entry removed")
    }

    // Universal Quick Capture Natural Language Parsing (Section 5)
    data class ParsedCapture(
        val type: String,
        val title: String,
        val detail: String,
        val extra: String,
        val explanation: String
    )

    fun parseNaturalLanguage(input: String): ParsedCapture {
        val trimmed = input.trim()
        val lower = trimmed.lowercase()
        return when {
            lower.contains("call") || lower.contains("remind") -> {
                val time = if (lower.contains("5")) "Tomorrow, 05:00 PM" else if (lower.contains("tomorrow")) "Tomorrow, 09:00 AM" else "Today, 06:00 PM"
                val name = trimmed.substringAfter("call", "").substringAfter("remind", "").substringBefore("tomorrow").substringBefore("at").trim()
                val title = if (name.isNotBlank()) "Call ${name.replaceFirstChar { it.uppercase() }}" else trimmed
                ParsedCapture("Reminder", title, "Time-based intelligent reminder", time, "Action: $title • Date/Time: $time • Type: Reminder")
            }
            lower.contains("buy") || lower.contains("milk") || lower.contains("grocery") || lower.contains("shopping") -> {
                val item = trimmed.substringAfter("buy", "").substringBefore("when").substringBefore("from").trim()
                val title = if (item.isNotBlank()) item.replaceFirstChar { it.uppercase() } else "Grocery Item"
                ParsedCapture("Shopping", title, "Groceries & Household (Location reminder at home)", "₹120", "Shopping item + location reminder: $title")
            }
            lower.contains("bill") || lower.contains("pay") || lower.contains("₹") -> {
                val amount = Regex("""(?:₹|rs\.?|inr)?\s*([0-9,]+)""", RegexOption.IGNORE_CASE).find(trimmed)?.groupValues?.get(1) ?: "1,500"
                val title = if (lower.contains("electricity")) "Electricity Bill" else if (lower.contains("internet") || lower.contains("wifi")) "Internet Bill" else "Utility Bill Payment"
                ParsedCapture("Bill", title, "Finance entry + reminder", "₹$amount", "Bill: $title • Amount: ₹$amount • Reminder: Due 25th")
            }
            lower.contains("meet") || lower.contains("sync") || lower.contains("meeting") -> {
                val who = trimmed.substringAfter("meet", "").substringBefore("next").substringBefore("at").substringBefore("tomorrow").trim()
                val title = if (who.isNotBlank()) "Meeting with ${who.replaceFirstChar { it.uppercase() }}" else "Scheduled Sync"
                ParsedCapture("Meeting", title, "Smart Scheduling Suggestion", "Next Tuesday, 03:00 PM", "Meeting: $title • Slot: Next Tuesday afternoon")
            }
            lower.contains("doctor") || lower.contains("service") || lower.contains("salon") || lower.contains("appointment") -> {
                ParsedCapture("Appointment", trimmed, "Appointment card with reminder", "Upcoming Slot", "Appointment: $trimmed • Alerts: 1 day & 1 hr before")
            }
            lower.contains("project") || lower.contains("launch") -> {
                ParsedCapture("Project", trimmed, "Project dashboard tracker", "₹50,000", "Project: $trimmed • Grouping tasks, meetings & budget")
            }
            else -> {
                ParsedCapture("Task", trimmed, "Task prioritized for DayMeet Command Center", "Today 05:00 PM", "Task: $trimmed • Synced to Personal Timeline")
            }
        }
    }

    fun executeNaturalLanguageCapture(input: String) {
        val parsed = parseNaturalLanguage(input)
        when (parsed.type) {
            "Reminder" -> {
                val newRem = SmartReminder("rem_${System.currentTimeMillis()}", parsed.title, "Time-based", parsed.extra)
                _reminders.value = listOf(newRem) + _reminders.value
                addCrossStreamItem(parsed.title, parsed.extra, "Reminder", "priority")
            }
            "Shopping" -> {
                addShoppingItem(parsed.title, "1 item", 120.0, "Groceries")
            }
            "Bill" -> {
                val amount = parsed.extra.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 1500.0
                val bill = UpcomingBill("b_${System.currentTimeMillis()}", parsed.title, "25th of Month", "Due soon", "Utilities", amount)
                _upcomingBills.value = listOf(bill) + _upcomingBills.value
                addCrossStreamItem(parsed.title, "₹$amount due on 25th", "Bill", "autopay")
            }
            "Meeting" -> {
                scheduleMeetingFromHub(parsed.title, listOf("Alex Chen"), parsed.extra)
            }
            "Appointment" -> {
                addAppointment(parsed.title, "General", "Next Week", "10:30 AM", "Scheduled Location")
            }
            "Project" -> {
                addProject(parsed.title, parsed.detail, parsed.extra)
            }
            else -> {
                saveNewTask(parsed.title, parsed.detail, Priority.HIGH, "General", emptyList())
            }
        }
        showToast("✓ Captured: ${parsed.title} (${parsed.type})")
    }


    // Home feed & filter actions
    fun setFeedFilter(filter: FeedCategory) {
        _feedFilter.value = filter
    }

    fun toggleFeedTaskDone(id: String) {
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(isCompleted = !item.isCompleted)
            } else {
                item
            }
        }
    }

    fun bulkMarkTasksCompleted(ids: Set<String>) {
        if (ids.isEmpty()) return
        val count = ids.size
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id in ids) {
                item.copy(isCompleted = true)
            } else {
                item
            }
        }
        showToast("Marked $count tasks as completed! ✓")
        triggerConfetti("🎉 Batch completed $count tasks!")
    }

    fun bulkRemoveTasks(ids: Set<String>) {
        if (ids.isEmpty()) return
        val count = ids.size
        _feedItems.value = _feedItems.value.filterNot { it.id in ids }
        showToast("Removed $count completed tasks")
    }

    fun removeFeedTask(id: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.filterNot { it.id == id }
        if (task != null) {
            showToast("Completed: ${task.title} (Removed)")
        }
    }

    fun updateTaskPriority(id: String, priority: Priority) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(priority = priority)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Updated priority of '${task.title}' to ${priority.label}")
        }
    }

    fun rescheduleTask(id: String, newTime: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(time = newTime)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Rescheduled '${task.title}' to $newTime")
        }
    }

    fun updateTaskNotes(id: String, newNotes: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(notes = newNotes)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Updated notes for '${task.title}' 📝")
        }
    }

    fun triggerTaskNotificationNow(context: Context, taskId: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        TaskNotificationScheduler.showNotification(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            time = task.time
        )
        showToast("🔔 Triggered due alert for '${task.title}'")
    }

    fun scheduleTaskNotification(context: Context, taskId: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        TaskNotificationScheduler.scheduleTaskAlert(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            timeStr = task.reminderTime ?: task.time
        )
        showToast("⏰ Alert scheduled for ${task.reminderTime ?: task.time}")
    }

    fun setTaskReminder(context: Context, taskId: String, reminderTimeStr: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(reminderTime = reminderTimeStr)
            } else item
        }
        TaskNotificationScheduler.scheduleTaskAlert(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            timeStr = reminderTimeStr
        )
        showToast("⏰ Reminder set for $reminderTimeStr")
    }

    fun moveTaskToCalendar(taskId: String, timeSlot: String, durationMinutes: Int = 30) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        
        // 1. Update task with calendar time and status
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(
                    time = timeSlot,
                    statusTag = "Calendar"
                )
            } else item
        }

        // 2. Add or update TimelineEvent in calendar
        val timeClean = if (timeSlot.contains(",")) timeSlot.substringAfter(",").trim() else timeSlot
        val period = if (timeSlot.contains("PM", ignoreCase = true)) "Afternoon" else "Morning"
        val newTimelineEvent = TimelineEvent(
            id = "timeline_${task.id}",
            time = timeClean,
            period = period,
            title = task.title,
            subtitle = task.notes ?: task.subtitle,
            durationMinutes = durationMinutes,
            type = TimelineType.TASK,
            isCompleted = task.isCompleted
        )
        _timelineEvents.value = listOf(newTimelineEvent) + _timelineEvents.value.filterNot { it.id == newTimelineEvent.id }

        // 3. Add to cross-stream
        val crossStream = CrossStreamItem(
            id = "cs_cal_${task.id}",
            time = timeClean,
            title = task.title,
            subtitle = "Scheduled task • $durationMinutes mins",
            tag = "Calendar Task",
            tagType = "task",
            isCompleted = task.isCompleted
        )
        _crossStreamItems.value = listOf(crossStream) + _crossStreamItems.value.filterNot { it.id == crossStream.id }

        showToast("📅 Moved '${task.title}' to Calendar at $timeSlot")
    }

    fun editTask(
        id: String,
        title: String,
        subtitle: String,
        priority: Priority,
        category: String,
        time: String,
        notes: String?
    ) {
        val task = _feedItems.value.firstOrNull { it.id == id } ?: return
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(
                    title = title.ifBlank { item.title },
                    subtitle = subtitle.ifBlank { item.subtitle },
                    priority = priority,
                    statusTag = category,
                    time = time.ifBlank { item.time },
                    notes = notes
                )
            } else item
        }
        showToast("Task '${title.ifBlank { task.title }}' updated ✓")
    }

    fun deleteTask(id: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.filterNot { it.id == id }
        if (task != null) {
            showToast("Deleted task '${task.title}'")
        }
    }

    fun toggleCrossStreamDone(id: String) {
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
        }
    }

    fun removeCrossStreamItem(id: String) {
        val item = _crossStreamItems.value.firstOrNull { it.id == id }
        _crossStreamItems.value = _crossStreamItems.value.filterNot { it.id == id }
        if (item != null) {
            showToast("Completed: ${item.title} (Removed)")
        }
    }

    fun toggleFocusMode() {
        val newState = !_isFocusModeActive.value
        _isFocusModeActive.value = newState
        if (newState) {
            _isFocusRunning.value = true
            showToast("🎯 Focus Mode Activated: All non-urgent notifications muted")
        } else {
            showToast("Focus Mode Deactivated: System notifications restored")
        }
    }

    fun selectFocusTask(taskId: String) {
        _selectedFocusTaskId.value = taskId
    }

    fun openLanguageSelector() {
        _showLanguageDialog.value = true
    }

    fun closeLanguageSelector() {
        _showLanguageDialog.value = false
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _showLanguageDialog.value = false
        val message = when (language) {
            AppLanguage.ENGLISH -> "Language changed to English 🇺🇸"
            AppLanguage.SPANISH -> "Idioma cambiado a Español 🇪🇸"
            AppLanguage.FRENCH -> "Langue changée en Français 🇫🇷"
            AppLanguage.GERMAN -> "Sprache auf Deutsch geändert 🇩🇪"
            AppLanguage.HINDI -> "भाषा बदलकर हिन्दी कर दी गई 🇮🇳"
            AppLanguage.JAPANESE -> "言語を日本語に変更しました 🇯🇵"
            AppLanguage.CHINESE -> "语言已切换为简体中文 🇨🇳"
            AppLanguage.ARABIC -> "تم تغيير اللغة إلى العربية 🇸🇦"
            AppLanguage.PORTUGUESE -> "Idioma alterado para Português 🇧🇷"
            AppLanguage.RUSSIAN -> "Язык изменен на Русский 🇷🇺"
            AppLanguage.ITALIAN -> "Lingua cambiata in Italiano 🇮🇹"
            AppLanguage.KOREAN -> "언어가 한국어로 변경되었습니다 🇰🇷"
            AppLanguage.TAMIL -> "மொழி தமிழுக்கு மாற்றப்பட்டது 🇮🇳"
        }
        showToast(message)
    }

    fun completeFocusTaskAndAdvance(taskId: String) {
        toggleFeedTaskDone(taskId)
        showToast("Task completed! Focusing on next task...")
        triggerConfetti("🎯 Task Completed in Focus Mode!")
    }

    fun toggleFocusTimer() {
        _isFocusRunning.value = !_isFocusRunning.value
    }

    fun start25MinPomodoroSession() {
        _focusTimerRemaining.value = 25 * 60
        _isFocusRunning.value = true
        _isFocusCompleted.value = false
        showToast("🍅 25-min Deep Work Pomodoro session started!")
    }

    fun completeFocusSession() {
        _isFocusCompleted.value = true
        _isFocusRunning.value = false
        showToast("Deep Work focus session completed!")
    }

    fun logWaterIntake() {
        addWater(0.25f)
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    fun setCalendarMode(mode: String) {
        _calendarMode.value = mode
    }

    fun setCalendarCategory(cat: String) {
        _calendarCategoryFilter.value = cat
    }

    fun toggleTimelineTask(id: String) {
        _timelineEvents.value = _timelineEvents.value.map { event ->
            if (event.id == id) {
                event.copy(isCompleted = !event.isCompleted)
            } else event
        }
    }

    fun toggleSubtask(eventId: String, subtaskId: String) {
        _timelineEvents.value = _timelineEvents.value.map { event ->
            if (event.id == eventId) {
                val updatedSubs = event.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                event.copy(subtasks = updatedSubs)
            } else event
        }
    }

    fun setMeetingSearch(query: String) {
        _meetingSearch.value = query
    }

    fun setMeetingTab(tab: String) {
        _meetingTab.value = tab
    }

    fun toggleMinuteAction(id: String) {
        _minutesActions.value = _minutesActions.value.map { action ->
            if (action.id == id) action.copy(isDone = !action.isDone) else action
        }
    }

    fun convertMinuteActionToTask(id: String) {
        _minutesActions.value = _minutesActions.value.map { action ->
            if (action.id == id) action.copy(isConverted = true) else action
        }
        showToast("Task added to Planner backlog")
    }

    fun applyAiProposal() {
        _chatMessages.value = _chatMessages.value.map { msg ->
            if (msg.proposal != null) {
                msg.copy(proposal = msg.proposal.copy(isApplied = true))
            } else msg
        }
        showToast("Calendar synced & reminder dispatched")
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            isUser = true,
            text = text,
            timestamp = "Just now"
        )
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            delay(600)
            val lower = text.lowercase()
            val (replyText, proposal) = when {
                lower.contains("plan") && (lower.contains("tomorrow") || lower.contains("day")) -> {
                    val planText = buildString {
                        appendLine("✨ **TOMORROW'S OPTIMIZED PLAN**")
                        appendLine("")
                        appendLine("📅 09:00 AM – Team Sprint Standup & Alignment")
                        appendLine("💻 10:30 AM – Deep Work: Architecture Spec (Focus Block)")
                        appendLine("🍽️ 12:30 PM – Healthy Lunch & Quick Walk")
                        appendLine("📞 02:00 PM – Client Strategy Review (Google Meet)")
                        appendLine("⚡ 04:00 PM – Wrap-up, Inbox Zero & Review")
                        appendLine("")
                        appendLine("💡 You have 2 pending tasks from today. Tapping below will auto-schedule them into your 10:30 AM focus block.")
                    }
                    val prop = ScheduleProposal(
                        rescheduled = ProposalItem("Reschedule", "Move 2 overdue tasks to 10:30 AM tomorrow", "Auto-inserts buffer", "Recommended"),
                        addedBlock = ProposalItem("Deep Work", "Add 90m Architecture Focus block", "Mutes notifications", "Optimal"),
                        autoReminder = ProposalItem("Health", "12:30 PM Hydration & 15m Walk Alert", "Vital routine", "Scheduled")
                    )
                    Pair(planText, prop)
                }
                lower.contains("spend") || lower.contains("expense") || lower.contains("budget") -> {
                    val totalExpenses = _transactions.value.filter { it.amount < 0 }.sumOf { -it.amount }
                    val reply = buildString {
                        appendLine("💰 **WEEKLY & MONTHLY SPENDING REPORT**")
                        appendLine("")
                        appendLine("• Total Expenses: ₹${String.format("%,.0f", totalExpenses)}")
                        appendLine("• Monthly Budget Limit: ₹${String.format("%,.0f", _monthlyBudgetTarget.value)}")
                        appendLine("• Remaining Buffer: ₹${String.format("%,.0f", (_monthlyBudgetTarget.value - totalExpenses).coerceAtLeast(0.0))}")
                        appendLine("")
                        appendLine("Top Categories:")
                        val grouped = _transactions.value.filter { it.amount < 0 }.groupBy { it.category }
                        grouped.forEach { (cat, list) ->
                            val sum = list.sumOf { -it.amount }
                            appendLine("  • $cat: ₹${String.format("%,.0f", sum)}")
                        }
                        appendLine("")
                        appendLine("Status: Healthy! You are within your target threshold.")
                    }
                    Pair(reply, null)
                }
                lower.contains("meeting") && (lower.contains("tomorrow") || lower.contains("next") || lower.contains("have")) -> {
                    val count = _meetings.value.size
                    val reply = buildString {
                        appendLine("📅 **SCHEDULED MEETINGS ($count Active)**")
                        appendLine("")
                        _meetings.value.take(4).forEachIndexed { i, m ->
                            appendLine("${i + 1}. **${m.title}** • ${m.time}")
                            appendLine("   Platform: ${m.platform} (${m.attendeesCount} attendees)")
                        }
                    }
                    Pair(reply, null)
                }
                lower.contains("remind") && (lower.contains("bill") || lower.contains("pay") || lower.contains("electricity")) -> {
                    val newRem = SmartReminder(
                        id = "rem_${System.currentTimeMillis()}",
                        title = "⚡ Pay Electricity Bill (Tata Power ₹1,840)",
                        triggerType = "Bill Due",
                        scheduledTime = "Friday, 09:00 AM",
                        isCompleted = false
                    )
                    _reminders.value = listOf(newRem) + _reminders.value
                    val reply = "⚡ **Reminder Confirmed:** I've scheduled an alert for **Friday 09:00 AM** to pay your Electricity Bill (Tata Power ₹1,840) with a 1-tap UPI payment button."
                    Pair(reply, null)
                }
                lower.contains("task") && (lower.contains("meeting") || lower.contains("action")) -> {
                    saveNewTask("Finalize client deliverables from Sprint Review", "Derived from Sprint Review meeting notes", Priority.HIGH, "Work", emptyList())
                    saveNewTask("Share updated Figma design system tokens", "David & Elena requested spec", Priority.MEDIUM, "Design", emptyList())
                    val reply = "✅ **Created 2 actionable follow-up tasks** from today's meeting notes and added them to your Planner backlog with high priority."
                    Pair(reply, null)
                }
                lower.contains("overdue") -> {
                    val pendingTasks = _feedItems.value.filter { !it.isCompleted }
                    val reply = buildString {
                        appendLine("⚠️ **TASK STATUS OVERVIEW**")
                        appendLine("")
                        appendLine("You have **${pendingTasks.size} tasks** pending today:")
                        pendingTasks.take(3).forEach { t ->
                            appendLine("• ${t.title} [${t.priority?.label ?: "Normal"}]")
                        }
                        appendLine("")
                        appendLine("Would you like me to auto-reschedule them to tomorrow morning?")
                    }
                    Pair(reply, null)
                }
                lower.contains("sub") || lower.contains("subscription") -> {
                    val total = _subscriptions.value.sumOf { it.monthlyCost }
                    val reply = buildString {
                        appendLine("💳 **ACTIVE SUBSCRIPTIONS (₹${String.format("%,.0f", total)}/mo)**")
                        appendLine("")
                        _subscriptions.value.forEach { s ->
                            appendLine("• ${s.name}: ₹${String.format("%.0f", s.monthlyCost)} (Renews ${s.renewalDate})")
                        }
                    }
                    Pair(reply, null)
                }
                else -> {
                    val reply = "DayMeet AI Assistant: I've processed your request. Your calendar, daily timeline, budget limits, and wellness routines are synchronized."
                    Pair(reply, null)
                }
            }

            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = replyText,
                timestamp = "Just now",
                proposal = proposal
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun executeAiAction(actionType: String) {
        when (actionType) {
            "plan_tomorrow" -> sendChatMessage("Plan my tomorrow")
            "check_spending" -> sendChatMessage("How much did I spend this week?")
            "check_meetings" -> sendChatMessage("What meetings do I have tomorrow?")
            "check_overdue" -> sendChatMessage("How many tasks are overdue?")
            "create_meeting_tasks" -> sendChatMessage("Create tasks from today's meetings")
            "check_subs" -> sendChatMessage("Show my upcoming subscriptions")
            else -> sendChatMessage(actionType)
        }
    }

    fun saveNewTask(
        title: String,
        notes: String,
        priority: Priority,
        space: String,
        subtasks: List<String>,
        reminderTime: String? = null
    ) {
        val taskId = "task_${System.currentTimeMillis()}"
        val taskTitle = title.ifBlank { "New Task" }
        val categoryTag = if (space.isNotBlank()) space else "Work"
        val taskSubtitle = if (notes.isNotBlank()) notes else "$categoryTag • Priority: ${priority.label}"
        val newTask = FeedItem(
            id = taskId,
            time = "05:00 PM",
            title = taskTitle,
            subtitle = taskSubtitle,
            category = FeedCategory.TASK,
            priority = priority,
            statusTag = categoryTag,
            isCompleted = false,
            reminderTime = reminderTime,
            notes = notes.ifBlank { null }
        )
        _feedItems.value = listOf(newTask) + _feedItems.value

        // Also add to Cross-Module Stream so it appears with entrance animation
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = "05:00 PM",
            title = taskTitle,
            subtitle = taskSubtitle,
            tag = "Task",
            tagType = "task",
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showCreateSheet.value = false
        showToast("Task created: $taskTitle")
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2400)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    // Google Play In-App Production Update Functions
    fun checkForAppUpdates(context: Context? = null, manual: Boolean = false) {
        if (!manual && !_isAutoCheckUpdateEnabled.value) return
        viewModelScope.launch {
            try {
                val update = if (context != null) {
                    PlayAppUpdateManager.checkPlayUpdate(
                        context = context,
                        onProgressUpdate = { bytes, total, status ->
                            val progress = if (total > 0) (bytes.toFloat() / total).coerceIn(0f, 1f) else 0f
                            val isDownloaded = status == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED
                            _appUpdateInfo.value = _appUpdateInfo.value?.copy(
                                downloadProgress = progress,
                                bytesDownloaded = bytes,
                                totalBytesToDownload = total,
                                isDownloading = !isDownloaded && progress > 0f,
                                isReadyToInstall = isDownloaded
                            )
                            if (isDownloaded) {
                                showToast("Google Play update downloaded! Ready to install.")
                            }
                        }
                    )
                } else {
                    AppUpdateManager.checkForUpdates()
                }

                _appUpdateInfo.value = update
                val hasHigherVersion = update.isUpdateAvailable && update.latestVersionCode > BuildConfig.VERSION_CODE
                if (hasHigherVersion) {
                    val isDismissed = context != null && AppUpdateManager.isVersionDismissed(context, update.latestVersionCode)
                    if (manual || !isDismissed) {
                        _showUpdateDialog.value = true
                    }
                    if (manual) {
                        val channelName = if (update.updateChannel == UpdateChannel.GOOGLE_PLAY) "Google Play" else "Production"
                        showToast("New $channelName update available: ${update.latestVersionName}")
                    }
                } else {
                    _showUpdateDialog.value = false
                    if (manual) {
                        showToast("DayMeet is up to date (v${update.currentVersionName})")
                    }
                }
            } catch (e: Exception) {
                if (manual) showToast("Could not check for updates")
            }
        }
    }

    fun launchUpdate(activity: Activity) {
        val current = _appUpdateInfo.value ?: return
        if (current.updateChannel == UpdateChannel.GOOGLE_PLAY && current.playUpdateInfo != null) {
            val launched = PlayAppUpdateManager.startPlayUpdate(
                activity = activity,
                playInfo = current.playUpdateInfo,
                mode = current.updateMode
            )
            if (launched) {
                _showUpdateDialog.value = false
                showToast("Starting Google Play update flow...")
            } else {
                startAppUpdateDownload(activity)
            }
        } else {
            startAppUpdateDownload(activity)
        }
    }

    fun completePlayUpdate(context: Context) {
        viewModelScope.launch {
            val completed = PlayAppUpdateManager.completePlayUpdate(context)
            if (completed) {
                applyInstalledUpdate(context)
            } else {
                installDownloadedUpdate(context)
            }
        }
    }

    fun startAppUpdateDownload(context: Context) {
        val current = _appUpdateInfo.value ?: return
        _appUpdateInfo.value = current.copy(isDownloading = true, downloadProgress = 0.05f)
        viewModelScope.launch {
            val result = AppUpdateManager.downloadApk(
                context = context,
                updateInfo = current,
                onProgress = { p ->
                    _appUpdateInfo.value = _appUpdateInfo.value?.copy(downloadProgress = p)
                }
            )
            result.onSuccess { file ->
                _appUpdateInfo.value = _appUpdateInfo.value?.copy(
                    isDownloading = false,
                    downloadProgress = 1f,
                    isReadyToInstall = true,
                    downloadedApkFile = file
                )
                val launched = AppUpdateManager.promptInstallApk(context, file)
                if (launched) {
                    showToast("Update ready! Opening installer...")
                } else {
                    showToast("Update downloaded! Tap Install to apply.")
                }
            }.onFailure { err ->
                _appUpdateInfo.value = _appUpdateInfo.value?.copy(
                    isDownloading = false,
                    errorMessage = err.message
                )
                showToast("Update download failed: ${err.message}")
            }
        }
    }

    fun installDownloadedUpdate(context: Context) {
        val current = _appUpdateInfo.value
        val file = current?.downloadedApkFile

        if (current != null) {
            AppUpdateManager.markUpdateInstalled(
                context = context,
                versionCode = current.latestVersionCode,
                versionName = current.latestVersionName
            )
        }

        if (file != null && AppUpdateManager.isValidApk(context, file)) {
            val launched = AppUpdateManager.promptInstallApk(context, file)
            if (launched) {
                showToast("Opening package installer...")
            }
        }
        applyInstalledUpdate(context)
    }

    fun applyInstalledUpdate(context: Context? = null) {
        val current = _appUpdateInfo.value ?: return
        if (context != null) {
            AppUpdateManager.markUpdateInstalled(
                context = context,
                versionCode = current.latestVersionCode,
                versionName = current.latestVersionName
            )
        }
        _appUpdateInfo.value = current.copy(
            isUpdateAvailable = false,
            currentVersionName = current.latestVersionName,
            currentVersionCode = current.latestVersionCode,
            isReadyToInstall = false,
            isDownloading = false
        )
        _showUpdateDialog.value = false
        showToast("DayMeet updated to v${current.latestVersionName}!")
    }

    fun dismissUpdateDialog(context: Context? = null) {
        _showUpdateDialog.value = false
        val current = _appUpdateInfo.value
        if (context != null && current != null) {
            AppUpdateManager.markVersionDismissed(context, current.latestVersionCode)
        }
    }

    fun resetUpdateStateForTesting(context: Context) {
        AppUpdateManager.resetUpdateStateForTesting(context)
        _appUpdateInfo.value = null
        checkForAppUpdates(context = context, manual = true)
    }

    fun openUpdateDialog(context: Context? = null) {
        if (_appUpdateInfo.value == null) {
            checkForAppUpdates(context = context, manual = true)
        } else {
            _showUpdateDialog.value = true
        }
    }

    fun toggleAutoCheckUpdates() {
        _isAutoCheckUpdateEnabled.value = !_isAutoCheckUpdateEnabled.value
        val state = if (_isAutoCheckUpdateEnabled.value) "enabled" else "disabled"
        showToast("Automatic update checks $state")
    }
}
