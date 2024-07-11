package com.chaddyt50.pomodoro.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.chaddyt50.pomodoro.model.Timer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun TimeDurationDisplay(
    focusUntilTimeInMilliseconds: Long,
    focusTimer: Timer
) {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(focusTimer.timeLeftInMilliseconds.value)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(
            focusTimer.timeLeftInMilliseconds.value - TimeUnit.MINUTES.toMillis(
                minutes
            )
        )

    val dateFormatter = SimpleDateFormat("HH:mm")
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = focusUntilTimeInMilliseconds

    if (!focusTimer.isActive.value) {
        Text(
            "Focus until",
            fontSize = 30.sp
        )
        Text(
            dateFormatter.format(calendar.time),
            fontSize = 50.sp
        )
    } else {
        Text(
            "${minutes}:${seconds.toString().padStart(2, '0')}",
            fontSize = 50.sp,
        )
    }
}