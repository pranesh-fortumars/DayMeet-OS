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
            title = "Submit Project Report",
            subtitle = "Client Deliverable • Q4 Execution",
            category = FeedCategory.TASK,
            statusTag = "Due in 1h",
            isCompleted = false
        ),
        FeedItem(
            id = "f3",
            time = "11:30 AM",
            title = "Call Client re: Contract",
            subtitle = "Review finalized terms before legal sign-off",
            category = FeedCategory.REMINDER,
            statusTag = "Follow-up"
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
            subtitle = "Hydration goal: 2.0L of 2.5L logged",
            category = FeedCategory.HABIT,
            statusTag = "12 days 🔥",
            habitProgress = 0.80f
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
            streakInfo = "10-day streak preserved"
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
            title = "Product Strategy Meeting",
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
            title = "Healthy Lunch & Walk",
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
            title = "Daily Review & Wrap-up",
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
            status = "Next Up • In 25m",
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
            amount = -24.50,
            method = "Card ••4109",
            iconType = "restaurant",
            linkedEvent = "Lunch with Design Lead (12:00 PM)"
        ),
        FinanceTransaction(
            id = "tx2",
            title = "Subway Metro Card",
            category = "Commute & Transit",
            time = "08:45 AM",
            amount = -5.00,
            method = "Tap to Pay",
            iconType = "subway"
        ),
        FinanceTransaction(
            id = "tx3",
            title = "Adobe Cloud Subscription",
            category = "Work Software",
            time = "Recurring Monthly",
            amount = -22.00,
            method = "Auto-Debit",
            iconType = "software",
            tags = listOf("Work", "Tax Deductible")
        )
    )

    fun getInitialUpcomingBills(): List<UpcomingBill> = listOf(
        UpcomingBill(
            id = "b1",
            name = "Figma Professional",
            scheduleDate = "Scheduled for Oct 27",
            daysLeft = "3d left",
            department = "Workspace Team",
            amount = 15.00,
            autoPay = false
        ),
        UpcomingBill(
            id = "b2",
            name = "Internet Fiber Gigabit",
            scheduleDate = "Scheduled for Oct 29",
            daysLeft = "5d left",
            department = "Home Office",
            amount = 65.00,
            autoPay = true
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
            text = "I've optimized your schedule for peak focus. Here is what I adjusted:",
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
                    detail = "\$42.50 logged today • Approaching cap",
                    statusTag = "05:30 PM"
                ),
                isApplied = false
            )
        )
    )
}
