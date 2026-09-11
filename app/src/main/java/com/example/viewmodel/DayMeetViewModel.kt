package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DayMeetRepository
import com.example.model.*
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

    private val _showCreateSheet = MutableStateFlow(false)
    val showCreateSheet: StateFlow<Boolean> = _showCreateSheet.asStateFlow()

    private val _showMeetingMinutes = MutableStateFlow(false)
    val showMeetingMinutes: StateFlow<Boolean> = _showMeetingMinutes.asStateFlow()

    private val _showAiAssistant = MutableStateFlow(false)
    val showAiAssistant: StateFlow<Boolean> = _showAiAssistant.asStateFlow()

    // Home feed & filter
    private val _feedFilter = MutableStateFlow(FeedCategory.ALL)
    val feedFilter: StateFlow<FeedCategory> = _feedFilter.asStateFlow()

    private val _feedItems = MutableStateFlow(DayMeetRepository.getInitialFeedItems())
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

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

    private val _calendarMode = MutableStateFlow("Chronological") // "Chronological" or "By Category"
    val calendarMode: StateFlow<String> = _calendarMode.asStateFlow()

    private val _calendarCategoryFilter = MutableStateFlow("All")
    val calendarCategoryFilter: StateFlow<String> = _calendarCategoryFilter.asStateFlow()

    private val _timelineEvents = MutableStateFlow(DayMeetRepository.getInitialTimeline())
    val timelineEvents: StateFlow<List<TimelineEvent>> = _timelineEvents.asStateFlow()

    // Meetings tab state
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

    init {
        // Start Focus Timer countdown
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isFocusRunning.value && !_isFocusCompleted.value && _focusTimerRemaining.value > 0) {
                    _focusTimerRemaining.value -= 1
                }
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
        _showMeetingMinutes.value = false
        _showAiAssistant.value = false
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

    fun openCreateTask() {
        _showCreateSheet.value = true
    }

    fun closeCreateTask() {
        _showCreateSheet.value = false
    }

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

    fun toggleFocusTimer() {
        _isFocusRunning.value = !_isFocusRunning.value
    }

    fun completeFocusSession() {
        _isFocusCompleted.value = true
        _isFocusRunning.value = false
        showToast("Deep Work focus session completed!")
    }

    fun logWaterIntake() {
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == "f6") {
                val newProgress = ((item.habitProgress ?: 0.8f) + 0.1f).coerceAtMost(1.0f)
                item.copy(
                    habitProgress = newProgress,
                    subtitle = if (newProgress >= 1f) "Goal achieved: 2.5L / 2.5L!" else "Hydration: ${(newProgress * 2.5f).let { String.format("%.1f", it) }}L of 2.5L logged"
                )
            } else item
        }
        showToast("Water intake logged! +250ml")
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

        // Trigger AI reply
        viewModelScope.launch {
            delay(1200)
            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = "Got it. I've updated your schedule and cross-checked your upcoming meetings and budget constraints.",
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
}
