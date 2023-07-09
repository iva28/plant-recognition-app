package com.example.plantrecognition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class MainActivity : ComponentActivity() {
    lateinit var gallery: Button
    lateinit var picture: Button
    lateinit var imageView: ImageView
    lateinit var result: TextView
    val modelManager: PlantModelManager = PlantModelManager()
    private val pickImage = 100;
    private val cameraRequest = 200;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this@MainActivity, "Welcome :)", Toast.LENGTH_SHORT)
            .show()
        gallery = findViewById(R.id.button_gallery)
        picture = findViewById(R.id.button_picture)
        imageView = findViewById(R.id.imageView)
        result = findViewById(R.id.result)

        picture.setOnClickListener {
            requestCameraPermission()
        }

        gallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent,pickImage)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == cameraRequest) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(photo)
            val label = modelManager.classifyImage(applicationContext, photo)
            result.text = label
        }
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageView.setImageURI(data?.data)

            val image = (imageView.drawable as BitmapDrawable).bitmap
            val label = modelManager.classifyImage(applicationContext, image)
            result.text = label
        }
        Toast.makeText(this@MainActivity, "Classified flower", Toast.LENGTH_SHORT)
            .show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)
        } else {
            // Permission already granted
            startCameraIntent()
        }
    }

    private fun startCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, cameraRequest)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequest) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                startCameraIntent()
            }
        }
    }

}
