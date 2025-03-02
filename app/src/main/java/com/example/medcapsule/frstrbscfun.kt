package com.example.medcapsule

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medcapsule.Quiz.MainApplication
import com.example.medcapsule.database.attemptSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Note(
    val name: String = ""
)

fun addNoteToFirestore(note: Note) {
    val notesCollection = firestore.collection("Chapters11").document("Living World").collection("Parts")
    notesCollection.add(note)
        .addOnSuccessListener {
            Log.d(TAG,"Successful entry")
        }
        .addOnFailureListener { exception ->
            // Handle failure
            Log.w(TAG, "Failure to add")
        }
}


