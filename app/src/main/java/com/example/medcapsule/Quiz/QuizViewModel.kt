package com.example.medcapsule.Quiz

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcapsule.SharedPreferencesManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel() : ViewModel() {

    private val _state = MutableStateFlow(STATE.LOADING)
    val state : StateFlow<STATE> = _state.asStateFlow()

    private val _quizSet = MutableStateFlow(QuizSet())
    val quizSet : StateFlow<QuizSet> = _quizSet.asStateFlow()

    private val _isAttempted = MutableStateFlow(false)
    val isAttempted : StateFlow<Boolean> = _isAttempted.asStateFlow()

    private val _quizQuestions = MutableStateFlow(mutableListOf<Question>())
    private val quizQuestions : StateFlow<List<Question>> = _quizQuestions.asStateFlow()

    private val _quizAnswers = MutableStateFlow(mutableListOf<Answer>())
    private val quizAnswers : StateFlow<List<Answer>> = _quizAnswers.asStateFlow()

    private val _currentQ = MutableStateFlow(0)
    val currentQ : StateFlow<Int> = _currentQ.asStateFlow()

    private val _attemptKey= MutableStateFlow(mapOf<Int,Int>())
    val attemptKey : StateFlow<Map<Int, Int>> = _attemptKey.asStateFlow()

    private val _debug1= MutableStateFlow("")
    val debug1 : StateFlow<String> = _debug1.asStateFlow()


    // timer data
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()
    private var timerJob: Job? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val quizSetRef = firestore.collection("Quiz").document("SpecificId")


    init{
        viewModelScope.launch{
            val initListOfAsyncFunc = listOf(
                viewModelScope.async { fetchQuizDetails() },
                viewModelScope.async { fetchQuestions() },
                viewModelScope.async { fetchAnswers() },
                viewModelScope.async { getPrevQuizAttemptKey() },
            )

            for (asyncFunc in initListOfAsyncFunc){
                asyncFunc.await()
            }

            _state.update{STATE.READY}
        }

    }


    private fun fetchQuizDetails(){
        viewModelScope.launch{// meta-data of quiz {timing and stuff}
            quizSetRef.get()
                .addOnSuccessListener { result ->
                    val resultObject = result.toObject(QuizSet::class.java)!!
                    resultObject.questionSet = quizQuestions.value
                    resultObject.answerSet = quizAnswers.value
                    _quizSet.update { resultObject }
                }
        }
    }

    private fun fetchQuestions(){
        viewModelScope.launch{//actual quiz data
            quizSetRef.collection("QuizQuestions").get()
                .addOnSuccessListener { result ->
                    for (document in result) {                                   // questions
                        val myObject = document.toObject(Question::class.java)
                        _quizQuestions.value.add(myObject)
                    }
                }
        }
    }

    private fun fetchAnswers(){
        viewModelScope.launch{//Fetching Answer Key
            try {
                quizSetRef.collection("QuizAnswers")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {                                   // questions
                            val myObject = Answer(
                                document["QuestionNumber"].toString().toInt(),
                                document["Answer"].toString().toInt(),
                                document["Description"].toString()
                            )
                            _quizAnswers.value.add(myObject)
                        }

                        _debug1.value = "success"
                    }
            } catch (_: Exception) {
            }
            _currentQ.update { 0 }
        }
    }

    private fun getPrevQuizAttemptKey(){
        viewModelScope.launch{
            val prevAttemptSet: MutableMap<Int, Int> = mutableMapOf()
            val defaultString = "Default"
            val prevQuizAttemptKeyJsonString =
                SharedPreferencesManager.getString("Quiz_${quizSet.value.id}", defaultString)
            if (prevQuizAttemptKeyJsonString != defaultString) {
                _isAttempted.value = true
                val prevQuizAttemptKeyString =
                    prevQuizAttemptKeyJsonString.drop(1).dropLast(1).replace(
                        " ",
                        ""
                    )  // removes spaces and cleares the { } at the start and the end of the json data
                for (qaPair in prevQuizAttemptKeyString.split(",")) {
                    val qaTuple =
                        qaPair.split("=") // qaTuple contain the question and answer in a list at [0 index the question] [1 index the answer]
                    try {
                        if (qaTuple[0] != "" && qaTuple.last() != "") {
                            prevAttemptSet[qaTuple[0].toInt()] = qaTuple.last().toInt()
                        }
                    } catch (_: Exception) {
                    }
                }

                _quizSet.value.attemptKey = prevAttemptSet
            }
        }



    }

    // navigating questions
    fun nextQ(){
        _currentQ.update { _currentQ.value + 1 }
    }

    fun prevQ(){
        _currentQ.update { _currentQ.value - 1 }
    }

    // selecting options
    fun  selectOption(currentQ : Int, option : Int){
        _attemptKey.update {oldMap ->
            oldMap + (currentQ to option)
        }
    }

    //submitting the quiz
    fun submitQuiz(){
        _state.update{STATE.READY}
        viewModelScope.launch {
            SharedPreferencesManager.saveString("Quiz_${quizSet.value.id}", attemptKey.value.toString())
        }
        _isAttempted.value = true
        _currentQ.value = 0
    }

    fun startQuiz(){
        _state.update{STATE.QUIZ}
        // when we start the quiz we have to empty the attempt key is previously full
        _attemptKey.value = mapOf()
    }

    fun review()
    {
        _state.update{STATE.REVIEW}
    }

    fun analysis()
    {
        _state.update{STATE.ANALYSIS}
    }

    //starting timer when the test starts
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
    LOADING,
    READY,
    QUIZ,
    ANALYSIS,
    REVIEW
}