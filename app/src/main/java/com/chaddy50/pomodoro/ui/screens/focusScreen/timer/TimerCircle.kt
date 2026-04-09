package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TimerCircle(uiState: TimerUiState) {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = {
                if (uiState.timerLengthInMilliseconds == 0L) 0f
                else 1f - (uiState.timeLeftInMilliseconds.toFloat() / uiState.timerLengthInMilliseconds.toFloat())
            },
            modifier = Modifier.matchParentSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 16.dp,
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            strokeCap = StrokeCap.Round,
        )
        Text(
            getTimerDisplayText(uiState),
            fontSize = 52.sp,
        )
    }
}