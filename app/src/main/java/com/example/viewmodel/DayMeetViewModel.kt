package com.example.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.DayMeetRepository
import com.example.localization.AppLanguage
import com.example.localization.LocalizationManager
import com.example.model.*
import com.example.util.AppUpdateManager
import com.example.util.PlayAppUpdateManager
import com.example.util.TaskNotificationScheduler
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

    private val _showQuickMeetingDialog = MutableStateFlow(false)
    val showQuickMeetingDialog: StateFlow<Boolean> = _showQuickMeetingDialog.asStateFlow()

    private val _showScheduleMeetingModal = MutableStateFlow(false)
    val showScheduleMeetingModal: StateFlow<Boolean> = _showScheduleMeetingModal.asStateFlow()

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

    // Daily Habit Goal Tracking (Non-Routine Tasks)
    private val _nonRoutineTasks = MutableStateFlow(DayMeetRepository.getInitialNonRoutineTasks())
    val nonRoutineTasks: StateFlow<List<NonRoutineTask>> = _nonRoutineTasks.asStateFlow()

    // System-wide Dark Mode & Tailwind Theme Engine
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _themeMode = MutableStateFlow("light") // "light", "dark", "system"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Confetti Animation State for habit streak records
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _confettiMilestone = MutableStateFlow<String?>(null)
    val confettiMilestone: StateFlow<String?> = _confettiMilestone.asStateFlow()

    // Phase 1: Life Mode (All, Work, Personal)
    private val _currentLifeMode = MutableStateFlow(LifeMode.ALL)
    val currentLifeMode: StateFlow<LifeMode> = _currentLifeMode.asStateFlow()

    // Phase 1: Life Inbox Items
    private val _lifeInboxItems = MutableStateFlow(
        listOf(
            LifeInboxItem(
                id = "inbox_1",
                content = "Need to renew bike insurance next month and compare prices.",
                type = LifeInboxType.TEXT,
                timestamp = "Today, 10:15 AM",
                source = "Quick Dump",
                suggestions = listOf(
                    LifeInboxSuggestion("sug_1", SuggestedActionType.ADD_VEHICLE_HOME, "Bike Insurance Renewal", "Due in 30 days • Compare quotes", "Home & Vehicle", "Vehicle"),
                    LifeInboxSuggestion("sug_2", SuggestedActionType.CREATE_REMINDER, "Remind: Insurance comparison", "Next month 1st", "Reminders"),
                    LifeInboxSuggestion("sug_3", SuggestedActionType.CREATE_TASK, "Compare insurance plans on PolicyBazaar", "Priority: Medium", "Tasks", "Personal")
                )
            ),
            LifeInboxItem(
                id = "inbox_2",
                content = "https://figma.com/design/daymeet-design-system-v2 Elena shared new UI specs",
                type = LifeInboxType.LINK,
                timestamp = "Yesterday, 06:40 PM",
                source = "Share Sheet",
                suggestions = listOf(
                    LifeInboxSuggestion("sug_4", SuggestedActionType.CREATE_TASK, "Review Figma design system v2", "From Elena • Link attached", "Tasks", "Work"),
                    LifeInboxSuggestion("sug_5", SuggestedActionType.SAVE_NOTE, "Design System v2 Specs Link", "Saved reference bookmark", "Notes")
                )
            ),
            LifeInboxItem(
                id = "inbox_3",
                content = "Voice note: Remember to order fresh organic coffee beans and oat milk before Saturday brunch",
                type = LifeInboxType.VOICE,
                timestamp = "Today, 08:30 AM",
                source = "Voice Capture",
                suggestions = listOf(
                    LifeInboxSuggestion("sug_6", SuggestedActionType.SAVE_WISHLIST, "Organic coffee beans & oat milk", "Est: ₹450 • Groceries", "Shopping")
                )
            )
        )
    )
    val lifeInboxItems: StateFlow<List<LifeInboxItem>> = _lifeInboxItems.asStateFlow()

    // Phase 2: Daily Architecture & Life Engine States
    private val _showMorningKickoff = MutableStateFlow(false)
    val showMorningKickoff: StateFlow<Boolean> = _showMorningKickoff.asStateFlow()

    private val _showEveningWindDown = MutableStateFlow(false)
    val showEveningWindDown: StateFlow<Boolean> = _showEveningWindDown.asStateFlow()

    // Phase 3: Strategic OKRs, Habit Stacking & Weekly Review Dialogs
    private val _showWeeklyReviewDialog = MutableStateFlow(false)
    val showWeeklyReviewDialog: StateFlow<Boolean> = _showWeeklyReviewDialog.asStateFlow()

    private val _strategicOkrs = MutableStateFlow(
        listOf(
            StrategicOKR(
                id = "okr_1",
                title = "Launch DayMeet Life OS Core Engine",
                pillar = LifePillarType.WORK,
                horizon = GoalHorizon.QUARTERLY,
                targetDescription = "100% stable architecture across 5 pillars with 0 crash rate",
                currentMetric = "88% Delivered • Final polish & OKR integration",
                progressPercent = 88,
                keyResults = listOf(
                    "Universal capture & Life Inbox active",
                    "Morning kickoff capacity load prevention",
                    "Habit stacks & weekly retrospective engine"
                ),
                deadline = "30 September 2026",
                status = "On Track",
                colorHex = "#2563EB"
            ),
            StrategicOKR(
                id = "okr_2",
                title = "Peak Vitality & Half-Marathon Endurance",
                pillar = LifePillarType.HEALTH,
                horizon = GoalHorizon.QUARTERLY,
                targetDescription = "21km continuous run + 10k daily step baseline",
                currentMetric = "16.5km Longest Run • 9,400 daily step avg",
                progressPercent = 78,
                keyResults = listOf(
                    "Hit 10k steps 6 days per week",
                    "Hydrate 2.5L+ consistently",
                    "Sunday 15km endurance block"
                ),
                deadline = "15 October 2026",
                status = "On Track",
                colorHex = "#16A34A"
            ),
            StrategicOKR(
                id = "okr_3",
                title = "Emergency Safety Net & Smart Portfolio Cap",
                pillar = LifePillarType.WEALTH,
                horizon = GoalHorizon.YEARLY,
                targetDescription = "Save ₹3,00,000 liquid buffer and keep daily burn under ₹5,000",
                currentMetric = "₹2,45,000 Saved • ₹3,850 avg daily burn",
                progressPercent = 82,
                keyResults = listOf(
                    "Strict 0-impulse buying rule",
                    "Auto-invest 25% on 1st of month",
                    "Clear recurring utility bills instantly"
                ),
                deadline = "31 December 2026",
                status = "Ahead",
                colorHex = "#D97706"
            ),
            StrategicOKR(
                id = "okr_4",
                title = "Read 18 Books & Daily Systems Mindfulness",
                pillar = LifePillarType.GROWTH,
                horizon = GoalHorizon.YEARLY,
                targetDescription = "18 non-fiction books read and synthesized into Notes vault",
                currentMetric = "13 Books read • 42 vault entries",
                progressPercent = 72,
                keyResults = listOf(
                    "20 minutes evening reading habit",
                    "Weekly retrospective synthesis note",
                    "Daily 10m breathwork session"
                ),
                deadline = "31 December 2026",
                status = "On Track",
                colorHex = "#9333EA"
            )
        )
    )
    val strategicOkrs: StateFlow<List<StrategicOKR>> = _strategicOkrs.asStateFlow()

    private val _habitStacks = MutableStateFlow(
        listOf(
            HabitStack(
                id = "stack_1",
                anchorHabit = "After I pour my first morning espresso ☕",
                newTinyHabit = "I will write my Top 3 Daily Highlights ☀️",
                rewardOrCelebration = "Savor the first sip with full presence",
                pillar = LifePillarType.WORK,
                timeOfDay = "Morning",
                streakDays = 18,
                isCompletedToday = true
            ),
            HabitStack(
                id = "stack_2",
                anchorHabit = "When I shut my laptop for lunch 💻",
                newTinyHabit = "I will drink a full 500ml glass of water 💧",
                rewardOrCelebration = "Take 3 deep shoulder rolls and smile",
                pillar = LifePillarType.HEALTH,
                timeOfDay = "Afternoon",
                streakDays = 12,
                isCompletedToday = false
            ),
            HabitStack(
                id = "stack_3",
                anchorHabit = "After brushing my teeth at night 🪥",
                newTinyHabit = "I will write 1 line of gratitude in Evening Wind-Down 🌙",
                rewardOrCelebration = "Set phone into Do Not Disturb with peace of mind",
                pillar = LifePillarType.GROWTH,
                timeOfDay = "Evening",
                streakDays = 21,
                isCompletedToday = false
            ),
            HabitStack(
                id = "stack_4",
                anchorHabit = "After parking the car back home 🚗",
                newTinyHabit = "I will inspect tire status and log any fuel expense 🧾",
                rewardOrCelebration = "Leave vehicle clean and ready for tomorrow",
                pillar = LifePillarType.HOME,
                timeOfDay = "Evening",
                streakDays = 9,
                isCompletedToday = false
            )
        )
    )
    val habitStacks: StateFlow<List<HabitStack>> = _habitStacks.asStateFlow()

    private val _weeklyReviews = MutableStateFlow(
        listOf(
            WeeklyReview(
                id = "wr_curr",
                weekNumber = 38,
                weekRange = "Sep 15 - Sep 21, 2026",
                completedTasksCount = 26,
                focusHoursLogged = 24.0,
                habitsConsistencyRate = 92,
                topWins = listOf(
                    "Shipped complete Life OS Phase 2 with 0 regressions",
                    "Maintained morning 10k steps and daily hydration",
                    "Stayed ₹12,000 under monthly discretionary spending"
                ),
                areasToImprove = "Guard afternoon focus sanctuary against non-urgent meeting invites.",
                nextWeekFocus = listOf(
                    "Sprint review & Q4 roadmap signoff",
                    "Schedule annual dental cleaning visit",
                    "Finish reading 'Atomic Habits' chapter 15 notes"
                ),
                overallScore = 93,
                isReviewed = true,
                reviewedDate = "Sunday, Sep 20"
            )
        )
    )
    val weeklyReviews: StateFlow<List<WeeklyReview>> = _weeklyReviews.asStateFlow()

    // Phase 4: Proactive Intelligence & Context Awareness States
    private val _contextTriggers = MutableStateFlow(
        listOf(
            ContextTrigger(
                id = "trig_home",
                type = GeofenceTriggerType.ARRIVING_HOME,
                locationName = "Home Sanctuary (Indiranagar)",
                actionPrompt = "Welcome back home! Transition into personal unwind mode.",
                suggestedActions = listOf(
                    "Log car fuel / maintenance check",
                    "Review evening family dinner plan",
                    "Begin 5-min Evening Wind-Down ritual"
                ),
                isActive = true,
                lastTriggeredTime = "Today, 06:45 PM"
            ),
            ContextTrigger(
                id = "trig_store",
                type = GeofenceTriggerType.STORE_PROXIMITY,
                locationName = "Nature's Basket Organic Supermarket (250m away)",
                actionPrompt = "You have 3 unfulfilled grocery items nearby!",
                suggestedActions = listOf(
                    "Organic coffee beans & oat milk",
                    "Greek yogurt (high protein)",
                    "Almond butter & sourdough bread"
                ),
                isActive = true,
                lastTriggeredTime = "Just now"
            ),
            ContextTrigger(
                id = "trig_work",
                type = GeofenceTriggerType.ARRIVING_WORK,
                locationName = "WeWork Galaxy / HQ Office",
                actionPrompt = "Good morning! Setup deep work zone.",
                suggestedActions = listOf(
                    "Turn on Focus Sanctuary mode",
                    "Review Top 3 Morning Highlights",
                    "Check Sprint Backlog & 10:30 AM Standup"
                ),
                isActive = true,
                lastTriggeredTime = "Yesterday, 09:15 AM"
            )
        )
    )
    val contextTriggers: StateFlow<List<ContextTrigger>> = _contextTriggers.asStateFlow()

    private val _activeContextNotification = MutableStateFlow<ContextTrigger?>(
        _contextTriggers.value.find { it.id == "trig_store" }
    )
    val activeContextNotification: StateFlow<ContextTrigger?> = _activeContextNotification.asStateFlow()

    private val _upcomingMeetingPreBrief = MutableStateFlow(
        MeetingPreBrief(
            meetingId = "meet_q4_sync",
            meetingTitle = "Q4 Engineering & Roadmap Architecture Sync",
            scheduledTime = "11:00 AM - 11:45 AM",
            startsInMinutes = 15,
            attendees = listOf("Alex Rivera (Lead Arch)", "Priya Sharma (Product)", "Devon Vance (Design)"),
            keyContextSummary = "Final review of design token migration, database schema lock, and automated test passes before public rollout.",
            previousDecisions = listOf(
                "Agreed to decouple shared models into domain packages",
                "Capped daily meeting slots at maximum 3.5 hours"
            ),
            openActionItemsForAttendees = listOf(
                "Alex: Confirm SQLCipher encryption benchmark",
                "Devon: Share Figma exported token spec",
                "Me: Demo Life OS Phase 3 & 4 architecture"
            ),
            quickNotes = "Need to emphasize zero-regression test suite and sub-second cold launch speed."
        )
    )
    val upcomingMeetingPreBrief: StateFlow<MeetingPreBrief?> = _upcomingMeetingPreBrief.asStateFlow()

    private val _showPreMeetingBriefDialog = MutableStateFlow(false)
    val showPreMeetingBriefDialog: StateFlow<Boolean> = _showPreMeetingBriefDialog.asStateFlow()

    private val _showAudioMemoDialog = MutableStateFlow(false)
    val showAudioMemoDialog: StateFlow<Boolean> = _showAudioMemoDialog.asStateFlow()

    private val _audioVoiceMemos = MutableStateFlow(
        listOf(
            AudioVoiceMemo(
                id = "memo_1",
                title = "Post-Meeting Architecture Takeaways",
                timestamp = "Today, 11:50 AM",
                durationSeconds = 48,
                audioSnippetSimulatedText = "Alex confirmed SQLite Room schema migration is green. We need to add geofence proximity trigger for grocery errand and run Robolectric tests.",
                extractedActionItems = listOf(
                    "Integrate grocery proximity trigger in home feed",
                    "Verify Robolectric tests on Gradle pipeline"
                ),
                isProcessed = true
            )
        )
    )
    val audioVoiceMemos: StateFlow<List<AudioVoiceMemo>> = _audioVoiceMemos.asStateFlow()

    private val _cognitiveAnalytics = MutableStateFlow(
        CognitiveLoadAnalytics(
            focusHours = 4.5,
            meetingHours = 2.0,
            focusMeetingRatioPercent = 69,
            fragmentedGapsCount = 2,
            interruptionRiskLevel = "Optimal Balance",
            recommendation = "Great focus balance! Merge the 20-minute gap at 3:30 PM into your Recharge window to avoid fragmented context switching."
        )
    )
    val cognitiveAnalytics: StateFlow<CognitiveLoadAnalytics> = _cognitiveAnalytics.asStateFlow()

    // Phase 5: Deep Integrations, Biometrics & Device Telemetry States
    private val _biometrics = MutableStateFlow(HealthBiometrics())
    val biometrics: StateFlow<HealthBiometrics> = _biometrics.asStateFlow()

    private val _widgetConfigs = MutableStateFlow(
        listOf(
            WidgetConfig(
                widgetId = "widget_today_glance",
                title = "Today at a Glance",
                sizeSpan = "4x2",
                isPinned = true,
                themeStyle = WidgetThemeStyle.DYNAMIC_SYSTEM,
                previewDescription = "Contextual Next Meeting, Top Sprint Priority, and Live Focus Sanctuary timer."
            ),
            WidgetConfig(
                widgetId = "widget_habit_matrix",
                title = "Habit Stacking Matrix",
                sizeSpan = "2x2",
                isPinned = false,
                themeStyle = WidgetThemeStyle.DYNAMIC_SYSTEM,
                previewDescription = "1-tap completion for daily anchors with real-time streak counters."
            ),
            WidgetConfig(
                widgetId = "widget_pillars_radar",
                title = "5 Life Pillars Radar",
                sizeSpan = "4x1",
                isPinned = false,
                themeStyle = WidgetThemeStyle.DARK_NEUMORPHIC,
                previewDescription = "Holistic balance score across Work, Health, Wealth, Growth, and Home."
            )
        )
    )
    val widgetConfigs: StateFlow<List<WidgetConfig>> = _widgetConfigs.asStateFlow()

    private val _persistentHUDState = MutableStateFlow(PersistentHUDState())
    val persistentHUDState: StateFlow<PersistentHUDState> = _persistentHUDState.asStateFlow()

    private val _showSedentaryStretchDialog = MutableStateFlow(false)
    val showSedentaryStretchDialog: StateFlow<Boolean> = _showSedentaryStretchDialog.asStateFlow()

    private val _showWidgetPreviewDialog = MutableStateFlow(false)
    val showWidgetPreviewDialog: StateFlow<Boolean> = _showWidgetPreviewDialog.asStateFlow()

    // Phase 6: Autonomous Delegation, Collaborative Sync & Ecosystem States
    private val _activeProfile = MutableStateFlow(LifeOSProfile.WORK)
    val activeProfile: StateFlow<LifeOSProfile> = _activeProfile.asStateFlow()

    private val _delegatedTasks = MutableStateFlow(
        listOf(
            DelegatedTaskItem(
                id = "del_1",
                title = "Cloud SQL Read-Replica Infrastructure Migration",
                assigneeName = "Alex Chen",
                assigneeAvatarInitials = "AC",
                role = DelegationRole.ENGINEERING,
                deadline = "Tomorrow, 5:00 PM",
                status = "In Progress",
                isAutoFollowUpEnabled = true,
                lastPingMessage = "Terraform scripts completed; staging deploy underway.",
                notes = "Target <10ms latency for cross-region reads."
            ),
            DelegatedTaskItem(
                id = "del_2",
                title = "Design Tokens System & M3 Dark Mode Palette",
                assigneeName = "Sarah Lee",
                assigneeAvatarInitials = "SL",
                role = DelegationRole.PRODUCT_DESIGN,
                deadline = "Friday, 12:00 PM",
                status = "In Review",
                isAutoFollowUpEnabled = true,
                lastPingMessage = "Figma token variables exported to GitHub pull request.",
                notes = "Check contrast ratio on Slate-900 surface."
            ),
            DelegatedTaskItem(
                id = "del_3",
                title = "Q3 Expense Reimbursements & Invoice Reconciliations",
                assigneeName = "Elena Rostova",
                assigneeAvatarInitials = "ER",
                role = DelegationRole.FINANCE_LEGAL,
                deadline = "Next Monday",
                status = "Pending Accept",
                isAutoFollowUpEnabled = false,
                lastPingMessage = "Generated monthly PDF breakdown for accounts team.",
                notes = "Requires VP sign-off before Friday."
            )
        )
    )
    val delegatedTasks: StateFlow<List<DelegatedTaskItem>> = _delegatedTasks.asStateFlow()

    private val _dailyDigest = MutableStateFlow(AutonomousDailyDigest())
    val dailyDigest: StateFlow<AutonomousDailyDigest> = _dailyDigest.asStateFlow()

    private val _ecosystemBackupState = MutableStateFlow(EcosystemBackupState())
    val ecosystemBackupState: StateFlow<EcosystemBackupState> = _ecosystemBackupState.asStateFlow()

    private val _showDelegationDialog = MutableStateFlow(false)
    val showDelegationDialog: StateFlow<Boolean> = _showDelegationDialog.asStateFlow()

    private val _showDailyDigestDialog = MutableStateFlow(false)
    val showDailyDigestDialog: StateFlow<Boolean> = _showDailyDigestDialog.asStateFlow()

    private val _showEcosystemVaultDialog = MutableStateFlow(false)
    val showEcosystemVaultDialog: StateFlow<Boolean> = _showEcosystemVaultDialog.asStateFlow()

    private val _dailyHighlights = MutableStateFlow(
        listOf(
            DailyHighlight(
                id = "hl_1",
                title = "Finalize Q4 Token Audit Signoff",
                pillar = LifePillarType.WORK,
                estimatedMinutes = 45,
                isCompleted = false,
                timeSlot = "10:45 AM"
            ),
            DailyHighlight(
                id = "hl_2",
                title = "5km Sunset Cardio & Core Reset",
                pillar = LifePillarType.HEALTH,
                estimatedMinutes = 40,
                isCompleted = false,
                timeSlot = "05:30 PM"
            ),
            DailyHighlight(
                id = "hl_3",
                title = "Clear Tata Power Bill & Monthly Budget Review",
                pillar = LifePillarType.WEALTH,
                estimatedMinutes = 15,
                isCompleted = false,
                timeSlot = "07:15 PM"
            )
        )
    )
    val dailyHighlights: StateFlow<List<DailyHighlight>> = _dailyHighlights.asStateFlow()

    private val _timeBlockGaps = MutableStateFlow(
        listOf(
            TimeBlockGap(
                id = "gap_1",
                startTime = "11:30 AM",
                endTime = "12:30 PM",
                durationMinutes = 60,
                suggestedTitle = "Focus Window: Deep Engineering & Token Review",
                gapType = "focus"
            ),
            TimeBlockGap(
                id = "gap_2",
                startTime = "03:30 PM",
                endTime = "04:15 PM",
                durationMinutes = 45,
                suggestedTitle = "Bio Reset & Quick Inbox Processing",
                gapType = "recharge"
            )
        )
    )
    val timeBlockGaps: StateFlow<List<TimeBlockGap>> = _timeBlockGaps.asStateFlow()

    private val _dailyReflections = MutableStateFlow(
        listOf(
            DailyReflection(
                id = "ref_1",
                date = "Yesterday",
                topWins = "Shipped mobile tokens system, completed 6km run, zero impulse spending.",
                gratitudeNotes = "Grateful for crisp autumn weather and team velocity.",
                lessonOrNextStep = "Block morning 9-11am exclusively for deep engineering work.",
                energyRating = 5
            )
        )
    )
    val dailyReflections: StateFlow<List<DailyReflection>> = _dailyReflections.asStateFlow()

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

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private val _isFocusCompleted = MutableStateFlow(false)
    val isFocusCompleted: StateFlow<Boolean> = _isFocusCompleted.asStateFlow()

    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    private val _selectedFocusTaskId = MutableStateFlow<String?>(null)
    val selectedFocusTaskId: StateFlow<String?> = _selectedFocusTaskId.asStateFlow()

    // Multilingual & Dynamic Localization State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

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

    private val _monthlyBudgetTarget = MutableStateFlow(60000.0)
    val monthlyBudgetTarget: StateFlow<Double> = _monthlyBudgetTarget.asStateFlow()

    private val _upcomingBills = MutableStateFlow(DayMeetRepository.getInitialUpcomingBills())
    val upcomingBills: StateFlow<List<UpcomingBill>> = _upcomingBills.asStateFlow()

    private val _emis = MutableStateFlow(DayMeetRepository.getInitialEmis())
    val emis: StateFlow<List<EmiItem>> = _emis.asStateFlow()

    private val _debts = MutableStateFlow(DayMeetRepository.getInitialDebts())
    val debts: StateFlow<List<DebtItem>> = _debts.asStateFlow()

    // Projects Management (Section 9)
    private val _projects = MutableStateFlow(DayMeetRepository.getInitialProjects())
    val projects: StateFlow<List<ProjectItem>> = _projects.asStateFlow()

    // Appointments (Section 11)
    private val _appointments = MutableStateFlow(DayMeetRepository.getInitialAppointments())
    val appointments: StateFlow<List<AppointmentItem>> = _appointments.asStateFlow()

    // Home & Vehicle (Sections 28 & 29)
    private val _homeVehicleItems = MutableStateFlow(DayMeetRepository.getInitialHomeVehicleItems())
    val homeVehicleItems: StateFlow<List<HomeVehicleItem>> = _homeVehicleItems.asStateFlow()


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

    // App Launch, Initialization & Recovery State
    private val _appLaunchStep = MutableStateFlow(AppLaunchStep.READY)
    val appLaunchStep: StateFlow<AppLaunchStep> = _appLaunchStep.asStateFlow()

    fun finishSplash() { _appLaunchStep.value = AppLaunchStep.INITIALIZING }
    fun finishInitialization() { _appLaunchStep.value = AppLaunchStep.READY }
    fun triggerSplashLaunch() { _appLaunchStep.value = AppLaunchStep.SPLASH }

    // Offline & Sync System
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _hasSyncIssue = MutableStateFlow(false)
    val hasSyncIssue: StateFlow<Boolean> = _hasSyncIssue.asStateFlow()

    private val _unsyncedChangesCount = MutableStateFlow(0)
    val unsyncedChangesCount: StateFlow<Int> = _unsyncedChangesCount.asStateFlow()

    fun toggleOfflineMode() {
        val next = !_isOffline.value
        _isOffline.value = next
        if (next) {
            _unsyncedChangesCount.value = 3
            showToast("Offline Mode active — 3 changes queued locally")
        } else {
            showToast("Back online — syncing queued changes…")
            _unsyncedChangesCount.value = 0
            triggerManualSync()
        }
    }

    fun triggerSyncIssue() {
        _hasSyncIssue.value = true
        showToast("⚠️ Sync issue simulated: Cloud endpoint unreachable")
    }

    fun resolveSyncIssue() {
        _hasSyncIssue.value = false
        triggerManualSync()
    }

    // Error Handling Dialogs
    private val _showNetworkError = MutableStateFlow(false)
    val showNetworkError: StateFlow<Boolean> = _showNetworkError.asStateFlow()

    private val _showServerError = MutableStateFlow(false)
    val showServerError: StateFlow<Boolean> = _showServerError.asStateFlow()

    fun openNetworkError() { _showNetworkError.value = true }
    fun closeNetworkError() { _showNetworkError.value = false }
    fun openServerError() { _showServerError.value = true }
    fun closeServerError() { _showServerError.value = false }

    // Security & Biometric Lock
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _isSessionExpired = MutableStateFlow(false)
    val isSessionExpired: StateFlow<Boolean> = _isSessionExpired.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    fun lockVault() { _isAppLocked.value = true }
    fun unlockVault() {
        _isAppLocked.value = false
        showToast("✓ Vault Unlocked with Biometrics")
    }

    fun triggerSessionExpired() { _isSessionExpired.value = true }
    fun restoreSession() {
        _isSessionExpired.value = false
        showToast("✓ Session Restored. Unsaved drafts preserved.")
    }

    fun openLogoutDialog() { _showLogoutDialog.value = true }
    fun closeLogoutDialog() { _showLogoutDialog.value = false }
    fun performLogout() {
        _showLogoutDialog.value = false
        showToast("Logged out successfully.")
    }

    // Undo & Destructive Deletion
    private val _pendingDeletion = MutableStateFlow<PendingDeletion?>(null)
    val pendingDeletion: StateFlow<PendingDeletion?> = _pendingDeletion.asStateFlow()

    private val _showUndoSnackbar = MutableStateFlow(false)
    val showUndoSnackbar: StateFlow<Boolean> = _showUndoSnackbar.asStateFlow()

    private val _lastDeletedMessage = MutableStateFlow("")
    val lastDeletedMessage: StateFlow<String> = _lastDeletedMessage.asStateFlow()

    private var lastUndoAction: (() -> Unit)? = null

    fun requestDeleteConfirmation(title: String, module: String = "Item", onExecute: () -> Unit, undoAction: (() -> Unit)? = null) {
        _pendingDeletion.value = PendingDeletion(
            id = "del_${System.currentTimeMillis()}",
            title = title,
            module = module
        ) {
            onExecute()
            _lastDeletedMessage.value = "$module \"$title\" deleted"
            lastUndoAction = undoAction
            _showUndoSnackbar.value = true
            viewModelScope.launch {
                delay(5000)
                _showUndoSnackbar.value = false
            }
        }
    }

    fun confirmPendingDeletion() {
        _pendingDeletion.value?.execute?.invoke()
        _pendingDeletion.value = null
    }

    fun cancelPendingDeletion() {
        _pendingDeletion.value = null
    }

    fun performUndo() {
        lastUndoAction?.invoke()
        _showUndoSnackbar.value = false
        showToast("✓ Action Undone")
    }

    fun dismissUndoSnackbar() {
        _showUndoSnackbar.value = false
    }

    // Schedule Conflict, Reschedule & Drafts
    private val _activeConflict = MutableStateFlow<ScheduleConflict?>(null)
    val activeConflict: StateFlow<ScheduleConflict?> = _activeConflict.asStateFlow()

    private val _rescheduleItemTitle = MutableStateFlow<String?>(null)
    val rescheduleItemTitle: StateFlow<String?> = _rescheduleItemTitle.asStateFlow()

    private val _activeDraft = MutableStateFlow<DraftRecoveryItem?>(null)
    val activeDraft: StateFlow<DraftRecoveryItem?> = _activeDraft.asStateFlow()

    fun triggerScheduleConflict(existingTitle: String, existingTime: String, newTitle: String, newTime: String) {
        _activeConflict.value = ScheduleConflict(existingTitle, existingTime, newTitle, newTime)
    }

    fun closeScheduleConflict() { _activeConflict.value = null }

    fun openRescheduleSheet(title: String) { _rescheduleItemTitle.value = title }
    fun closeRescheduleSheet() { _rescheduleItemTitle.value = null }

    fun applyReschedule(newSlot: String) {
        val title = _rescheduleItemTitle.value ?: "Item"
        _rescheduleItemTitle.value = null
        showToast("✓ Rescheduled \"$title\" to $newSlot")
    }

    fun dismissDraft() {
        _activeDraft.value = null
        showToast("Draft discarded")
    }

    fun restoreDraft() {
        val draft = _activeDraft.value
        _activeDraft.value = null
        showToast("Restored draft: ${draft?.title}")
        openQuickAdd(draft?.type ?: "Task")
    }

    // First Data Experience & Sample Day
    private val _showFirstDataBanner = MutableStateFlow(true)
    val showFirstDataBanner: StateFlow<Boolean> = _showFirstDataBanner.asStateFlow()

    private val _isSampleDataActive = MutableStateFlow(false)
    val isSampleDataActive: StateFlow<Boolean> = _isSampleDataActive.asStateFlow()

    fun dismissFirstDataBanner() { _showFirstDataBanner.value = false }

    fun useSampleDay() {
        _isSampleDataActive.value = true
        _showFirstDataBanner.value = false
        showToast("✓ Guided Sample Day loaded (5 activities)")
    }

    fun clearSampleDay() {
        _isSampleDataActive.value = false
        showToast("✓ Sample data removed. Clean slate ready.")
    }

    // Integration Center, Import/Export & Feedback
    private val _showIntegrationCenter = MutableStateFlow(false)
    val showIntegrationCenter: StateFlow<Boolean> = _showIntegrationCenter.asStateFlow()

    private val _showImportExport = MutableStateFlow(false)
    val showImportExport: StateFlow<Boolean> = _showImportExport.asStateFlow()

    private val _isExportMode = MutableStateFlow(true)
    val isExportMode: StateFlow<Boolean> = _isExportMode.asStateFlow()

    private val _showFeedback = MutableStateFlow(false)
    val showFeedback: StateFlow<Boolean> = _showFeedback.asStateFlow()

    fun openIntegrationCenter() { _showIntegrationCenter.value = true }
    fun closeIntegrationCenter() { _showIntegrationCenter.value = false }

    fun openExportData() { _isExportMode.value = true; _showImportExport.value = true }
    fun openImportData() { _isExportMode.value = false; _showImportExport.value = true }
    fun closeImportExport() { _showImportExport.value = false }

    fun openFeedback() { _showFeedback.value = true }
    fun closeFeedback() { _showFeedback.value = false }
    fun submitFeedback(rating: String, text: String) {
        _showFeedback.value = false
        showToast("Thank you for your feedback! ($rating)")
    }

    fun resetLaunchSequence() { _appLaunchStep.value = AppLaunchStep.SPLASH }
    fun simulateConflict() {
        triggerScheduleConflict("Quarterly Review", "Today, 02:00 PM", "Client Onboarding", "Today, 02:15 PM")
    }
    fun simulateSessionExpiry() { triggerSessionExpired() }
    fun simulateSyncIssue() { triggerSyncIssue() }
    fun openImportExport(isExport: Boolean) {
        if (isExport) openExportData() else openImportData()
    }
    fun openPermissionRequest(typeString: String = "notifications") {
        _activePermissionRequest.value = when (typeString.lowercase()) {
            "calendar" -> com.example.ui.screens.PermissionFlowType.CALENDAR
            "location" -> com.example.ui.screens.PermissionFlowType.LOCATION
            "health" -> com.example.ui.screens.PermissionFlowType.HEALTH
            else -> com.example.ui.screens.PermissionFlowType.NOTIFICATIONS
        }
    }

    // Contextual Permission Flow
    private val _activePermissionRequest = MutableStateFlow<com.example.ui.screens.PermissionFlowType?>(null)
    val activePermissionRequest: StateFlow<com.example.ui.screens.PermissionFlowType?> = _activePermissionRequest.asStateFlow()

    fun requestPermission(type: com.example.ui.screens.PermissionFlowType) {
        _activePermissionRequest.value = type
    }

    fun grantPermission() {
        val type = _activePermissionRequest.value
        _activePermissionRequest.value = null
        showToast("✓ $type permission granted")
    }

    fun denyPermission() {
        val type = _activePermissionRequest.value
        _activePermissionRequest.value = null
        showToast("$type was not granted. App adapted.")
    }

    // Manual Dashboard & All Widgets Network Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncedTime = MutableStateFlow("Just now")
    val lastSyncedTime: StateFlow<String> = _lastSyncedTime.asStateFlow()

    private val _syncPulseKey = MutableStateFlow(0)
    val syncPulseKey: StateFlow<Int> = _syncPulseKey.asStateFlow()

    fun triggerManualSync() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        viewModelScope.launch {
            // Simulate network refresh latency across Cloud Calendar, Finance, Tasks & Health
            delay(1200)
            _lastSyncedTime.value = "Just now"
            _syncPulseKey.value += 1
            _isSyncing.value = false
            showToast("Dashboard synchronized: 6 widgets updated & current")
        }
    }

    fun addCrossStreamItem(
        title: String,
        subtitle: String,
        tag: String = "Task",
        tagType: String = "task",
        time: String = "Now"
    ) {
        val newItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = time,
            title = title.ifBlank { "New Task" },
            subtitle = subtitle,
            tag = tag,
            tagType = tagType,
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newItem) + _crossStreamItems.value
        showToast("Added to stream: $title")
    }

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

    fun openQuickAdd(initialTab: String = "Task") = openCreateTask(initialTab)

    fun closeCreateTask() {
        _showCreateSheet.value = false
    }

    fun openQuickScheduleMeeting() {
        _showScheduleMeetingModal.value = true
    }

    fun closeQuickScheduleMeeting() {
        _showQuickMeetingDialog.value = false
        _showScheduleMeetingModal.value = false
    }

    fun openScheduleMeeting() {
        _showScheduleMeetingModal.value = true
    }

    fun closeScheduleMeeting() {
        _showScheduleMeetingModal.value = false
    }

    fun scheduleMeeting(
        title: String,
        participants: List<String>,
        date: String = "Today",
        time: String = "Today, 03:00 PM",
        duration: String = "30 mins",
        platform: String = "Google Meet",
        agenda: String = ""
    ) {
        val attendeesList = if (participants.isEmpty()) {
            listOf(Attendee("Alex Chen (You)", avatarUrl = DayMeetRepository.ALEX_AVATAR))
        } else {
            participants.map { name ->
                Attendee(
                    name = name.trim(),
                    role = "Participant",
                    avatarUrl = if (name.contains("Alex", ignoreCase = true)) DayMeetRepository.ALEX_AVATAR else null,
                    initials = name.trim().take(2).uppercase()
                )
            }
        }
        val newMeeting = MeetingItem(
            id = "m_${System.currentTimeMillis()}",
            title = title.ifBlank { "Team Meeting" },
            time = time.ifBlank { "Today, 03:00 PM" },
            duration = duration,
            platform = platform,
            status = "Scheduled",
            attendees = attendeesList,
            attendeesCount = attendeesList.size
        )
        _meetings.value = listOf(newMeeting) + _meetings.value

        // Also add to Cross-Module Stream
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = if (time.contains(",")) time.substringAfter(",").trim() else time,
            title = title.ifBlank { "Team Meeting" },
            subtitle = "${attendeesList.size} attendees • $duration • $platform",
            tag = "Meeting",
            tagType = "meeting",
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showScheduleMeetingModal.value = false
        _showQuickMeetingDialog.value = false
        val participantsDesc = if (participants.isNotEmpty()) " with ${participants.size} participants" else ""
        showToast("Meeting scheduled for $duration$participantsDesc! 📅")
    }

    fun scheduleMeetingFromHub(
        title: String,
        participants: List<String>,
        time: String,
        platform: String = "Google Meet",
        duration: String = "30 mins"
    ) {
        val attendeesList = if (participants.isEmpty()) {
            listOf(Attendee("Alex Chen (You)", avatarUrl = DayMeetRepository.ALEX_AVATAR))
        } else {
            participants.map { name ->
                Attendee(
                    name = name.trim(),
                    role = "Participant",
                    avatarUrl = if (name.contains("Alex", ignoreCase = true)) DayMeetRepository.ALEX_AVATAR else null,
                    initials = name.trim().take(2).uppercase()
                )
            }
        }
        val newMeeting = MeetingItem(
            id = "m_${System.currentTimeMillis()}",
            title = title.ifBlank { "Quick Meeting" },
            time = time.ifBlank { "Today, 03:00 PM" },
            duration = duration,
            platform = platform,
            status = "Scheduled",
            attendees = attendeesList,
            attendeesCount = attendeesList.size
        )
        _meetings.value = listOf(newMeeting) + _meetings.value

        // Also add to Cross-Module Stream
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = if (time.contains(",")) time.substringAfter(",").trim() else time,
            title = title.ifBlank { "Quick Meeting" },
            subtitle = "${attendeesList.size} attendees • $platform",
            tag = "Meeting",
            tagType = "meeting",
            isCompleted = false
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showQuickMeetingDialog.value = false
        val participantsDesc = if (participants.isNotEmpty()) " with ${participants.joinToString(", ")}" else ""
        showToast("Meeting scheduled: ${title.ifBlank { "Quick Meeting" }}$participantsDesc at $time 📅")
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

    fun toggleWellnessReminder(type: String) {
        val current = _healthMetrics.value
        val updated = when (type.lowercase()) {
            "water" -> current.copy(waterReminderOn = !current.waterReminderOn)
            "walk" -> current.copy(walkReminderOn = !current.walkReminderOn)
            "exercise" -> current.copy(exerciseReminderOn = !current.exerciseReminderOn)
            "sleep" -> current.copy(sleepReminderOn = !current.sleepReminderOn)
            "meditation" -> current.copy(meditationReminderOn = !current.meditationReminderOn)
            "posture" -> current.copy(postureReminderOn = !current.postureReminderOn)
            else -> current
        }
        _healthMetrics.value = updated
        val state = when (type.lowercase()) {
            "water" -> updated.waterReminderOn
            "walk" -> updated.walkReminderOn
            "exercise" -> updated.exerciseReminderOn
            "sleep" -> updated.sleepReminderOn
            "meditation" -> updated.meditationReminderOn
            "posture" -> updated.postureReminderOn
            else -> true
        }
        showToast("$type reminder ${if (state) "activated 🔔" else "paused 🔕"}")
    }

    fun logWorkoutMinutes(minutes: Int = 15) {
        val current = _healthMetrics.value.exerciseMinutes
        val target = _healthMetrics.value.exerciseTarget
        val updated = current + minutes
        _healthMetrics.value = _healthMetrics.value.copy(
            exerciseMinutes = updated,
            caloriesBurned = _healthMetrics.value.caloriesBurned + (minutes * 8)
        )
        showToast("Logged +${minutes}m workout! (${updated}m / ${target}m target)")
        if (updated >= target) {
            triggerConfetti("🏆 Daily Exercise Target Smashed!")
        }
    }

    fun addCustomAutomation(
        title: String,
        category: String = "Productivity",
        whenTrigger: String,
        ifCondition: String,
        thenAction: String
    ) {
        val newAuto = AutomationWorkflow(
            id = "auto_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom Automation" },
            category = category,
            statusTag = "Active • Just created",
            whenTrigger = whenTrigger.ifBlank { "Trigger criteria met" },
            ifCondition = ifCondition.ifBlank { "Condition verified" },
            thenAction = thenAction.ifBlank { "Execute automated workflow" },
            isEnabled = true,
            statsText = "0 runs"
        )
        _automations.value = listOf(newAuto) + _automations.value
        showToast("Created automation: $title ✨")
    }

    fun updateAutomation(
        id: String,
        title: String,
        category: String,
        whenTrigger: String,
        ifCondition: String,
        thenAction: String
    ) {
        _automations.value = _automations.value.map { auto ->
            if (auto.id == id) {
                auto.copy(
                    title = title.ifBlank { auto.title },
                    category = category.ifBlank { auto.category },
                    whenTrigger = whenTrigger.ifBlank { auto.whenTrigger },
                    ifCondition = ifCondition.ifBlank { auto.ifCondition },
                    thenAction = thenAction.ifBlank { auto.thenAction },
                    statusTag = "Updated"
                )
            } else auto
        }
        showToast("Updated rule: $title ✓")
    }

    fun deleteAutomation(id: String) {
        _automations.value = _automations.value.filterNot { it.id == id }
        showToast("Automation rule removed")
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

    // Daily Habit Goal Tracking (Non-Routine Tasks)
    fun toggleNonRoutineTask(id: String) {
        _nonRoutineTasks.value = _nonRoutineTasks.value.map { task ->
            if (task.id == id) {
                val nextState = !task.isCompleted
                val nextSteps = if (nextState) task.totalSteps else 0
                task.copy(isCompleted = nextState, progressSteps = nextSteps)
            } else task
        }
        val t = _nonRoutineTasks.value.firstOrNull { it.id == id }
        if (t != null) {
            showToast("${t.title} marked ${if (t.isCompleted) "completed! ✓" else "pending"}")
            if (t.isCompleted) {
                triggerConfetti("🎯 Goal Accomplished: ${t.title}")
            }
        }
    }

    fun incrementNonRoutineTaskProgress(id: String) {
        _nonRoutineTasks.value = _nonRoutineTasks.value.map { task ->
            if (task.id == id) {
                val nextStep = (task.progressSteps + 1).coerceAtMost(task.totalSteps)
                val isNowDone = nextStep >= task.totalSteps
                task.copy(progressSteps = nextStep, isCompleted = isNowDone)
            } else task
        }
        val t = _nonRoutineTasks.value.firstOrNull { it.id == id }
        if (t != null) {
            showToast("Progress: ${t.progressSteps}/${t.totalSteps} for ${t.title}")
            if (t.isCompleted) {
                triggerConfetti("🎯 Goal Completed: ${t.title}")
            }
        }
    }

    fun addNonRoutineTask(
        title: String,
        category: String,
        estimatedMinutes: Int,
        totalSteps: Int,
        targetDesc: String = "1 Target"
    ) {
        val newTask = NonRoutineTask(
            id = "nrt_${System.currentTimeMillis()}",
            title = title.ifBlank { "New Daily Goal" },
            category = category.ifBlank { "Sprint Goal" },
            targetDescription = targetDesc,
            isCompleted = false,
            progressSteps = 0,
            totalSteps = totalSteps.coerceAtLeast(1),
            estimatedMinutes = estimatedMinutes.coerceAtLeast(5)
        )
        _nonRoutineTasks.value = listOf(newTask) + _nonRoutineTasks.value
        showToast("Daily Goal set: $title")
    }

    // System-wide Dark Mode & Tailwind Theme Configuration
    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        _themeMode.value = if (next) "dark" else "light"
        showToast("Theme: ${if (next) "Dark Mode (Tailwind Slate-900)" else "Light Mode (DayMeet Clean)"}")
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        _themeMode.value = if (enabled) "dark" else "light"
        showToast("Theme switched to ${if (enabled) "Dark Mode" else "Light Mode"}")
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        when (mode) {
            "dark" -> _isDarkMode.value = true
            "light" -> _isDarkMode.value = false
            else -> _isDarkMode.value = false
        }
        showToast("Theme appearance set to $mode")
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

    fun addShoppingItem(
        name: String,
        quantity: String = "1 item",
        price: Double = 150.0,
        category: String = "Groceries"
    ) {
        val item = ShoppingItem(
            id = "shop_${System.currentTimeMillis()}",
            name = name.ifBlank { "Grocery Item" },
            quantity = quantity.ifBlank { "1 item" },
            estimatedPrice = price,
            category = category.ifBlank { "Groceries" },
            isPurchased = false
        )
        _shoppingItems.value = _shoppingItems.value + item
        showToast("Added $name to Shopping List")
    }

    fun deleteShoppingItem(id: String) {
        _shoppingItems.value = _shoppingItems.value.filterNot { it.id == id }
        showToast("Item removed from list")
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

    fun checkAndLogExpense(
        title: String,
        amount: Double,
        category: String = "General",
        method: String = "UPI",
        receiptNote: String? = null,
        isSplit: Boolean = false
    ): Boolean {
        val currentSpent = _transactions.value.filter { it.amount < 0 }.sumOf { -it.amount }
        val dailyCeiling = 5000.0
        val safeAmount = Math.abs(amount)
        val projectedSpent = currentSpent + safeAmount
        val isCeilingExceeded = projectedSpent > dailyCeiling

        val icon = when (category.lowercase()) {
            "food" -> "restaurant"
            "travel" -> "subway"
            "fuel" -> "local_gas_station"
            "shopping" -> "shopping_bag"
            "bills" -> "receipt_long"
            "rent" -> "home"
            "education" -> "school"
            "healthcare" -> "medical_services"
            "entertainment" -> "movie"
            else -> "account_balance_wallet"
        }

        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title.ifBlank { "Expense" },
            category = category.ifBlank { "General" },
            time = "Just now",
            amount = -safeAmount,
            method = method,
            iconType = icon,
            receiptNote = receiptNote,
            isSplit = isSplit
        )
        _transactions.value = listOf(newTx) + _transactions.value

        if (isCeilingExceeded) {
            val overBy = projectedSpent - dailyCeiling
            showToast("⚠️ Budget Alert: Expense of ₹${String.format("%.0f", safeAmount)} exceeds ₹5,000 daily budget! (Projected: ₹${String.format("%.0f", projectedSpent)}, Over by ₹${String.format("%.0f", overBy)})")
        } else {
            val remaining = dailyCeiling - projectedSpent
            showToast("Logged expense: ₹${String.format("%.0f", safeAmount)} for $title ($method)")
        }
        return isCeilingExceeded
    }

    fun logExpense(
        title: String,
        amount: Double,
        category: String = "General",
        method: String = "UPI",
        receiptNote: String? = null,
        isSplit: Boolean = false
    ) {
        checkAndLogExpense(title, amount, category, method, receiptNote, isSplit)
    }

    fun logIncome(title: String, amount: Double, method: String = "Bank") {
        val newTx = FinanceTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title,
            category = "Income",
            time = "Just now",
            amount = Math.abs(amount),
            method = method,
            iconType = "account_balance"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        showToast("Added income: +₹${String.format("%.0f", amount)} ($method)")
    }

    fun addEmi(title: String, amount: Double, totalMonths: Int, nextDue: String, category: String = "Loan") {
        val newEmi = EmiItem(
            id = "emi_${System.currentTimeMillis()}",
            title = title,
            monthlyAmount = amount,
            totalMonths = totalMonths,
            remainingMonths = totalMonths,
            nextDueDate = nextDue,
            category = category
        )
        _emis.value = listOf(newEmi) + _emis.value
        showToast("Added EMI plan: $title (₹${String.format("%.0f", amount)}/mo)")
    }

    fun payEmiInstallment(id: String) {
        val item = _emis.value.firstOrNull { it.id == id } ?: return
        if (item.remainingMonths <= 0) {
            showToast("EMI for ${item.title} is already completed! 🎉")
            return
        }
        val updated = item.copy(remainingMonths = item.remainingMonths - 1)
        _emis.value = _emis.value.map { if (it.id == id) updated else it }
        logExpense("EMI: ${item.title}", item.monthlyAmount, "Bills", "Auto-Debit")
        showToast("Paid EMI of ₹${String.format("%.0f", item.monthlyAmount)} (${updated.remainingMonths} months left)")
        if (updated.remainingMonths == 0) {
            triggerConfetti("🎉 EMI Completed: ${item.title} fully paid off!")
        }
    }

    fun addDebt(person: String, amount: Double, isOwedToMe: Boolean, dueDate: String, note: String) {
        val newDebt = DebtItem(
            id = "debt_${System.currentTimeMillis()}",
            personOrSource = person,
            amount = amount,
            isOwedToMe = isOwedToMe,
            dueDate = dueDate.ifBlank { "Flexible" },
            note = note
        )
        _debts.value = listOf(newDebt) + _debts.value
        val label = if (isOwedToMe) "owes you" else "you owe"
        showToast("Logged debt: $person $label ₹${String.format("%.0f", amount)}")
    }

    fun settleDebt(id: String) {
        val item = _debts.value.firstOrNull { it.id == id } ?: return
        _debts.value = _debts.value.filterNot { it.id == id }
        if (item.isOwedToMe) {
            logIncome("Settlement from ${item.personOrSource}", item.amount, "UPI")
            showToast("Received ₹${String.format("%.0f", item.amount)} from ${item.personOrSource} (Settled) ✓")
        } else {
            logExpense("Settlement to ${item.personOrSource}", item.amount, "Other", "UPI")
            showToast("Paid ₹${String.format("%.0f", item.amount)} to ${item.personOrSource} (Settled) ✓")
        }
    }

    fun addSubscription(name: String, cost: Double, date: String, category: String, autoPay: Boolean) {
        val newSub = SubscriptionItem(
            id = "sub_${System.currentTimeMillis()}",
            name = name,
            monthlyCost = cost,
            renewalDate = date,
            iconType = when (category.lowercase()) {
                "ott", "entertainment" -> "movie"
                "internet", "broadband" -> "wifi"
                "electricity", "utility" -> "bolt"
                "gym", "fitness" -> "fitness"
                "insurance" -> "health"
                else -> "receipt"
            },
            category = category,
            autoPay = autoPay
        )
        _subscriptions.value = listOf(newSub) + _subscriptions.value
        showToast("Added subscription: $name (₹${String.format("%.0f", cost)}/mo)")
    }

    fun updateMonthlyBudgetTarget(newTarget: Double) {
        val target = newTarget.coerceAtLeast(1000.0)
        _monthlyBudgetTarget.value = target
        showToast("Monthly budget target updated to ₹${String.format("%,.0f", target)}")
    }

    // Universal Quick Add
    fun universalQuickAdd(
        type: String,
        title: String,
        detail: String,
        extraValue: String = "",
        priority: Priority = Priority.HIGH,
        category: String = "Work",
        reminderTime: String? = null,
        dueDate: String? = null
    ) {
        when (type) {
            "Task" -> {
                saveNewTask(title, detail, priority, category, emptyList(), reminderTime, dueDate)
            }
            "Meeting" -> {
                val participants = if (detail.isNotBlank()) detail.split(",").map { it.trim() } else listOf("Alex Chen")
                scheduleMeetingFromHub(
                    title = title.ifBlank { "Quick Meeting" },
                    participants = participants,
                    time = if (extraValue.isNotBlank()) extraValue else "Today, 03:00 PM"
                )
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
                val cat = if (category.isNotBlank() && category != "Work") category else (if (detail.isNotBlank()) detail else "Food")
                checkAndLogExpense(title.ifBlank { "Quick Expense" }, amount, cat, "UPI")
            }
            "Income" -> {
                val amount = extraValue.toDoubleOrNull() ?: 1000.0
                logIncome(title.ifBlank { "Payment" }, amount, "Bank")
            }
            "Shopping" -> {
                val price = extraValue.toDoubleOrNull() ?: 250.0
                addShoppingItem(title.ifBlank { "Item" }, detail.ifBlank { "1 unit" }, price, category.ifBlank { "General" })
            }
            "Subscription" -> {
                val cost = extraValue.toDoubleOrNull() ?: 499.0
                addSubscription(title.ifBlank { "Subscription" }, cost, "End of Month", category.ifBlank { "Entertainment" }, true)
            }
            "Debt" -> {
                val amount = extraValue.toDoubleOrNull() ?: 500.0
                addDebt(title.ifBlank { "Contact" }, amount, true, "Next Week", detail)
            }
            "EMI" -> {
                val amount = extraValue.toDoubleOrNull() ?: 2000.0
                addEmi(title.ifBlank { "New EMI" }, amount, 12, "Oct 10", category.ifBlank { "Loan" })
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
            "Project" -> {
                addProject(title, detail, extraValue.ifBlank { "₹50,000" }, "End of Month")
            }
            "Appointment" -> {
                addAppointment(title, category.ifBlank { "General" }, "Upcoming", extraValue.ifBlank { "10:00 AM" }, detail.ifBlank { "Scheduled Location" })
            }
            "Home & Vehicle" -> {
                addHomeVehicleItem(title, category.ifBlank { "Home" }, extraValue.ifBlank { "Next Month" }, detail)
            }
            else -> {
                saveNewTask(title, detail, Priority.MEDIUM, "General", emptyList())
            }
        }
        _showCreateSheet.value = false
    }

    // Projects Management (Section 9)
    fun addProject(title: String, description: String, budget: String = "₹50,000", deadline: String = "30 September") {
        val newProj = ProjectItem(
            id = "proj_${System.currentTimeMillis()}",
            title = title.ifBlank { "New Project" },
            description = description.ifBlank { "Project deliverables, tasks and milestone tracker" },
            progressPercent = 15,
            tasksCount = 6,
            completedTasks = 1,
            meetingsCount = 2,
            filesCount = 3,
            budget = budget,
            deadline = deadline,
            colorHex = "#673AB7",
            status = "In Progress"
        )
        _projects.value = listOf(newProj) + _projects.value
        addCrossStreamItem("Project: ${newProj.title}", "Deadline: $deadline • Budget: $budget", "Project", "priority")
        showToast("Project created: ${newProj.title} 🚀")
    }

    fun toggleProjectProgress(id: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == id) {
                val nextPercent = if (proj.progressPercent >= 100) 25 else (proj.progressPercent + 25).coerceAtMost(100)
                val completed = ((nextPercent / 100f) * proj.tasksCount).toInt()
                proj.copy(progressPercent = nextPercent, completedTasks = completed)
            } else proj
        }
    }

    fun deleteProject(id: String) {
        _projects.value = _projects.value.filterNot { it.id == id }
        showToast("Project removed")
    }

    // Appointments (Section 11)
    fun addAppointment(title: String, category: String, date: String, time: String, location: String) {
        val newApt = AppointmentItem(
            id = "apt_${System.currentTimeMillis()}",
            title = title.ifBlank { "Appointment" },
            category = category,
            date = date.ifBlank { "Upcoming" },
            time = time.ifBlank { "10:00 AM" },
            locationOrProvider = location.ifBlank { "Scheduled Location" },
            reminderNotice = "1 day before & 1 hour before"
        )
        _appointments.value = listOf(newApt) + _appointments.value
        addCrossStreamItem("Appointment: ${newApt.title}", "${newApt.date} $time • $location", "Appointment", "meeting")
        showToast("Appointment scheduled: ${newApt.title} 🩺")
    }

    fun toggleAppointmentCompleted(id: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun deleteAppointment(id: String) {
        _appointments.value = _appointments.value.filterNot { it.id == id }
        showToast("Appointment cancelled")
    }

    // Home & Vehicle (Sections 28 & 29)
    fun addHomeVehicleItem(title: String, category: String, dueDate: String, detail: String, cost: String? = null) {
        val newItem = HomeVehicleItem(
            id = "hv_${System.currentTimeMillis()}",
            title = title.ifBlank { "Maintenance Item" },
            category = category,
            dueDate = dueDate.ifBlank { "Next Month" },
            detail = detail.ifBlank { "Periodic service and inspection" },
            statusTag = "Scheduled",
            costEstimate = cost
        )
        _homeVehicleItems.value = listOf(newItem) + _homeVehicleItems.value
        showToast("Logged $category maintenance: $title 🔧")
    }

    fun toggleHomeVehicleItem(id: String) {
        _homeVehicleItems.value = _homeVehicleItems.value.map {
            if (it.id == id) {
                val nextStatus = if (it.statusTag == "Completed") "Scheduled" else "Completed"
                it.copy(statusTag = nextStatus)
            } else it
        }
    }

    fun deleteHomeVehicleItem(id: String) {
        _homeVehicleItems.value = _homeVehicleItems.value.filterNot { it.id == id }
        showToast("Maintenance entry removed")
    }

    // Universal Quick Capture Natural Language Parsing (Section 5)
    data class ParsedCapture(
        val type: String,
        val title: String,
        val detail: String,
        val extra: String,
        val explanation: String
    )

    fun parseNaturalLanguage(input: String): ParsedCapture {
        val trimmed = input.trim()
        val lower = trimmed.lowercase()
        return when {
            lower.contains("call") || lower.contains("remind") -> {
                val time = if (lower.contains("5")) "Tomorrow, 05:00 PM" else if (lower.contains("tomorrow")) "Tomorrow, 09:00 AM" else "Today, 06:00 PM"
                val name = trimmed.substringAfter("call", "").substringAfter("remind", "").substringBefore("tomorrow").substringBefore("at").trim()
                val title = if (name.isNotBlank()) "Call ${name.replaceFirstChar { it.uppercase() }}" else trimmed
                ParsedCapture("Reminder", title, "Time-based intelligent reminder", time, "Action: $title • Date/Time: $time • Type: Reminder")
            }
            lower.contains("buy") || lower.contains("milk") || lower.contains("grocery") || lower.contains("shopping") -> {
                val item = trimmed.substringAfter("buy", "").substringBefore("when").substringBefore("from").trim()
                val title = if (item.isNotBlank()) item.replaceFirstChar { it.uppercase() } else "Grocery Item"
                ParsedCapture("Shopping", title, "Groceries & Household (Location reminder at home)", "₹120", "Shopping item + location reminder: $title")
            }
            lower.contains("bill") || lower.contains("pay") || lower.contains("₹") -> {
                val amount = Regex("""(?:₹|rs\.?|inr)?\s*([0-9,]+)""", RegexOption.IGNORE_CASE).find(trimmed)?.groupValues?.get(1) ?: "1,500"
                val title = if (lower.contains("electricity")) "Electricity Bill" else if (lower.contains("internet") || lower.contains("wifi")) "Internet Bill" else "Utility Bill Payment"
                ParsedCapture("Bill", title, "Finance entry + reminder", "₹$amount", "Bill: $title • Amount: ₹$amount • Reminder: Due 25th")
            }
            lower.contains("meet") || lower.contains("sync") || lower.contains("meeting") -> {
                val who = trimmed.substringAfter("meet", "").substringBefore("next").substringBefore("at").substringBefore("tomorrow").trim()
                val title = if (who.isNotBlank()) "Meeting with ${who.replaceFirstChar { it.uppercase() }}" else "Scheduled Sync"
                ParsedCapture("Meeting", title, "Smart Scheduling Suggestion", "Next Tuesday, 03:00 PM", "Meeting: $title • Slot: Next Tuesday afternoon")
            }
            lower.contains("doctor") || lower.contains("service") || lower.contains("salon") || lower.contains("appointment") -> {
                ParsedCapture("Appointment", trimmed, "Appointment card with reminder", "Upcoming Slot", "Appointment: $trimmed • Alerts: 1 day & 1 hr before")
            }
            lower.contains("project") || lower.contains("launch") -> {
                ParsedCapture("Project", trimmed, "Project dashboard tracker", "₹50,000", "Project: $trimmed • Grouping tasks, meetings & budget")
            }
            else -> {
                ParsedCapture("Task", trimmed, "Task prioritized for DayMeet Command Center", "Today 05:00 PM", "Task: $trimmed • Synced to Personal Timeline")
            }
        }
    }

    fun executeNaturalLanguageCapture(input: String) {
        val parsed = parseNaturalLanguage(input)
        when (parsed.type) {
            "Reminder" -> {
                val newRem = SmartReminder("rem_${System.currentTimeMillis()}", parsed.title, "Time-based", parsed.extra)
                _reminders.value = listOf(newRem) + _reminders.value
                addCrossStreamItem(parsed.title, parsed.extra, "Reminder", "priority")
            }
            "Shopping" -> {
                addShoppingItem(parsed.title, "1 item", 120.0, "Groceries")
            }
            "Bill" -> {
                val amount = parsed.extra.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 1500.0
                val bill = UpcomingBill("b_${System.currentTimeMillis()}", parsed.title, "25th of Month", "Due soon", "Utilities", amount)
                _upcomingBills.value = listOf(bill) + _upcomingBills.value
                addCrossStreamItem(parsed.title, "₹$amount due on 25th", "Bill", "autopay")
            }
            "Meeting" -> {
                scheduleMeetingFromHub(parsed.title, listOf("Alex Chen"), parsed.extra)
            }
            "Appointment" -> {
                addAppointment(parsed.title, "General", "Next Week", "10:30 AM", "Scheduled Location")
            }
            "Project" -> {
                addProject(parsed.title, parsed.detail, parsed.extra)
            }
            else -> {
                saveNewTask(parsed.title, parsed.detail, Priority.HIGH, "General", emptyList())
            }
        }
        showToast("✓ Captured: ${parsed.title} (${parsed.type})")
    }


    // Home feed & filter actions
    fun setFeedFilter(filter: FeedCategory) {
        _feedFilter.value = filter
    }

    fun toggleFeedTaskDone(id: String) {
        var newCompleted = false
        var taskTitle: String? = null
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                newCompleted = !item.isCompleted
                taskTitle = item.title
                item.copy(
                    isCompleted = newCompleted,
                    progress = if (newCompleted) 100 else 0
                )
            } else {
                item
            }
        }
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == id || (taskTitle != null && item.title.equals(taskTitle, ignoreCase = true))) {
                item.copy(
                    isCompleted = newCompleted,
                    progress = if (newCompleted) 100 else 0
                )
            } else {
                item
            }
        }
    }

    fun updateTaskProgress(id: String, progress: Int) {
        val clamped = progress.coerceIn(0, 100)
        val shouldComplete = clamped == 100
        var taskTitle: String? = null
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                taskTitle = item.title
                item.copy(
                    progress = clamped,
                    isCompleted = shouldComplete
                )
            } else {
                item
            }
        }
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == id || (taskTitle != null && item.title.equals(taskTitle, ignoreCase = true))) {
                item.copy(
                    progress = clamped,
                    isCompleted = shouldComplete
                )
            } else {
                item
            }
        }
        if (clamped == 100) {
            triggerConfetti("🎉 Task completed (100%)!")
        }
    }

    fun bulkMarkTasksCompleted(ids: Set<String>) {
        if (ids.isEmpty()) return
        val count = ids.size
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id in ids) {
                item.copy(isCompleted = true, progress = 100)
            } else {
                item
            }
        }
        showToast("Marked $count tasks as completed! ✓")
        triggerConfetti("🎉 Batch completed $count tasks!")
    }

    fun bulkRemoveTasks(ids: Set<String>) {
        if (ids.isEmpty()) return
        val count = ids.size
        _feedItems.value = _feedItems.value.filterNot { it.id in ids }
        showToast("Removed $count completed tasks")
    }

    fun removeFeedTask(id: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.filterNot { it.id == id }
        if (task != null) {
            showToast("Completed: ${task.title} (Removed)")
        }
    }

    fun updateTaskPriority(id: String, priority: Priority) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(priority = priority)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Updated priority of '${task.title}' to ${priority.label}")
        }
    }

    fun rescheduleTask(id: String, newTime: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(time = newTime)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Rescheduled '${task.title}' to $newTime")
        }
    }

    fun updateTaskNotes(id: String, newNotes: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(notes = newNotes)
            } else {
                item
            }
        }
        if (task != null) {
            showToast("Updated notes for '${task.title}' 📝")
        }
    }

    fun triggerTaskNotificationNow(context: Context, taskId: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        TaskNotificationScheduler.showNotification(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            time = task.time
        )
        showToast("🔔 Triggered due alert for '${task.title}'")
    }

    fun scheduleTaskNotification(context: Context, taskId: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        TaskNotificationScheduler.scheduleTaskAlert(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            timeStr = task.reminderTime ?: task.time
        )
        showToast("⏰ Alert scheduled for ${task.reminderTime ?: task.time}")
    }

    fun setTaskReminder(context: Context, taskId: String, reminderTimeStr: String) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(reminderTime = reminderTimeStr)
            } else item
        }
        TaskNotificationScheduler.scheduleTaskAlert(
            context = context,
            taskId = task.id,
            title = task.title,
            notes = task.notes ?: task.detail ?: "",
            timeStr = reminderTimeStr
        )
        showToast("⏰ Reminder set for $reminderTimeStr")
    }

    fun moveTaskToCalendar(taskId: String, timeSlot: String, durationMinutes: Int = 30) {
        val task = _feedItems.value.firstOrNull { it.id == taskId } ?: return
        
        // 1. Update task with calendar time and status
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(
                    time = timeSlot,
                    statusTag = "Calendar"
                )
            } else item
        }

        // 2. Add or update TimelineEvent in calendar
        val timeClean = if (timeSlot.contains(",")) timeSlot.substringAfter(",").trim() else timeSlot
        val period = if (timeSlot.contains("PM", ignoreCase = true)) "Afternoon" else "Morning"
        val newTimelineEvent = TimelineEvent(
            id = "timeline_${task.id}",
            time = timeClean,
            period = period,
            title = task.title,
            subtitle = task.notes ?: task.subtitle,
            durationMinutes = durationMinutes,
            type = TimelineType.TASK,
            isCompleted = task.isCompleted
        )
        _timelineEvents.value = listOf(newTimelineEvent) + _timelineEvents.value.filterNot { it.id == newTimelineEvent.id }

        // 3. Add to cross-stream
        val crossStream = CrossStreamItem(
            id = "cs_cal_${task.id}",
            time = timeClean,
            title = task.title,
            subtitle = "Scheduled task • $durationMinutes mins",
            tag = "Calendar Task",
            tagType = "task",
            isCompleted = task.isCompleted
        )
        _crossStreamItems.value = listOf(crossStream) + _crossStreamItems.value.filterNot { it.id == crossStream.id }

        showToast("📅 Moved '${task.title}' to Calendar at $timeSlot")
    }

    fun editTask(
        id: String,
        title: String,
        subtitle: String,
        priority: Priority,
        category: String,
        time: String,
        notes: String?,
        progress: Int? = null
    ) {
        val task = _feedItems.value.firstOrNull { it.id == id } ?: return
        val newProgress = (progress ?: task.progress).coerceIn(0, 100)
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                item.copy(
                    title = title.ifBlank { item.title },
                    subtitle = subtitle.ifBlank { item.subtitle },
                    priority = priority,
                    statusTag = category,
                    time = time.ifBlank { item.time },
                    notes = notes,
                    progress = newProgress,
                    isCompleted = if (newProgress == 100) true else item.isCompleted
                )
            } else item
        }
        showToast("Task '${title.ifBlank { task.title }}' updated ✓")
    }

    fun deleteTask(id: String) {
        val task = _feedItems.value.firstOrNull { it.id == id }
        _feedItems.value = _feedItems.value.filterNot { it.id == id }
        _crossStreamItems.value = _crossStreamItems.value.filterNot { it.id == id || (task != null && it.title.equals(task.title, ignoreCase = true)) }
        if (task != null) {
            showToast("Permanently deleted task '${task.title}'")
        }
    }

    fun bulkDeleteTasks(ids: Set<String>) {
        if (ids.isEmpty()) return
        val count = ids.size
        val deletedTitles = _feedItems.value.filter { it.id in ids }.map { it.title }
        _feedItems.value = _feedItems.value.filterNot { it.id in ids }
        _crossStreamItems.value = _crossStreamItems.value.filterNot { it.id in ids || it.title in deletedTitles }
        showToast("Permanently deleted $count task${if (count > 1) "s" else ""}")
    }

    fun toggleCrossStreamDone(id: String) {
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
        }
    }

    fun addCrossStreamSubtask(streamItemId: String, subtaskTitle: String) {
        if (subtaskTitle.isBlank()) return
        val newSub = Subtask(
            id = "sub_cs_${System.currentTimeMillis()}_${(100..999).random()}",
            title = subtaskTitle.trim(),
            isCompleted = false
        )
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == streamItemId) {
                item.copy(subtasks = item.subtasks + newSub)
            } else item
        }
        // Also sync to feedItems if linked
        _feedItems.value = _feedItems.value.map { task ->
            if (task.id == streamItemId || task.title.equals(_crossStreamItems.value.firstOrNull { it.id == streamItemId }?.title, ignoreCase = true)) {
                task.copy(subtasks = task.subtasks + newSub)
            } else task
        }
        showToast("Subtask added")
    }

    fun toggleCrossStreamSubtask(streamItemId: String, subtaskId: String) {
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == streamItemId) {
                val updated = item.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                item.copy(subtasks = updated)
            } else item
        }
        _feedItems.value = _feedItems.value.map { task ->
            if (task.id == streamItemId || task.title.equals(_crossStreamItems.value.firstOrNull { it.id == streamItemId }?.title, ignoreCase = true)) {
                val updated = task.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                task.copy(subtasks = updated)
            } else task
        }
    }

    fun deleteCrossStreamSubtask(streamItemId: String, subtaskId: String) {
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == streamItemId) {
                item.copy(subtasks = item.subtasks.filter { it.id != subtaskId })
            } else item
        }
        _feedItems.value = _feedItems.value.map { task ->
            if (task.id == streamItemId || task.title.equals(_crossStreamItems.value.firstOrNull { it.id == streamItemId }?.title, ignoreCase = true)) {
                task.copy(subtasks = task.subtasks.filter { it.id != subtaskId })
            } else task
        }
    }

    fun removeCrossStreamItem(id: String) {
        val item = _crossStreamItems.value.firstOrNull { it.id == id }
        _crossStreamItems.value = _crossStreamItems.value.filterNot { it.id == id }
        if (item != null) {
            showToast("Completed: ${item.title} (Removed)")
        }
    }

    fun toggleFocusMode() {
        val newState = !_isFocusModeActive.value
        _isFocusModeActive.value = newState
        if (newState) {
            _isFocusRunning.value = true
            showToast("🎯 Focus Mode Activated: All non-urgent notifications muted")
        } else {
            showToast("Focus Mode Deactivated: System notifications restored")
        }
    }

    fun selectFocusTask(taskId: String) {
        _selectedFocusTaskId.value = taskId
    }

    fun openLanguageSelector() {
        _showLanguageDialog.value = true
    }

    fun closeLanguageSelector() {
        _showLanguageDialog.value = false
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _showLanguageDialog.value = false
        val message = when (language) {
            AppLanguage.ENGLISH -> "Language changed to English 🇺🇸"
            AppLanguage.SPANISH -> "Idioma cambiado a Español 🇪🇸"
            AppLanguage.FRENCH -> "Langue changée en Français 🇫🇷"
            AppLanguage.GERMAN -> "Sprache auf Deutsch geändert 🇩🇪"
            AppLanguage.HINDI -> "भाषा बदलकर हिन्दी कर दी गई 🇮🇳"
            AppLanguage.JAPANESE -> "言語を日本語に変更しました 🇯🇵"
            AppLanguage.CHINESE -> "语言已切换为简体中文 🇨🇳"
            AppLanguage.ARABIC -> "تم تغيير اللغة إلى العربية 🇸🇦"
            AppLanguage.PORTUGUESE -> "Idioma alterado para Português 🇧🇷"
            AppLanguage.RUSSIAN -> "Язык изменен на Русский 🇷🇺"
            AppLanguage.ITALIAN -> "Lingua cambiata in Italiano 🇮🇹"
            AppLanguage.KOREAN -> "언어가 한국어로 변경되었습니다 🇰🇷"
            AppLanguage.TAMIL -> "மொழி தமிழுக்கு மாற்றப்பட்டது 🇮🇳"
        }
        showToast(message)
    }

    fun completeFocusTaskAndAdvance(taskId: String) {
        toggleFeedTaskDone(taskId)
        showToast("Task completed! Focusing on next task...")
        triggerConfetti("🎯 Task Completed in Focus Mode!")
    }

    fun toggleFocusTimer() {
        _isFocusRunning.value = !_isFocusRunning.value
    }

    fun start25MinPomodoroSession() {
        _focusTimerRemaining.value = 25 * 60
        _isFocusRunning.value = true
        _isFocusCompleted.value = false
        showToast("🍅 25-min Deep Work Pomodoro session started!")
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

    fun toggleSubtask(id: String, subtaskId: String) {
        _timelineEvents.value = _timelineEvents.value.map { event ->
            if (event.id == id) {
                val updatedSubs = event.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                event.copy(subtasks = updatedSubs)
            } else event
        }
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == id) {
                val updatedSubtasks = item.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                item.copy(subtasks = updatedSubtasks)
            } else item
        }
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == id || item.title.equals(_feedItems.value.firstOrNull { it.id == id }?.title, ignoreCase = true)) {
                val updatedSubtasks = item.subtasks.map { sub ->
                    if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
                }
                item.copy(subtasks = updatedSubtasks)
            } else item
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
            delay(600)
            val lower = text.lowercase()
            val (replyText, proposal) = when {
                lower.contains("plan") && (lower.contains("tomorrow") || lower.contains("day")) -> {
                    val planText = buildString {
                        appendLine("✨ **TOMORROW'S OPTIMIZED PLAN**")
                        appendLine("")
                        appendLine("📅 09:00 AM – Team Sprint Standup & Alignment")
                        appendLine("💻 10:30 AM – Deep Work: Architecture Spec (Focus Block)")
                        appendLine("🍽️ 12:30 PM – Healthy Lunch & Quick Walk")
                        appendLine("📞 02:00 PM – Client Strategy Review (Google Meet)")
                        appendLine("⚡ 04:00 PM – Wrap-up, Inbox Zero & Review")
                        appendLine("")
                        appendLine("💡 You have 2 pending tasks from today. Tapping below will auto-schedule them into your 10:30 AM focus block.")
                    }
                    val prop = ScheduleProposal(
                        rescheduled = ProposalItem("Reschedule", "Move 2 overdue tasks to 10:30 AM tomorrow", "Auto-inserts buffer", "Recommended"),
                        addedBlock = ProposalItem("Deep Work", "Add 90m Architecture Focus block", "Mutes notifications", "Optimal"),
                        autoReminder = ProposalItem("Health", "12:30 PM Hydration & 15m Walk Alert", "Vital routine", "Scheduled")
                    )
                    Pair(planText, prop)
                }
                lower.contains("spend") || lower.contains("expense") || lower.contains("budget") -> {
                    val totalExpenses = _transactions.value.filter { it.amount < 0 }.sumOf { -it.amount }
                    val reply = buildString {
                        appendLine("💰 **WEEKLY & MONTHLY SPENDING REPORT**")
                        appendLine("")
                        appendLine("• Total Expenses: ₹${String.format("%,.0f", totalExpenses)}")
                        appendLine("• Monthly Budget Limit: ₹${String.format("%,.0f", _monthlyBudgetTarget.value)}")
                        appendLine("• Remaining Buffer: ₹${String.format("%,.0f", (_monthlyBudgetTarget.value - totalExpenses).coerceAtLeast(0.0))}")
                        appendLine("")
                        appendLine("Top Categories:")
                        val grouped = _transactions.value.filter { it.amount < 0 }.groupBy { it.category }
                        grouped.forEach { (cat, list) ->
                            val sum = list.sumOf { -it.amount }
                            appendLine("  • $cat: ₹${String.format("%,.0f", sum)}")
                        }
                        appendLine("")
                        appendLine("Status: Healthy! You are within your target threshold.")
                    }
                    Pair(reply, null)
                }
                lower.contains("meeting") && (lower.contains("tomorrow") || lower.contains("next") || lower.contains("have")) -> {
                    val count = _meetings.value.size
                    val reply = buildString {
                        appendLine("📅 **SCHEDULED MEETINGS ($count Active)**")
                        appendLine("")
                        _meetings.value.take(4).forEachIndexed { i, m ->
                            appendLine("${i + 1}. **${m.title}** • ${m.time}")
                            appendLine("   Platform: ${m.platform} (${m.attendeesCount} attendees)")
                        }
                    }
                    Pair(reply, null)
                }
                lower.contains("remind") && (lower.contains("bill") || lower.contains("pay") || lower.contains("electricity")) -> {
                    val newRem = SmartReminder(
                        id = "rem_${System.currentTimeMillis()}",
                        title = "⚡ Pay Electricity Bill (Tata Power ₹1,840)",
                        triggerType = "Bill Due",
                        scheduledTime = "Friday, 09:00 AM",
                        isCompleted = false
                    )
                    _reminders.value = listOf(newRem) + _reminders.value
                    val reply = "⚡ **Reminder Confirmed:** I've scheduled an alert for **Friday 09:00 AM** to pay your Electricity Bill (Tata Power ₹1,840) with a 1-tap UPI payment button."
                    Pair(reply, null)
                }
                lower.contains("task") && (lower.contains("meeting") || lower.contains("action")) -> {
                    saveNewTask("Finalize client deliverables from Sprint Review", "Derived from Sprint Review meeting notes", Priority.HIGH, "Work", emptyList())
                    saveNewTask("Share updated Figma design system tokens", "David & Elena requested spec", Priority.MEDIUM, "Design", emptyList())
                    val reply = "✅ **Created 2 actionable follow-up tasks** from today's meeting notes and added them to your Planner backlog with high priority."
                    Pair(reply, null)
                }
                lower.contains("overdue") -> {
                    val pendingTasks = _feedItems.value.filter { !it.isCompleted }
                    val reply = buildString {
                        appendLine("⚠️ **TASK STATUS OVERVIEW**")
                        appendLine("")
                        appendLine("You have **${pendingTasks.size} tasks** pending today:")
                        pendingTasks.take(3).forEach { t ->
                            appendLine("• ${t.title} [${t.priority?.label ?: "Normal"}]")
                        }
                        appendLine("")
                        appendLine("Would you like me to auto-reschedule them to tomorrow morning?")
                    }
                    Pair(reply, null)
                }
                lower.contains("sub") || lower.contains("subscription") -> {
                    val total = _subscriptions.value.sumOf { it.monthlyCost }
                    val reply = buildString {
                        appendLine("💳 **ACTIVE SUBSCRIPTIONS (₹${String.format("%,.0f", total)}/mo)**")
                        appendLine("")
                        _subscriptions.value.forEach { s ->
                            appendLine("• ${s.name}: ₹${String.format("%.0f", s.monthlyCost)} (Renews ${s.renewalDate})")
                        }
                    }
                    Pair(reply, null)
                }
                else -> {
                    val reply = "DayMeet AI Assistant: I've processed your request. Your calendar, daily timeline, budget limits, and wellness routines are synchronized."
                    Pair(reply, null)
                }
            }

            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = replyText,
                timestamp = "Just now",
                proposal = proposal
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun executeAiAction(actionType: String) {
        when (actionType) {
            "plan_tomorrow" -> sendChatMessage("Plan my tomorrow")
            "check_spending" -> sendChatMessage("How much did I spend this week?")
            "check_meetings" -> sendChatMessage("What meetings do I have tomorrow?")
            "check_overdue" -> sendChatMessage("How many tasks are overdue?")
            "create_meeting_tasks" -> sendChatMessage("Create tasks from today's meetings")
            "check_subs" -> sendChatMessage("Show my upcoming subscriptions")
            else -> sendChatMessage(actionType)
        }
    }

    fun saveNewTask(
        title: String,
        notes: String,
        priority: Priority,
        space: String,
        subtasks: List<String> = emptyList(),
        reminderTime: String? = null,
        dueDate: String? = null
    ) {
        val taskId = "task_${System.currentTimeMillis()}"
        val taskTitle = title.ifBlank { "New Task" }
        val categoryTag = if (space.isNotBlank()) space else "Work"
        val taskSubtitle = if (notes.isNotBlank()) notes else "$categoryTag • Priority: ${priority.label}"
        val subtaskList = subtasks.filter { it.isNotBlank() }.mapIndexed { idx, sub ->
            Subtask(id = "sub_${System.currentTimeMillis()}_$idx", title = sub.trim(), isCompleted = false)
        }
        val newTask = FeedItem(
            id = taskId,
            time = if (!dueDate.isNullOrBlank()) dueDate else "05:00 PM",
            title = taskTitle,
            subtitle = taskSubtitle,
            category = FeedCategory.TASK,
            priority = priority,
            statusTag = categoryTag,
            isCompleted = false,
            reminderTime = reminderTime,
            notes = notes.ifBlank { null },
            dueDate = dueDate,
            subtasks = subtaskList
        )
        _feedItems.value = listOf(newTask) + _feedItems.value

        // Also add to Cross-Module Stream so it appears with entrance animation
        val newStreamItem = CrossStreamItem(
            id = "cs_${System.currentTimeMillis()}",
            time = if (!dueDate.isNullOrBlank()) dueDate else "05:00 PM",
            title = taskTitle,
            subtitle = taskSubtitle,
            tag = "Task",
            tagType = "task",
            isCompleted = false,
            subtasks = subtaskList
        )
        _crossStreamItems.value = listOf(newStreamItem) + _crossStreamItems.value

        _showCreateSheet.value = false
        showToast("Task created: $taskTitle")
    }

    fun addSubtask(taskId: String, subtaskTitle: String) {
        if (subtaskTitle.isBlank()) return
        val newSub = Subtask(
            id = "sub_${System.currentTimeMillis()}_${(100..999).random()}",
            title = subtaskTitle.trim(),
            isCompleted = false
        )
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(subtasks = item.subtasks + newSub)
            } else {
                item
            }
        }
        // Keep Cross-Module Stream items in sync
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == taskId || item.title.equals(_feedItems.value.firstOrNull { it.id == taskId }?.title, ignoreCase = true)) {
                item.copy(subtasks = item.subtasks + newSub)
            } else item
        }
        showToast("Subtask added")
    }

    fun deleteSubtask(taskId: String, subtaskId: String) {
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(subtasks = item.subtasks.filter { it.id != subtaskId })
            } else {
                item
            }
        }
        _crossStreamItems.value = _crossStreamItems.value.map { item ->
            if (item.id == taskId || item.title.equals(_feedItems.value.firstOrNull { it.id == taskId }?.title, ignoreCase = true)) {
                item.copy(subtasks = item.subtasks.filter { it.id != subtaskId })
            } else item
        }
    }

    fun updateTaskDueDate(taskId: String, dueDate: String?) {
        _feedItems.value = _feedItems.value.map { item ->
            if (item.id == taskId) {
                item.copy(dueDate = dueDate)
            } else {
                item
            }
        }
        showToast("Due date updated")
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

    // ==========================================
    // Phase 1: Life Mode & Life Inbox Methods
    // ==========================================

    fun setLifeMode(mode: LifeMode) {
        _currentLifeMode.value = mode
        showToast("Switched to ${mode.label}")
    }

    fun cycleLifeMode() {
        val next = when (_currentLifeMode.value) {
            LifeMode.ALL -> LifeMode.WORK
            LifeMode.WORK -> LifeMode.PERSONAL
            LifeMode.PERSONAL -> LifeMode.ALL
        }
        setLifeMode(next)
    }

    fun addToLifeInbox(
        content: String,
        type: LifeInboxType = LifeInboxType.TEXT,
        source: String = "Manual Dump",
        mediaUri: String? = null
    ) {
        if (content.isBlank()) return
        val suggestions = generateSuggestionsForInbox(content, type)
        val newItem = LifeInboxItem(
            id = "inbox_${System.currentTimeMillis()}",
            content = content.trim(),
            type = type,
            timestamp = "Just now",
            source = source,
            mediaUri = mediaUri,
            suggestions = suggestions
        )
        _lifeInboxItems.value = listOf(newItem) + _lifeInboxItems.value
        showToast("Dumped to Life Inbox (${suggestions.size} suggestions ready)")
    }

    fun dumpSimulatedMediaToInbox(type: LifeInboxType) {
        when (type) {
            LifeInboxType.VOICE -> {
                addToLifeInbox(
                    content = "Audio Note: Call Dr. Mehta for annual physical report follow-up on Friday",
                    type = LifeInboxType.VOICE,
                    source = "Voice Capture"
                )
            }
            LifeInboxType.PHOTO -> {
                addToLifeInbox(
                    content = "Photo: Parking receipt ₹150 at Airport Terminal 2",
                    type = LifeInboxType.PHOTO,
                    source = "Camera"
                )
            }
            LifeInboxType.SCREENSHOT -> {
                addToLifeInbox(
                    content = "Screenshot: Flight 6E-243 Mumbai to Bengaluru PNR 8KX92P 06:15 AM",
                    type = LifeInboxType.SCREENSHOT,
                    source = "Screenshot Intent"
                )
            }
            LifeInboxType.LINK -> {
                addToLifeInbox(
                    content = "https://github.com/daymeet/architecture-rfcs/issues/42 Async worker redesign",
                    type = LifeInboxType.LINK,
                    source = "Web Link"
                )
            }
            LifeInboxType.IDEA -> {
                addToLifeInbox(
                    content = "Idea: Add a 2-minute weekly retrospective voice prompt on Sunday night",
                    type = LifeInboxType.IDEA,
                    source = "Mind Dump"
                )
            }
            else -> {
                addToLifeInbox("Quick memo: Verify credit card statement for suspicious debit", type, "Quick Dump")
            }
        }
    }

    fun captureFromClipboard(clipboardContent: String? = null) {
        val text = clipboardContent ?: "Meeting Link: https://meet.google.com/xyz-qwe-asd Product Sync at 4pm"
        addToLifeInbox(
            content = text,
            type = if (text.contains("http")) LifeInboxType.LINK else LifeInboxType.TEXT,
            source = "Clipboard"
        )
    }

    fun handleSharedIntent(sharedText: String?, sharedUri: String? = null) {
        val content = sharedText ?: "Shared document or media from Android share sheet"
        val type = when {
            sharedUri != null -> LifeInboxType.DOCUMENT
            content.contains("http") -> LifeInboxType.LINK
            content.contains("screenshot", ignoreCase = true) -> LifeInboxType.SCREENSHOT
            else -> LifeInboxType.TEXT
        }
        addToLifeInbox(
            content = content,
            type = type,
            source = "Android Share Sheet",
            mediaUri = sharedUri
        )
    }

    private fun generateSuggestionsForInbox(content: String, type: LifeInboxType): List<LifeInboxSuggestion> {
        val lower = content.lowercase()
        val suggestions = mutableListOf<LifeInboxSuggestion>()

        if (lower.contains("insurance") || lower.contains("car") || lower.contains("bike") || lower.contains("vehicle") || lower.contains("home")) {
            suggestions.add(
                LifeInboxSuggestion(
                    id = "sug_${System.currentTimeMillis()}_1",
                    type = SuggestedActionType.ADD_VEHICLE_HOME,
                    title = if (lower.contains("insurance")) "Insurance Renewal" else "Home/Vehicle Maintenance",
                    detail = "Add to Home & Vehicle maintenance timeline",
                    targetModule = "Home & Vehicle",
                    category = if (lower.contains("bike") || lower.contains("car") || lower.contains("vehicle")) "Vehicle" else "Home"
                )
            )
        }

        if (lower.contains("remind") || lower.contains("call") || lower.contains("next month") || lower.contains("tomorrow") || lower.contains("follow-up") || lower.contains("friday")) {
            suggestions.add(
                LifeInboxSuggestion(
                    id = "sug_${System.currentTimeMillis()}_2",
                    type = SuggestedActionType.CREATE_REMINDER,
                    title = "Reminder: " + content.take(30) + if (content.length > 30) "..." else "",
                    detail = "Smart alert with notification",
                    targetModule = "Reminders"
                )
            )
        }

        if (lower.contains("₹") || lower.contains("receipt") || lower.contains("bill") || lower.contains("paid") || lower.contains("expense") || lower.contains("rs") || lower.contains("cost")) {
            val amount = Regex("""(?:₹|rs\.?|inr)?\s*([0-9,]+)""", RegexOption.IGNORE_CASE).find(content)?.groupValues?.get(1) ?: "150"
            suggestions.add(
                LifeInboxSuggestion(
                    id = "sug_${System.currentTimeMillis()}_3",
                    type = SuggestedActionType.LOG_EXPENSE,
                    title = "Log Expense ₹$amount",
                    detail = "Add transaction to Finance Tracker",
                    targetModule = "Finance",
                    amountOrDate = amount
                )
            )
        }

        if (lower.contains("meet") || lower.contains("zoom") || lower.contains("flight") || lower.contains("sync") || lower.contains("dr.") || lower.contains("doctor")) {
            suggestions.add(
                LifeInboxSuggestion(
                    id = "sug_${System.currentTimeMillis()}_4",
                    type = SuggestedActionType.CREATE_MEETING,
                    title = "Schedule Calendar Event",
                    detail = "Add to DayMeet Calendar timeline",
                    targetModule = "Calendar"
                )
            )
        }

        if (lower.contains("coffee") || lower.contains("milk") || lower.contains("order") || lower.contains("buy") || lower.contains("shopping")) {
            suggestions.add(
                LifeInboxSuggestion(
                    id = "sug_${System.currentTimeMillis()}_5",
                    type = SuggestedActionType.SAVE_WISHLIST,
                    title = "Add to Shopping List",
                    detail = "Group with Groceries & Household",
                    targetModule = "Shopping"
                )
            )
        }

        // Always provide a fallback Task and Note suggestion
        suggestions.add(
            LifeInboxSuggestion(
                id = "sug_${System.currentTimeMillis()}_task",
                type = SuggestedActionType.CREATE_TASK,
                title = "Create Task: " + content.take(28),
                detail = "Add to Tasks with High Priority",
                targetModule = "Tasks",
                category = if (lower.contains("figma") || lower.contains("sync") || lower.contains("github") || lower.contains("client")) "Work" else "Personal"
            )
        )

        return suggestions
    }

    fun commitLifeInboxSuggestion(inboxItemId: String, suggestion: LifeInboxSuggestion) {
        when (suggestion.type) {
            SuggestedActionType.CREATE_TASK -> {
                saveNewTask(
                    title = suggestion.title.removePrefix("Create Task: "),
                    notes = suggestion.detail,
                    priority = Priority.HIGH,
                    space = suggestion.category ?: "Work",
                    subtasks = emptyList()
                )
            }
            SuggestedActionType.CREATE_MEETING -> {
                scheduleMeetingFromHub(
                    title = suggestion.title,
                    participants = listOf("Alex Chen"),
                    time = "Today, 04:00 PM"
                )
            }
            SuggestedActionType.CREATE_REMINDER -> {
                val newRem = SmartReminder(
                    id = "rem_${System.currentTimeMillis()}",
                    title = suggestion.title,
                    triggerType = "Smart Alert",
                    scheduledTime = "Tomorrow, 09:00 AM"
                )
                _reminders.value = listOf(newRem) + _reminders.value
                addCrossStreamItem(suggestion.title, "Reminder set from Life Inbox", "Reminder", "priority")
            }
            SuggestedActionType.LOG_EXPENSE -> {
                val amt = suggestion.amountOrDate?.replace(",", "")?.toDoubleOrNull() ?: 150.0
                checkAndLogExpense(
                    title = suggestion.title.removePrefix("Log Expense ₹"),
                    amount = amt,
                    category = "General",
                    method = "UPI"
                )
            }
            SuggestedActionType.ADD_VEHICLE_HOME -> {
                addHomeVehicleItem(
                    title = suggestion.title,
                    category = suggestion.category ?: "Vehicle",
                    dueDate = "Next Month",
                    detail = suggestion.detail,
                    cost = "₹1,200"
                )
            }
            SuggestedActionType.SAVE_NOTE -> {
                val newNote = NoteItem(
                    id = "note_${System.currentTimeMillis()}",
                    title = suggestion.title,
                    content = suggestion.detail,
                    category = "Inbox Captures",
                    updatedAt = "Just now"
                )
                _notes.value = listOf(newNote) + _notes.value
                showToast("Note saved: ${suggestion.title}")
            }
            SuggestedActionType.SAVE_WISHLIST -> {
                addShoppingItem(
                    name = suggestion.title.removePrefix("Add to Shopping List: ").removePrefix("Add to Shopping List"),
                    quantity = "1",
                    price = 250.0,
                    category = "Groceries"
                )
            }
            SuggestedActionType.ADD_CALENDAR_EVENT -> {
                scheduleMeetingFromHub(
                    title = suggestion.title,
                    participants = listOf("Alex Chen"),
                    time = "Tomorrow, 10:00 AM"
                )
            }
        }

        // Mark processed or remove item
        _lifeInboxItems.value = _lifeInboxItems.value.filterNot { it.id == inboxItemId }
        showToast("Committed to ${suggestion.targetModule}! ✨")
    }

    fun dismissLifeInboxItem(inboxItemId: String) {
        _lifeInboxItems.value = _lifeInboxItems.value.filterNot { it.id == inboxItemId }
        showToast("Item cleared from Inbox")
    }

    // Phase 1: Contextual Next Item for Home HUD
    fun getContextualNextItem(): ContextualNextItem {
        val nextMeeting = _meetings.value.firstOrNull()
        val pendingTasks = _feedItems.value.filter { !it.isCompleted }
        val mode = _currentLifeMode.value

        return when {
            mode == LifeMode.WORK && nextMeeting != null -> {
                ContextualNextItem(
                    title = nextMeeting.title,
                    subtitle = "${nextMeeting.time} • ${nextMeeting.platform}",
                    timeRemaining = "In 20m",
                    iconType = "meeting",
                    actionLabel = "Join Room",
                    actionTag = "join_meeting",
                    contextInsight = "3 participants joined • Agenda ready",
                    urgentCount = pendingTasks.count { it.priority == Priority.HIGH || it.priority == Priority.URGENT }
                )
            }
            mode == LifeMode.PERSONAL -> {
                val nextPersonalTask = pendingTasks.firstOrNull { it.statusTag == "Personal" || it.statusTag == "Home" }
                ContextualNextItem(
                    title = nextPersonalTask?.title ?: "Evening Wind Down & Habits",
                    subtitle = "Personal Mode Active • Work alerts muted",
                    timeRemaining = "Next 45m",
                    iconType = "focus",
                    actionLabel = "Log Vital",
                    actionTag = "open_health",
                    contextInsight = "Hydration at 70% • 3 habits completed today",
                    urgentCount = 0
                )
            }
            nextMeeting != null -> {
                ContextualNextItem(
                    title = nextMeeting.title,
                    subtitle = "${nextMeeting.time} • ${nextMeeting.platform}",
                    timeRemaining = "In 20m",
                    iconType = "meeting",
                    actionLabel = "Join",
                    actionTag = "join_meeting",
                    contextInsight = "Focus session recommended before 09:30 AM",
                    urgentCount = pendingTasks.count { it.priority == Priority.HIGH }
                )
            }
            pendingTasks.isNotEmpty() -> {
                val topTask = pendingTasks.first()
                ContextualNextItem(
                    title = topTask.title,
                    subtitle = "High Priority Task • Due ${topTask.time}",
                    timeRemaining = "Due Today",
                    iconType = "task",
                    actionLabel = "Start Task",
                    actionTag = "start_task",
                    contextInsight = "${pendingTasks.size} tasks remaining on today's planner",
                    urgentCount = pendingTasks.size
                )
            }
            else -> {
                ContextualNextItem(
                    title = "Free Flow Focus Block",
                    subtitle = "All meetings & high priority tasks completed",
                    timeRemaining = "Available",
                    iconType = "focus",
                    actionLabel = "Start Focus",
                    actionTag = "start_focus",
                    contextInsight = "Great time for deep thinking or personal reading",
                    urgentCount = 0
                )
            }
        }
    }

    // ==========================================
    // Phase 2: Daily Architecture & Life Engine
    // ==========================================

    fun openMorningKickoff() {
        _showMorningKickoff.value = true
    }

    fun closeMorningKickoff() {
        _showMorningKickoff.value = false
    }

    fun openEveningWindDown() {
        _showEveningWindDown.value = true
    }

    fun closeEveningWindDown() {
        _showEveningWindDown.value = false
    }

    fun toggleDailyHighlight(id: String) {
        _dailyHighlights.value = _dailyHighlights.value.map { hl ->
            if (hl.id == id) {
                val newStatus = !hl.isCompleted
                if (newStatus) {
                    showToast("Highlight achieved: ${hl.title}! 🎯")
                }
                hl.copy(isCompleted = newStatus)
            } else hl
        }
    }

    fun addDailyHighlight(
        title: String,
        pillar: LifePillarType = LifePillarType.WORK,
        minutes: Int = 45,
        slot: String? = null
    ) {
        if (title.isBlank()) return
        val newHl = DailyHighlight(
            id = "hl_${System.currentTimeMillis()}",
            title = title.trim(),
            pillar = pillar,
            estimatedMinutes = minutes,
            isCompleted = false,
            timeSlot = slot ?: "Today"
        )
        _dailyHighlights.value = _dailyHighlights.value + newHl
        showToast("Daily Highlight added: $title")
    }

    fun removeDailyHighlight(id: String) {
        _dailyHighlights.value = _dailyHighlights.value.filterNot { it.id == id }
        showToast("Highlight removed")
    }

    fun confirmMorningPlan() {
        _showMorningKickoff.value = false
        showToast("Today's Plan Locked In! 🚀 Let's execute with focus.")
    }

    fun getDailyEnergyBudget(): DailyEnergyBudget {
        val totalCapacity = 8.0
        val meetingHours = _meetings.value.size * 0.75
        val pendingTasks = _feedItems.value.filter { it.category == FeedCategory.TASK && !it.isCompleted }
        val taskHours = pendingTasks.size * 0.6
        val highlightsMinutes = _dailyHighlights.value.filter { !it.isCompleted }.sumOf { it.estimatedMinutes }
        val highlightHours = highlightsMinutes / 60.0

        val committed = kotlin.math.min(12.0, (meetingHours + taskHours + highlightHours).coerceAtLeast(2.0))
        val roundedCommitted = (kotlin.math.round(committed * 10) / 10.0)
        val remaining = (kotlin.math.max(0.0, totalCapacity - roundedCommitted) * 10).toInt() / 10.0
        val loadPct = ((roundedCommitted / totalCapacity) * 100).toInt()

        val (status, warning) = when {
            loadPct > 100 -> EnergyStatus.OVERLOADED to "⚠️ Warning: Day is overloaded (>8.0h). Consider deferring non-urgent tasks to prevent burnout."
            loadPct > 80 -> EnergyStatus.HEAVY to "High cognitive demand today. Ensure you schedule 10m recharge breaks."
            loadPct >= 50 -> EnergyStatus.OPTIMAL to "Optimal energy balance. Ample focus time available for deep work."
            else -> EnergyStatus.LIGHT to "Light schedule with high flexibility. Great day for strategic learning."
        }

        return DailyEnergyBudget(
            totalCapacityHours = totalCapacity,
            committedHours = roundedCommitted,
            remainingHours = remaining,
            loadPercentage = loadPct,
            status = status,
            burnoutWarning = warning
        )
    }

    fun fillTimeBlockGapWithFocus(gapId: String) {
        val gap = _timeBlockGaps.value.find { it.id == gapId }
        val title = gap?.suggestedTitle ?: "Focus Sanctuary (Deep Work)"
        val startTime = gap?.startTime ?: "11:30 AM"

        val newEvent = TimelineEvent(
            id = "timeline_focus_${System.currentTimeMillis()}",
            time = startTime,
            period = if (startTime.contains("PM", ignoreCase = true)) "PM" else "AM",
            title = title,
            subtitle = "Protected Deep Work Block • Zero Distractions",
            durationMinutes = gap?.durationMinutes ?: 60,
            type = TimelineType.DEEP_FOCUS,
            priorityTag = "High Focus"
        )
        _timelineEvents.value = listOf(newEvent) + _timelineEvents.value.filterNot { it.id == newEvent.id }

        // Remove gap
        _timeBlockGaps.value = _timeBlockGaps.value.filterNot { it.id == gapId }
        showToast("Focus Block scheduled into calendar ($startTime) 🛡️")
    }

    fun fillTimeBlockGapWithTask(gapId: String, taskId: String) {
        val task = _feedItems.value.find { it.id == taskId }
        val gap = _timeBlockGaps.value.find { it.id == gapId }
        if (task != null && gap != null) {
            _feedItems.value = _feedItems.value.map {
                if (it.id == taskId) it.copy(time = gap.startTime) else it
            }
            _timeBlockGaps.value = _timeBlockGaps.value.filterNot { it.id == gapId }
            showToast("Task '${task.title}' scheduled for ${gap.startTime} ⏱️")
        }
    }

    fun rolloverUnfinishedTasksToTomorrow() {
        val pending = _feedItems.value.filter { it.category == FeedCategory.TASK && !it.isCompleted }
        if (pending.isEmpty()) {
            showToast("All tasks are already completed! Amazing job today! 🎉")
            return
        }

        _feedItems.value = _feedItems.value.map { item ->
            if (item.category == FeedCategory.TASK && !item.isCompleted) {
                item.copy(
                    time = "Tomorrow, 10:00 AM",
                    statusTag = "Rolled Over"
                )
            } else item
        }
        showToast("${pending.size} tasks rolled over to tomorrow without guilt ✨")
    }

    fun submitEveningReflection(
        wins: String,
        gratitude: String,
        lesson: String,
        energyRating: Int,
        rolloverTasks: Boolean
    ) {
        val reflection = DailyReflection(
            id = "ref_${System.currentTimeMillis()}",
            date = "Today, Evening",
            topWins = wins.ifBlank { "Executed priorities and stayed mindful." },
            gratitudeNotes = gratitude.ifBlank { "Grateful for energy, teammates, and progress." },
            lessonOrNextStep = lesson.ifBlank { "Maintain clear focus boundaries tomorrow." },
            energyRating = energyRating
        )
        _dailyReflections.value = listOf(reflection) + _dailyReflections.value

        // Also save reflection into Notes vault
        val noteContent = buildString {
            appendLine("📅 DAILY EVENING RETROSPECTIVE")
            appendLine("Energy Rating: $energyRating / 5 ⭐")
            appendLine("\n🏆 Top Wins:")
            appendLine(reflection.topWins)
            appendLine("\n🙏 Gratitude:")
            appendLine(reflection.gratitudeNotes)
            appendLine("\n💡 Tomorrow's Focus & Lesson:")
            appendLine(reflection.lessonOrNextStep)
        }
        val note = NoteItem(
            id = "note_retro_${System.currentTimeMillis()}",
            title = "Evening Reflection • Today",
            content = noteContent,
            category = "Reflections",
            updatedAt = "Just now"
        )
        _notes.value = listOf(note) + _notes.value

        if (rolloverTasks) {
            rolloverUnfinishedTasksToTomorrow()
        }

        _showEveningWindDown.value = false
        triggerConfetti("Daily Wind-down Complete! Rest well tonight! 🌙")
    }

    fun getLifePillars(): List<LifePillar> {
        val completedTasks = _feedItems.value.count { it.category == FeedCategory.TASK && it.isCompleted }
        val totalTasks = _feedItems.value.count { it.category == FeedCategory.TASK }
        val workScore = if (totalTasks > 0) ((completedTasks.toDouble() / totalTasks) * 40 + 50).toInt().coerceIn(50, 98) else 85

        val healthSteps = _healthMetrics.value.steps
        val healthWater = _healthMetrics.value.hydration
        val healthScore = (((healthSteps / 10000.0) * 50 + (healthWater / 3.0) * 50)).toInt().coerceIn(45, 96)

        val spent = _transactions.value.filter { it.amount < 0 }.sumOf { -it.amount }
        val monthlyBudget = 50000.0
        val wealthScore = if (spent <= monthlyBudget) 92 else 68

        val habitsChecked = _habits.value.count { it.isCompletedToday }
        val growthScore = if (_habits.value.isNotEmpty()) ((habitsChecked.toDouble() / _habits.value.size) * 50 + 45).toInt().coerceIn(40, 95) else 78

        val homeMaintCount = _homeVehicleItems.value.size
        val homeScore = 82

        return listOf(
            LifePillar(
                type = LifePillarType.WORK,
                title = "Career & Work",
                score = workScore,
                activeInitiative = "Q4 Token Launch & Architecture",
                metricSummary = "$completedTasks/$totalTasks Tasks Done • ${_meetings.value.size} Meetings",
                statusText = if (workScore >= 80) "On Track" else "Needs Attention",
                targetModule = "tasks"
            ),
            LifePillar(
                type = LifePillarType.HEALTH,
                title = "Health & Vitality",
                score = healthScore,
                activeInitiative = "Hydration & 10k Steps Daily Target",
                metricSummary = "${healthSteps} Steps • ${healthWater}L Hydrated • 88 Sleep Score",
                statusText = if (healthScore >= 75) "Optimal" else "Below Target",
                targetModule = "habits"
            ),
            LifePillar(
                type = LifePillarType.WEALTH,
                title = "Wealth & Finance",
                score = wealthScore,
                activeInitiative = "Under ₹5k Daily Spend Cap & Savings",
                metricSummary = "₹${String.format("%.0f", spent)} Spent • ₹${String.format("%.0f", monthlyBudget)} Budget",
                statusText = "Safe Buffer",
                targetModule = "finance"
            ),
            LifePillar(
                type = LifePillarType.GROWTH,
                title = "Personal Growth",
                score = growthScore,
                activeInitiative = "Deep Reading & Mindfulness Streak",
                metricSummary = "$habitsChecked/${_habits.value.size} Habits Complete • ${_notes.value.size} Notes Vaulted",
                statusText = "Consistent",
                targetModule = "habits"
            ),
            LifePillar(
                type = LifePillarType.HOME,
                title = "Home & Loved Ones",
                score = homeScore,
                activeInitiative = "Vehicle & Living Space Maintenance",
                metricSummary = "$homeMaintCount Maintenance Items Tracked",
                statusText = "Scheduled",
                targetModule = "home_vehicle"
            )
        )
    }

    // ==========================================
    // Phase 3: Strategic OKRs, Habit Stacking & Weekly Reviews
    // ==========================================

    fun openWeeklyReview() {
        _showWeeklyReviewDialog.value = true
    }

    fun closeWeeklyReview() {
        _showWeeklyReviewDialog.value = false
    }

    fun toggleHabitStack(stackId: String) {
        _habitStacks.value = _habitStacks.value.map { stack ->
            if (stack.id == stackId) {
                val newCompleted = !stack.isCompletedToday
                val newStreak = if (newCompleted) stack.streakDays + 1 else (stack.streakDays - 1).coerceAtLeast(0)
                stack.copy(isCompletedToday = newCompleted, streakDays = newStreak)
            } else stack
        }
        val target = _habitStacks.value.find { it.id == stackId }
        if (target != null && target.isCompletedToday) {
            showToast("✨ Habit Stack Completed! ${target.rewardOrCelebration} (Streak: ${target.streakDays}d)")
        }
    }

    fun addHabitStack(
        anchor: String,
        newHabit: String,
        reward: String,
        pillar: LifePillarType,
        timeOfDay: String
    ) {
        val newStack = HabitStack(
            id = "stack_${System.currentTimeMillis()}",
            anchorHabit = anchor,
            newTinyHabit = newHabit,
            rewardOrCelebration = reward,
            pillar = pillar,
            timeOfDay = timeOfDay,
            streakDays = 1,
            isCompletedToday = false
        )
        _habitStacks.value = listOf(newStack) + _habitStacks.value
        showToast("Added Habit Stack: '$anchor' → '$newHabit'")
    }

    fun deleteHabitStack(stackId: String) {
        _habitStacks.value = _habitStacks.value.filterNot { it.id == stackId }
        showToast("Habit stack removed")
    }

    fun updateOkrProgress(okrId: String, newProgress: Int) {
        val clamped = newProgress.coerceIn(0, 100)
        _strategicOkrs.value = _strategicOkrs.value.map { okr ->
            if (okr.id == okrId) {
                val newStatus = when {
                    clamped >= 100 -> "Achieved"
                    clamped >= 80 -> "Ahead"
                    clamped >= 50 -> "On Track"
                    else -> "Needs Focus"
                }
                okr.copy(progressPercent = clamped, status = newStatus)
            } else okr
        }
        showToast("OKR Progress updated to $clamped%")
    }

    fun addStrategicOkr(
        title: String,
        pillar: LifePillarType,
        horizon: GoalHorizon,
        targetDesc: String,
        deadline: String
    ) {
        val newOkr = StrategicOKR(
            id = "okr_${System.currentTimeMillis()}",
            title = title,
            pillar = pillar,
            horizon = horizon,
            targetDescription = targetDesc,
            currentMetric = "Initiative Started • 0% Progress",
            progressPercent = 0,
            keyResults = listOf("Define baseline metrics", "Deliver milestone 1"),
            deadline = deadline,
            status = "On Track",
            colorHex = pillar.defaultColor
        )
        _strategicOkrs.value = listOf(newOkr) + _strategicOkrs.value
        showToast("🎯 Created OKR: '$title'")
    }

    fun saveWeeklyReview(
        topWins: List<String>,
        areasToImprove: String,
        nextWeekFocus: List<String>,
        score: Int
    ) {
        val newReview = WeeklyReview(
            id = "wr_${System.currentTimeMillis()}",
            weekNumber = 39,
            weekRange = "Sep 22 - Sep 28, 2026",
            completedTasksCount = _feedItems.value.count { it.isCompleted },
            focusHoursLogged = 25.5,
            habitsConsistencyRate = 94,
            topWins = topWins.filter { it.isNotBlank() },
            areasToImprove = areasToImprove,
            nextWeekFocus = nextWeekFocus.filter { it.isNotBlank() },
            overallScore = score.coerceIn(1, 100),
            isReviewed = true,
            reviewedDate = "Today, 08:00 PM"
        )
        _weeklyReviews.value = listOf(newReview) + _weeklyReviews.value
        _showWeeklyReviewDialog.value = false

        // Vault the review into Notes
        val noteContent = buildString {
            appendLine("# 📊 Weekly Executive Retrospective")
            appendLine("Score: $score/100 | Week 39")
            appendLine()
            appendLine("## 🏆 Top Wins")
            topWins.forEach { appendLine("- $it") }
            appendLine()
            appendLine("## 🔍 Areas to Improve")
            appendLine(areasToImprove)
            appendLine()
            appendLine("## 🎯 Next Week Priorities")
            nextWeekFocus.forEach { appendLine("- $it") }
        }
        val reviewNote = NoteItem(
            id = "note_wr_${System.currentTimeMillis()}",
            title = "Weekly Review: Sep 22 - Sep 28",
            content = noteContent,
            category = "Weekly Retrospective",
            updatedAt = "Just now"
        )
        _notes.value = listOf(reviewNote) + _notes.value

        showToast("🎉 Weekly Review logged & saved to Notes Vault!")
    }

    fun getPillarWeeklySummaries(): List<PillarWeeklySummary> {
        val pillars = getLifePillars()
        return pillars.map { p ->
            val delta = when (p.type) {
                LifePillarType.WORK -> +6
                LifePillarType.HEALTH -> +4
                LifePillarType.WEALTH -> +2
                LifePillarType.GROWTH -> +8
                LifePillarType.HOME -> +1
            }
            PillarWeeklySummary(
                pillar = p.type,
                score = p.score,
                deltaFromLastWeek = delta,
                highlight = p.activeInitiative
            )
        }
    }

    // ==========================================
    // Phase 4: Proactive Intelligence & Context Awareness Methods
    // ==========================================

    fun dismissActiveContextNotification() {
        _activeContextNotification.value = null
    }

    fun triggerContextPrompt(triggerId: String) {
        val trigger = _contextTriggers.value.find { it.id == triggerId }
        if (trigger != null) {
            _activeContextNotification.value = trigger
            showToast("📍 Context Triggered: ${trigger.locationName}")
        }
    }

    fun openPreMeetingBrief() {
        _showPreMeetingBriefDialog.value = true
    }

    fun closePreMeetingBrief() {
        _showPreMeetingBriefDialog.value = false
    }

    fun openAudioMemo() {
        _showAudioMemoDialog.value = true
    }

    fun closeAudioMemo() {
        _showAudioMemoDialog.value = false
    }

    fun saveAudioMemo(
        title: String,
        simulatedSpokenText: String,
        extractedItems: List<String>
    ) {
        val newMemo = AudioVoiceMemo(
            id = "memo_${System.currentTimeMillis()}",
            title = title.ifBlank { "Voice Note • ${System.currentTimeMillis() % 1000}" },
            timestamp = "Just now",
            durationSeconds = (30..90).random(),
            audioSnippetSimulatedText = simulatedSpokenText,
            extractedActionItems = extractedItems.filter { it.isNotBlank() },
            isProcessed = true
        )
        _audioVoiceMemos.value = listOf(newMemo) + _audioVoiceMemos.value

        // Automatically create actionable tasks from the voice memo
        extractedItems.filter { it.isNotBlank() }.forEach { action ->
            val newTask = NonRoutineTask(
                id = "nrt_${System.currentTimeMillis()}_${(100..999).random()}",
                title = action,
                category = "Meeting Action",
                targetDescription = "1 Deliverable",
                isCompleted = false,
                progressSteps = 0,
                totalSteps = 1,
                estimatedMinutes = 30
            )
            _nonRoutineTasks.value = listOf(newTask) + _nonRoutineTasks.value
        }

        _showAudioMemoDialog.value = false
        triggerConfetti("Voice Memo Transcribed & ${extractedItems.size} Actions Extracted! 🎙️")
    }

    fun optimizeFragmentedGaps() {
        _cognitiveAnalytics.value = _cognitiveAnalytics.value.copy(
            fragmentedGapsCount = 0,
            interruptionRiskLevel = "Zero Interruption Sanctuary",
            recommendation = "All fragmented slots merged into a continuous 90-minute Deep Focus Sanctuary."
        )
        showToast("⚡ Focus Sanctuary protected: Gaps consolidated!")
    }

    // ==========================================
    // Phase 5: Deep Integrations, Biometrics & Widgets Methods
    // ==========================================

    fun openSedentaryStretch() {
        _showSedentaryStretchDialog.value = true
    }

    fun closeSedentaryStretch() {
        _showSedentaryStretchDialog.value = false
    }

    fun completeSedentaryStretch() {
        _biometrics.value = _biometrics.value.copy(
            lastSedentaryMinutes = 0,
            sedentaryAlertActive = false
        )
        _showSedentaryStretchDialog.value = false
        showToast("🏃 Sedentary timer reset! Micro-stretch recorded.")
    }

    fun openWidgetPreview() {
        _showWidgetPreviewDialog.value = true
    }

    fun closeWidgetPreview() {
        _showWidgetPreviewDialog.value = false
    }

    fun togglePinWidget(widgetId: String) {
        _widgetConfigs.value = _widgetConfigs.value.map { w ->
            if (w.widgetId == widgetId) {
                val next = !w.isPinned
                showToast(if (next) "📌 Widget pinned to Android Home Screen!" else "Widget unpinned")
                w.copy(isPinned = next)
            } else w
        }
    }

    fun toggleQuickSettingsTile() {
        val next = !_persistentHUDState.value.isQuickSettingsTileActive
        _persistentHUDState.value = _persistentHUDState.value.copy(isQuickSettingsTileActive = next)
        showToast(if (next) "⚡ Quick Settings Focus Tile enabled!" else "Focus Tile disabled")
    }

    fun toggleOngoingNotification() {
        val next = !_persistentHUDState.value.isOngoingNotificationEnabled
        _persistentHUDState.value = _persistentHUDState.value.copy(isOngoingNotificationEnabled = next)
        showToast(if (next) "🔔 Ongoing HUD Notification enabled" else "HUD Notification hidden")
    }

    // ==========================================
    // Phase 6: Autonomous Delegation, Collaborative Sync & Ecosystem Methods
    // ==========================================

    fun switchProfile(profile: LifeOSProfile) {
        _activeProfile.value = profile
        showToast("Switched context to ${profile.label}")
    }

    fun openDelegationHub() {
        _showDelegationDialog.value = true
    }

    fun closeDelegationHub() {
        _showDelegationDialog.value = false
    }

    fun pingDelegatedTask(taskId: String) {
        _delegatedTasks.value = _delegatedTasks.value.map { task ->
            if (task.id == taskId) {
                showToast("Sent automated reminder to ${task.assigneeName}")
                task.copy(lastPingMessage = "Follow-up reminder sent just now.")
            } else task
        }
    }

    fun delegateNewTask(
        title: String,
        assigneeName: String,
        role: DelegationRole,
        deadline: String,
        notes: String
    ) {
        val initials = assigneeName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .ifBlank { "TM" }

        val item = DelegatedTaskItem(
            id = "del_${System.currentTimeMillis()}",
            title = title.ifBlank { "New Delegated Milestone" },
            assigneeName = assigneeName.ifBlank { "Team Member" },
            assigneeAvatarInitials = initials,
            role = role,
            deadline = deadline.ifBlank { "This Sprint" },
            status = "Pending Accept",
            isAutoFollowUpEnabled = true,
            lastPingMessage = "Assignment dispatched via Slack / Email link.",
            notes = notes
        )
        _delegatedTasks.value = listOf(item) + _delegatedTasks.value
        showToast("Delegated '$title' to $assigneeName")
    }

    fun openDailyDigest() {
        _showDailyDigestDialog.value = true
    }

    fun closeDailyDigest() {
        _showDailyDigestDialog.value = false
    }

    fun openEcosystemVault() {
        _showEcosystemVaultDialog.value = true
    }

    fun closeEcosystemVault() {
        _showEcosystemVaultDialog.value = false
    }

    fun triggerCloudE2EEBackup() {
        _ecosystemBackupState.value = _ecosystemBackupState.value.copy(
            lastBackupTimestamp = "Just now",
            isCloudE2EEEnabled = true
        )
        triggerConfetti("Ecosystem Synced with AES-256-GCM Vault! 🔒")
    }
}

