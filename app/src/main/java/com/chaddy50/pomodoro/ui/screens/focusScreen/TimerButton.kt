package com.chaddy50.pomodoro.ui.screens.focusScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerUiState

@Composable
fun TimerButton(
    uiState: TimerUiState,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FilledIconButton(
            onClick = if (uiState.isTimerActive) onStopTimer else onStartTimer,
            modifier = Modifier.height(60.dp).widthIn(60.dp),
        ) {
            Icon(
                imageVector = if (uiState.isTimerActive) Icons.Filled.Close else Icons.Filled.PlayArrow,
                contentDescription = if (uiState.isTimerActive) "Stop timer" else "Start timer",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}