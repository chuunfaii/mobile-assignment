package me.chunfai.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _user = MutableLiveData(User())
    val user: LiveData<User> = _user

    private val _restaurants = MutableLiveData<MutableList<Restaurant>>()
    val restaurants: LiveData<MutableList<Restaurant>> = _restaurants

    fun setUser(currentUser: User) {
        _user.value = currentUser
    }

    fun setRestaurants(restaurantsList: MutableList<Restaurant>) {
        _restaurants.value = restaurantsList
    }

    fun resetData() {
        setRestaurants(restaurantsList = mutableListOf())
    }

}