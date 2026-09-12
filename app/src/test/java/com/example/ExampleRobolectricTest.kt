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
}
