package com.example.medcapsule.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class attemptSet (
    val attemptSet : String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizName : String
)