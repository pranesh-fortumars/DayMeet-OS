package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DayMeetBottomDock
import com.example.ui.components.DayMeetHeader
import com.example.ui.screens.*
import com.example.ui.theme.InverseOnSurface
import com.example.ui.theme.InverseSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DayMeetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DayMeetApp()
            }
        }
    }
}

@Composable
fun DayMeetApp(
    viewModel: DayMeetViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showMeetingMinutes by viewModel.showMeetingMinutes.collectAsState()
    val showAiAssistant by viewModel.showAiAssistant.collectAsState()
    val showCreateSheet by viewModel.showCreateSheet.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Handle back button on sub-screens
    BackHandler(enabled = showMeetingMinutes || showAiAssistant) {
        if (showMeetingMinutes) viewModel.closeMeetingMinutes()
        else if (showAiAssistant) viewModel.closeAiAssistant()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!showMeetingMinutes && !showAiAssistant) {
                DayMeetHeader(
                    onSearchClick = {
                        viewModel.navigateTo("meetings")
                    },
                    onNotificationsClick = {
                        viewModel.showToast("No new notifications")
                    },
                    onProfileClick = {
                        viewModel.showToast("Alex Chen • Product Lead")
                    },
                    onAiClick = {
                        viewModel.openAiAssistant()
                    }
                )
            }
        },
        bottomBar = {
            if (!showMeetingMinutes && !showAiAssistant) {
                DayMeetBottomDock(
                    currentScreen = currentScreen,
                    onTabSelected = { screen -> viewModel.navigateTo(screen) },
                    onCreateClick = { viewModel.openCreateTask() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // Main tabs transition
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "calendar" -> CalendarScreen(viewModel = viewModel)
                    "tasks" -> TasksScreen(viewModel = viewModel)
                    "meetings" -> MeetingsScreen(viewModel = viewModel)
                    "finance" -> FinanceScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
            }

            // Meeting Minutes Subscreen Overlay
            AnimatedVisibility(
                visible = showMeetingMinutes,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut()
            ) {
                MeetingMinutesScreen(viewModel = viewModel)
            }

            // AI Assistant Subscreen Overlay
            AnimatedVisibility(
                visible = showAiAssistant,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                AiAssistantScreen(viewModel = viewModel)
            }

            // Toast Alert Banner
            toastMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = InverseSurface.copy(alpha = 0.92f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .testTag("app_toast_banner")
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = InverseOnSurface,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }

            // Create Task Bottom Sheet Modal
            if (showCreateSheet) {
                CreateTaskSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeCreateTask() }
                )
            }
        }
    }
}
