package com.example.capstone

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeLog::class], version = 1, exportSchema = false)
abstract class LogDatabase : RoomDatabase() {
    abstract fun RecipeLogDao(): RecipeLogDao

    companion object{
        private var instance: LogDatabase? = null

        @Synchronized
        fun getInstance(context: Context): LogDatabase? {
            if (instance == null){
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogDatabase::class.java,
                    "database-RecipeLog"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}