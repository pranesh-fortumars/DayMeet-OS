package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * BroadcastReceiver triggered by AlarmManager when a task's due date and time is reached.
 * Dispatches a native Android notification with high importance and vibration.
 */
class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: "task_${System.currentTimeMillis()}"
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Due"
        val taskNotes = intent.getStringExtra(EXTRA_TASK_NOTES) ?: ""
        val taskTime = intent.getStringExtra(EXTRA_TASK_TIME) ?: ""

        Log.d("TaskNotification", "Alarm received for task: $taskTitle ($taskId)")
        TaskNotificationScheduler.showNotification(
            context = context,
            taskId = taskId,
            title = taskTitle,
            notes = taskNotes,
            time = taskTime
        )
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_NOTES = "extra_task_notes"
        const val EXTRA_TASK_TIME = "extra_task_time"
    }
}

/**
 * Local notification scheduler utilizing Android's native AlarmManager and NotificationManager.
 * Schedules precise or inexact alarms when a task's due date/time is reached.
 */
object TaskNotificationScheduler {
    const val CHANNEL_ID = "task_due_reminders_channel"
    private const val CHANNEL_NAME = "Task Due Date & Time Reminders"
    private const val CHANNEL_DESCRIPTION = "Alerts triggered when a task reaches its scheduled due date and time"

    /**
     * Initializes the notification channel on Android 8.0+ (Oreo).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Calculates the epoch millisecond trigger time from a time string like "05:00 PM",
     * "Today 05:00 PM", "Tomorrow 09:00 AM", or "In 15 Mins".
     */
    fun calculateTriggerTimeMillis(timeStr: String): Long {
        val now = LocalDateTime.now()
        val zone = ZoneId.systemDefault()

        val lower = timeStr.trim().lowercase()
        return when {
            lower.contains("in 15 min") || lower.contains("15 min") -> {
                now.plusMinutes(15).atZone(zone).toInstant().toEpochMilli()
            }
            lower.contains("in 30 min") || lower.contains("30 min") -> {
                now.plusMinutes(30).atZone(zone).toInstant().toEpochMilli()
            }
            lower.contains("in 1 hour") || lower.contains("1 hour") -> {
                now.plusHours(1).atZone(zone).toInstant().toEpochMilli()
            }
            lower.contains("in 2 hour") || lower.contains("2 hour") -> {
                now.plusHours(2).atZone(zone).toInstant().toEpochMilli()
            }
            else -> {
                // Parse standard time like "05:00 PM", "Today 05:00 PM", "Tomorrow 09:00 AM"
                val parsedTime = TimeUtils.parseTime(timeStr) ?: LocalTime.now().plusHours(1)
                var targetDate = LocalDate.now()
                if (lower.contains("tomorrow")) {
                    targetDate = targetDate.plusDays(1)
                } else if (lower.contains("next week")) {
                    targetDate = targetDate.plusWeeks(1)
                }

                var targetDateTime = LocalDateTime.of(targetDate, parsedTime)
                // If the time has already passed today and no specific date prefix was given, default to next occurrence (or 1 min from now)
                if (targetDateTime.isBefore(now)) {
                    targetDateTime = if (!lower.contains("today")) {
                        targetDateTime.plusDays(1)
                    } else {
                        // If marked "Today" but in the past, trigger shortly for testing/demo
                        now.plusSeconds(10)
                    }
                }
                targetDateTime.atZone(zone).toInstant().toEpochMilli()
            }
        }
    }

    /**
     * Schedules a local notification alert using AlarmManager when a task's due date and time is reached.
     */
    fun scheduleTaskAlert(
        context: Context,
        taskId: String,
        title: String,
        notes: String = "",
        timeStr: String
    ) {
        try {
            createNotificationChannel(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val triggerMillis = calculateTriggerTimeMillis(timeStr)

            val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
                action = "com.example.action.TASK_REMINDER_$taskId"
                putExtra(TaskNotificationReceiver.EXTRA_TASK_ID, taskId)
                putExtra(TaskNotificationReceiver.EXTRA_TASK_TITLE, title)
                putExtra(TaskNotificationReceiver.EXTRA_TASK_NOTES, notes)
                putExtra(TaskNotificationReceiver.EXTRA_TASK_TIME, timeStr)
            }

            val requestCode = taskId.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } catch (_: SecurityException) {
                    // Fallback to inexact window if exact alarm permission is restricted
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
            Log.d("TaskScheduler", "Scheduled notification alert for task '$title' at $triggerMillis ms")
        } catch (e: Exception) {
            Log.e("TaskScheduler", "Error scheduling task alert: ${e.message}")
        }
    }

    /**
     * Cancels an existing scheduled notification alert for a task.
     */
    fun cancelTaskAlert(context: Context, taskId: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
                action = "com.example.action.TASK_REMINDER_$taskId"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        } catch (e: Exception) {
            Log.e("TaskScheduler", "Error canceling task alert: ${e.message}")
        }
    }

    /**
     * Immediately triggers and displays the native Android notification for a task.
     */
    fun showNotification(
        context: Context,
        taskId: String,
        title: String,
        notes: String = "",
        time: String = ""
    ) {
        try {
            createNotificationChannel(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Launch MainActivity when user taps on the notification
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("NAVIGATE_TO", "tasks")
                putExtra("TASK_ID", taskId)
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                taskId.hashCode(),
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val contentText = when {
                notes.isNotBlank() && time.isNotBlank() -> "Due at $time • $notes"
                notes.isNotBlank() -> notes
                time.isNotBlank() -> "Task due at $time"
                else -> "Your scheduled task is due now!"
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Task Due: $title")
                .setContentText(contentText)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(if (notes.isNotBlank()) "Due: $time\n\nNotes:\n$notes" else "Due at $time\n\nYour task is ready for action.")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)

            notificationManager.notify(taskId.hashCode(), builder.build())
        } catch (e: Exception) {
            Log.e("TaskNotification", "Failed to dispatch notification: ${e.message}")
        }
    }
}
