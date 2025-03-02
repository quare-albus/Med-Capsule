package com.example.medcapsule.Quiz

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.medcapsule.SharedPreferencesManager
import com.example.medcapsule.database.AttemptSetDao
import com.example.medcapsule.database.attemptSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class QuizViewModel(private val id: String) : ViewModel() {



    private val _attemptSetExist = MutableStateFlow(0)
    val attemptSetExist : StateFlow<Int> = _attemptSetExist.asStateFlow()

    private val _state = MutableStateFlow(STATE.LOADING)
    val state : StateFlow<STATE> = _state.asStateFlow()

    private val _quizSet = MutableStateFlow(QuizSet())
    val quizSet : StateFlow<QuizSet> = _quizSet.asStateFlow()

    private val _totalQ = MutableStateFlow(0)
    val totalQ : StateFlow<Int> = _totalQ.asStateFlow()

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
    private val quizSetRef = firestore.collection("Quiz").document(id)


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
                        _totalQ.value += 1
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
                                document["questionNumber"].toString().toInt(),
                                document["answer"].toString().toInt(),
                                document["description"].toString()
                            )
                            _quizAnswers.value.add(myObject)
                        }

                        _debug1.value += "success"
                    }
            } catch (_: Exception) {
            }
            _currentQ.update { 0 }
        }
    }

    @OptIn(UnstableApi::class)
    fun getPrevQuizAttemptKey(){
        viewModelScope.launch{
            val prevAttemptSet: MutableMap<Int, Int> = mutableMapOf()
            val defaultString = "Default"
            val prevQuizAttemptKeyJsonString =
                MainApplication.database.attemptSetDao().read(id).first().attemptSet
            Log.d("debug",prevQuizAttemptKeyJsonString)
            Log.d("debug",MainApplication.database.attemptSetDao().read(id).first().attemptSet)
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
                _attemptKey.value = prevAttemptSet
                _debug1.value += prevAttemptSet.toString()
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
        _attemptKey.update { old ->
            old + (currentQ to option)
        }
        _quizSet.value.attemptKey += (currentQ to option)
    }

    //submitting the quiz
    fun submitQuiz(){
        _state.update{STATE.READY}
        _isAttempted.value = true
        SharedPreferencesManager.saveString(id,_attemptKey.value.toString())
        //Room have to work on it ||||||| Not now
        viewModelScope.launch{
            MainApplication.database.attemptSetDao().upsert(attemptSet(_attemptKey.value.toString(),id))
        }

        //reset timer
        timerJob?.cancel()
        _timer.value = 0

        //reset question counter
        _currentQ.value = 0
    }

    fun startQuiz(){
        _state.update{STATE.QUIZ}
        // when we start the quiz we have to empty the attempt key is previously full
        _quizSet.value.attemptKey = mapOf()
        _attemptKey.value = mapOf()

        startTimer(5) { submitQuiz() }
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
        _timer.value = 0
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

                if (answer.answer == quizSet.value.attemptKey[answer.questionNumber - 1]) {
                    correctAnswers.value += 1
                }
                else{
                    _debug1.update{old -> old + "${answer.questionNumber}  ${answer.answer} ${quizSet.value.attemptKey[answer.questionNumber]} "}
                }
            }
            return correctAnswers.value
        }
        else{
            return 3
        }
    }

    class Factory(private val id : String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return QuizViewModel(id) as T
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