package com.example.medcapsule.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AttemptSetDao {

    @Upsert
    suspend fun upsert(attemptSet: attemptSet)

    @Query("SELECT COUNT() FROM attemptSet WHERE quizName = :id")
    suspend fun search(id : String) : Int

    @Query("SELECT * FROM attemptSet WHERE quizName = :id")
    suspend fun read(id: String) : List<attemptSet>
}