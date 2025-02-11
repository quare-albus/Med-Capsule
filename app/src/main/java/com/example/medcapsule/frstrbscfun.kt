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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = ""
)

fun addNoteToFirestore(note: Note) {
    val notesCollection = firestore.collection("notes")
    notesCollection.add(note)
        .addOnSuccessListener {
            Log.d(TAG,"Successful entry")
        }
        .addOnFailureListener { exception ->
            // Handle failure
            Log.w(TAG, "Failure to add")
        }
}


@Composable
fun Crud() {

    var title by remember { mutableStateOf("") }
    var content by  remember { mutableStateOf("") }

    Column {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") }
        )
        Button(onClick = {
            val note = Note(
                title = title,
                content = content
            )
            addNoteToFirestore(note)
        }) {
            Text("Add Note")
        }
        FetchNotesFromFirestore()
    }


}

@Composable
fun FetchNotesFromFirestore() {
    val notes = remember { mutableStateListOf<Note>() }
    val notesCollection = firestore.collection("notes")

    LaunchedEffect(Unit) {
        notesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    NoteList(notes = notes)
}

@Composable
fun NoteList(notes: List<Note>) {
    LazyRow{
        items(notes) { note ->
            Text(text = note.title)
            Text(text = note.content)
        }
    }
}