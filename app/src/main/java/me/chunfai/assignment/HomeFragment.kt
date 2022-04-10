package me.chunfai.assignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.FragmentHomeBinding
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RestaurantAdapter

    private lateinit var database: FirebaseFirestore

    private lateinit var restaurants: MutableList<Restaurant>

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        database = FirebaseFirestore.getInstance()

        restaurants = mutableListOf()

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.VISIBLE

        binding.floatingActionButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddRestaurantFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        restaurants.clear()

        viewLifecycleOwner.lifecycleScope.launch {
            restaurants = sharedViewModel.restaurants
            setRecyclerView()
        }
    }

//    private suspend fun getRestaurantDetails() {
//        val restaurantRef = database.collection("restaurants")
//        val snapshot = restaurantRef.get().await()
//
//        for (document in snapshot.documents) {
//
//            val restaurant = getRestaurant(document.id)
//            Log.i("HomeFragment1",restaurant.toString())
//            restaurants.add(restaurant)
//
//        }
//    }

//    private suspend fun getRestaurant(restaurantId: String): Restaurant {
//        val restaurantRef = database.collection("restaurants")
//        val snapshot = restaurantRef.get().await()
//
//        for (document in snapshot.documents) {
//
//            val id = document.id
//            val name = document.get("name").toString()
//            val address = document.get("address").toString()
//            val openTime = document.get("openTime").toString()
//            val closeTime = document.get("closeTime").toString()
//            val contact = document.get("contact").toString()
//            val description = document.get("description").toString()
//            val imageName = document.get("imageName").toString()
//
//            return Restaurant(
//                id,
//                name,
//                address,
//                openTime,
//                closeTime,
//                contact,
//                description,
//                imageName
//            )
//
//        }
//
//        return Restaurant()
//    }

    private fun setRecyclerView() {
        binding.recyclerView.layoutManager = linearLayoutManager
        adapter = RestaurantAdapter(restaurants)
        binding.recyclerView.adapter = adapter
    }

}