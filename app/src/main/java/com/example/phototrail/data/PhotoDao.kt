package com.example.phototrail.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PhotoDao {
    @Insert
    suspend fun insertPhoto(photo: Photo)

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Query("SELECT * FROM photo WHERE albumId = :albumId")
    suspend fun getPhotosByAlbum(albumId: Int): List<Photo>

    @Delete
    suspend fun deletePhoto(photo: Photo)
}
