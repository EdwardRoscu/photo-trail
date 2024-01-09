package com.example.phototrail

import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.app.AlertDialog
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.phototrail.data.Photo
import com.example.phototrail.ui.album.PhotoAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.runBlocking

class AlbumActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var photosRecyclerView: RecyclerView
    private lateinit var map: GoogleMap
    private lateinit var addPhotoButton: Button
    private lateinit var backButton: Button
    private lateinit var database: AppDatabase
    private var albumId: Int = 0
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isMapReady = false

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            intent?.data?.let { uri ->
                showPhotoNameDialog(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        albumId = intent.getIntExtra("ALBUM_ID", 0)

        database = AppDatabase.getDatabase(applicationContext)

        photosRecyclerView = findViewById(R.id.photosRecyclerView)
        photosRecyclerView.layoutManager = LinearLayoutManager(this)
        addPhotoButton = findViewById(R.id.addPhotoButton)

        addPhotoButton.setOnClickListener {
            pickPhotoFromGallery()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadPhotos()

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
    }

    private fun getPhotoLocation(uri: Uri): Pair<Double?, Double?> {
        val inputStream = contentResolver.openInputStream(uri)
        val exifInterface = inputStream?.let { ExifInterface(it) }

        val latLong = exifInterface?.latLong
        return if (latLong != null) Pair(latLong[0], latLong[1]) else Pair(null, null)
    }

    private fun getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
            }
        }
    }

    private fun openPhotoFullScreen(photo: Photo) {
        val intent = Intent(this, FullScreenPhotoActivity::class.java)
        intent.putExtra("PHOTO_URI", photo.uri)
        startActivity(intent)
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                showPhotoNameDialog(uri)
            }
        }
    }

    private fun showPhotoNameDialog(photoUri: Uri) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Name Your Photo")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val photoName = input.text.toString()
            addPhotoToAlbum(photoUri, photoName)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun addPhotoToAlbum(uri: Uri, photoName: String) = runBlocking {
        launch {
            val (exifLatitude, exifLongitude) = getPhotoLocation(uri)
            val latitude = exifLatitude ?: currentLatitude
            val longitude = exifLongitude ?: currentLongitude

            val photoCount = database.photoDao().getPhotosByAlbum(albumId).size
            val photoNumber = photoCount + 1

            val newPhoto = Photo(
                id = 0,
                albumId = albumId,
                uri = uri.toString(),
                latitude = latitude,
                longitude = longitude,
                name = photoName,
                number = photoNumber
            )
            database.photoDao().insertPhoto(newPhoto)
            loadPhotos()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true

        map.uiSettings.isZoomControlsEnabled = true

        val defaultLocation = LatLng(currentLatitude, currentLongitude)
        val defaultZoom = 1.0f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))

        loadPhotos()
    }

    private fun loadPhotos() = runBlocking {
        val photos = database.photoDao().getPhotosByAlbum(albumId)
        photosRecyclerView.adapter = PhotoAdapter(photos) { photo ->
            openPhotoFullScreen(photo)
        }
        if (isMapReady) {
            updateMapMarkers(photos)
        }
    }

    private fun updateMapMarkers(photos: List<Photo>) {
        if (isMapReady) {
            map.clear()
            photos.forEach { photo ->
                val location = LatLng(photo.latitude, photo.longitude)
                map.addMarker(MarkerOptions().position(location).title("Photo ${photo.number}"))
            }
        }
    }

    companion object {
        private const val PHOTO_PICK_CODE = 1000
    }
}
