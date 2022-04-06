package me.chunfai.assignment

import java.io.Serializable

data class User(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null
) : Serializable