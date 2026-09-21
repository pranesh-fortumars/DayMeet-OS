package com.example.model

/**
 * Phase 3: Weekly Review, Habit Stacking, and Strategic OKRs
 */

// 1. Goal Horizon for Strategic Alignment
enum class GoalHorizon(val label: String, val badge: String) {
    YEARLY("1-Year Vision", "Annual"),
    QUARTERLY("Q3 2026 Milestone", "90-Day"),
    SPRINT("Monthly Sprint", "30-Day")
}

data class StrategicOKR(
    val id: String,
    val title: String,
    val pillar: LifePillarType,
    val horizon: GoalHorizon,
    val targetDescription: String,
    val currentMetric: String,
    val progressPercent: Int, // 0-100
    val keyResults: List<String> = emptyList(),
    val deadline: String = "30 September 2026",
    val status: String = "On Track", // "On Track", "Ahead", "Needs Focus", "Achieved"
    val colorHex: String = "#2563EB"
)

// 2. Habit Stacking: Anchor Habit -> Micro New Habit -> Immediate Celebration
data class HabitStack(
    val id: String,
    val anchorHabit: String,       // "After I brew morning espresso..."
    val newTinyHabit: String,       // "...I will write down top 3 highlights"
    val rewardOrCelebration: String, // "...then check off my streak and sip first taste"
    val pillar: LifePillarType,
    val timeOfDay: String,          // "Morning", "Afternoon", "Evening"
    val streakDays: Int = 14,
    val isCompletedToday: Boolean = false
)

// 3. Weekly Review & Retrospective
data class WeeklyReview(
    val id: String,
    val weekNumber: Int = 38,
    val weekRange: String = "Sep 15 - Sep 21, 2026",
    val completedTasksCount: Int = 24,
    val focusHoursLogged: Double = 22.5,
    val habitsConsistencyRate: Int = 91,
    val topWins: List<String> = listOf(
        "Shipped Android release v2.4 with 0 crashes",
        "Hit 10k daily step goal 6 out of 7 days",
        "Stayed under weekly dining out expense budget"
    ),
    val areasToImprove: String = "Protect deeper morning focus blocks without slack distractions",
    val nextWeekFocus: List<String> = listOf(
        "Kick off Quarter 4 OKR planning",
        "Vehicle brake pad service check",
        "Prep family weekend getaway itinerary"
    ),
    val overallScore: Int = 92, // Score out of 100
    val isReviewed: Boolean = true,
    val reviewedDate: String = "Sunday, Sep 20"
)

// 4. Life OS Weekly Scorecard per Pillar
data class PillarWeeklySummary(
    val pillar: LifePillarType,
    val score: Int,
    val deltaFromLastWeek: Int, // e.g. +4 or -2
    val highlight: String
)
