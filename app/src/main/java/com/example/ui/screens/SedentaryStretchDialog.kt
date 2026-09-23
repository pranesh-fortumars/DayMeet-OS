package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.DayMeetViewModel

/**
 * 30-Second Sedentary Micro-Stretch & Eye Rest Dialog
 */
@Composable
fun SedentaryStretchDialog(
    viewModel: DayMeetViewModel,
    onDismiss: () -> Unit
) {
    var timerSeconds by remember { mutableIntStateOf(30) }

    LaunchedEffect(Unit) {
        while (timerSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            timerSeconds--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            modifier = Modifier.fillMaxWidth().testTag("sedentary_stretch_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circular Timer Gauge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${timerSeconds}s",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    )
                }

                Text(
                    text = "Active Recovery & Eye Rest",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "You've been focused at your desk for over 70 minutes. Take 30 seconds to recharge:",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, lineHeight = 18.sp)
                )

                // 3 Guided Steps
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    StretchStepItem(number = "1", text = "Look 20 feet away at a distant wall or window (relax optic nerve)")
                    StretchStepItem(number = "2", text = "Roll shoulders backward 5 times and clasp hands overhead")
                    StretchStepItem(number = "3", text = "Take 3 deep box breaths (4s in, 4s hold, 4s out)")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.completeSedentaryStretch() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("finish_stretch_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refreshed! Reset Timer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StretchStepItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFD97706),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = number, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall.copy(color = OnSurface, fontSize = 11.sp))
    }
}
