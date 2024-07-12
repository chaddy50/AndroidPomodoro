package com.chaddyt50.pomodoro.component

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun TimeDurationDisplay(
    context: Context,
    focusUntilTimeInMilliseconds: Long,
    timeLeftInMilliseconds: Long,
    isFocusTimerActive: Boolean
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
    } else {
        Text(
            "${minutes}:${seconds.toString().padStart(2, '0')}",
            fontSize = 75.sp,
        )
    }
}