package com.example.medcapsule.Quiz

data class Question (
    val Question : String = "",
    val Options : List<String> = listOf<String>(),
    val QuestionNumber : Int = 0
)