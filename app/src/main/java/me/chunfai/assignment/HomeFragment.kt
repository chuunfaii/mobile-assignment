package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var database: FirebaseFirestore

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RestaurantAdapter

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var restaurants: MutableList<Restaurant>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        database = FirebaseFirestore.getInstance()

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        restaurants = mutableListOf()

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.VISIBLE

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.title = "Foodie"

        binding.restaurantSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        binding.floatingActionButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddRestaurantFragment())
                .addToBackStack(null)
                .commit()
        }

        sharedViewModel.restaurants.observe(viewLifecycleOwner) {
            restaurants = it
            setRecyclerView()
        }

        return binding.root
    }

    private fun setRecyclerView() {
        adapter = RestaurantAdapter(restaurants, sharedViewModel)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
    }

}