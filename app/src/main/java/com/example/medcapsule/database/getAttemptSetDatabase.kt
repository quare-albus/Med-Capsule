package com.example.medcapsule.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getAttemptSetDatabase (context: Context): AttemptSetDatabase {
    val dbFile = context.getDatabasePath("attemptSetDatabase.db")
    return Room.databaseBuilder<AttemptSetDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}