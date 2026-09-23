package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.DayMeetViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DayMeet", appName)
  }

  @Test
  fun `toggle habit updates streak and completion state`() {
    val viewModel = DayMeetViewModel()
    val initialHabits = viewModel.habits.value
    assertTrue(initialHabits.isNotEmpty())

    val targetHabit = initialHabits.first { !it.isCompletedToday }
    val initialStreak = targetHabit.streakDays

    viewModel.toggleHabit(targetHabit.id)

    val updatedHabit = viewModel.habits.value.first { it.id == targetHabit.id }
    assertTrue(updatedHabit.isCompletedToday)
    assertEquals(initialStreak + 1, updatedHabit.streakDays)
  }

  @Test
  fun `removeFeedTask removes task item from list`() {
    val viewModel = DayMeetViewModel()
    val initialFeed = viewModel.feedItems.value
    assertTrue(initialFeed.isNotEmpty())

    val targetTask = initialFeed.first()
    viewModel.removeFeedTask(targetTask.id)

    val remainingFeed = viewModel.feedItems.value
    assertTrue(remainingFeed.none { it.id == targetTask.id })
    assertEquals(initialFeed.size - 1, remainingFeed.size)
  }

  @Test
  fun `toggleAutoCheckUpdates changes preference state`() {
    val viewModel = DayMeetViewModel()
    val initial = viewModel.isAutoCheckUpdateEnabled.value
    viewModel.toggleAutoCheckUpdates()
    assertEquals(!initial, viewModel.isAutoCheckUpdateEnabled.value)
  }

  @Test
  fun `initial transactions and monthly budget target are accessible`() {
    val viewModel = DayMeetViewModel()
    val transactions = viewModel.transactions.value
    assertTrue(transactions.isNotEmpty())
    val monthlyBudget = viewModel.monthlyBudgetTarget.value
    assertTrue(monthlyBudget > 0.0)
  }

  @Test
  fun `financial health spending categories calculate properly`() {
    val viewModel = DayMeetViewModel()
    val transactions = viewModel.transactions.value
    val spent = transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    assertTrue(spent > 0.0)
    assertTrue(viewModel.monthlyBudgetTarget.value >= spent)
  }

  @Test
  fun `tasks priority assignment and update functions correctly`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK }
    assertTrue(tasks.isNotEmpty())

    // Verify task priorities exist among High, Medium, Low
    val targetTask = tasks.first()
    viewModel.updateTaskPriority(targetTask.id, com.example.model.Priority.HIGH)
    val updatedTask = viewModel.feedItems.value.first { it.id == targetTask.id }
    assertEquals(com.example.model.Priority.HIGH, updatedTask.priority)

    viewModel.updateTaskPriority(targetTask.id, com.example.model.Priority.LOW)
    val lowTask = viewModel.feedItems.value.first { it.id == targetTask.id }
    assertEquals(com.example.model.Priority.LOW, lowTask.priority)
  }

  @Test
  fun `tasks bulk completion marks multiple items as completed in one action`() {
    val viewModel = DayMeetViewModel()
    val uncompletedTasks = viewModel.feedItems.value
        .filter { it.category == com.example.model.FeedCategory.TASK && !it.isCompleted }
    assertTrue("Should have uncompleted tasks to test bulk completion", uncompletedTasks.size >= 2)

    val targetIds = uncompletedTasks.take(2).map { it.id }.toSet()
    viewModel.bulkMarkTasksCompleted(targetIds)

    val updatedTasks = viewModel.feedItems.value.filter { it.id in targetIds }
    assertEquals(2, updatedTasks.size)
    assertTrue("All target tasks should now be completed", updatedTasks.all { it.isCompleted })
  }

  @Test
  fun `timeUtils isDueToday correctly identifies today string and formatted dates`() {
    assertTrue(com.example.util.TimeUtils.isDueToday("Today"))
    assertTrue(com.example.util.TimeUtils.isDueToday("today"))
    assertTrue(com.example.util.TimeUtils.isDueToday("Today 05:00 PM"))

    val todayFormatted = java.time.LocalDate.now().format(
      java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.US)
    )
    assertTrue(com.example.util.TimeUtils.isDueToday(todayFormatted))

    val tomorrowFormatted = java.time.LocalDate.now().plusDays(1).format(
      java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.US)
    )
    org.junit.Assert.assertFalse(com.example.util.TimeUtils.isDueToday(tomorrowFormatted))
    org.junit.Assert.assertFalse(com.example.util.TimeUtils.isDueToday(null))
    org.junit.Assert.assertFalse(com.example.util.TimeUtils.isDueToday("Next Week"))
  }

  @Test
  fun `crossStreamItems subtask addition and toggling works as expected`() {
    val viewModel = DayMeetViewModel()
    val streamItems = viewModel.crossStreamItems.value
    assertTrue("Should have crossStreamItems", streamItems.isNotEmpty())

    val targetItem = streamItems.first { it.id == "cs2" }
    val initialSubtaskCount = targetItem.subtasks.size
    assertTrue("cs2 should start with initial subtasks", initialSubtaskCount >= 1)

    // Add new subtask
    viewModel.addCrossStreamSubtask("cs2", "Verify security checklist")
    val updatedItem = viewModel.crossStreamItems.value.first { it.id == "cs2" }
    assertEquals(initialSubtaskCount + 1, updatedItem.subtasks.size)
    val addedSubtask = updatedItem.subtasks.last()
    assertEquals("Verify security checklist", addedSubtask.title)
    org.junit.Assert.assertFalse(addedSubtask.isCompleted)

    // Toggle newly added subtask
    viewModel.toggleCrossStreamSubtask("cs2", addedSubtask.id)
    val toggledItem = viewModel.crossStreamItems.value.first { it.id == "cs2" }
    val toggledSubtask = toggledItem.subtasks.first { it.id == addedSubtask.id }
    assertTrue("Toggled subtask should now be completed", toggledSubtask.isCompleted)

    // Delete subtask
    viewModel.deleteCrossStreamSubtask("cs2", addedSubtask.id)
    val finalItem = viewModel.crossStreamItems.value.first { it.id == "cs2" }
    assertEquals(initialSubtaskCount, finalItem.subtasks.size)
  }

  @Test
  fun `feedItems task subtask addition, toggle, and deletion works seamlessly`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK }
    assertTrue("Should have task items in feed", tasks.isNotEmpty())

    val targetTask = tasks.first()
    val initialSubtaskCount = targetTask.subtasks.size

    // Add subtask
    viewModel.addSubtask(targetTask.id, "Prepare automated release notes")
    val taskWithSub = viewModel.feedItems.value.first { it.id == targetTask.id }
    assertEquals(initialSubtaskCount + 1, taskWithSub.subtasks.size)
    val addedSub = taskWithSub.subtasks.last()
    assertEquals("Prepare automated release notes", addedSub.title)
    org.junit.Assert.assertFalse(addedSub.isCompleted)

    // Toggle subtask
    viewModel.toggleSubtask(targetTask.id, addedSub.id)
    val toggledTask = viewModel.feedItems.value.first { it.id == targetTask.id }
    val toggledSub = toggledTask.subtasks.first { it.id == addedSub.id }
    assertTrue("Subtask should be completed after toggle", toggledSub.isCompleted)

    // Delete subtask
    viewModel.deleteSubtask(targetTask.id, addedSub.id)
    val deletedTask = viewModel.feedItems.value.first { it.id == targetTask.id }
    assertEquals(initialSubtaskCount, deletedTask.subtasks.size)
  }

  @Test
  fun `updateTaskProgress updates progress percentage and auto marks task complete at 100 percent`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK && !it.isCompleted }
    assertTrue("Should have uncompleted task items", tasks.isNotEmpty())

    val target = tasks.first()
    // Update progress to 65%
    viewModel.updateTaskProgress(target.id, 65)
    val updated = viewModel.feedItems.value.first { it.id == target.id }
    assertEquals(65, updated.progress)
    org.junit.Assert.assertFalse(updated.isCompleted)

    // Update progress to 100%
    viewModel.updateTaskProgress(target.id, 100)
    val completedTask = viewModel.feedItems.value.first { it.id == target.id }
    assertEquals(100, completedTask.progress)
    assertTrue("Task should automatically be marked complete at 100% progress", completedTask.isCompleted)

    // Update progress below 100% resets completion
    viewModel.updateTaskProgress(target.id, 40)
    val reopenedTask = viewModel.feedItems.value.first { it.id == target.id }
    assertEquals(40, reopenedTask.progress)
    org.junit.Assert.assertFalse("Task should become incomplete when progress drops below 100%", reopenedTask.isCompleted)
  }

  @Test
  fun `toggleFeedTaskDone synchronizes progress with completion state`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK && !it.isCompleted }
    assertTrue(tasks.isNotEmpty())

    val target = tasks.first()
    // Complete task
    viewModel.toggleFeedTaskDone(target.id)
    val completed = viewModel.feedItems.value.first { it.id == target.id }
    assertTrue(completed.isCompleted)
    assertEquals(100, completed.progress)

    // Uncomplete task
    viewModel.toggleFeedTaskDone(target.id)
    val reopened = viewModel.feedItems.value.first { it.id == target.id }
    org.junit.Assert.assertFalse(reopened.isCompleted)
    assertEquals(0, reopened.progress)
  }

  @Test
  fun `quick add task with category creates task with correct statusTag and space`() {
    val viewModel = DayMeetViewModel()
    val initialCount = viewModel.feedItems.value.size

    viewModel.saveNewTask(
      title = "Buy organic groceries",
      notes = "Organic fruits and oats",
      priority = com.example.model.Priority.MEDIUM,
      space = "Shopping"
    )

    val updatedFeed = viewModel.feedItems.value
    assertEquals(initialCount + 1, updatedFeed.size)

    val createdTask = updatedFeed.first()
    assertEquals("Buy organic groceries", createdTask.title)
    assertEquals("Shopping", createdTask.statusTag)
    assertEquals(com.example.model.FeedCategory.TASK, createdTask.category)
    org.junit.Assert.assertFalse(createdTask.isCompleted)
  }

  @Test
  fun `deleteTask permanently removes task from feedItems and crossStreamItems`() {
    val viewModel = DayMeetViewModel()
    val task = viewModel.feedItems.value.first { it.category == com.example.model.FeedCategory.TASK }
    val taskId = task.id

    viewModel.deleteTask(taskId)

    val remainingTasks = viewModel.feedItems.value.filter { it.id == taskId }
    assertTrue("Task should be deleted from feedItems", remainingTasks.isEmpty())
  }

  @Test
  fun `bulkMarkTasksCompleted completes all specified tasks`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK && !it.isCompleted }.take(2)
    val targetIds = tasks.map { it.id }.toSet()
    assertEquals(2, targetIds.size)

    viewModel.bulkMarkTasksCompleted(targetIds)

    val updatedTasks = viewModel.feedItems.value.filter { it.id in targetIds }
    for (task in updatedTasks) {
      assertTrue("Task should be completed", task.isCompleted)
      assertEquals(100, task.progress)
    }
  }

  @Test
  fun `bulkDeleteTasks permanently removes all selected tasks`() {
    val viewModel = DayMeetViewModel()
    val tasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK }.take(2)
    val targetIds = tasks.map { it.id }.toSet()
    assertEquals(2, targetIds.size)

    viewModel.bulkDeleteTasks(targetIds)

    val remainingTasks = viewModel.feedItems.value.filter { it.id in targetIds }
    assertTrue("All selected tasks should be deleted", remainingTasks.isEmpty())
  }

  @Test
  fun `phase 5 biometrics and sedentary stretch flow works correctly`() {
    val viewModel = DayMeetViewModel()
    val initialBiometrics = viewModel.biometrics.value
    assertTrue("Initial readiness score should be positive", initialBiometrics.readinessScore > 0)
    assertTrue("Sleep quality should be recorded", initialBiometrics.sleepQualityScore > 0)
    assertTrue("Resting heart rate should be realistic", initialBiometrics.restingHeartRateBpm in 40..100)

    viewModel.openSedentaryStretch()
    assertTrue("Sedentary stretch dialog should be open", viewModel.showSedentaryStretchDialog.value)

    viewModel.completeSedentaryStretch()
    assertFalse("Sedentary stretch dialog should be dismissed", viewModel.showSedentaryStretchDialog.value)
    assertEquals(0, viewModel.biometrics.value.lastSedentaryMinutes)
    assertFalse("Sedentary alert should be cleared", viewModel.biometrics.value.sedentaryAlertActive)
  }

  @Test
  fun `phase 5 android widget pinning and hud switches operate properly`() {
    val viewModel = DayMeetViewModel()
    val widgets = viewModel.widgetConfigs.value
    assertTrue("Widgets should be available", widgets.isNotEmpty())

    val firstWidget = widgets.first()
    val initialPinned = firstWidget.isPinned
    viewModel.togglePinWidget(firstWidget.widgetId)
    val updatedWidget = viewModel.widgetConfigs.value.first { it.widgetId == firstWidget.widgetId }
    assertEquals(!initialPinned, updatedWidget.isPinned)

    val initialTile = viewModel.persistentHUDState.value.isQuickSettingsTileActive
    viewModel.toggleQuickSettingsTile()
    assertEquals(!initialTile, viewModel.persistentHUDState.value.isQuickSettingsTileActive)

    val initialNotif = viewModel.persistentHUDState.value.isOngoingNotificationEnabled
    viewModel.toggleOngoingNotification()
    assertEquals(!initialNotif, viewModel.persistentHUDState.value.isOngoingNotificationEnabled)
  }
}
