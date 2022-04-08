package me.chunfai.assignment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

import me.chunfai.assignment.databinding.ActivityAddRestaurantBinding
import java.io.ByteArrayOutputStream

import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class AddRestaurant : AppCompatActivity() {

    private lateinit var binding: ActivityAddRestaurantBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    lateinit var imageView: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private lateinit var imageUri: Uri
    private lateinit var imageByteArray: ByteArray

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        binding.btnSubmit.setOnClickListener { store() }

        binding.btnSelect.setOnClickListener { imageChooser() }

        binding.btnCamera.setOnClickListener { openCam() }
    }

    private fun openCam() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCamActivityResultLauncher.launch(intent)
    }

    private val openCamActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                imageByteArray = baos.toByteArray()
                binding.imageView.setImageBitmap(imageBitmap)
            }
        }

    private fun imageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imageChooseActivityResultLauncher.launch(intent)
    }

    private val imageChooseActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                val selectedImage = Objects.requireNonNull(data)?.data
                var imageStream: InputStream? = null
                imageUri = data?.data!!

                try {
                    imageStream = contentResolver.openInputStream(selectedImage!!)!!
                } catch (error: FileNotFoundException) {
                    Log.d("activityLauncher", error.toString())
                }

                BitmapFactory.decodeStream(imageStream)
                binding.imageView.setImageURI(selectedImage)
            }
        }

    private fun store() {
        val name = binding.resName.text.toString()
        val address = binding.resAddress.text.toString()
        val contact = binding.resPhone.text.toString()
        val open = binding.resTimeOpen.text.toString()
        val close = binding.resTimeClose.text.toString()
        val desc = binding.resDescription.text.toString()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now)

        if (name.isBlank() || address.isBlank() || contact.isBlank() || open.isBlank() || close.isBlank() || desc.isBlank()) {
            Toast.makeText(this, "All fields are required to input.", Toast.LENGTH_LONG).show()
        } else {
            val restaurant = Restaurant(name, address, open, close, contact, desc, filename)
            database.collection("restaurants").document().set(restaurant)

            val storageReference = FirebaseStorage.getInstance().getReference("images/$filename")

            try {
                storageReference.putFile(imageUri).addOnSuccessListener {
                    Toast.makeText(this, "done upload image", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                storageReference.putBytes(imageByteArray).addOnSuccessListener {
                    Toast.makeText(this, "done upload image", Toast.LENGTH_LONG).show()
                }
            }

            Toast.makeText(this, "Restaurant Added Successfully", Toast.LENGTH_LONG).show()
        }
    }

}