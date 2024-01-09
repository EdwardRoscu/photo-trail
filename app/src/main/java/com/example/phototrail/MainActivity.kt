package com.example.phototrail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.example.phototrail.data.Album
import com.example.phototrail.ui.album.AlbumAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var albumsRecyclerView: RecyclerView
    private lateinit var addAlbumButton: Button
    private lateinit var database: AppDatabase
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(applicationContext)

        albumsRecyclerView = findViewById(R.id.albumsRecyclerView)
        addAlbumButton = findViewById(R.id.addAlbumButton)

        albumsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadAlbums()

        addAlbumButton.setOnClickListener {
            showAddAlbumDialog()
        }

        exitButton = findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            finish()
        }
    }

    private fun showAddAlbumDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Album")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val albumName = input.text.toString()
            if (albumName.isNotEmpty()) {
                addNewAlbum(albumName)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun setupRecyclerView() {
        albumsRecyclerView.layoutManager = LinearLayoutManager(this)
        loadAlbums()
    }

    private fun loadAlbums() = runBlocking {
        val albums = database.albumDao().getAllAlbums().toMutableList()
        albumsRecyclerView.adapter = AlbumAdapter(albums, { album, position ->
            deleteAlbum(album, position)
        }, { album ->
            openAlbum(album)
        })
    }

    private fun openAlbum(album: Album) {
        val intent = Intent(this, AlbumActivity::class.java)
        intent.putExtra("ALBUM_ID", album.id)
        startActivity(intent)
    }

    private fun deleteAlbum(album: Album, position: Int) = runBlocking {
        launch {
            database.albumDao().deleteAlbum(album)
            (albumsRecyclerView.adapter as AlbumAdapter).apply {
                albumList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun addNewAlbum(name: String) = runBlocking {
        launch {
            val newAlbum = Album(0, name)
            database.albumDao().insertAlbum(newAlbum)
            loadAlbums()
        }
    }

}
