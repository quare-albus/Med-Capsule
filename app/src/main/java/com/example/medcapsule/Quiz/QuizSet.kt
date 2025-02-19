package com.example.medcapsule.Quiz

data class QuizSet (
    var id : String = "",
    var questionSet : List<Question> = listOf<Question>(),
    var answerSet : List<Answer> = listOf<Answer>()
)