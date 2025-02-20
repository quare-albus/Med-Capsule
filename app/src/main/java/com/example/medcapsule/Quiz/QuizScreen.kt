package com.example.medcapsule.Quiz

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medcapsule.Quiz.Pages.AnalysisPage
import com.example.medcapsule.Quiz.Pages.QuestionPage
import com.example.medcapsule.Quiz.Pages.ReviewPage

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

            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            val data = sharedPref.getString("Quiz_${quizSet.id}", "")
            if (data != null) {
                quizViewModel.setDebug(data)
            }

            val totalQ = quizSet.questionSet.size
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
                        },
                        actions = {
                            Text(quizViewModel.timer.collectAsState().value.toString())
                        }
                    )
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
                        isStarted = 0f
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
                                    quizViewModel.eraseAttempt()
                                    quizViewModel.startTimer(5){
                                        val sharedPref = getPreferences(Context.MODE_PRIVATE)
                                        with (sharedPref.edit()) {
                                            putString("Quiz_${quizSet.id}", attemptKey.toString())
                                            apply()
                                        }

                                        navController.navigate("FirstPage")}
                                },
                                modifier = Modifier
                                    .alpha(
                                        if (totalQ > 0) {
                                            1f
                                        } else 0f
                                    )
                            ) {
                                Text("Start Test")
                            }

                            Text("total number of Q: $totalQ")

                            if(attemptKey != null){
                                Button(onClick = {
                                    quizViewModel.fetchAnswerKey()
                                    navController.navigate("AnalysisPage")
                                }){
                                    Text("Analysis")
                                }
                            }
                            val debug1 = quizViewModel.debug1.collectAsState().value
//                           Text(quizViewModel.debug1.collectAsState().value)
                            Text(quizSet.answerSet.size.toString())

                        }
                    }

                    composable("QuestionPage") {
                        isStarted = 1f
                        QuestionPage(
                            quizSet.questionSet[currentQ],
                            totalQ,
                            attemptKey[currentQ],
                            onNext = {quizViewModel.nextQ()},
                            onPrev = {quizViewModel.prevQ()},
                            onSubmit = {
                                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putString("Quiz_${quizSet.id}", attemptKey.toString())
                                    apply()
                                }

                                navController.navigate("FirstPage")
                            },
                         onOptionSelect = { selectedOption ->
                            quizViewModel.selectOption(currentQ, selectedOption)
                        })
                    }

                    composable("AnalysisPage"){
                        if (quizSet.answerSet.isNotEmpty()){
                            AnalysisPage(attemptKey,quizSet.answerSet,quizViewModel.noCorrect()) {navController.navigate("ReviewPage") }
                        }
//                        AnalysisPage(attemptKey,quizSet.answerSet,{/*navController.navigate("ReviewPage")*/})
                    }
                    composable("ReviewPage") {
                        ReviewPage(quizSet.questionSet[currentQ],totalQ,quizSet.answerSet[currentQ],attemptKey[currentQ]!!, onNext = {quizViewModel.nextQ()},
                            onPrev = {quizViewModel.prevQ()})
                    }
                }

            }

        }
    }
}

