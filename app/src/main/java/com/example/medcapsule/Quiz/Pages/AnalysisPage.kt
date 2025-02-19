package com.example.medcapsule.Quiz.Pages

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medcapsule.Quiz.Answer

@Composable
fun AnalysisPage(attemptKey: Map<Int, Int>, answerSet: List<Answer>, review: () -> Unit) {

    var correctAnswers by remember{ mutableStateOf(0) }
    val totalQ = answerSet.size

    // calculatin the number of correct answer
    for (answer in answerSet){
        if (answer.Answer == attemptKey[answer.QuestionNumber])
        {
            correctAnswers += 1
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        Text("number of Correct Answers: $correctAnswers")
        Button(onClick = {
            review()
        }) {
            Text("Review")
        }
    }
}