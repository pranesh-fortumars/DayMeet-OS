package com.example.model

/**
 * Phase 4: Proactive Intelligence & Context Awareness Models
 */

// 1. Context & Geofenced Smart Triggers
enum class GeofenceTriggerType(val label: String, val icon: String) {
    ARRIVING_HOME("Arriving Home", "🏡"),
    ARRIVING_WORK("Arriving Office / Work", "🏢"),
    STORE_PROXIMITY("Supermarket / Pharmacy Proximity", "🛒"),
    GYM_PROXIMITY("Fitness / Gym Facility", "🏋️")
}

data class ContextTrigger(
    val id: String,
    val type: GeofenceTriggerType,
    val locationName: String, // e.g., "Home Sanctuary (Indiranagar)", "Office HQ (Koramangala)"
    val actionPrompt: String,
    val suggestedActions: List<String>,
    val isActive: Boolean = true,
    val lastTriggeredTime: String? = null
)

// 2. Pre-Meeting Briefing & Smart Audio Prep
data class MeetingPreBrief(
    val meetingId: String,
    val meetingTitle: String,
    val scheduledTime: String,
    val startsInMinutes: Int, // e.g. 15
    val attendees: List<String>,
    val keyContextSummary: String,
    val previousDecisions: List<String>,
    val openActionItemsForAttendees: List<String>,
    val quickNotes: String = ""
)

// 3. Audio Voice Memo with Automated Action Extraction
data class AudioVoiceMemo(
    val id: String,
    val title: String,
    val timestamp: String,
    val durationSeconds: Int,
    val audioSnippetSimulatedText: String,
    val extractedActionItems: List<String>,
    val isProcessed: Boolean = true
)

// 4. Cognitive Load & Focus-to-Meeting Heatmap Analysis
data class CognitiveLoadAnalytics(
    val focusHours: Double,
    val meetingHours: Double,
    val focusMeetingRatioPercent: Int, // e.g., 68% focus, 32% meetings
    val fragmentedGapsCount: Int, // e.g. 3 small gaps < 30 mins
    val interruptionRiskLevel: String, // "Low", "Moderate", "High Risk (Fragmented)"
    val recommendation: String
)
