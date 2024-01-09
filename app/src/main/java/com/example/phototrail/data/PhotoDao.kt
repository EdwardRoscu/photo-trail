package com.example.phototrail.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert
    suspend fun insertPhoto(photo: Photo)

    @Query("SELECT * FROM photo WHERE albumId = :albumId")
    suspend fun getPhotosByAlbum(albumId: Int): List<Photo>
}
