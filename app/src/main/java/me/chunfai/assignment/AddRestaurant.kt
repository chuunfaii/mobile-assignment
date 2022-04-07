package me.chunfai.assignment

import android.app.Activity
import android.content.Intent
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
import me.chunfai.assignment.databinding.ActivityAddRestaurantBinding
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class AddRestaurant : AppCompatActivity() {

    private lateinit var binding: ActivityAddRestaurantBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    lateinit var imageView: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        binding.btnSubmit.setOnClickListener { store() }

        binding.btnSelect.setOnClickListener{
            imageChooser()
        }
    }

    private fun imageChooser(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        someActivityResultLauncher.launch(intent)

//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), pickImage)

//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT

//        Log.d("imageChooser()", "Done initializing intent.")

//        Log.d("imageChooser()", "Before launching getResult.")

//        getResult.launch(intent)

//        Log.d("imageChooser()", "After launching getResult.")
//        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        registerForActivityResult(gallery, pickImage)

    }

    private val someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val selectedImage = Objects.requireNonNull(data)?.data
            var imageStream: InputStream? = null

            try {
                imageStream = contentResolver.openInputStream(selectedImage!!)!!
            } catch (error: FileNotFoundException) {
                Log.d("activityLauncher", error.toString())
            }

            BitmapFactory.decodeStream(imageStream)
            binding.imageView.setImageURI(selectedImage)
        }
    }

    private fun store(){
        val name = binding.resName.text.toString()
        val address = binding.resAddress.text.toString()
        val contact = binding.resPhone.text.toString()
        val open = binding.resTimeOpen.text.toString()
        val close = binding.resTimeClose.text.toString()
        val desc = binding.resDescription.text.toString()

        if (name.isBlank() || address.isBlank() || contact.isBlank() || open.isBlank() || close.isBlank() || desc.isBlank()) {
            Toast.makeText(this, "All fields are required to input.", Toast.LENGTH_LONG).show()
            return
        }

        val restaurant = Restaurant(name, address, open, close, contact, desc)

        database.collection("restaurants").document().set(restaurant)

    }
}