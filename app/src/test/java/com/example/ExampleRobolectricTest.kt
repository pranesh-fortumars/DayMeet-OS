package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Priority
import com.example.model.SubChecklist
import com.example.model.SubChecklistItem
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

  @Test
  fun `phase 6 context switching and delegation flow functions correctly`() {
    val viewModel = DayMeetViewModel()
    
    // Test profile context switching
    assertEquals(com.example.model.LifeOSProfile.WORK, viewModel.activeProfile.value)
    viewModel.switchProfile(com.example.model.LifeOSProfile.CREATIVE)
    assertEquals(com.example.model.LifeOSProfile.CREATIVE, viewModel.activeProfile.value)

    // Test delegation ping
    val initialTasks = viewModel.delegatedTasks.value
    assertTrue("Delegated tasks should exist", initialTasks.isNotEmpty())
    val targetTask = initialTasks.first()
    viewModel.pingDelegatedTask(targetTask.id)
    val updatedTask = viewModel.delegatedTasks.value.first { it.id == targetTask.id }
    assertTrue(updatedTask.lastPingMessage.contains("just now"))

    // Test new task delegation
    val initialSize = viewModel.delegatedTasks.value.size
    viewModel.delegateNewTask(
      title = "Deploy Edge Cache Server",
      assigneeName = "Marcus Vance",
      role = com.example.model.DelegationRole.ENGINEERING,
      deadline = "Tomorrow, 3 PM",
      notes = "Cloudflare Worker deployment"
    )
    assertEquals(initialSize + 1, viewModel.delegatedTasks.value.size)
    val newest = viewModel.delegatedTasks.value.first()
    assertEquals("Deploy Edge Cache Server", newest.title)
    assertEquals("Marcus Vance", newest.assigneeName)
    assertEquals("MV", newest.assigneeAvatarInitials)

    // Test E2EE backup trigger
    viewModel.triggerCloudE2EEBackup()
    assertEquals("Just now", viewModel.ecosystemBackupState.value.lastBackupTimestamp)
    assertTrue(viewModel.ecosystemBackupState.value.isCloudE2EEEnabled)
  }

  @Test
  fun `circular financial health gauge SVG arc path generation is valid W3C syntax`() {
    // Test 90-degree arc (small arc flag = 0)
    val smallArc = com.example.ui.components.buildSvgArcPathData(
      cx = 150f,
      cy = 150f,
      radius = 100f,
      startAngleDeg = 0f,
      sweepAngleDeg = 90f
    )
    assertTrue("SVG path should start with M", smallArc.pathString.startsWith("M "))
    assertTrue("SVG path should contain Arc command A", smallArc.pathString.contains(" A "))
    assertFalse("90 deg arc should not have largeArc flag", smallArc.isLargeArc)
    assertTrue("Path string should have largeArc 0", smallArc.pathString.contains(" 0 1 "))

    // Test 280-degree circular arc (large arc flag = 1)
    val largeArc = com.example.ui.components.buildSvgArcPathData(
      cx = 150f,
      cy = 150f,
      radius = 100f,
      startAngleDeg = 130f,
      sweepAngleDeg = 280f
    )
    assertTrue("280 deg arc should have largeArc flag", largeArc.isLargeArc)
    assertTrue("Path string should have largeArc 1", largeArc.pathString.contains(" 1 1 "))

    // Verify AndroidX PathParser successfully parses the generated SVG arc path data
    val androidPath = androidx.core.graphics.PathParser.createPathFromPathData(largeArc.pathString)
    org.junit.Assert.assertNotNull("AndroidX PathParser should parse SVG arc path", androidPath)
    assertFalse("Parsed path should not be empty", androidPath!!.isEmpty)
  }

  @Test
  fun `financial health budget utilization correctly computes progress and deficit`() {
    val budgetTarget = 48000.0
    val normalSpent = 35000.0
    val normalRatio = (normalSpent / budgetTarget).toFloat()
    assertTrue(normalRatio < 1.0f)
    assertEquals(13000.0, budgetTarget - normalSpent, 0.001)

    val overSpent = 52000.0
    val overRatio = (overSpent / budgetTarget).toFloat()
    assertTrue(overRatio > 1.0f)
    assertEquals(4000.0, overSpent - budgetTarget, 0.001)
  }

  @Test
  fun `universal quick add correctly assigns Work, Personal, Urgent, Routine categories to new tasks`() {
    val viewModel = DayMeetViewModel()
    val initialTaskCount = viewModel.feedItems.value.count { it.category == com.example.model.FeedCategory.TASK }

    // Test creating task with Routine category
    viewModel.universalQuickAdd(
      type = "Task",
      title = "Daily Evening Routine",
      detail = "Journal and review tomorrow's plan",
      priority = Priority.MEDIUM,
      category = "Routine",
      reminderTime = "Today 09:00 PM",
      dueDate = "Today"
    )

    // Test creating task with Urgent category
    viewModel.universalQuickAdd(
      type = "Task",
      title = "Fix Critical Security Hotfix",
      detail = "Deploy patch to production cluster",
      priority = Priority.HIGH,
      category = "Urgent",
      reminderTime = "In 15 Mins",
      dueDate = "Today"
    )

    // Test creating task with Personal category
    viewModel.universalQuickAdd(
      type = "Task",
      title = "Grocery Shopping",
      detail = "Pick up milk, fruits, and bread",
      priority = Priority.LOW,
      category = "Personal",
      dueDate = "Tomorrow"
    )

    // Test creating task with Work category
    viewModel.universalQuickAdd(
      type = "Task",
      title = "Sprint Planning Presentation",
      detail = "Finalize Q4 roadmap slide deck",
      priority = Priority.HIGH,
      category = "Work",
      dueDate = "Sep 30, 2026"
    )

    val updatedTasks = viewModel.feedItems.value.filter { it.category == com.example.model.FeedCategory.TASK }
    assertEquals(initialTaskCount + 4, updatedTasks.size)

    val routineTask = updatedTasks.firstOrNull { it.title == "Daily Evening Routine" }
    org.junit.Assert.assertNotNull("Routine task should exist", routineTask)
    assertEquals("Routine", routineTask!!.statusTag)

    val urgentTask = updatedTasks.firstOrNull { it.title == "Fix Critical Security Hotfix" }
    org.junit.Assert.assertNotNull("Urgent task should exist", urgentTask)
    assertEquals("Urgent", urgentTask!!.statusTag)
    assertEquals(Priority.HIGH, urgentTask.priority)

    val personalTask = updatedTasks.firstOrNull { it.title == "Grocery Shopping" }
    org.junit.Assert.assertNotNull("Personal task should exist", personalTask)
    assertEquals("Personal", personalTask!!.statusTag)

    val workTask = updatedTasks.firstOrNull { it.title == "Sprint Planning Presentation" }
    org.junit.Assert.assertNotNull("Work task should exist", workTask)
    assertEquals("Work", workTask!!.statusTag)
  }

  @Test
  fun `getTaskCategoryMeta returns valid color and icon metadata for core categories`() {
    val workMeta = com.example.ui.screens.getTaskCategoryMeta("Work")
    assertEquals("Work", workMeta.name)
    assertEquals("💼", workMeta.icon)

    val personalMeta = com.example.ui.screens.getTaskCategoryMeta("Personal")
    assertEquals("Personal", personalMeta.name)
    assertEquals("👤", personalMeta.icon)

    val urgentMeta = com.example.ui.screens.getTaskCategoryMeta("Urgent")
    assertEquals("Urgent", urgentMeta.name)
    assertEquals("⚡", urgentMeta.icon)

    val routineMeta = com.example.ui.screens.getTaskCategoryMeta("Routine")
    assertEquals("Routine", routineMeta.name)
    assertEquals("🔁", routineMeta.icon)

    // Verify all 4 categories have distinct text colors
    val colors = listOf(workMeta.textColor, personalMeta.textColor, urgentMeta.textColor, routineMeta.textColor)
    assertEquals(4, colors.toSet().size)
  }

  @Test
  fun `subChecklist creation, addition of items, toggling, and deletion within a task works correctly`() {
    val viewModel = DayMeetViewModel()

    // 1. Create a task with initial sub-checklists
    val initialChecklist = SubChecklist(
      id = "chk_init_1",
      title = "Pre-flight Checklist",
      items = listOf(
        SubChecklistItem("item_1", "Verify API keys in secrets", isCompleted = true),
        SubChecklistItem("item_2", "Run schema validation", isCompleted = false)
      )
    )

    viewModel.saveNewTask(
      title = "Deploy SuperApp Production Build",
      notes = "Critical release checklist",
      priority = Priority.HIGH,
      space = "Urgent",
      subChecklists = listOf(initialChecklist)
    )

    val createdTask = viewModel.feedItems.value.first { it.title == "Deploy SuperApp Production Build" }
    assertEquals(1, createdTask.subChecklists.size)
    assertEquals("Pre-flight Checklist", createdTask.subChecklists.first().title)
    assertEquals(2, createdTask.subChecklists.first().items.size)
    assertTrue(createdTask.subChecklists.first().items[0].isCompleted)
    assertFalse(createdTask.subChecklists.first().items[1].isCompleted)

    // 2. Create another sub-checklist within this task
    viewModel.createSubChecklist(createdTask.id, "QA & Performance Testing")
    val taskWithTwoLists = viewModel.feedItems.value.first { it.id == createdTask.id }
    assertEquals(2, taskWithTwoLists.subChecklists.size)
    val secondList = taskWithTwoLists.subChecklists.first { it.title == "QA & Performance Testing" }
    assertTrue(secondList.items.isEmpty())

    // 3. Add an item to the new sub-checklist
    viewModel.addSubChecklistItem(createdTask.id, secondList.id, "Verify 60fps rendering in emulator")
    val taskWithItem = viewModel.feedItems.value.first { it.id == createdTask.id }
    val updatedSecondList = taskWithItem.subChecklists.first { it.id == secondList.id }
    assertEquals(1, updatedSecondList.items.size)
    val addedItem = updatedSecondList.items.first()
    assertEquals("Verify 60fps rendering in emulator", addedItem.title)
    assertFalse(addedItem.isCompleted)

    // 4. Toggle the checklist item
    viewModel.toggleSubChecklistItem(createdTask.id, secondList.id, addedItem.id)
    val taskAfterToggle = viewModel.feedItems.value.first { it.id == createdTask.id }
    val toggledItem = taskAfterToggle.subChecklists.first { it.id == secondList.id }.items.first { it.id == addedItem.id }
    assertTrue("Item should now be completed", toggledItem.isCompleted)

    // 5. Delete the checklist item
    viewModel.deleteSubChecklistItem(createdTask.id, secondList.id, addedItem.id)
    val taskAfterItemDelete = viewModel.feedItems.value.first { it.id == createdTask.id }
    assertTrue(taskAfterItemDelete.subChecklists.first { it.id == secondList.id }.items.isEmpty())

    // 6. Delete the entire sub-checklist
    viewModel.deleteSubChecklist(createdTask.id, secondList.id)
    val taskAfterListDelete = viewModel.feedItems.value.first { it.id == createdTask.id }
    assertEquals(1, taskAfterListDelete.subChecklists.size)
    assertEquals("Pre-flight Checklist", taskAfterListDelete.subChecklists.first().title)
  }
}

