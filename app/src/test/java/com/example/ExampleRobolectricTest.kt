package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.DayMeetViewModel
import org.junit.Assert.assertEquals
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
}
