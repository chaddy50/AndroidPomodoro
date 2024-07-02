package com.chaddyt50.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.chaddyt50.pomodoro.ui.theme.PomodoroTheme
import com.chaddyt50.pomodoro.view.FocusTimer
import com.chaddyt50.pomodoro.viewmodel.FocusTimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        val viewModel: FocusTimerViewModel by viewModels()
                        FocusTimer(applicationContext, viewModel)
                    }
                }
            }
        }
    }
}