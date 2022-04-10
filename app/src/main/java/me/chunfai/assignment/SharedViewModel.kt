package me.chunfai.assignment

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    var user: User? = User()
    var restaurants: MutableList<Restaurant> = mutableListOf()

}