package com.chaddy50.pomodoro.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    time: Long,
    updateTime: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val sessionEndCalendar = Calendar.getInstance().apply {
        timeInMillis = time
    }
    val timePickerState = rememberTimePickerState(
        initialHour = sessionEndCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = sessionEndCalendar.get(Calendar.MINUTE),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val endTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= Calendar.getInstance().timeInMillis) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                updateTime(endTime.timeInMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}