package com.example.medcapsule.Quiz

import android.app.Application
import androidx.room.Room
import com.example.medcapsule.SharedPreferencesManager
import com.example.medcapsule.database.AttemptSetDatabase

class MainApplication : Application(){
    companion object {
        lateinit var database: AttemptSetDatabase
    }

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
        database = Room.databaseBuilder(
            applicationContext,
            AttemptSetDatabase::class.java,
            "attemptSetDb"
        ).build()
    }

}