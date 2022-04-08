package me.chunfai.assignment

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import me.chunfai.assignment.databinding.ActivityAddReviewBinding
import me.chunfai.assignment.databinding.ActivityRestaurantDetailBinding
import java.io.File


class Restaurant_detail : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        val restaurant = intent.getSerializableExtra("restaurant") as Restaurant

        val openTime = restaurant.openTime
        val closeTime = restaurant.closeTime

        val imageName = restaurant.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName.jpg")
        val localfile = File.createTempFile("TempImage","jpg")
        imageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.restaurantImage.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"Failed to retrieve the image",Toast.LENGTH_SHORT).show()
        }

        binding.restaurantTitle.text = restaurant.name
        binding.restaurantAddress.text = restaurant.address
        binding.restaurantContact.text = "Contact Number : " + restaurant.contact
        binding.restaurantBusinessHour.text = "Business Hour : $openTime - $closeTime"



        val menuBtn = findViewById<ImageView>(R.id.option_menu)
        menuBtn.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, menuBtn)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit ->
                        Toast.makeText(
                            this@Restaurant_detail,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                    R.id.action_delete ->
                        Toast.makeText(
                            this@Restaurant_detail,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                true
            })
            popupMenu.show()
        }

        binding.icFavorite.setOnClickListener { store() }
    }

    private fun store(){
        Toast.makeText(this, "Image View Clicked", Toast.LENGTH_LONG).show()
    }


}

