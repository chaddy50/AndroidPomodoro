package com.chaddy50.pomodoro.ui.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType
import com.chaddy50.pomodoro.ui.screens.focusScreen.formatTimeForDisplay
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerUiState
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.getTimerDisplayText

@Composable
internal fun ActiveSessionDisplay(uiState: TimerUiState, onReturnToFocus: () -> Unit) {
    val context = LocalContext.current

    val statusLabel = when (uiState.timerType) {
        TimerType.FocusUntil -> "Focusing"
        TimerType.ShortBreak, TimerType.LongBreak -> "On a break"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(statusLabel, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(getTimerDisplayText(uiState), fontSize = 52.sp)

        if (uiState.sessionSegmentCount > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Block ${uiState.currentSegmentIndex} of ${uiState.sessionSegmentCount}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Session ends at ${formatTimeForDisplay(context, uiState.sessionEndTimeInMilliseconds)}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onReturnToFocus) {
            Text("Return to Focus")
        }
    }
}