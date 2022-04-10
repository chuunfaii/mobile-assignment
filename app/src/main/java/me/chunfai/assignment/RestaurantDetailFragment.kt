package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.BitmapFactory
import android.telecom.Call
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityRestaurantDetailBinding
import me.chunfai.assignment.databinding.FragmentRestaurantDetailBinding
import me.chunfai.assignment.databinding.ActivityReviewAdapterBinding
import java.io.File
import kotlin.coroutines.CoroutineContext

class RestaurantDetailFragment : Fragment(R.layout.fragment_restaurant_detail),CoroutineScope {

    private lateinit var binding: FragmentRestaurantDetailBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>

    private lateinit var users: MutableList<User>
    private lateinit var reviews: MutableList<Review>

    private lateinit var sharedViewModel: SharedViewModel

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_detail, container, false)

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        users = mutableListOf()
        reviews = mutableListOf()

        //Get Restaurant Details
        val restaurant = sharedViewModel.selectedRestaurant

        val openTime = restaurant?.openTime
        val closeTime = restaurant?.closeTime

        val imageName = restaurant?.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val localfile = File.createTempFile("TempImage", "jpg")
        imageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.restaurantImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()

        }

        binding.restaurantTitle.text = restaurant?.name
        binding.restaurantAddress.text = restaurant?.address
        binding.restaurantContact.text = "Contact Number : " + restaurant?.contact
        binding.restaurantBusinessHour.text = "Business Hour : $openTime - $closeTime"

        binding.icFavorite.setOnClickListener { store() }

        binding.review.setOnClickListener {
            val intent = Intent(context, AddReview::class.java)
            intent.putExtra("restaurantId", restaurant?.id)
            startActivity(intent)
        }

//    bindingReview.btnUpdate.setOnClickListener{
//        updateReview()
//    }

        return binding.root
    }

    override fun onResume() {
        super.onResume()


        viewLifecycleOwner.lifecycleScope.launch {
            getAllReview()
            getAvgRating()
            setRecyclerView()
        }
    }

    private fun store() {
        val uid = auth.currentUser!!.uid
        val restaurant = sharedViewModel.selectedRestaurant
        Log.d("Test",uid)


        val resId = restaurant?.id
        val favHaspMap = hashMapOf(
            "$uid" to true,
        )
        if (resId != null) {
            database.collection("favorites").document(resId).set(favHaspMap, SetOptions.merge())
        }
        Toast.makeText(context, "Added to Favorite", Toast.LENGTH_LONG).show()
    }

    private suspend fun getAllReview() {
        val selectedRestaurant = sharedViewModel.selectedRestaurant
        val reviewRef = database.collection("reviews")
        val snapshot = reviewRef.get().await()

        for (document in snapshot.documents) {
            if(document.get("restaurantId")== selectedRestaurant?.id) {
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
//        //val reviewId = review.id.toString()
//        val comment = review.review.toString()
//        /*val reviewRef = database.collection("reviews").document(reviewId)
//        reviewRef.get().addOnSuccessListener {
//            val comment: String? = it.getString("review")
//        }*/
//                displayReview.text = comment
//        editReview.requestFocus()
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editReview, 0)
//    }
//
//    private fun updateReview(){
//        val editReview = findViewById<EditText>(R.id.editReview)
//        val displayReview = findViewById<TextView>(R.id.user_review)
//        val cancelBtn = findViewById<Button>(R.id.btnCancel)
//        val updateBtn = findViewById<Button>(R.id.btnUpdate)
//
//        val reviewText = bindingReview.editReview.editText?.text.toString()
//
//        val review  = intent.getSerializableExtra("review") as Review
//        val reviewId = review.id.toString()
//
//        val revRef = database.collection("reviews").document(reviewId)
//
//        val updates = hashMapOf<String, Any>(
//            "review" to reviewText
//        )
//
//        revRef.update(updates)
//
//        editReview.visibility = View.GONE
//        cancelBtn.visibility = View.GONE
//        updateBtn.visibility = View.GONE
//        displayReview.visibility = View.VISIBLE
//
//        Toast.makeText(this, "Your review has been updated", Toast.LENGTH_SHORT).show()
//    }

//    private fun deleteReview() {
//        val review = intent.getSerializableExtra("review") as Review
//        val reviewId = review.id.toString()
//
//        val reviewRef = database.collection("reviews").document(reviewId)
//        reviewRef.delete()
//
//        Toast.makeText(this, "Your review has been removed", Toast.LENGTH_SHORT).show()
//    }

    private suspend fun getUser(uid: String): User {
        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

    private suspend fun getAvgRating() {
        //Try to get all review ratingBar data
        val allReview = database.collection("reviews")
        val snapshot = allReview.get().await()
        var reviewCount = 0
        var totalRating = 0f

        for (document in snapshot.documents) {
            val rating = document.get("rating").toString()
            reviewCount += 1
            totalRating += rating.toFloat()
        }
        var avgRating = totalRating / reviewCount

        binding.ratingBar.rating = avgRating
    }

    private fun setRecyclerView() {
        binding.recyclerView.layoutManager = linearLayoutManager
        adapter = ReviewAdapter(reviews)
        binding.recyclerView.adapter = adapter
    }
}

