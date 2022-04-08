package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import me.chunfai.assignment.databinding.ActivityTestAllRestaurantsBinding

class TestAllRestaurants : AppCompatActivity() {

    private lateinit var binding: ActivityTestAllRestaurantsBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>

    private lateinit var database: FirebaseFirestore

    private lateinit var restaurants: MutableList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestAllRestaurantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        restaurants = mutableListOf()

        layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        retrieveAllRestaurants(object : FirebaseCallback {
            override fun onCallback() {}
        })
    }

    private fun retrieveAllRestaurants(firebaseCallback: FirebaseCallback) {
        database.collection("restaurants").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val name = document.get("name").toString()
                    val address = document.get("address").toString()
                    val openTime = document.get("openTime").toString()
                    val closeTime = document.get("closeTime").toString()
                    val contact = document.get("contact").toString()
                    val description = document.get("description").toString()
                    val imageName = document.get("imageName").toString()

                    firebaseCallback.onCallback()

                    val restaurant = Restaurant(
                        id,
                        name,
                        address,
                        openTime,
                        closeTime,
                        contact,
                        description,
                        imageName
                    )

                    restaurants.add(restaurant)
                }

                adapter = RecyclerAdapter(restaurants)
                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d("TestAllRestaurants", exception.toString())
            }
    }

    private interface FirebaseCallback {
        fun onCallback()
    }

}