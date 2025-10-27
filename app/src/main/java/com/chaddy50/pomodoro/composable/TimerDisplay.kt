package com.chaddy50.pomodoro.composable

import android.app.Activity
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel
import com.chaddy50.pomodoro.viewmodel.TimerMode
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun TimerDisplay(
    context: Context,
    viewModel: PomodoroViewModel
) {
    val isTimerActive = viewModel.isTimerActive.collectAsState()
    val timeLeftInMilliseconds = viewModel.timeLeftInMilliseconds.collectAsState()
    val timerLengthInMilliseconds = viewModel.timerLengthInMilliseconds.collectAsState()
    val timerMode = viewModel.timerMode.collectAsState()

    if (!isTimerActive.value) {
        Text(
            getTimerLabel(timerMode.value),
            fontSize = 30.sp
        )
    }

    Text(
        getTimerDisplay(
            context,
            isTimerActive.value,
            timerMode.value,
            timeLeftInMilliseconds.value,
            timerLengthInMilliseconds.value
        ),
        fontSize = 75.sp
    )

    if (!isTimerActive.value) {
        Button(
            onClick = {
                viewModel.startTimer()
            }
        ) {
            Text(
                getTimerButtonLabel(timerMode.value),
                fontSize = 30.sp
            )
        }
    }

    when (isTimerActive.value) {
        true -> hideStatusBar(context)
        false -> showStatusBar(context)
    }
}

//#region Private Functions
private fun getTimerLabel(timerMode: TimerMode): String {
    return when (timerMode) {
        TimerMode.FocusUntil -> "Focus Until"
        TimerMode.Break -> "Take a break"
    }
}

private fun getTimerDisplay(
    context: Context,
    isTimerActive: Boolean,
    timerMode: TimerMode,
    timeLeftInMilliseconds: Long,
    timerLengthInMilliseconds: Long
): String {
    if ((timerMode == TimerMode.FocusUntil) && !isTimerActive) {
        val dateFormatter = DateFormat.getTimeFormat(context)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis += timerLengthInMilliseconds
        return dateFormatter.format(calendar.time)
    }
    else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        return "${minutes}:${seconds.toString().padStart(2, '0')}"
    }
}

private fun getTimerButtonLabel(timerMode: TimerMode): String {
    return when (timerMode) {
        TimerMode.FocusUntil -> "Focus"
        TimerMode.Break -> "Start Break"
    }
}

private fun hideStatusBar(context: Context) {
    (context as Activity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    // Remember that you should never show the action bar if the
    // status bar is hidden, so hide that too if necessary.
    context.actionBar?.hide()
}

private fun showStatusBar(context: Context) {
    (context as Activity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    // Remember that you should never show the action bar if the
    // status bar is hidden, so hide that too if necessary.
    context.actionBar?.show()
}
//#endregion