package com.example.medcapsule.Quiz

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medcapsule.Quiz.Pages.Page

class QuizScreen : ComponentActivity() {
    val quizViewModel : QuizViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            quizViewModel.fetchQuizSet()
            val quizSet by quizViewModel.quizSet.collectAsState()
            val currentQ by quizViewModel.currentQ.collectAsState()
            val attemptKey by quizViewModel.attemptKey.collectAsState()
            var totalQ = quizSet.questionSet.size
            var isStarted by remember { mutableFloatStateOf(0f) }
            var optionSelected by remember { mutableStateOf(0) }


            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                quizSet.id,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            val activity = (LocalActivity.current as? Activity)
                            IconButton(onClick = { activity?.finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .alpha(isStarted)) {
                            Text("${attemptKey[currentQ]} : $currentQ")
                            Row {
                                Button(
                                    onClick = {
                                         quizViewModel.prevQ()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .padding(4.dp)
                                        .alpha(
                                            if (currentQ == 0) {
                                                0f
                                            } else {
                                                1f
                                            }
                                        )
                                ) {
                                    Text("Previous")
                                }
                                Button(
                                    onClick = {
                                        quizViewModel.nextQ()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .padding(4.dp)
                                        .alpha(
                                            if (currentQ + 1 == totalQ) {
                                                0f
                                            } else {
                                                1f
                                            }
                                        )
                                ) {
                                    Text("Next")
                                }
                            }
                            Button(
                                onClick = {       },
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text("Submit")
                            }
                        }
                }
            ){innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "FirstPage",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable("FirstPage") {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("This is the start page of the Quiz")
                            Text("Name Of Quiz: ${quizSet.id}")
                            Button(
                                onClick = {
                                    navController.navigate("QuestionPage")
                                    isStarted = 1f
                                },
                                modifier = Modifier
                                    .alpha(
                                        if (totalQ > 0) {
                                            1f
                                        } else 0f
                                    )
                            ) {
                                Text("Click Me")
                            }

                            Text("total number of Q: $totalQ")
                        }
                    }

                    composable("QuestionPage") {
                        Page(
                            quizSet.questionSet[currentQ],
                            attemptKey[currentQ]
                        ) { selectedOption ->
                            quizViewModel.selectOption(currentQ, selectedOption)
                        }
                    }

                }

            }

        }
    }
}

