package com.example.model

enum class EnergyStatus(val label: String, val colorHex: String) {
    LIGHT("Light & Flexible", "#10B981"),
    OPTIMAL("Optimal Balance", "#3B82F6"),
    HEAVY("High Demand", "#F59E0B"),
    OVERLOADED("Overloaded", "#EF4444")
}

data class DailyEnergyBudget(
    val totalCapacityHours: Double = 8.0,
    val committedHours: Double = 5.5,
    val remainingHours: Double = 2.5,
    val loadPercentage: Int = 69,
    val status: EnergyStatus = EnergyStatus.OPTIMAL,
    val burnoutWarning: String? = null
)

enum class LifePillarType(val label: String, val icon: String, val defaultColor: String) {
    WORK("Career & Work", "💼", "#2563EB"),
    HEALTH("Health & Vitality", "💚", "#16A34A"),
    WEALTH("Wealth & Finance", "💰", "#D97706"),
    GROWTH("Personal Growth", "🧠", "#9333EA"),
    HOME("Home & Relationships", "🏡", "#EA580C")
}

data class DailyHighlight(
    val id: String,
    val title: String,
    val pillar: LifePillarType = LifePillarType.WORK,
    val estimatedMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val timeSlot: String? = "10:30 AM"
)

data class TimeBlockGap(
    val id: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val suggestedTitle: String,
    val gapType: String = "focus" // "focus", "recharge", "catchup"
)

data class LifePillar(
    val type: LifePillarType,
    val title: String,
    val score: Int, // 0 to 100
    val activeInitiative: String,
    val metricSummary: String,
    val statusText: String,
    val targetModule: String
)

data class DailyReflection(
    val id: String,
    val date: String,
    val topWins: String,
    val gratitudeNotes: String,
    val lessonOrNextStep: String,
    val energyRating: Int = 4, // 1-5 scale
    val timestamp: Long = System.currentTimeMillis()
)
