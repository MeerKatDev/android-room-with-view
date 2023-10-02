package com.example.roomtestwithblocks

import android.app.Application
import com.example.roomtestwithblocks.data.WordRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WordsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val applicationScope = CoroutineScope(SupervisorJob())
        database = WordRoomDatabase.getDatabase(this.applicationContext, applicationScope)
    }

    companion object {
        lateinit var database: WordRoomDatabase
    }
}
