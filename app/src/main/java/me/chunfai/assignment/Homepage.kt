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
import me.chunfai.assignment.databinding.ActivityHomepageBinding


class Homepage : AppCompatActivity()  {

    private lateinit var binding: ActivityHomepageBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
    private lateinit var restaurants: MutableList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        restaurants = mutableListOf()

        layoutManager = LinearLayoutManager(this)
        binding.homepageRecyclerView.layoutManager = layoutManager

        getRestaurantData(object : FirebaseCallback {
            override fun onCallback() {}
        })
    }


    private fun getRestaurantData(firebaseCallback: FirebaseCallback){
        database.collection("restaurants").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val name = document.get("name").toString()
//

                    firebaseCallback.onCallback()

                    val restaurant = Restaurant(
                        id,
                        name
                    )

                    restaurants.add(restaurant)
                }

                adapter = RecyclerAdapter(restaurants)
                binding.homepageRecyclerView.layoutManager = layoutManager
            }
            .addOnFailureListener { exception ->
                Log.d("TestAllRestaurants", exception.toString())
            }



    }

    private interface FirebaseCallback {
        fun onCallback()
    }

}