package com.chaddy50.pomodoro.ui.screens.timerScreen

import android.content.Context
import android.content.res.Configuration
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.media.MusicUiState
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Preview
@Composable
private fun TimerDisplayPreview() {
    TimerDisplay(
        uiState = TimerUiState(
            timeLeftInMilliseconds = TimeUnit.MINUTES.toMillis(25),
            timerLengthInMilliseconds = TimeUnit.MINUTES.toMillis(25),
            focusUntilTimeInMilliseconds = Calendar.getInstance().timeInMillis,
        ),
        musicUiState = MusicUiState(),
        onStartTimer = {},
        onStopTimer = {},
        onPlayPause = {},
        onNext = {},
        onPrevious = {},
    )
}

@Composable
fun TimerDisplay(
    uiState: TimerUiState,
    musicUiState: MusicUiState,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            TimerSection(
                uiState = uiState,
                onStartTimer = onStartTimer,
                onStopTimer = onStopTimer,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            MusicControlsSection(
                uiState = musicUiState,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            TimerSection(
                uiState = uiState,
                onStartTimer = onStartTimer,
                onStopTimer = onStopTimer,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            MusicControlsSection(
                uiState = musicUiState,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TimerSection(
    uiState: TimerUiState,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val timerLabel = getTimerLabel(uiState.timerType, uiState.isTimerActive)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (timerLabel.isNotEmpty()) {
            Text(timerLabel, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

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
                getTimerDisplay(context, uiState),
                fontSize = 52.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledIconButton(
            onClick = if (uiState.isTimerActive) onStopTimer else onStartTimer,
            modifier = Modifier
                .height(60.dp)
                .widthIn(60.dp),
        ) {
            Icon(
                imageVector = getTimerButtonIcon(uiState.isTimerActive),
                contentDescription = "Start timer",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

//#region Private Functions
private fun getTimerLabel(
    timerType: TimerType,
    isTimerActive: Boolean,
): String {
    return when (timerType) {
        TimerType.FocusUntil -> if (isTimerActive) "" else "Focus until"
        else -> "Take a break"
    }
}

private fun getTimerDisplay(context: Context, uiState: TimerUiState): String {
    if (uiState.timerType == TimerType.FocusUntil && !uiState.isTimerActive) {
        return formatTimeForDisplay(context, uiState.focusUntilTimeInMilliseconds)
    } else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uiState.timeLeftInMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(uiState.timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        return "${minutes}:${seconds.toString().padStart(2, '0')}"
    }
}

private fun getTimerButtonIcon(isTimerActive: Boolean): ImageVector {
    return if (isTimerActive) Icons.Filled.Close else Icons.Filled.PlayArrow
}

private fun formatTimeForDisplay(context: Context, timeInMilliseconds: Long): String {
    val dateFormatter = DateFormat.getTimeFormat(context)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMilliseconds
    return dateFormatter.format(calendar.time)
}
//#endregion
