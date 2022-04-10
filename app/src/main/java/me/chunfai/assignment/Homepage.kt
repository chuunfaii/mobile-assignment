package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext
import me.chunfai.assignment.databinding.ActivityHomepageBinding


class Homepage : AppCompatActivity() , CoroutineScope {

    private lateinit var binding: ActivityHomepageBinding

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<RestaurantAdapter.ViewHolder>

    private lateinit var database: FirebaseFirestore

    private lateinit var restaurantIds: MutableList<String>
    private lateinit var restaurants: MutableList<Restaurant>

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        restaurantIds = mutableListOf()
        restaurants = mutableListOf()

        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        launch {
            getRestaurantIds(uid)
            getRestaurants()

//            adapter = RestaurantAdapter(restaurants, share)
            binding.recyclerView.adapter = adapter
        }
    }

    private suspend fun getRestaurants() {
        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        for (document in snapshot.documents) {
            if (restaurantIds.contains(document.id)) {
                val id = document.id
                val name = document.get("name").toString()
                val address = document.get("address").toString()
                val openTime = document.get("openTime").toString()
                val closeTime = document.get("closeTime").toString()
                val contact = document.get("contact").toString()
                val description = document.get("description").toString()
                val imageName = document.get("imageName").toString()

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
        }
    }

    private suspend fun getRestaurantIds(uid: String) {
        val favouritesRef = database.collection("restaurants")
        val snapshot = favouritesRef.get().await()

        for (document in snapshot.documents) {
            if (document.getBoolean(uid) != null) {
                restaurantIds.add(document.id)
            }
        }
    }



}