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
    val detail: String? = null,
    val reminderTime: String? = null,
    val notes: String? = null,
    val dueDate: String? = null,
    val subtasks: List<Subtask> = emptyList(),
    val progress: Int = 0
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
    val tags: List<String> = emptyList(),
    val receiptNote: String? = null,
    val isSplit: Boolean = false
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

// ==========================================
// ALL-IN-ONE SUPER APP DATA MODELS
// ==========================================

data class CrossStreamItem(
    val id: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val tagType: String, // "meeting", "priority", "expense", "focus", "wellness", "autopay", "travel"
    val isCompleted: Boolean = false,
    val subtasks: List<Subtask> = emptyList(),
    val progress: Int = 0
)

data class HealthMetrics(
    val score: Int = 88,
    val scoreLabel: String = "Optimal",
    val sleepDuration: String = "7h 24m",
    val sleepQuality: String = "85% Qual",
    val sleepDeep: String = "1h 45m",
    val steps: Int = 7842,
    val stepsTarget: Int = 10000,
    val stepsDistance: String = "3.8 km",
    val hydration: Float = 1.8f,
    val hydrationTarget: Float = 2.5f,
    val caloriesBurned: Int = 480,
    val caloriesTarget: Int = 600,
    val exerciseMinutes: Int = 32,
    val exerciseTarget: Int = 45,
    val completedRoutines: Int = 4,
    val totalRoutines: Int = 5,
    val heartRateBpm: Int = 68,
    val heartRateRange: String = "54 - 118",
    val mentalState: String = "Happy",
    val postureReminderOn: Boolean = true,
    val vitaminLogged: Boolean = true,
    val bedtimeDndOn: Boolean = true,
    val waterReminderOn: Boolean = true,
    val walkReminderOn: Boolean = true,
    val exerciseReminderOn: Boolean = true,
    val sleepReminderOn: Boolean = true,
    val meditationReminderOn: Boolean = true,
    val wearableStatus: String = "Oura Ring Gen 3 • 84% Bat"
)

data class AutomationWorkflow(
    val id: String,
    val title: String,
    val category: String, // "Productivity", "Finance", "Health", "Deadlines", "Social"
    val statusTag: String,
    val whenTrigger: String,
    val ifCondition: String,
    val thenAction: String,
    val isEnabled: Boolean = true,
    val statsText: String
)

data class AutomationLog(
    val id: String,
    val title: String,
    val detail: String,
    val time: String,
    val isSuccess: Boolean = true
)

data class GoalItem(
    val id: String,
    val title: String,
    val category: String, // "Financial", "Fitness", "Career", "Personal"
    val target: String,
    val current: String,
    val progressPercent: Float,
    val deadline: String
)

data class NonRoutineTask(
    val id: String,
    val title: String,
    val category: String = "Sprint Goal", // "Sprint Goal", "Focus Deep Work", "Learning", "Errand"
    val targetDescription: String = "1 Target",
    val isCompleted: Boolean = false,
    val progressSteps: Int = 0,
    val totalSteps: Int = 1,
    val estimatedMinutes: Int = 45
)

data class HabitItem(
    val id: String,
    val name: String,
    val streakDays: Int,
    val targetFrequency: String = "Daily",
    val isCompletedToday: Boolean = false,
    val category: String = "Mindfulness",
    val iconKey: String = "self_improvement",
    val colorHex: String = "#673AB7",
    val bestStreakDays: Int = 18
)

data class NoteItem(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val updatedAt: String,
    val tags: List<String> = emptyList()
)

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: String,
    val estimatedPrice: Double,
    val category: String,
    val isPurchased: Boolean = false
)

data class TravelTrip(
    val id: String,
    val destination: String,
    val dates: String,
    val flightNumber: String,
    val checkInStatus: String,
    val hotel: String,
    val budget: String
)

data class ContactItem(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val email: String,
    val avatarUrl: String?,
    val lastInteraction: String
)

data class DocumentItem(
    val id: String,
    val title: String,
    val category: String,
    val expiryDate: String?,
    val isEncrypted: Boolean = true,
    val size: String
)

data class SubscriptionItem(
    val id: String,
    val name: String,
    val monthlyCost: Double,
    val renewalDate: String,
    val iconType: String,
    val category: String = "Entertainment",
    val autoPay: Boolean = true,
    val billingCycle: String = "Monthly",
    val isActive: Boolean = true
) {
    val amount: Double get() = monthlyCost
}

data class DebtItem(
    val id: String,
    val personOrSource: String,
    val amount: Double,
    val isOwedToMe: Boolean, // true if someone owes user, false if user owes
    val dueDate: String,
    val note: String,
    val isSettled: Boolean = false
) {
    val personName: String get() = personOrSource
}

data class EmiItem(
    val id: String,
    val title: String,
    val monthlyAmount: Double,
    val totalMonths: Int,
    val remainingMonths: Int,
    val nextDueDate: String,
    val category: String = "Loan",
    val totalAmount: Double = monthlyAmount * totalMonths,
    val lender: String = "Bank",
    val dueDayOfMonth: String = "5th"
) {
    val totalTenureMonths: Int get() = totalMonths
}

data class SmartReminder(
    val id: String,
    val title: String,
    val triggerType: String, // "Time", "Location", "Event", "Bill", "Habit"
    val scheduledTime: String,
    val isCompleted: Boolean = false
)

data class ScheduledMessage(
    val id: String,
    val recipientName: String,
    val platform: String, // "WhatsApp Integration", "Slack", "SMS", "Email"
    val messageContent: String,
    val scheduledTime: String,
    val isSent: Boolean = false,
    val isAutomated: Boolean = true
)

data class ProjectItem(
    val id: String,
    val title: String,
    val description: String,
    val progressPercent: Int,
    val tasksCount: Int,
    val completedTasks: Int,
    val meetingsCount: Int,
    val filesCount: Int,
    val budget: String,
    val deadline: String,
    val colorHex: String = "#673AB7",
    val status: String = "In Progress",
    val category: String = "Productivity"
)

data class AppointmentItem(
    val id: String,
    val title: String,
    val category: String, // "Doctor", "Vehicle Servicing", "Salon", "Banking", "Classes"
    val date: String,
    val time: String,
    val locationOrProvider: String,
    val reminderNotice: String = "1 day before & 1 hour before",
    val isCompleted: Boolean = false,
    val notes: String = ""
) {
    val type: String get() = category
}

data class HomeVehicleItem(
    val id: String,
    val title: String,
    val category: String, // "Home", "Vehicle", "Appliance"
    val dueDate: String,
    val detail: String,
    val statusTag: String = "Scheduled", // "Due Soon", "Warranty Active", "Scheduled"
    val costEstimate: String? = null,
    val isCompleted: Boolean = false
) {
    val type: String get() = category
    val details: String get() = detail
    val estimatedCost: String? get() = costEstimate
}

enum class AppLaunchStep {
    SPLASH,
    INITIALIZING,
    READY
}

data class PendingDeletion(
    val id: String,
    val title: String,
    val module: String,
    val execute: () -> Unit
)

data class ScheduleConflict(
    val existingTitle: String,
    val existingTime: String,
    val newTitle: String,
    val newTime: String,
    val conflictDuration: String = "30 mins"
)

data class DraftRecoveryItem(
    val type: String,
    val title: String,
    val content: String
)

