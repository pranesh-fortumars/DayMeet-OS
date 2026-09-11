package com.example.model

enum class FeedCategory(val label: String) {
    ALL("All"),
    MEETING("Meetings"),
    TASK("Tasks"),
    REMINDER("Reminders"),
    HABIT("Habits")
}

enum class Priority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

enum class TimelineType {
    HABIT,
    TASK,
    MEETING,
    DEEP_FOCUS,
    PERSONAL,
    TASK_GROUP,
    REMINDER
}

data class FeedItem(
    val id: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val category: FeedCategory,
    val priority: Priority? = null,
    val statusTag: String? = null,
    val platform: String? = null,
    val membersCount: Int? = null,
    val isCompleted: Boolean = false,
    val habitProgress: Float? = null,
    val detail: String? = null
)

data class Subtask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

data class TimelineEvent(
    val id: String,
    val time: String,
    val period: String,
    val title: String,
    val subtitle: String = "",
    val durationMinutes: Int = 30,
    val type: TimelineType,
    val isCompleted: Boolean = false,
    val isHappeningNow: Boolean = false,
    val streakInfo: String? = null,
    val linkUrl: String? = null,
    val attendeesWaiting: Int? = null,
    val priorityTag: String? = null,
    val subtasks: List<Subtask> = emptyList()
)

data class Attendee(
    val name: String,
    val role: String? = null,
    val avatarUrl: String? = null,
    val initials: String? = null
)

data class MeetingItem(
    val id: String,
    val title: String,
    val time: String,
    val duration: String,
    val organizer: String = "Alex Chen (You)",
    val platform: String,
    val agendaItems: List<String> = emptyList(),
    val attendees: List<Attendee> = emptyList(),
    val attendeesCount: Int = 0,
    val status: String,
    val recurring: String? = null,
    val notificationBefore: String? = null,
    val hasMinutes: Boolean = false
)

data class FinanceTransaction(
    val id: String,
    val title: String,
    val category: String,
    val time: String,
    val amount: Double,
    val method: String,
    val iconType: String,
    val linkedEvent: String? = null,
    val tags: List<String> = emptyList()
)

data class UpcomingBill(
    val id: String,
    val name: String,
    val scheduleDate: String,
    val daysLeft: String,
    val department: String,
    val amount: Double,
    val autoPay: Boolean = false
)

data class ProposalItem(
    val typeTag: String,
    val title: String,
    val detail: String,
    val statusTag: String? = null
)

data class ScheduleProposal(
    val rescheduled: ProposalItem?,
    val addedBlock: ProposalItem?,
    val autoReminder: ProposalItem?,
    val isApplied: Boolean = false
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val proposal: ScheduleProposal? = null
)
