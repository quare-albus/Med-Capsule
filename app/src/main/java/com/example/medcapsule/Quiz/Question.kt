package com.example.medcapsule.Quiz

data class Question (
    val question : String = "",
    val options : List<String> = listOf<String>(),
    val questionNumber : Int = 0
)