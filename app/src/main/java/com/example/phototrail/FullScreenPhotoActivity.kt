package com.example.phototrail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.net.Uri

class FullScreenPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_photo)

        val imageView: ImageView = findViewById(R.id.fullScreenImageView)
        val backButton: Button = findViewById(R.id.backButtonFullScreen)

        val photoUri = intent.getStringExtra("PHOTO_URI")
        photoUri?.let {
            val inputStream = contentResolver.openInputStream(Uri.parse(it))
            Glide.with(this)
                .load(inputStream)
                .error(R.drawable.error_image)
                .into(imageView)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}