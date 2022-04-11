package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var database: FirebaseFirestore

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FavouriteRestaurantAdapter

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var favRestaurants: MutableList<Restaurant>

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)

        database = FirebaseFirestore.getInstance()

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

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
            setRecyclerView()
        }

        return binding.root
    }

    suspend fun removeFromFavourites(restaurant: Restaurant) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val restaurantId = restaurant.id.toString()

        val updates = hashMapOf<String, Any>(
            uid to FieldValue.delete()
        )

        database.collection("favorites").document(restaurantId).update(updates).await()

        Toast.makeText(
            context,
            "${restaurant.name} has been removed from your favorites.",
            Toast.LENGTH_SHORT
        ).show()

        val newFavRestaurants = (activity as MainActivity).getFavouriteRestaurants(uid)
        sharedViewModel.setFavouriteRestaurants(newFavRestaurants)
    }

    private fun setRecyclerView() {
        adapter = FavouriteRestaurantAdapter(favRestaurants, sharedViewModel)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
    }

}