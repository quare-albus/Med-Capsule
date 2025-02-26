package com.example.medcapsule.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
entities =[attemptSet::class],
version = 1
)

abstract class AttemptSetDatabase :RoomDatabase(){

    abstract fun attemptSetDao() : AttemptSetDao
}