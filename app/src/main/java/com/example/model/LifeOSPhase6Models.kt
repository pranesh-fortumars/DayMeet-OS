package com.example.model

/**
 * Phase 6: Autonomous Delegation, Collaborative Sync & Ecosystem Models
 */

// 1. Autonomous Smart Task Delegation
enum class DelegationRole(val title: String, val badgeColorHex: String) {
    ENGINEERING("Engineering", "#2563EB"),
    PRODUCT_DESIGN("Design", "#7C3AED"),
    FINANCE_LEGAL("Finance / Legal", "#059669"),
    EXECUTIVE_OPS("Executive Ops", "#D97706"),
    HOUSEHOLD("Family / Home", "#DC2626")
}

data class DelegatedTaskItem(
    val id: String,
    val title: String,
    val assigneeName: String,
    val assigneeAvatarInitials: String,
    val role: DelegationRole,
    val deadline: String,
    val status: String = "In Review", // "Pending Accept", "In Progress", "In Review", "Completed"
    val isAutoFollowUpEnabled: Boolean = true,
    val lastPingMessage: String = "Requested status report for tomorrow's demo",
    val notes: String = "Shared Figma prototype link with feedback tokens."
)

// 2. Multi-Profile Context Engine
enum class LifeOSProfile(val label: String, val iconKey: String, val colorHex: String) {
    WORK("Work & Startup", "work", "#3B82F6"),
    PERSONAL("Personal & Wellness", "favorite", "#10B981"),
    CREATIVE("Creative Studio", "palette", "#8B5CF6"),
    FAMILY("Family & Household", "home", "#F59E0B")
}

// 3. Smart Digest & Daily Narrative Synthesis
data class AutonomousDailyDigest(
    val dateLabel: String = "Wednesday, Sep 23, 2026",
    val synthesizedExecutiveSummary: String = "Productive sprint velocity! 4 of 5 core OKR deliverables completed. Meeting load remained under 30%. Alex Chen completed the Cloud SQL migration schema; Sarah Lee is reviewing UX tokens.",
    val delegationHealthScore: Int = 94, // % on-track delegated tasks
    val autonomousReschedulingCount: Int = 3, // Tasks re-balanced automatically
    val energyForecast: String = "Sustained high energy window until 4:30 PM"
)

// 4. End-to-End Encrypted Ecosystem Vault
data class EcosystemBackupState(
    val lastBackupTimestamp: String = "Today, 04:30 AM",
    val isCloudE2EEEnabled: Boolean = true,
    val backupSizeKb: Int = 2480,
    val encryptionStandard: String = "AES-256-GCM Hardware Keystore",
    val linkedDevicesCount: Int = 3 // Phone, Tablet, Laptop Companion
)
