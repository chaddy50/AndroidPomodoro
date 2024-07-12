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
import com.chaddyt50.pomodoro.view.Pomodoro
import com.chaddyt50.pomodoro.viewmodel.PomodoroViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        createNotificationChannels(context)
        enableEdgeToEdge()
        setContent {
            PomodoroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        val viewModel: PomodoroViewModel by viewModels()
                        Pomodoro(context, viewModel)
                    }
                }
            }
        }
    }
}