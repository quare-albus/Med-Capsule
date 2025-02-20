package com.example.medcapsule.Quiz

import android.app.Activity
import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel() : ViewModel() {
    val State : STATE
        get() {
            return STATE
        }
    private val _quizSet = MutableStateFlow(QuizSet())
    val quizSet : StateFlow<QuizSet> = _quizSet.asStateFlow()

    private val _currentQ = MutableStateFlow(0)
    val currentQ : StateFlow<Int> = _currentQ.asStateFlow()

    private val _totalQ = MutableStateFlow(0)
    val totalQ : StateFlow<Int> = _totalQ.asStateFlow()

    private val _optionSelected= MutableStateFlow(0)
    val optionSelected : StateFlow<Int> = _optionSelected.asStateFlow()

    private val _attemptKey= MutableStateFlow(mapOf<Int,Int>())
    val attemptKey : StateFlow<Map<Int, Int>> = _attemptKey.asStateFlow()

    private val _prevQuizData= MutableStateFlow("")
    val prevQuizData : StateFlow<String> = _prevQuizData.asStateFlow()

    private val _debug1= MutableStateFlow("")
    val debug1 : StateFlow<String> = _debug1.asStateFlow()

    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()
    private var timerJob: Job? = null

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
                _totalQ.update{_quizSet.value.questionSet.size}
                setAnswer(prevQuizData.value)
            }
    }
    fun fetchAnswerKey(){

        //Fetching Answer Key
        try {
            val answerList = mutableListOf<Answer>()
            quizSetRef.collection("QuizAnswers").get()
                .addOnSuccessListener { result ->
                    for (document in result) {                                   // questions
                        val myObject = Answer(document["QuestionNumber"].toString().toInt(),document["Answer"].toString().toInt(),document["Description"].toString())
                        answerList.add(myObject)
                    }
                    val resultObject = _quizSet.value
                    resultObject.answerSet = answerList.sortedWith(compareBy<Answer> {
                        it.QuestionNumber
                    })
                    _quizSet.update{resultObject}
                    _debug1.value  = "success"
                }
        }
        catch(e : Exception){
            _debug1.value = e.toString()
        }

        _currentQ.update {0}

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


    fun startTimer(timeLimit: Int = 0, OnTimeFinish: () -> Unit = {}) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timer.value < timeLimit ) {
                delay(1000)
                _timer.value++
            }
            OnTimeFinish()
            delay(1000)
            _timer.value = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun setAnswer(data: String){
        val datan = data.drop(1).dropLast(1).replace(" ","")
        val attemptSet : MutableMap<Int,Int> = mutableMapOf()
        for (eachdata in datan.split(",")){
            val collec = eachdata.split("=")
            try {
                if (collec[0] != "" && collec.last() != "") {
                    attemptSet[collec[0].toInt()] = collec.last().toInt()
                }
            }
            catch(e : Exception)
            {
                _debug1.value = e.toString()
            }
//            questionList +(collec[0].toInt() to collec.last().toInt())
        }

        _attemptKey.value = attemptSet
    }

    fun setDebug(setString : String){
        _prevQuizData.value = setString
    }

    fun eraseAttempt(){
        _attemptKey.value = emptyMap()
    }

    fun noCorrect():Int{
        val correctAnswers = mutableStateOf(0)
        if(quizSet.value.answerSet.isNotEmpty()) {
            for (answer in quizSet.value.answerSet) {

                if (answer.Answer == attemptKey.value[answer.QuestionNumber - 1]) {
                    correctAnswers.value += 1
                }
                else{
                    _debug1.update{old -> old + "${answer.QuestionNumber}  ${answer.Answer} ${attemptKey.value[answer.QuestionNumber]} "}
                }
            }
            return correctAnswers.value
        }
        else{
            return 3
        }
    }
}

enum class STATE{
    LOADING_QUIZ,
    READY
}