package com.example.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.DayMeetRepository
import com.example.model.*
import com.example.util.AppUpdateManager
import com.example.util.PlayAppUpdateManager
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

    private val _isFocusRunning = MutableStateFlow(true)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private val _isFocusCompleted = MutableStateFlow(false)
    val isFocusCompleted: StateFlow<Boolean> = _isFocusCompleted.asStateFlow()

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

    private val _upcomingBills = MutableStateFlow(DayMeetRepository.getInitialUpcomingBills())
    val upcomingBills: StateFlow<List<UpcomingBill>> = _upcomingBills.asStateFlow()

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

    fun closeCreateTask() {
        _showCreateSheet.value = false
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

    fun logExpense(title: String, amount: Double, category: String) {
        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title,
            category = category,
            time = "Just now",
            amount = -Math.abs(amount),
            method = "UPI / Card",
            iconType = "restaurant"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        showToast("Logged expense: ₹${String.format("%.0f", amount)} for $title")
    }

    fun logIncome(title: String, amount: Double) {
        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title,
            category = "Income",
            time = "Just now",
            amount = Math.abs(amount),
            method = "Direct Deposit",
            iconType = "subway"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        showToast("Added income: +₹${String.format("%.0f", amount)}")
    }

    // Universal Quick Add
    fun universalQuickAdd(
        type: String,
        title: String,
        detail: String,
        extraValue: String = ""
    ) {
        when (type) {
            "Task" -> {
                saveNewTask(title, detail, Priority.HIGH, "General", emptyList())
            }
            "Meeting" -> {
                val newMeeting = MeetingItem(
                    id = "m_${System.currentTimeMillis()}",
                    title = title.ifBlank { "New Meeting" },
                    time = if (extraValue.isNotBlank()) extraValue else "Today, 03:00 PM",
                    duration = "30 mins",
                    platform = "Google Meet",
                    status = "Scheduled",
                    attendees = listOf(Attendee("Alex Chen", avatarUrl = DayMeetRepository.ALEX_AVATAR)),
                    attendeesCount = 1
                )
                _meetings.value = listOf(newMeeting) + _meetings.value
                showToast("Meeting scheduled: $title")
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
                logExpense(title.ifBlank { "Expense" }, amount, detail.ifBlank { "General" })
            }
            "Income" -> {
                val amount = extraValue.toDoubleOrNull() ?: 1000.0
                logIncome(title.ifBlank { "Payment" }, amount)
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
            else -> {
                saveNewTask(title, detail, Priority.MEDIUM, "General", emptyList())
            }
        }
        _showCreateSheet.value = false
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

    fun removeFeedTask(id: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.filterNot { it.id == id }
        if (task != null) {
            showToast("Completed: ${task.title} (Removed)")
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

    fun toggleFocusTimer() {
        _isFocusRunning.value = !_isFocusRunning.value
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
            delay(1200)
            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = "DayMeet Super App Intelligence: I've updated your schedule, adjusted the cross-stream timeline, and validated your ₹5,000 daily budget.",
                timestamp = "Just now"
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun saveNewTask(title: String, notes: String, priority: Priority, space: String, subtasks: List<String>) {
        val newTask = FeedItem(
            id = "task_${System.currentTimeMillis()}",
            time = "05:00 PM",
            title = title.ifBlank { "New Task" },
            subtitle = if (notes.isNotBlank()) notes else "$space • Priority: ${priority.label}",
            category = FeedCategory.TASK,
            priority = priority,
            statusTag = priority.label,
            isCompleted = false
        )
        _feedItems.value = listOf(newTask) + _feedItems.value
        _showCreateSheet.value = false
        showToast("Task created: $title")
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
