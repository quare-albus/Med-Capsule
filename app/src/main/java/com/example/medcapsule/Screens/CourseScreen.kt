package com.example.medcapsule.Screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.example.medcapsule.Quiz.QuizScreen
import com.example.medcapsule.Quiz.QuizViewModel


@Composable
fun CourseScreen(context: Context)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(text = "Course Screen")
        Button(onClick = {
            val intent = Intent(context,QuizScreen::class.java)
            context.startActivity(intent)
        }){
            Text("Arinjay brooooo")
        }
    }

}