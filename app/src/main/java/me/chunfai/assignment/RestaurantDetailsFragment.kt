package me.chunfai.assignment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.FragmentRestaurantDetailsBinding
import java.io.File
import kotlin.coroutines.CoroutineContext

class RestaurantDetailsFragment : Fragment(R.layout.fragment_restaurant_details), CoroutineScope {

    private lateinit var binding: FragmentRestaurantDetailsBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>

    private lateinit var reviews: MutableList<Review>

    private lateinit var sharedViewModel: SharedViewModel

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_details, container,false)

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        reviews = mutableListOf()

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.GONE

        val restaurant = sharedViewModel.selectedRestaurant.value
        val restaurantOpenTime = restaurant?.openTime
        val restaurantClosingTime = restaurant?.closeTime

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = restaurant?.name

        val imageName = restaurant?.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val localFile = File.createTempFile("TempImage", "jpg")
        imageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageRestaurant.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }

        val favouriteRestaurants = sharedViewModel.favouriteRestaurants.value
        val selectedRestaurant = sharedViewModel.selectedRestaurant.value

        if (favouriteRestaurants!!.contains(selectedRestaurant)) {
            binding.favouriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

        binding.textRestaurantName.text = restaurant?.name
        binding.textRestaurantAddress.text = restaurant?.address
        binding.textRestaurantContact.text = "Contact No.: " + restaurant?.contact
        binding.textRestaurantBusinessHours.text =
            "Business Hours: $restaurantOpenTime - $restaurantClosingTime"
        binding.textRestaurantDescription.text = restaurant?.description

        binding.favouriteIcon.setOnClickListener {
            launch {
                addToFavourites()
            }
        }

        binding.review.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddReviewFragment())
                .addToBackStack(null)
                .commit()
        }

        launch {
            getReviews()
            setAverageRating()
            setRecyclerView()
        }

        return binding.root
    }

    private suspend fun addToFavourites() {
        val favouriteRestaurants = sharedViewModel.favouriteRestaurants.value
        val selectedRestaurant = sharedViewModel.selectedRestaurant.value

        if (favouriteRestaurants!!.contains(selectedRestaurant)) {
            Toast.makeText(context, "You have already favourite this restaurant.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val restaurant = sharedViewModel.selectedRestaurant.value!!
        val restaurantId = restaurant.id

        val favouritesHashMap = hashMapOf(
            uid to true,
        )

        if (restaurantId != null) {
            database.collection("favorites").document(restaurantId).set(favouritesHashMap)
        }

        Toast.makeText(context, "${restaurant.name} has been added to your favourites.", Toast.LENGTH_SHORT)
            .show()

        val newFavRestaurants = (activity as MainActivity).getFavouriteRestaurants(uid)
        sharedViewModel.setFavouriteRestaurants(newFavRestaurants)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
            .commit()
    }

    private suspend fun getReviews() {

        val selectedRestaurant = sharedViewModel.selectedRestaurant.value
        val reviewRef = database.collection("reviews")
        val snapshot = reviewRef.get().await()

        for (document in snapshot.documents) {
            if (document.get("restaurantId") == selectedRestaurant?.id) {
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

    private suspend fun getUser(uid: String): User {
        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

    private suspend fun setAverageRating() {
        val selectedRestaurant = sharedViewModel.selectedRestaurant.value
        val allReviews = database.collection("reviews")
        val snapshot = allReviews.get().await()

        var reviewCount = 0
        var totalRating = 0f

        for (document in snapshot.documents) {
            if (document.get("restaurantId") == selectedRestaurant?.id) {
                val rating = document.get("rating").toString()
                reviewCount += 1
                totalRating += rating.toFloat()
            }
        }

        val averageRating = totalRating / reviewCount

        binding.ratingBar.rating = averageRating
    }

    private fun setRecyclerView() {
        adapter = ReviewAdapter(reviews, sharedViewModel)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
    }

}