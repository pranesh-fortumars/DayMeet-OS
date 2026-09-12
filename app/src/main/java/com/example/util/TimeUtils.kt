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
}
