package com.example.phototrail.data

import androidx.room.*

@Dao
interface AlbumDao {
    @Insert
    suspend fun insertAlbum(album: Album)

    @Query("SELECT * FROM album")
    suspend fun getAllAlbums(): List<Album>

    @Query("SELECT name FROM album WHERE id = :albumId")
    fun getAlbumNameById(albumId: Int): String

    @Delete
    suspend fun deleteAlbum(album: Album)
}
