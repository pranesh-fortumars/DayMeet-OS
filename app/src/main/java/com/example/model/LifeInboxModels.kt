package com.example.model

enum class LifeInboxType(val label: String, val iconName: String) {
    TEXT("Text", "Edit"),
    VOICE("Voice", "Mic"),
    PHOTO("Photo", "CameraAlt"),
    SCREENSHOT("Screenshot", "Screenshot"),
    LINK("Link", "Link"),
    DOCUMENT("Document", "Description"),
    EXPENSE("Expense", "ReceiptLong"),
    IDEA("Idea", "Lightbulb"),
    TASK("Task", "CheckCircle")
}

enum class SuggestedActionType {
    CREATE_TASK,
    CREATE_MEETING,
    CREATE_REMINDER,
    LOG_EXPENSE,
    ADD_VEHICLE_HOME,
    SAVE_NOTE,
    ADD_CALENDAR_EVENT,
    SAVE_WISHLIST
}

data class LifeInboxSuggestion(
    val id: String,
    val type: SuggestedActionType,
    val title: String,
    val detail: String,
    val targetModule: String,
    val category: String? = null,
    val amountOrDate: String? = null
)

data class LifeInboxItem(
    val id: String,
    val content: String,
    val type: LifeInboxType,
    val timestamp: String,
    val source: String = "Manual Capture", // "Share Sheet", "Clipboard", "Camera", "Voice", "Manual"
    val mediaUri: String? = null,
    val isProcessed: Boolean = false,
    val suggestions: List<LifeInboxSuggestion> = emptyList(),
    val tags: List<String> = emptyList()
)

enum class LifeMode(val label: String, val iconName: String) {
    ALL("All Streams", "Dashboard"),
    WORK("Work Mode", "Work"),
    PERSONAL("Personal Mode", "Home")
}

data class ContextualNextItem(
    val title: String,
    val subtitle: String,
    val timeRemaining: String,
    val iconType: String, // "meeting", "task", "free_time", "focus"
    val actionLabel: String,
    val actionTag: String,
    val contextInsight: String,
    val urgentCount: Int = 0
)
