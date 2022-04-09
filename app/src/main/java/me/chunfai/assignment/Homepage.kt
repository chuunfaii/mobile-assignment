package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext
import me.chunfai.assignment.databinding.ActivityHomepageBinding


class Homepage : AppCompatActivity() , CoroutineScope {

    private lateinit var binding: ActivityHomepageBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
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

        restaurants = mutableListOf()

        layoutManager = LinearLayoutManager(this)
        binding.homepageRecyclerView.layoutManager = layoutManager

        launch {
            getAllRestaurants()

            adapter = RecyclerAdapter(restaurants)
            binding.homepageRecyclerView.adapter = adapter
        }
    }

    private suspend fun getAllRestaurants() {
        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        for (document in snapshot.documents) {
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