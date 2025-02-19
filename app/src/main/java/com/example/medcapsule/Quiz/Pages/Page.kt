package com.example.medcapsule.Quiz.Pages

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medcapsule.DiscoverScreenActivities.ChapterScreen
import com.example.medcapsule.Quiz.Question

@Composable
fun Page(currentQuestion: Question,selectedOption : Int?, OnOptionSelect: (Int) -> Unit)
{
    Column(modifier = Modifier.fillMaxSize(),
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
                            OnOptionSelect(currentQuestion.Options.indexOf(option))
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