package me.chunfai.assignment

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import me.chunfai.assignment.databinding.ActivityRestaurantDetailBinding
import java.io.File
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext



class Restaurant_detail : AppCompatActivity() ,CoroutineScope{

    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>

    private lateinit var users: MutableList<User>
    private lateinit var reviews: MutableList<Review>

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        users = mutableListOf()
        reviews = mutableListOf()

        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager

//        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        //Get Restaurant Details
        val restaurant = intent.getSerializableExtra("restaurant") as Restaurant

        val openTime = restaurant.openTime
        val closeTime = restaurant.closeTime

        val imageName = restaurant.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
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

        launch{
            getAllReview()

            adapter = ReviewAdapter(reviews)
            binding.recyclerView.adapter = adapter
        }
        //Edit and Delete Reviews
//        val menuBtn = findViewById<ImageView>(R.id.option_menu)
//        menuBtn.setOnClickListener {
//            val popupMenu: PopupMenu = PopupMenu(this, menuBtn)
//            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.action_edit ->
//                        Toast.makeText(
//                            this@Restaurant_detail,
//                            "You Clicked : " + item.title,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    R.id.action_delete ->
//                        Toast.makeText(
//                            this@Restaurant_detail,
//                            "You Clicked : " + item.title,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                }
//                true
//            })
//            popupMenu.show()
//        }
//
//        binding.icFavorite.setOnClickListener { store() }
//
        binding.review.setOnClickListener {
            val intent = Intent(this, AddReview::class.java)
            intent.putExtra("restaurantId", restaurant.id)
            startActivity(intent)
        }
    }

    private fun store(){
        val uid = auth.currentUser!!.uid
        val restaurant = intent.getSerializableExtra("restaurant") as Restaurant

        val resId = restaurant.id
        val favHaspMap = hashMapOf(
            "$uid" to true,
        )
        if (resId != null) {
            database.collection("favorites").document(resId).set(favHaspMap, SetOptions.merge())
        }
        Toast.makeText(this, "Added to Favorite", Toast.LENGTH_LONG).show()
    }

    private suspend fun getAllReview(){
        val reviewRef = database.collection("reviews")
        val snapshot = reviewRef.get().await()

        for(document in snapshot.documents){
            val id = document.id
            val rating = document.get("rating").toString()
            val restaurantId = document.get("restaurantId").toString()
            val comment = document.get("review").toString()
            val userId = document.get("userId").toString()
            val user = getUser(userId)
            val username = user.firstName + " " + user.lastName
            val review = Review(
                id,
                comment,
                restaurantId,
                rating,
                userId,
                username
            )

            reviews.add(review)
        }
    }

    private suspend fun getUser(uid: String): User {
        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

}
