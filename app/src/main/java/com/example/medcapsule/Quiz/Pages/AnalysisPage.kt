package com.example.medcapsule.Quiz.Pages

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
fun AnalysisPage(
    attemptKey: Map<Int, Int>,
    answerSet: List<Answer>,
    noCorrect: Int,
    review: () -> Unit
) {

    val totalQ = answerSet.size

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        Text("number of Correct Answers: $noCorrect")
        Button(onClick = {
            review()
        }) {
            Text("Review")
        }
    }
}