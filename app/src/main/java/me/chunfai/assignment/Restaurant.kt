package me.chunfai.assignment

import java.io.Serializable

data class Restaurant(
    var id: String? = null,
    var name: String? = null,
    var address: String? = null,
    var openTime: String? = null,
    var closeTime: String? = null,
    var contact: String? = null,
    var description: String? = null,
    var imageName: String? = null,
) : Serializable



