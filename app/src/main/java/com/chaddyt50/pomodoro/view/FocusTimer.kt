package com.chaddyt50.pomodoro.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chaddyt50.pomodoro.component.TimeDurationDisplay
import com.chaddyt50.pomodoro.viewmodel.FocusTimerViewModel


@Composable
fun FocusTimer(
    viewModel: FocusTimerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimeDurationDisplay(viewModel.focusUntilTimeInMilliseconds.value, viewModel.focusTimer.value)

        Button(
            onClick = {
                viewModel.focusTimer.value.start()
            },
            enabled = !viewModel.focusTimer.value.isActive.value
        ) {
            Text("Start Timer")
        }

    }
}