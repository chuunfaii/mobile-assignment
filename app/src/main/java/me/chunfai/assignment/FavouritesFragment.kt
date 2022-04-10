package me.chunfai.assignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.FragmentFavouritesBinding
import kotlin.coroutines.CoroutineContext

class FavouritesFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FavouriteRestaurantAdapter

    private lateinit var database: FirebaseFirestore

    private lateinit var favRestaurants: MutableList<Restaurant>

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        database = FirebaseFirestore.getInstance()

        favRestaurants = mutableListOf()

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.VISIBLE

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.title = "Foodie"

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        favRestaurants.clear()

        viewLifecycleOwner.lifecycleScope.launch {
            getFavouriteRestaurants(uid)
            setRecyclerView()
        }
    }

    private suspend fun getFavouriteRestaurants(uid: String) {
        val favouritesRef = database.collection("favorites")
        val snapshot = favouritesRef.get().await()

        for (document in snapshot.documents) {
            if (document.getBoolean(uid) != null) {
                val restaurant = getRestaurant(document.id)
                Log.i("FavFragment1", restaurant.toString())
                favRestaurants.add(restaurant)
            }
        }
    }

    private suspend fun getRestaurant(restaurantId: String): Restaurant {
        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        for (document in snapshot.documents) {
            if (document.id == restaurantId) {
                val id = document.id
                val name = document.get("name").toString()
                val address = document.get("address").toString()
                val openTime = document.get("openTime").toString()
                val closeTime = document.get("closeTime").toString()
                val contact = document.get("contact").toString()
                val description = document.get("description").toString()
                val imageName = document.get("imageName").toString()

                return Restaurant(
                    id,
                    name,
                    address,
                    openTime,
                    closeTime,
                    contact,
                    description,
                    imageName
                )
            }
        }

        return Restaurant()
    }

    private fun setRecyclerView() {
        binding.recyclerView.layoutManager = linearLayoutManager
        Log.i("FavFrag2", favRestaurants.toString())
        adapter = FavouriteRestaurantAdapter(favRestaurants, sharedViewModel)
        binding.recyclerView.adapter = adapter
    }

    suspend fun removeFromFavourites(restaurant: Restaurant) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val restaurantId = restaurant.id.toString()

        val favouritesRef = database.collection("favorites").document(restaurantId)
        val updates = hashMapOf<String, Any>(
            uid to FieldValue.delete()
        )
        favouritesRef.update(updates).await()

        Toast.makeText(context, "${restaurant.name} has been removed from your favorites.", Toast.LENGTH_SHORT).show()
    }

}