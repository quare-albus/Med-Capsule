package com.example.medcapsule.Quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

class QuizViewModel() : ViewModel() {
    private val _quizSet = MutableStateFlow(QuizSet())
    val quizSet : StateFlow<QuizSet> = _quizSet.asStateFlow()

    private val _currentQ = MutableStateFlow(0)
    val currentQ : StateFlow<Int> = _currentQ.asStateFlow()

    private val _optionSelected= MutableStateFlow(0)
    val optionSelected : StateFlow<Int> = _optionSelected.asStateFlow()

    private val _attemptKey= MutableStateFlow(mapOf<Int,Int>())
    val attemptKey : StateFlow<Map<Int, Int>> = _attemptKey.asStateFlow()

    private val _debug= MutableStateFlow("")
    val debug : StateFlow<String> = _debug.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val quizSetRef = firestore.collection("Quiz").document("SpecificId")


    fun fetchQuizSet(){

         //actual quiz data
        val questionList = mutableListOf<Question>()
        quizSetRef.collection("QuizQuestions").get()
            .addOnSuccessListener { result ->
                for (document in result){                                   // questions
                    val data = document.data
                    val myObject = document.toObject(Question::class.java)
                    questionList.add(myObject)
                }
            }


        // meta-data of quiz {timing and stuff}
        var resultObject : QuizSet = QuizSet()
        quizSetRef.get()
            .addOnSuccessListener { result ->
               resultObject = result.toObject(QuizSet::class.java)!!
                resultObject.questionSet = questionList
                _quizSet.update{ resultObject }
            }
    }

    fun nextQ(){
        _currentQ.update { _currentQ.value + 1 }
    }

    fun prevQ(){
        _currentQ.update { _currentQ.value - 1 }
    }

    fun  selectOption(currentQ : Int, option : Int){
        _attemptKey.update {oldMap ->
            oldMap + (currentQ to option)
        }
    }

}