package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.ActivityAddRestaurantBinding


class Homepage : AppCompatActivity()  {

    private lateinit var binding: ActivityAddRestaurantBinding

    private lateinit var database: FirebaseFirestore
    private lateinit var restaurantRecyclerView: RecyclerView
    private lateinit var restaurantArrayList: ArrayList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        restaurantRecyclerView = findViewById(R.id.rv)
        restaurantRecyclerView.layoutManager = LinearLayoutManager(this)
        restaurantRecyclerView.setHasFixedSize(true)

        restaurantArrayList = arrayListOf<Restaurant>()
        getRestaurantData()
    }


    private fun getRestaurantData(){
        database = FirebaseFirestore.getInstance()
        database.collection("restaurants").get()



    }

}