package com.example.roomtestwithblocks.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.i("User12", "onCreate")
            INSTANCE?.let { database ->
                scope.launch {
                    val wordDao = database.wordDao()
                    // Delete all content here.
                    wordDao.deleteAll()

                    // Add sample words.
                    wordDao.insert(Word("Hello"))
                    wordDao.insert(Word("World!"))
                    wordDao.insert(Word("Android"))
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            Log.i("User12", "BEFORE INST")
            return INSTANCE ?: synchronized(this) {
                Log.i("User12", "CREATING")
                val instance = Room.databaseBuilder(
                    context,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}