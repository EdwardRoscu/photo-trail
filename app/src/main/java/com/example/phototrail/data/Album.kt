package com.example.phototrail.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)
