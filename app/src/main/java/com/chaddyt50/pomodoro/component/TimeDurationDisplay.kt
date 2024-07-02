package com.chaddyt50.pomodoro.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.chaddyt50.pomodoro.viewmodel.FocusTimerViewModel
import java.util.concurrent.TimeUnit

@Composable
fun TimeDurationDisplay(
    context: Context,
    viewModel: FocusTimerViewModel
) {
    val numberPickerDialog = createNumberPickerDialog(context, viewModel)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(viewModel.timeLeftInMilliseconds.value)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(
            viewModel.timeLeftInMilliseconds.value - TimeUnit.MINUTES.toMillis(
                minutes
            )
        )

    Box(
        modifier = Modifier
            .clickable(
                enabled = !viewModel.isTimerActive.value,
                onClick = { numberPickerDialog.show() }
            )
    ) {
        Text(
            "${minutes}:${seconds.toString().padStart(2, '0')}",
            fontSize = 50.sp,
        )
    }
}

private fun createNumberPickerDialog(context: Context, viewModel: FocusTimerViewModel): Dialog {
    val picker = NumberPicker(context)

    picker.setOnValueChangedListener { picker, oldValue, newValue ->
        viewModel.updateFocusTimeInMinutes(newValue.toLong())
    }

    val builder = AlertDialog.Builder(context)
    builder.setTitle("Choose a time (in minutes")
        .setPositiveButton("Accept") { dialog, which ->

        }
        .setNegativeButton("Cancel") { dialog, which ->

        }

    return builder.create()
}