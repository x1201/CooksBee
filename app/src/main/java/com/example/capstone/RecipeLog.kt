package com.example.capstone

import androidx.room.*

@Entity(tableName = "tb_recipeLog")
data class RecipeLog (
    @PrimaryKey(autoGenerate = true)var num:Long,
            var id : String
)