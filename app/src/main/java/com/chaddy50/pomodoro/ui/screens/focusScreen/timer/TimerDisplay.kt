package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerDisplay(
    uiState: TimerUiState,
    modifier: Modifier = Modifier,
) {
    val nextBreakLabel = getNextBreakLabel(uiState)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimerCircle(uiState)
        if (nextBreakLabel.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                nextBreakLabel,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (uiState.sessionSegmentCount > 1) {
            Spacer(modifier = Modifier.height(4.dp))
            SegmentCount(uiState)
        }
    }
}