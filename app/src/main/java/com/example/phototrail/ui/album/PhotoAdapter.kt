package com.example.phototrail.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phototrail.R
import com.example.phototrail.data.Photo
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val photoList: List<Photo>,
    private val onPhotoClick: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoNumber: TextView = view.findViewById(R.id.tvPhotoNumber)
        val imageName: TextView = view.findViewById(R.id.tvPhotoName)
        val imageView: ImageView = view.findViewById(R.id.ivPhotoThumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]

        holder.photoNumber.text = "${position + 1}."

        Glide.with(holder.imageView.context)
            .load(photo.uri)
            .into(holder.imageView)

        holder.imageName.text = photo.name

        holder.itemView.setOnClickListener { onPhotoClick(photo) }
    }


    override fun getItemCount() = photoList.size
}
