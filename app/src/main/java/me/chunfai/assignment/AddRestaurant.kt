package me.chunfai.assignment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import me.chunfai.assignment.databinding.ActivityAddRestaurantBinding

import java.io.File
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
    private var imageUri: Uri? = null

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String

    var storageReference: StorageReference? = null

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

        binding.btnCamera.setOnClickListener{
            openCam()
        }
    }

    private fun openCam(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Open Camera Failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
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

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

        if (name.isBlank() || address.isBlank() || contact.isBlank() || open.isBlank() || close.isBlank() || desc.isBlank()) {
            Toast.makeText(this, "All fields are required to input.", Toast.LENGTH_LONG).show()
            return
        }else{
            val restaurant = Restaurant(name, address, open, close, contact, desc)
            database.collection("restaurants").document().set(restaurant)
            /*storageReference = FirebaseStorage.getInstance().getReference("images/${filename}")
            storageReference!!.putFile(imageUri!!)*/
            Toast.makeText(this, "Restaurant Added Successfully", Toast.LENGTH_LONG).show()
        }

    }
}