package com.example.phototrail.data

import androidx.room.*

@Dao
interface AlbumDao {
    @Insert
    suspend fun insertAlbum(album: Album)

    @Query("SELECT * FROM album")
    suspend fun getAllAlbums(): List<Album>

    @Delete
    suspend fun deleteAlbum(album: Album)
}
