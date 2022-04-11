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
import com.google.firebase.ktx.Firebase
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

        sharedViewModel.favouriteRestaurants.observe(viewLifecycleOwner) {
            favRestaurants = it

            adapter = FavouriteRestaurantAdapter(favRestaurants, sharedViewModel)

            binding.recyclerView.layoutManager = linearLayoutManager
            binding.recyclerView.adapter = adapter
        }

        return binding.root
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

        val newFavRestaurants = (activity as MainActivity).getFavouriteRestaurants(uid)
        sharedViewModel.setFavouriteRestaurants(newFavRestaurants)
    }

}