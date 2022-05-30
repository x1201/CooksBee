package com.example.capstone

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeLogDao{
    @Query("SELECT * FROM tb_recipeLog")
    fun getAll(): List<RecipeLog>

    @Insert
    fun insertAll(vararg recipeLog: RecipeLog)

    @Delete
    fun delete(recipeLog: RecipeLog)
}