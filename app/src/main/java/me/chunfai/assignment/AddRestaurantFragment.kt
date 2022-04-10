package me.chunfai.assignment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import me.chunfai.assignment.databinding.FragmentAddRestaurantBinding
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddRestaurantFragment : Fragment() {

    private lateinit var binding: FragmentAddRestaurantBinding

    private lateinit var database: FirebaseFirestore

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var imageUri: Uri
    private lateinit var imageByteArray: ByteArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_restaurant, container, false)

        database = FirebaseFirestore.getInstance()

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.GONE

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "Add New Restaurant"

        binding.btnCancel.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStackImmediate()
        }
        binding.btnSelect.setOnClickListener { selectImage() }
        binding.btnCamera.setOnClickListener { openCamera() }
        binding.btnSubmit.setOnClickListener { addRestaurant() }

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        selectImageActivityResultLauncher.launch(intent)
    }

    private val selectImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                val selectedImage = Objects.requireNonNull(data!!).data
                var imageStream: InputStream? = null
                imageUri = data.data!!

                try {
                    imageStream = context?.contentResolver?.openInputStream(selectedImage!!)
                } catch (exception: FileNotFoundException) {
                    Log.d("AddRestaurantFragment", exception.message.toString())
                }

                BitmapFactory.decodeStream(imageStream)
                binding.imageView.setImageURI(selectedImage)
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraActivityResultLauncher.launch(intent)
    }

    private val openCameraActivityResultLauncher =
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

    private fun addRestaurant() {
        val restaurantName = binding.editRestaurantName.editText?.text.toString()
        val restaurantAddress = binding.editRestaurantAddress.editText?.text.toString()
        val restaurantContact = binding.editRestaurantContact.editText?.text.toString()
        val restaurantTimeOpen = binding.editRestaurantTimeOpen.editText?.text.toString()
        val restaurantTimeClose = binding.editRestaurantTimeClose.editText?.text.toString()
        val restaurantDescription = binding.editRestaurantDescription.editText?.text.toString()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)

        if (restaurantName.isBlank() || restaurantAddress.isBlank() || restaurantContact.isBlank() || restaurantTimeOpen.isBlank() || restaurantTimeClose.isBlank() || restaurantDescription.isBlank()) {
            Toast.makeText(context, "All fields are required to input.", Toast.LENGTH_LONG).show()
        } else {
            val restaurant = hashMapOf(
                "name" to restaurantName,
                "address" to restaurantAddress,
                "openTime" to restaurantTimeOpen,
                "closeTime" to restaurantTimeClose,
                "contact" to restaurantContact,
                "description" to restaurantDescription,
                "imageName" to fileName
            )
            database.collection("restaurants").add(restaurant)

            val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

            try {
                storageReference.putFile(imageUri)
            } catch (exception: Exception) {
                storageReference.putBytes(imageByteArray)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.resetData()

                val restaurants = (activity as MainActivity).getAllRestaurants()
                sharedViewModel.restaurants = restaurants

                Toast.makeText(
                    context,
                    "New restaurant has been added successfully.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}