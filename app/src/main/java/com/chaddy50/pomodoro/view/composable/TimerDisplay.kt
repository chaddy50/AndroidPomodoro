package com.chaddy50.pomodoro.view.composable

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.viewmodel.TimerMode
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Preview
@Composable
private fun TimerDisplayPreview() {
    TimerDisplay(
        LocalContext.current,
        false,
        TimeUnit.MINUTES.toMillis(25),
        TimeUnit.MINUTES.toMillis(25),
        TimerMode.FocusUntil,
        {},
        Calendar.getInstance().timeInMillis
    )
}

@Composable
fun TimerDisplay(
    context: Context,
    isTimerActive: Boolean,
    timeLeftInMilliseconds: Long,
    timerLengthInMilliseconds: Long,
    timerMode: TimerMode,
    startTimer: () -> Unit,
    focusUntilTimeInMilliseconds: Long,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1f - (timeLeftInMilliseconds.toFloat() / timerLengthInMilliseconds.toFloat()) },
                modifier = Modifier
                    .matchParentSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 16.dp,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                strokeCap = StrokeCap.Round,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    getTimerLabel(
                        timerMode,
                        isTimerActive
                    ),
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-60).dp)
                )

                Text(
                    getTimerDisplay(
                        context,
                        isTimerActive,
                        timerMode,
                        timeLeftInMilliseconds,
                        focusUntilTimeInMilliseconds
                    ),
                    fontSize = 75.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        FilledIconButton(
            onClick = startTimer,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp)
                .widthIn(60.dp)
                .offset(y = 50.dp)
        ) {
            Icon(
                imageVector = getTimerButtonIcon(isTimerActive),
                contentDescription = "Start timer",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

//#region Private Functions
private fun getTimerLabel(
    timerMode: TimerMode,
    isTimerActive: Boolean,
): String {
    return when (timerMode) {
        TimerMode.FocusUntil -> {
            return when (isTimerActive) {
                true -> ""
                false -> "Focus until"
            }
        }
        TimerMode.Break -> "Take a break"
    }
}

private fun getTimerDisplay(
    context: Context,
    isTimerActive: Boolean,
    timerMode: TimerMode,
    timeLeftInMilliseconds: Long,
    focusUntilTimeInMilliseconds: Long
): String {
    if ((timerMode == TimerMode.FocusUntil) && !isTimerActive) {
        return formatTimeForDisplay(context, focusUntilTimeInMilliseconds)
    }
    else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        return "${minutes}:${seconds.toString().padStart(2, '0')}"
    }
}

private fun getTimerButtonIcon(
    isTimerActive: Boolean
): ImageVector {
    return when(isTimerActive) {
        true -> Icons.Filled.Close
        false -> Icons.Filled.PlayArrow
    }
}

private fun formatTimeForDisplay(context: Context, timeInMilliseconds: Long): String {
    val dateFormatter = DateFormat.getTimeFormat(context)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMilliseconds
    return dateFormatter.format(calendar.time)
}
//#endregion