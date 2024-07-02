package com.chaddyt50.pomodoro.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddyt50.pomodoro.MainActivity
import com.chaddyt50.pomodoro.component.TimeDurationDisplay
import com.chaddyt50.pomodoro.viewmodel.FocusTimerViewModel


@Composable
fun FocusTimer(
    context: Context,
    viewModel: FocusTimerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimeDurationDisplay(context, viewModel)

        Button(
            onClick = {
                viewModel.startTimer()
            },
            enabled = !viewModel.isTimerActive.value
        ) {
            Text(text = "Start Timer")
        }

    }
}