package com.example.util

import java.time.Duration
import java.time.LocalTime
import java.util.Locale

object TimeUtils {
    /**
     * Parses a time string (e.g. "10:30 AM", "01:15 PM", "08:30", "14:00")
     * and optional period string (e.g. "AM", "PM") into a [LocalTime].
     */
    fun parseTime(timeStr: String, period: String? = null): LocalTime? {
        return try {
            val combined = if (!period.isNullOrBlank() &&
                !timeStr.contains("AM", ignoreCase = true) &&
                !timeStr.contains("PM", ignoreCase = true)
            ) {
                if (period.equals("NOW", ignoreCase = true)) {
                    "${timeStr.trim()} AM"
                } else {
                    "${timeStr.trim()} ${period.trim()}"
                }
            } else {
                timeStr.trim()
            }

            val match = Regex("""(\d{1,2}):(\d{2})(?:\s*(AM|PM|am|pm))?""").find(combined)
            if (match != null) {
                val hourPart = match.groupValues[1].toIntOrNull() ?: return null
                val minutePart = match.groupValues[2].toIntOrNull() ?: return null
                val amPmPart = match.groupValues[3].uppercase(Locale.ROOT)

                var hour = hourPart
                if (amPmPart == "PM" && hour < 12) {
                    hour += 12
                } else if (amPmPart == "AM" && hour == 12) {
                    hour = 0
                }
                LocalTime.of(hour.coerceIn(0, 23), minutePart.coerceIn(0, 59))
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Checks if a given time is within the next 2 hours.
     * Evaluates against both the device's current system time and the app's
     * simulated active schedule base time (09:10 AM, corresponding to the
     * 09:30 AM hero meeting marked "In 20m").
     */
    fun isDueWithinNextTwoHours(timeStr: String, period: String? = null): Boolean {
        val target = parseTime(timeStr, period) ?: return false

        // 1. Check against device's real system time
        try {
            val now = LocalTime.now()
            val diffReal = Duration.between(now, target).toMinutes()
            if (diffReal in 0..120) {
                return true
            }
        } catch (_: Exception) {}

        // 2. Check against app's simulated morning timeline base time (09:10 AM)
        val simulatedNow = LocalTime.of(9, 10)
        val diffSimulated = Duration.between(simulatedNow, target).toMinutes()
        if (diffSimulated in 0..120) {
            return true
        }

        return false
    }

    /**
     * Checks if a given due date string represents 'Today'.
     * Handles string matching like "Today" or specific formatted dates (e.g. "MMM dd, yyyy").
     */
    fun isDueToday(dueDateStr: String?): Boolean {
        if (dueDateStr.isNullOrBlank()) return false
        val trimmed = dueDateStr.trim()
        if (trimmed.equals("Today", ignoreCase = true)) return true

        // Check if date formatted as "MMM dd, yyyy" matches today's date
        try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)
            val parsedDate = java.time.LocalDate.parse(trimmed, formatter)
            if (parsedDate.isEqual(java.time.LocalDate.now())) {
                return true
            }
        } catch (_: Exception) {}

        // Also check if contains "Today" in strings like "Today 05:00 PM"
        if (trimmed.startsWith("Today", ignoreCase = true)) return true

        return false
    }

    /**
     * Parses a combination of dueDate string and time string into a LocalDateTime
     * for accurate chronological sorting of tasks.
     * Earliest deadlines sort first. If dueDate is absent, defaults to today.
     */
    fun parseDueDateTime(dueDateStr: String?, timeStr: String?): java.time.LocalDateTime {
        val today = java.time.LocalDate.now()
        val time = timeStr?.let { parseTime(it) } ?: LocalTime.MAX

        val date = if (dueDateStr.isNullOrBlank()) {
            today
        } else {
            val trimmed = dueDateStr.trim()
            when {
                trimmed.equals("Today", ignoreCase = true) || trimmed.startsWith("Today", ignoreCase = true) -> today
                trimmed.equals("Tomorrow", ignoreCase = true) || trimmed.startsWith("Tomorrow", ignoreCase = true) -> today.plusDays(1)
                trimmed.equals("This Weekend", ignoreCase = true) -> {
                    val daysUntilSat = (java.time.DayOfWeek.SATURDAY.value - today.dayOfWeek.value + 7) % 7
                    today.plusDays(if (daysUntilSat == 0) 7L else daysUntilSat.toLong())
                }
                trimmed.equals("Next Monday", ignoreCase = true) -> {
                    val daysUntilMon = (java.time.DayOfWeek.MONDAY.value - today.dayOfWeek.value + 7) % 7
                    today.plusDays(if (daysUntilMon == 0) 7L else daysUntilMon.toLong())
                }
                trimmed.equals("Next Week", ignoreCase = true) -> today.plusWeeks(1)
                trimmed.equals("Next Month", ignoreCase = true) -> today.plusMonths(1)
                else -> {
                    try {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)
                        java.time.LocalDate.parse(trimmed, formatter)
                    } catch (_: Exception) {
                        try {
                            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
                            java.time.LocalDate.parse(trimmed, formatter)
                        } catch (_: Exception) {
                            try {
                                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US)
                                java.time.LocalDate.parse("$trimmed ${today.year}", formatter)
                            } catch (_: Exception) {
                                try {
                                    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.US)
                                    java.time.LocalDate.parse("$trimmed ${today.year}", formatter)
                                } catch (_: Exception) {
                                    try {
                                        java.time.LocalDate.parse(trimmed)
                                    } catch (_: Exception) {
                                        today.plusDays(30)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return java.time.LocalDateTime.of(date, time)
    }
}
