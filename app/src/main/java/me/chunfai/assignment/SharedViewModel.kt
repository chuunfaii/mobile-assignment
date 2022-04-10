package me.chunfai.assignment

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    var user: User? = User()
    var restaurants: MutableList<Restaurant> = mutableListOf()
    var selectedRestaurant: Restaurant? = Restaurant()

    fun resetData() {
        restaurants = mutableListOf()
        selectedRestaurant = Restaurant()
    }

}