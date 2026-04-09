package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
internal fun SegmentCount(uiState: TimerUiState) {
    Text(
        "${uiState.currentSegmentIndex} of ${uiState.sessionSegmentCount}",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}