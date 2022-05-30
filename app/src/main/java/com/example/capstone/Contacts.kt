package com.example.capstone

import androidx.room.*

@Entity(tableName = "tb_contacts")
data class Contacts (
    @PrimaryKey(autoGenerate = true)var num:Long,
    var id: String
)