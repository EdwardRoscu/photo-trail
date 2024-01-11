package com.example.phototrail.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val albumId: Int, // Reference to Album
    val uri: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    var number: Int
)
