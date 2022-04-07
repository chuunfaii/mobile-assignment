package me.chunfai.assignment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.ActivityAddRestaurantBinding

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
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
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