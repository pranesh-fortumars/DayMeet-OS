package com.example.model

/**
 * Phase 5: Deep Integrations, Biometrics & Device Telemetry Models
 */

// 1. Health Connect & Sensor Biometrics Data
data class HealthBiometrics(
    val sleepDurationHours: Double = 7.5,
    val sleepQualityScore: Int = 88, // out of 100
    val restingHeartRateBpm: Int = 58,
    val hrvMilliseconds: Int = 62, // Heart Rate Variability
    val dailySteps: Int = 8420,
    val stepGoal: Int = 10000,
    val activeCaloriesBurned: Int = 460,
    val readinessScore: Int = 89, // Dynamic Readiness computed from Sleep + HRV
    val readinessStatus: String = "Peak Readiness", // "Peak Readiness", "Moderate", "Rest / Recovery"
    val lastSedentaryMinutes: Int = 72, // minutes since last movement
    val sedentaryAlertActive: Boolean = true
)

// 2. Android Home Screen Widget Configuration
enum class WidgetThemeStyle(val label: String) {
    DYNAMIC_SYSTEM("Dynamic System M3"),
    DARK_NEUMORPHIC("Dark Neumorphic"),
    OLED_MINIMAL("OLED Minimalist")
}

data class WidgetConfig(
    val widgetId: String,
    val title: String,
    val sizeSpan: String, // "4x2", "2x2", "4x1"
    val isPinned: Boolean,
    val themeStyle: WidgetThemeStyle = WidgetThemeStyle.DYNAMIC_SYSTEM,
    val previewDescription: String
)

// 3. Android Quick Settings & Persistent Notification HUD
data class PersistentHUDState(
    val isQuickSettingsTileActive: Boolean = false,
    val isOngoingNotificationEnabled: Boolean = true,
    val currentActiveTaskTitle: String = "Deep Architecture Review: Phase 5 Biometrics",
    val timeRemainingMinutes: Int = 22,
    val currentModeTag: String = "Focus Sanctuary"
)
