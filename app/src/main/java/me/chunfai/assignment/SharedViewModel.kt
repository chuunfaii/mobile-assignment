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

    val user: LiveData<User>
        get() = _user

    val restaurants: LiveData<MutableList<Restaurant>>
        get() = _restaurants

    val favouriteRestaurants: LiveData<MutableList<Restaurant>>
        get() = _favouriteRestaurants

    val selectedRestaurant: LiveData<Restaurant>
        get() = _selectedRestaurant

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

    suspend fun getReviews(): MutableList<Review> {
        val database = FirebaseFirestore.getInstance()

        val reviews = mutableListOf<Review>()

        val reviewsRef = database.collection("reviews")
        val snapshot = reviewsRef.get().await()

        for (document in snapshot.documents) {
            if (document.get("restaurantId") == selectedRestaurant.value?.id) {
                val id = document.id
                val rating = document.get("rating").toString()
                val restaurantId = document.get("restaurantId").toString()
                val comment = document.get("review").toString()
                val userId = document.get("userId").toString()
                val user = getUser(userId)
                val username = user.firstName + " " + user.lastName
                val review = Review(
                    id,
                    comment,
                    restaurantId,
                    rating,
                    userId,
                    username
                )

                reviews.add(review)
            }
        }

        return reviews
    }

    private suspend fun getUser(uid: String): User {
        val database = FirebaseFirestore.getInstance()

        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

}