package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityFavouritesBinding
import kotlin.coroutines.CoroutineContext

class Favourites : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityFavouritesBinding

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<FavouriteRestaurantAdapter.ViewHolder>

    private lateinit var database: FirebaseFirestore

    private lateinit var favRestaurantIds: MutableList<String>
    private lateinit var favRestaurants: MutableList<Restaurant>

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        favRestaurantIds = mutableListOf()
        favRestaurants = mutableListOf()

        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        launch {
            getFavouriteRestaurantIds(uid)
            getFavouriteRestaurants()

            adapter = FavouriteRestaurantAdapter(favRestaurants)
            binding.recyclerView.adapter = adapter
        }
    }

    private suspend fun getFavouriteRestaurants() {
        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        for (document in snapshot.documents) {
            if (favRestaurantIds.contains(document.id)) {
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

                favRestaurants.add(restaurant)
            }
        }
    }

    private suspend fun getFavouriteRestaurantIds(uid: String) {
        val favouritesRef = database.collection("favorites")
        val snapshot = favouritesRef.get().await()

        for (document in snapshot.documents) {
            if (document.getBoolean(uid) != null) {
                favRestaurantIds.add(document.id)
            }
        }
    }

}