package me.chunfai.assignment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityMainBinding
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var sharedViewModel: SharedViewModel

    private val favouritesFragment = FavouritesFragment()
    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        setSupportActionBar(binding.topAppBar)

        replaceFragment(homeFragment)

        binding.bottomNavigation.selectedItemId = R.id.homeItem

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favouritesItem -> replaceFragment(favouritesFragment)
                R.id.homeItem -> replaceFragment(homeFragment)
                R.id.profileItem -> replaceFragment(profileFragment)
            }
            true
        }

        setViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setViewModel() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        launch {
            val user = getUser(uid)
            val favRestaurants = getFavouriteRestaurants(uid)

            sharedViewModel.setUser(user)
            sharedViewModel.setFavouriteRestaurants(favRestaurants)
        }
    }

    private suspend fun getUser(uid: String): User {
        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

    suspend fun getFavouriteRestaurants(uid: String): MutableList<Restaurant> {
        val favouritesRef = database.collection("favorites")
        val snapshot = favouritesRef.get().await()

        val favRestaurants = mutableListOf<Restaurant>()

        for (document in snapshot.documents) {
            if (document.getBoolean(uid) != null) {
                val restaurant = getRestaurant(document.id)
                favRestaurants.add(restaurant)
            }
        }

        return favRestaurants
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

}