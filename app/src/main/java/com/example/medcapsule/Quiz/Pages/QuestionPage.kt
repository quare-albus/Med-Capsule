package com.example.medcapsule.Quiz.Pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcapsule.Quiz.Question

@Composable
fun QuestionPage(currentQuestion: Question, totalQ: Int, selectedOption : Int?, onNext: () -> Unit, onPrev: () -> Unit, onSubmit:() -> Unit, onOptionSelect: (Int) -> Unit)
{
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier
                .fillMaxWidth()) {
                Row {
                    Button(
                        onClick = {
                            onPrev()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(4.dp)
                            .alpha(
                                if (currentQuestion.QuestionNumber == 1) {
                                    0f
                                } else {
                                    1f
                                }
                            ),
                        enabled = (currentQuestion.QuestionNumber != 1)
                    ) {
                        Text("Previous")
                    }
                    Button(
                        onClick = {
                            onNext()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(4.dp)
                            .alpha(
                                if (currentQuestion.QuestionNumber == totalQ) {
                                    0f
                                } else {
                                    1f
                                }
                            ),
                        enabled = (currentQuestion.QuestionNumber != totalQ)
                    ) {
                        Text("Next")
                    }
                }
                Button(
                    onClick = {
                        onSubmit()},
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    ) {innerPadding ->
        Column(modifier = Modifier.fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(IntrinsicSize.Min),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xfffefce8), //Card background color
                    contentColor = Color.Black  //Card content color,e.g.text
                ),
                shape = RoundedCornerShape(15.dp)
            ){
                Column(modifier = Modifier.fillMaxSize()
                    .height(IntrinsicSize.Min)
                    .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    // chapter number
                    Box(modifier = Modifier.fillMaxWidth()
                        .padding(4.dp),
                        contentAlignment = Alignment.CenterStart) {
                        Text(
                            "Question ${currentQuestion.QuestionNumber}",
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Chapter Question
                    Box(modifier = Modifier.fillMaxWidth()
                        .padding(4.dp),
                        contentAlignment = Alignment.CenterStart) {
                        Text(
                            currentQuestion.Question
                        )
                    }

                    //options
                    for (option in currentQuestion.Options){
                        Row(modifier = Modifier.fillMaxWidth()
                            .padding(4.dp)
                            .border(border = BorderStroke(1.dp, if (currentQuestion.Options.indexOf(option) == selectedOption) {Color.Black} else {Color(0X00000000)}),
                                shape = RoundedCornerShape(9.dp) ) // outline is always present the color is from Black to transparent
                            .clip(RoundedCornerShape(9.dp))
                            .clickable(onClick = {
                                onOptionSelect(currentQuestion.Options.indexOf(option))
                            }),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically){
                            RadioButton(
                                selected = (currentQuestion.Options.indexOf(option) == selectedOption),
                                onClick = null,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(option)
                        }
                    }
                }
            }
        }
    }

}