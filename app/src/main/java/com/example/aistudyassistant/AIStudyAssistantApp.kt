package com.example.aistudyassistant

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

import androidx.room.Room
import com.example.aistudyassistant.data.local.AppDatabase
import com.example.aistudyassistant.data.local.ChatHistoryRepository

class AIStudyAssistantApp : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
        lateinit var historyRepository: ChatHistoryRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ai_study_assistant_db"
        ).build()
        historyRepository = ChatHistoryRepository(database.chatHistoryDao())
        
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}
