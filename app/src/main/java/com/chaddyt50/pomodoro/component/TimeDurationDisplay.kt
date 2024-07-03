package com.chaddyt50.pomodoro.component

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.chaddyt50.pomodoro.MainActivity
import com.chaddyt50.pomodoro.viewmodel.FocusTimerViewModel
import java.util.concurrent.TimeUnit

@Composable
fun TimeDurationDisplay(
    viewModel: FocusTimerViewModel
) {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(viewModel.timeLeftInMilliseconds.value)
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(
            viewModel.timeLeftInMilliseconds.value - TimeUnit.MINUTES.toMillis(
                minutes
            )
        )

    Text(
        "${minutes}:${seconds.toString().padStart(2, '0')}",
        fontSize = 50.sp,
    )
}