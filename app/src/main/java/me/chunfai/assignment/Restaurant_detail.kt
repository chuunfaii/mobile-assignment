package me.chunfai.assignment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityRestaurantDetailBinding
import me.chunfai.assignment.databinding.ActivityReviewAdapterBinding
import java.io.File
import kotlin.coroutines.CoroutineContext


class Restaurant_detail : AppCompatActivity() ,CoroutineScope{

    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var bindingReview: ActivityReviewAdapterBinding
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

        bindingReview = ActivityReviewAdapterBinding.inflate(layoutInflater)
        setContentView(bindingReview.root)

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
            getAvgRating()

            adapter = ReviewAdapter(reviews)
            binding.recyclerView.adapter = adapter
        }
        //Edit and Delete Reviews
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
//                        editReview()
                    R.id.action_delete ->
                        Toast.makeText(
                            this@Restaurant_detail,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
//                        deleteReview()
                }
                true
            })
            popupMenu.show()
        }

        binding.icFavorite.setOnClickListener { store() }

        binding.review.setOnClickListener {
            val intent = Intent(this, AddReview::class.java)
            intent.putExtra("restaurantId", restaurant.id)
            startActivity(intent)
        }

//        bindingReview.btnUpdate.setOnClickListener{
//            updateReview()
//        }
//
//        bindingReview.btnCancel.setOnClickListener{
//            finish()
//        }
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
    
//    private fun editReview(){
//        val editReview = findViewById<EditText>(R.id.editReview)
//        val displayReview = findViewById<TextView>(R.id.user_review)
//        val cancelBtn = findViewById<Button>(R.id.btnCancel)
//        val updateBtn = findViewById<Button>(R.id.btnUpdate)
//
//        editReview.visibility = View.VISIBLE
//        cancelBtn.visibility = View.VISIBLE
//        updateBtn.visibility = View.VISIBLE
//        displayReview.visibility = View.GONE
//
//        val review  = intent.getSerializableExtra("review") as Review
//        val reviewId = review.id.toString()
//
//        val reviewRef = database.collection("reviews").document(reviewId)
//        reviewRef.get().addOnSuccessListener {
//            val comment: String? = it.getString("review")
//            displayReview.text = comment
//        }
//        editReview.requestFocus()
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editReview, 0)
//    }

//    private fun updateReview(){
//        val editReview = findViewById<EditText>(R.id.editReview)
//        val displayReview = findViewById<TextView>(R.id.user_review)
//        val cancelBtn = findViewById<Button>(R.id.btnCancel)
//        val updateBtn = findViewById<Button>(R.id.btnUpdate)
//
////        val reviewText = bindingReview.editReview.editText?.text.toString()
//
//        val review  = intent.getSerializableExtra("review") as Review
//        val reviewId = review.id.toString()
//
////        val reviewRef = Review(reviewText)
////        database.collection("reviews").document(reviewId).set(reviewRef)
////
////        editReview.visibility = View.GONE
////        cancelBtn.visibility = View.GONE
////        updateBtn.visibility = View.GONE
////        displayReview.visibility = View.VISIBLE
////
////        Toast.makeText(this, "Your review has been updated", Toast.LENGTH_SHORT).show()
//    }

    private fun deleteReview(){
        val review  = intent.getSerializableExtra("review") as Review
        val reviewId = review.id.toString()

        val reviewRef = database.collection("reviews").document(reviewId)
        reviewRef.delete()

        Toast.makeText(this, "Your review has been removed", Toast.LENGTH_SHORT).show()
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

    private suspend fun getAvgRating(){
        //Try to get all review ratingBar data
        val allReview = database.collection("reviews")
        val snapshot = allReview.get().await()
        var reviewCount = 0
        var totalRating = 0f

        for(document in snapshot.documents){
//            var rating = allReview.rating?.toInt()
            val rating = document.get("rating").toString()
            reviewCount += 1
            totalRating += rating.toFloat()
        }
        var avgRating = totalRating / reviewCount

        binding.ratingBar.rating = avgRating.toFloat()
    }
}
