package me.chunfai.assignment

import java.io.Serializable

data class Review(
    var id: String? = null,
    var review: String? = null,
    var restaurantId: String? = null,
    var rating: String? = null,
    var userId: String? = null,
    var username: String?=null,
) : Serializable