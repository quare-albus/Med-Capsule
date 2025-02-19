package com.example.medcapsule.Fetch

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.medcapsule.Models.Part
import com.example.medcapsule.firestore

@Composable
fun fetchParts(chapterName : String) : List<Part>{

    val parts = remember { mutableStateListOf<Part>() }
    val partCollection = firestore.collection("Chapters").document(chapterName).collection("Parts")

    LaunchedEffect(Unit){
        partCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val myObject = document.toObject(Part::class.java)
                    parts.add(myObject)
                }
            }
            .addOnFailureListener {
                // to handle exception in the future
                Log.w(TAG,"Failure Bro")
            }
    }
    return parts
}