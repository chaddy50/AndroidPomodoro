package com.chaddyt50.pomodoro

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chaddyt50.pomodoro.ui.theme.PomodoroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val timeLeftInMilliseconds = remember {
                        mutableStateOf("")
                    }

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = timeLeftInMilliseconds.value)

                    }

                    val timer = object : CountDownTimer(30000, 1000) {
                        override fun onTick(millisecondsUntilFinished: Long) {
                            timeLeftInMilliseconds.value =
                                (millisecondsUntilFinished / 1000).toString()
                        }

                        override fun onFinish() {
                            timeLeftInMilliseconds.value = "Timer done"
                        }
                    }
                    if (timeLeftInMilliseconds.value == "") {
                        timer.start()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PomodoroTheme {
        Greeting("Android")
    }
}