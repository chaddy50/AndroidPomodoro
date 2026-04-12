package com.chaddy50.pomodoro.ui.screens.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.ui.composables.TimePickerDialog
import com.chaddy50.pomodoro.ui.screens.focusScreen.formatTimeForDisplay
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerUiState

@Composable
internal fun IdleSessionDisplay(
    uiState: TimerUiState,
    onStartFocus: () -> Unit,
    onSetSessionEndTime: (Long) -> Unit,
) {
    val context = LocalContext.current
    var shouldShowTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Focus until", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { shouldShowTimePicker = true },
        ) {
            Text(
                text = formatTimeForDisplay(context, uiState.sessionEndTimeInMilliseconds),
                fontSize = 52.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit session end time",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartFocus,
            enabled = uiState.sessionSegmentCount > 0,
        ) {
            Text("Start Focus")
        }
    }

    if (shouldShowTimePicker) {
        TimePickerDialog(
            uiState.sessionEndTimeInMilliseconds,
            onSetSessionEndTime,
            { shouldShowTimePicker = false },
        )
    }
}