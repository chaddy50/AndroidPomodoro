package com.chaddyt50.pomodoro.component

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun FocusTimer(
    context: Context,
    focusUntilTimeInMilliseconds: Long,
    timeLeftInMilliseconds: Long,
    isFocusTimerActive: Boolean,
    startFocusTimer: ()->Unit
) {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMilliseconds)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(
            timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(
                minutes
            )
        )

    val dateFormatter = android.text.format.DateFormat.getTimeFormat(context)
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = focusUntilTimeInMilliseconds

    if (!isFocusTimerActive) {
        Text(
            "Focus until",
            fontSize = 30.sp
        )
        Text(
            dateFormatter.format(calendar.time),
            fontSize = 75.sp
        )
        Button(
            onClick = {
                startFocusTimer()
            }
        ) {
            Text("Start Timer")
        }
    } else {
        (context as Activity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        context.actionBar?.hide()

        Text(
            "${minutes}:${seconds.toString().padStart(2, '0')}",
            fontSize = 75.sp,
        )
    }
}