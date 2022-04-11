package me.chunfai.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel : ViewModel() {

    private var _user = MutableLiveData<User>()
    private val _restaurants = MutableLiveData<MutableList<Restaurant>>()
    private val _favouriteRestaurants = MutableLiveData<MutableList<Restaurant>>()
    private val _selectedRestaurant = MutableLiveData<Restaurant>()

    private val _review = MutableLiveData<MutableList<Review>>()
    private val _selectedReview = MutableLiveData<Review>()

    val user: LiveData<User>
        get() = _user

    val restaurants: LiveData<MutableList<Restaurant>>
        get() = _restaurants

    val favouriteRestaurants: LiveData<MutableList<Restaurant>>
        get() = _favouriteRestaurants

    val selectedRestaurant: LiveData<Restaurant>
        get() = _selectedRestaurant

    val review: LiveData<MutableList<Review>>
        get() = _review

    val selectedReview: LiveData<Review>
        get() = _selectedReview

    init {
        viewModelScope.launch {
            resetRestaurants()
        }
    }

    fun setUser(newUser: User) {
        _user.value = newUser
    }

    fun setFavouriteRestaurants(newFavouriteRestaurants: MutableList<Restaurant>) {
        _favouriteRestaurants.value = newFavouriteRestaurants
    }

    fun setSelectedRestaurant(newSelectedRestaurant: Restaurant) {
        _selectedRestaurant.value = newSelectedRestaurant
    }

    suspend fun resetRestaurants() {
        _restaurants.value = getAllRestaurants()
    }

    fun setReview(newReview: MutableList<Review>){
        _review.value = newReview
    }

    fun setSelectedReview(newSelectedReview: Review){
        _selectedReview.value = newSelectedReview
    }

    private suspend fun getAllRestaurants(): MutableList<Restaurant> {
        val database = FirebaseFirestore.getInstance()

        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        val restaurants: MutableList<Restaurant> = mutableListOf()

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

        return restaurants
    }

}