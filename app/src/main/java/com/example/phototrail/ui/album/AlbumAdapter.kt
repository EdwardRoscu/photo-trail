package com.example.phototrail.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phototrail.R
import com.example.phototrail.data.Album

class AlbumAdapter(
    val albumList: MutableList<Album>,
    private val onDeleteClick: (Album, Int) -> Unit,
    private val onAlbumClick: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumName: TextView = view.findViewById(R.id.tvAlbumName)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteAlbum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        holder.albumName.text = album.name
        holder.deleteButton.setOnClickListener { onDeleteClick(album, position) }
        holder.itemView.setOnClickListener { onAlbumClick(album) }
    }

    override fun getItemCount() = albumList.size
}
