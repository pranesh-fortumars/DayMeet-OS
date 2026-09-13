package com.example.data

import com.example.model.*

object DayMeetRepository {
    val ALEX_AVATAR = "https://lh3.googleusercontent.com/aida-public/AB6AXuBFZWPH3cfWKAF-53Pgj0hMIWO_45Dl8ZbZF8XMAP5LVxvnHioIzq_5vf--bgqsUf-i1A7QFJYbD9pq6d8Yt197XLVak3Wb4Sj549ogVkuJQAsdzISpKS7dUCq1b_e_u7kFj6yB_5N3lCgqXGW46VnQkrONIAx6V-UGkc33mrUpQY-x_QVoTIXGLk6ZPFqHNGTzTCB6_Xab_s8mNHo5JVBYCzgAdg0ziV3Cb4fFeC36pkvyLAREzYM_"
    val MAYA_AVATAR = "https://lh3.googleusercontent.com/aida-public/AB6AXuBIk6DZy1ctCegYw_fPIke1cS-hgJFQPfzIoiU8iS9F1d1ORxrADCdLzkbosknioDAbkurwHti7xpdZSvlRDn8RDdJJXxkAnBW5dpbbQKdfr7rCUf_qMECRi-6dmKjNxfwD1FqsoFudNURvHm_Nabw0HJNHt_zA_LJFxe9hgi2z2yRHBp5RoWkVacHjEXAyUT1Yejk8GjwBGw2l2QGvKLlPAYtBtvyjure-MXDzbAE6RW63OKdfq5F_"
    val DAVID_AVATAR = "https://lh3.googleusercontent.com/aida-public/AB6AXuDdEq7ya9A_DPdSAlt5MvP4crBjvsPEeQKlGNLp-EHMyxXXjr57SEvZ54jSFHQdDUhDVbDzRqKM1TrS-gpCMYQGzKDZRguH2oe7l0xmGro6l5eE-v6xdz5L73ZGMSGX2UrCZpCW9_zhOT3CXz068QfTb51XSR-LuHiRBxtRJ1aXu00X8Wzup3ZyocVkVufPdnZ17qE0Go3lTacGyMwYRWLkaKxkVak0dLTAF4CYjDFJV1CYdV0HKS0h"
    val ELENA_AVATAR = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhIJnjth64UGJBQcz_X70T0tsMjo2fCP4TboFWf2h-NzhfyBtHFlz-cPt1XwwQMUAHDnPFPg-meO4T3jAKnpbaNRzWOF9MRgv3eT6uSufQ3rgCdRrnSMs4DefkONwXNN7N3QO04cJor62pdHInYcVCMOtJ8GvFHzS18BWMoATsXQksUQkeQoOh1rBgJt0YUrovzYfO043lFDXx6hgw75GIVIl6gG2aGLzDpe9AAwReieJ8kOCvfR_"
    val CAFE_IMAGE = "https://lh3.googleusercontent.com/aida-public/AB6AXuCGV-dbTN1vUkyCJsnWG0QPcQMZOQtigMfgXcTIsP5WnwnQWM4jXmG4p8sDy9U1mqZQHxVxVaE6NROB3043_XM7mdRs_r88KNS9ggZiPl_YN3XYlza15rPg_DD0N7wfBvlhsw0dSyDll6MzvgN1uqSuNIMFNBq6lRge5BHm2TjxmOnBYP3zlksUGwvhq5HJ76wqbTLzTsmFj8iUp8_iDJqY2QFQjafjqgRmNo0uQTY-Jr9z1UvxeT2f"

    fun getInitialFeedItems(): List<FeedItem> = listOf(
        FeedItem(
            id = "f1",
            time = "09:30 AM",
            title = "Product Strategy Meeting",
            subtitle = "Google Meet • 4 members",
            category = FeedCategory.MEETING,
            statusTag = "High Priority",
            platform = "Google Meet",
            membersCount = 4
        ),
        FeedItem(
            id = "f2",
            time = "10:30 AM",
            title = "Submit Q4 Token Audit",
            subtitle = "Tasks Workspace • Final signoff required",
            category = FeedCategory.TASK,
            statusTag = "Priority",
            isCompleted = false
        ),
        FeedItem(
            id = "f3",
            time = "11:30 AM",
            title = "Electricity Bill Due Tomorrow",
            subtitle = "Tata Power • ₹2,400",
            category = FeedCategory.REMINDER,
            statusTag = "Due Tomorrow"
        ),
        FeedItem(
            id = "f4",
            time = "01:15 PM",
            title = "Review Sprint Backlog",
            subtitle = "Figma component sync & tokens review",
            category = FeedCategory.TASK,
            statusTag = "Design Team",
            isCompleted = false
        ),
        FeedItem(
            id = "f5",
            time = "03:00 PM",
            title = "Client Discussion & Demo",
            subtitle = "3 external attendees • Presentation deck ready",
            category = FeedCategory.MEETING,
            statusTag = "Zoom Demo",
            platform = "Zoom",
            membersCount = 3
        ),
        FeedItem(
            id = "f6",
            time = "05:00 PM",
            title = "Drink 2.5L Water",
            subtitle = "Hydration goal: 1.8L of 2.5L logged",
            category = FeedCategory.HABIT,
            statusTag = "18d streak 🔥",
            habitProgress = 0.72f
        )
    )

    fun getInitialCrossStreamItems(): List<CrossStreamItem> = listOf(
        CrossStreamItem(
            id = "cs1",
            time = "09:30 AM",
            title = "Product Strategy Review",
            subtitle = "Google Meet • Alex, Sarah + 3 others",
            tag = "Meeting",
            tagType = "meeting"
        ),
        CrossStreamItem(
            id = "cs2",
            time = "10:45 AM",
            title = "Submit Q4 Token Audit",
            subtitle = "Tasks Workspace • Final signoff required",
            tag = "Priority",
            tagType = "priority"
        ),
        CrossStreamItem(
            id = "cs3",
            time = "12:30 PM",
            title = "Artisan Bistro Lunch",
            subtitle = "Auto-linked with Calendar 'Lunch with Dev Team'",
            tag = "₹450 Expense",
            tagType = "expense"
        ),
        CrossStreamItem(
            id = "cs4",
            time = "02:00 PM",
            title = "Deep Focus Session",
            subtitle = "Slack & Email auto-responder active",
            tag = "90 mins",
            tagType = "focus"
        ),
        CrossStreamItem(
            id = "cs5",
            time = "04:15 PM",
            title = "Hydration & Garden Walk",
            subtitle = "Target: 1,500 more steps to hit daily 10k",
            tag = "Wellness",
            tagType = "wellness"
        ),
        CrossStreamItem(
            id = "cs6",
            time = "06:00 PM",
            title = "Internet Broadband Bill...",
            subtitle = "Airtel Xstream Fiber • ₹1,179 scheduled",
            tag = "Auto-Pay",
            tagType = "autopay"
        ),
        CrossStreamItem(
            id = "cs7",
            time = "In 3 Days",
            title = "Chennai Flight 6E 412",
            subtitle = "Web check-in opens Sunday 07:30 AM • Hotel Taj Coromandel",
            tag = "Trip Info",
            tagType = "travel"
        )
    )

    fun getInitialTimeline(): List<TimelineEvent> = listOf(
        TimelineEvent(
            id = "t1",
            time = "06:30",
            period = "AM",
            title = "Morning Routine & Meditation",
            durationMinutes = 30,
            type = TimelineType.HABIT,
            isCompleted = true,
            streakInfo = "18-day streak preserved"
        ),
        TimelineEvent(
            id = "t2",
            time = "07:30",
            period = "AM",
            title = "Workout & Stretching",
            durationMinutes = 45,
            type = TimelineType.HABIT,
            isCompleted = true
        ),
        TimelineEvent(
            id = "t3",
            time = "08:30",
            period = "AM",
            title = "DayKick Planning & Email triage",
            durationMinutes = 45,
            type = TimelineType.TASK,
            isCompleted = false
        ),
        TimelineEvent(
            id = "t4",
            time = "09:30",
            period = "NOW",
            title = "Product Strategy Review",
            subtitle = "Quarterly roadmap alignment with Core Team",
            durationMinutes = 45,
            type = TimelineType.MEETING,
            isHappeningNow = true,
            linkUrl = "meet.google.com/dmy-krtx-vsa",
            attendeesWaiting = 5
        ),
        TimelineEvent(
            id = "t5",
            time = "10:30",
            period = "AM",
            title = "Deep Work: UI Systems & Spec Draft",
            subtitle = "Token architecture & multi-density layout rules",
            durationMinutes = 90,
            type = TimelineType.DEEP_FOCUS,
            priorityTag = "Deep Focus • DND Mode"
        ),
        TimelineEvent(
            id = "t6",
            time = "12:30",
            period = "PM",
            title = "Artisan Bistro Lunch & Dev Team Sync",
            durationMinutes = 45,
            type = TimelineType.PERSONAL
        ),
        TimelineEvent(
            id = "t7",
            time = "01:30",
            period = "PM",
            title = "Tasks & Engineering Review",
            durationMinutes = 75,
            type = TimelineType.TASK_GROUP,
            subtasks = listOf(
                Subtask("st1", "Review PR #348 Token Architecture", isCompleted = true),
                Subtask("st2", "Verify calendar drag constraints on iOS", isCompleted = false),
                Subtask("st3", "Approve dark palette saturation values", isCompleted = false)
            )
        ),
        TimelineEvent(
            id = "t8",
            time = "03:00",
            period = "PM",
            title = "Client Follow-up Call",
            subtitle = "Acme Corp Q4 Deliverables Demo",
            durationMinutes = 45,
            type = TimelineType.MEETING,
            priorityTag = "High",
            linkUrl = "Zoom Conference"
        ),
        TimelineEvent(
            id = "t9",
            time = "05:00",
            period = "PM",
            title = "Daily Review & Evening Wrap-up",
            subtitle = "Archive inbox, note blockers, celebrate wins.",
            durationMinutes = 30,
            type = TimelineType.REMINDER,
            isCompleted = false
        )
    )

    fun getInitialMeetings(): List<MeetingItem> = listOf(
        MeetingItem(
            id = "m1",
            title = "Product Strategy & Roadmap Review",
            time = "09:30 AM - 10:15 AM",
            duration = "45 mins",
            platform = "Google Meet",
            organizer = "Alex Chen (You)",
            status = "In 20m",
            agendaItems = listOf("Q4 Goals", "Mobile launch", "Resource allocation"),
            attendees = listOf(
                Attendee("Alex Chen", "Product Lead", ALEX_AVATAR),
                Attendee("Sarah Lee", "Engineering Manager", MAYA_AVATAR),
                Attendee("David Kumar", "VP Product", DAVID_AVATAR),
                Attendee("Elena Gomez", "UX Designer", ELENA_AVATAR)
            ),
            attendeesCount = 5,
            hasMinutes = true
        ),
        MeetingItem(
            id = "m2",
            title = "Client Discussion: Enterprise Tier",
            time = "02:00 PM - 02:45 PM",
            duration = "45 mins",
            platform = "Zoom",
            organizer = "Mark Davis, Sarah Lee, Alex Chen",
            status = "Confirmed",
            notificationBefore = "15m before",
            attendees = listOf(
                Attendee("Mark Davis", avatarUrl = DAVID_AVATAR),
                Attendee("Sarah Lee", avatarUrl = MAYA_AVATAR),
                Attendee("Alex Chen", avatarUrl = ALEX_AVATAR)
            ),
            attendeesCount = 3
        ),
        MeetingItem(
            id = "m3",
            title = "Weekly Engineering Sync",
            time = "04:15 PM - 05:00 PM",
            duration = "45 mins",
            platform = "MS Teams",
            organizer = "Core Engineering Team",
            status = "Upcoming",
            recurring = "Every Thursday",
            attendees = listOf(
                Attendee("Evan King", initials = "EK"),
                Attendee("Maya Rao", initials = "MR"),
                Attendee("Alex Chen", avatarUrl = ALEX_AVATAR)
            ),
            attendeesCount = 8
        )
    )

    fun getInitialTransactions(): List<FinanceTransaction> = listOf(
        FinanceTransaction(
            id = "tx1",
            title = "Artisan Cafe Bistro",
            category = "Food & Dining",
            time = "12:30 PM",
            amount = -450.00,
            method = "UPI / Card",
            iconType = "restaurant",
            linkedEvent = "Lunch with Dev Team (12:30 PM)"
        ),
        FinanceTransaction(
            id = "tx2",
            title = "Metro Smart Transit",
            category = "Commute & Transit",
            time = "08:45 AM",
            amount = -80.00,
            method = "Tap to Pay",
            iconType = "subway"
        ),
        FinanceTransaction(
            id = "tx3",
            title = "Adobe Creative Cloud",
            category = "Work Subscriptions",
            time = "Recurring Monthly",
            amount = -720.00,
            method = "Auto-Debit",
            iconType = "software",
            tags = listOf("Work", "Tax Deductible")
        )
    )

    fun getInitialUpcomingBills(): List<UpcomingBill> = listOf(
        UpcomingBill(
            id = "b1",
            name = "Electricity Bill Due Tomorrow",
            scheduleDate = "Due Tomorrow",
            daysLeft = "1d left",
            department = "Tata Power",
            amount = 2400.00,
            autoPay = false
        ),
        UpcomingBill(
            id = "b2",
            name = "Airtel Xstream Fiber",
            scheduleDate = "Scheduled for Oct 29",
            daysLeft = "5d left",
            department = "Home Broadband",
            amount = 1179.00,
            autoPay = true
        ),
        UpcomingBill(
            id = "b3",
            name = "Figma Professional Team",
            scheduleDate = "Scheduled for Oct 27",
            daysLeft = "3d left",
            department = "Workspace Team",
            amount = 1250.00,
            autoPay = false
        )
    )

    fun getInitialHealthMetrics(): HealthMetrics = HealthMetrics(
        score = 88,
        scoreLabel = "Optimal",
        sleepDuration = "7h 20m",
        sleepQuality = "85% Qual",
        sleepDeep = "1h 45m",
        steps = 7845,
        stepsTarget = 10000,
        stepsDistance = "3.8 km",
        hydration = 1.8f,
        hydrationTarget = 2.5f,
        caloriesBurned = 480,
        caloriesTarget = 600,
        heartRateBpm = 68,
        heartRateRange = "54 - 118",
        mentalState = "Happy",
        postureReminderOn = true,
        vitaminLogged = true,
        bedtimeDndOn = true,
        wearableStatus = "Oura Ring Gen 3 • 84% Bat"
    )

    fun getInitialAutomations(): List<AutomationWorkflow> = listOf(
        AutomationWorkflow(
            id = "a1",
            title = "Meeting Follow-up Orchestrator",
            category = "Productivity",
            statusTag = "Triggered 2h ago • 98% success",
            whenTrigger = "Calendar meeting ends",
            ifCondition = "Organizer is Alex & action items detected",
            thenAction = "Auto-create tasks in workspace & draft follow-up Slack ping",
            isEnabled = true,
            statsText = "28 runs this month"
        ),
        AutomationWorkflow(
            id = "a2",
            title = "Daily Expense Safeguard",
            category = "Finance",
            statusTag = "Threshold tracking active",
            whenTrigger = "Daily expense ledger exceeds ₹3,500",
            ifCondition = "Category is Dining or Shopping",
            thenAction = "Send high-priority push alert & pause non-essential auto-debits",
            isEnabled = true,
            statsText = "Saved ₹8,400 past 30 days"
        ),
        AutomationWorkflow(
            id = "a3",
            title = "Deep Work Health Sanctuary",
            category = "Health",
            statusTag = "Scheduled next at 2:00 PM",
            whenTrigger = "Deep Work block starts",
            ifCondition = "Scheduled on Calendar",
            thenAction = "Activate DND, set Slack status to \"In Focus\", & queue posture check",
            isEnabled = true,
            statsText = "14 focus sessions protected"
        ),
        AutomationWorkflow(
            id = "a4",
            title = "Critical Bill Early Warning",
            category = "Deadlines",
            statusTag = "Rent due in 3 days",
            whenTrigger = "Recurring bill is due in < 48 hours",
            ifCondition = "Category is Rent or Electricity",
            thenAction = "Pin sticky banner to Home Dashboard & schedule calendar payment buffer",
            isEnabled = true,
            statsText = "Zero overdue penalties since setup"
        ),
        AutomationWorkflow(
            id = "a5",
            title = "Network Birthday Concierge",
            category = "Social",
            statusTag = "2 upcoming in next 7 days",
            whenTrigger = "Participant birthday arrives (09:00 AM)",
            ifCondition = "Contact has WhatsApp or SMS linked",
            thenAction = "Draft personalized warm greeting & queue for 1-tap review",
            isEnabled = true,
            statsText = "Review queue: 0 pending"
        )
    )

    fun getInitialAutomationLogs(): List<AutomationLog> = listOf(
        AutomationLog(
            id = "l1",
            title = "Slack DND Auto-Responder",
            detail = "Auto-responded to 4 Slack pings during Deep Focus block.",
            time = "09:30 AM",
            isSuccess = true
        ),
        AutomationLog(
            id = "l2",
            title = "Ledger Instant Capture",
            detail = "Synced Artisan Bistro bill (₹450) to Food & Dining budget.",
            time = "08:00 AM",
            isSuccess = true
        ),
        AutomationLog(
            id = "l3",
            title = "Morning Routine Briefing",
            detail = "Synthesized weather, calendar, and high-priority deliverables.",
            time = "07:15 AM",
            isSuccess = true
        )
    )

    fun getInitialGoals(): List<GoalItem> = listOf(
        GoalItem(
            id = "g1",
            title = "Save ₹50,000 Emergency Fund",
            category = "Financial",
            target = "₹50,000",
            current = "₹32,500",
            progressPercent = 0.65f,
            deadline = "Nov 30, 2026"
        ),
        GoalItem(
            id = "g2",
            title = "Half Marathon Preparation (21 km)",
            category = "Fitness",
            target = "21 km",
            current = "14 km",
            progressPercent = 0.66f,
            deadline = "Dec 15, 2026"
        ),
        GoalItem(
            id = "g3",
            title = "Design System & Tokens Certification",
            category = "Career",
            target = "10 Modules",
            current = "8 Modules",
            progressPercent = 0.80f,
            deadline = "Oct 31, 2026"
        )
    )

    fun getInitialHabits(): List<HabitItem> = listOf(
        HabitItem("h_meditation", "Morning Meditation", 19, "Daily", false, "Mindfulness", "self_improvement", "#673AB7", 19),
        HabitItem("h_exercise", "Morning Exercise", 14, "Daily", false, "Fitness", "fitness_center", "#2E7D32", 14),
        HabitItem("h_reading", "Daily Book Reading", 12, "Daily", false, "Reading", "menu_book", "#0288D1", 12),
        HabitItem("h_learning", "AI System Architecture", 9, "Daily", false, "Learning", "school", "#F57C00", 8),
        HabitItem("h1", "Wake up early (06:30 AM)", 18, "Daily", true, "Wellness", "local_fire_department", "#E91E63", 18),
        HabitItem("h2", "Drink 2.5L Water", 12, "Daily", false, "Health", "water_drop", "#00ACC1", 10),
        HabitItem("h4", "Midday 4-7-8 Breathwork", 14, "Daily", true, "Mindfulness", "psychology", "#8E24AA", 14)
    )

    fun getInitialNotes(): List<NoteItem> = listOf(
        NoteItem(
            id = "n1",
            title = "Mobile Token Architecture v2",
            content = "Unified elevation shadows across light/dark surfaces. Adopt 8dp baseline grid and M3 dynamic token aliases.",
            category = "Meeting Notes",
            updatedAt = "Today, 11:20 AM",
            tags = listOf("Design", "Tokens", "Figma")
        ),
        NoteItem(
            id = "n2",
            title = "Q4 Emergency Fund & SIP Allocation",
            content = "Keep ₹50,000 in liquid debt fund. Split remaining monthly surplus 60% index mutual funds, 40% high-yield deposit.",
            category = "Financial",
            updatedAt = "Yesterday",
            tags = listOf("Finance", "Savings")
        ),
        NoteItem(
            id = "n3",
            title = "Chennai Trip Packing & Key Docs",
            content = "Pack lightweight formals, charger cables, Taj reservation QR code, and passport ID copy.",
            category = "Travel Notes",
            updatedAt = "Oct 22",
            tags = listOf("Travel", "Chennai")
        )
    )

    fun getInitialShoppingItems(): List<ShoppingItem> = listOf(
        ShoppingItem("s1", "Organic Almond Milk 1L", "2 cartons", 180.0, "Groceries", false),
        ShoppingItem("s2", "Greek Yogurt (Plain)", "400g tub", 120.0, "Groceries", true),
        ShoppingItem("s3", "USB-C Braided Cable 2m", "1 unit", 499.0, "Electronics", false),
        ShoppingItem("s4", "Ergonomic Desk Mat", "Dark Slate", 850.0, "Office", true)
    )

    fun getInitialTrip(): TravelTrip = TravelTrip(
        id = "tr1",
        destination = "Chennai, Tamil Nadu",
        dates = "Oct 27 – Oct 30 (4 days)",
        flightNumber = "IndiGo 6E 412 (Web check-in opens Sun 07:30 AM)",
        checkInStatus = "Confirmed • Boarding Pass Ready in 3d",
        hotel = "Taj Coromandel, Nungambakkam",
        budget = "₹24,000 (₹18,500 booked)"
    )

    fun getInitialContacts(): List<ContactItem> = listOf(
        ContactItem(
            id = "co1",
            name = "Sarah Lee",
            role = "Engineering Manager",
            phone = "+1 (555) 0192",
            email = "sarah.lee@daymeet.app",
            avatarUrl = MAYA_AVATAR,
            lastInteraction = "Slack ping 1h ago"
        ),
        ContactItem(
            id = "co2",
            name = "David Kumar",
            role = "VP Product",
            phone = "+1 (555) 0143",
            email = "david.k@daymeet.app",
            avatarUrl = DAVID_AVATAR,
            lastInteraction = "Strategy Meeting 09:30 AM"
        ),
        ContactItem(
            id = "co3",
            name = "Elena Gomez",
            role = "Lead UX Designer",
            phone = "+1 (555) 0187",
            email = "elena.g@daymeet.app",
            avatarUrl = ELENA_AVATAR,
            lastInteraction = "Figma comment yesterday"
        )
    )

    fun getInitialDocuments(): List<DocumentItem> = listOf(
        DocumentItem("doc1", "Passport (International)", "Identity", "Expires: Aug 2028", true, "2.4 MB"),
        DocumentItem("doc2", "Health Insurance Card", "Insurance", "Renewal: Dec 2026", true, "1.1 MB"),
        DocumentItem("doc3", "Lease Rental Agreement", "Housing", "Expires: Mar 2027", false, "850 KB")
    )

    fun getInitialSubscriptions(): List<SubscriptionItem> = listOf(
        SubscriptionItem("sub1", "Airtel Xstream Fiber", 1179.0, "Oct 29", "wifi"),
        SubscriptionItem("sub2", "Netflix Ultra 4K", 649.0, "Nov 05", "movie"),
        SubscriptionItem("sub3", "Figma Professional", 1250.0, "Oct 27", "design"),
        SubscriptionItem("sub4", "Spotify Premium Duo", 149.0, "Nov 12", "music")
    )

    fun getInitialReminders(): List<SmartReminder> = listOf(
        SmartReminder("rem1", "Pay Electricity Bill (Tata Power ₹2,400)", "Bill", "Tomorrow, 09:00 AM", false),
        SmartReminder("rem2", "Review Q4 Token Audit Signoff", "Task", "Today, 10:45 AM", false),
        SmartReminder("rem3", "Drink 250ml Water & Stretch", "Habit", "Today, 02:00 PM", false),
        SmartReminder("rem4", "Pick up dry cleaning", "Location: Near Home", "Today, 06:30 PM", false)
    )

    fun getInitialScheduledMessages(): List<ScheduledMessage> = listOf(
        ScheduledMessage(
            id = "sm1",
            recipientName = "Maya Rao",
            platform = "WhatsApp Integration",
            messageContent = "Happy Birthday Maya! Wishing you a brilliant and fruitful year ahead 🎉🎂",
            scheduledTime = "Oct 26, 09:00 AM",
            isSent = false,
            isAutomated = true
        ),
        ScheduledMessage(
            id = "sm2",
            recipientName = "Core Engineering Squad",
            platform = "Slack",
            messageContent = "Hi team, quick reminder that our Weekly Engineering Sync starts at 04:15 PM today.",
            scheduledTime = "Today, 03:45 PM",
            isSent = false,
            isAutomated = true
        )
    )

    fun getInitialChatMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "c1",
            isUser = true,
            text = "Can you organize my afternoon? I need 90 minutes of deep focus before my 3:30 PM client presentation, and remind me to review the budget.",
            timestamp = "11:42 AM"
        ),
        ChatMessage(
            id = "c2",
            isUser = false,
            text = "I've optimized your schedule across Calendar, Tasks & Finance. Here is what I adjusted:",
            timestamp = "11:42 AM",
            proposal = ScheduleProposal(
                rescheduled = ProposalItem(
                    typeTag = "RESCHEDULED",
                    title = "Design Sprint Review",
                    detail = "Shifted to 10:00 AM – 11:00 AM",
                    statusTag = "Tomorrow"
                ),
                addedBlock = ProposalItem(
                    typeTag = "ADDED BLOCK",
                    title = "90m Deep Focus: Client Deck Prep",
                    detail = "01:30 PM – 03:00 PM (Prior to 3:30 PM Deck)",
                    statusTag = "DND Active"
                ),
                autoReminder = ProposalItem(
                    typeTag = "AUTO-REMINDER",
                    title = "Review Daily Spending",
                    detail = "₹3,450 logged today • Under ₹5k limit",
                    statusTag = "05:30 PM"
                ),
                isApplied = false
            )
        )
    )

    fun getInitialNonRoutineTasks(): List<NonRoutineTask> = listOf(
        NonRoutineTask(
            id = "nrt_1",
            title = "Finalize Q3 OKR Roadmap & Deck",
            category = "Sprint Goal",
            targetDescription = "3 Review Milestones",
            isCompleted = false,
            progressSteps = 2,
            totalSteps = 3,
            estimatedMinutes = 60
        ),
        NonRoutineTask(
            id = "nrt_2",
            title = "Kotlin Coroutines Deep Dive Chapter 4",
            category = "Learning",
            targetDescription = "Read & Annotate",
            isCompleted = true,
            progressSteps = 1,
            totalSteps = 1,
            estimatedMinutes = 35
        ),
        NonRoutineTask(
            id = "nrt_3",
            title = "Schedule Annual Dental & Health Checkup",
            category = "Personal Errand",
            targetDescription = "Confirm Appointment",
            isCompleted = false,
            progressSteps = 0,
            totalSteps = 1,
            estimatedMinutes = 15
        ),
        NonRoutineTask(
            id = "nrt_4",
            title = "Audit Cloud Resource Usage & Downgrade Idle Tiers",
            category = "Focus Deep Work",
            targetDescription = "4 Micro-Services",
            isCompleted = false,
            progressSteps = 1,
            totalSteps = 4,
            estimatedMinutes = 45
        )
    )
}
