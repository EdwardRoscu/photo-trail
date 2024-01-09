package com.example.phototrail

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.phototrail.data.Album
import com.example.phototrail.data.AlbumDao
import com.example.phototrail.data.Photo
import com.example.phototrail.data.PhotoDao

@Database(entities = [Album::class, Photo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "photo_trail_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
