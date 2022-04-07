package me.chunfai.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeButtonRegister = findViewById<Button>(R.id.homeButtonRegister)
        val homeButtonLogin = findViewById<Button>(R.id.homeButtonLogin)
        val homeButtonUserProfile = findViewById<Button>(R.id.homeButtonUserProfile)
        val homeButtonAddRestaurant = findViewById<Button>(R.id.homeButtonAddRestaurant)
        val homeButtonFavourite = findViewById<Button>(R.id.homeButtonFavourites)
        val homeButtonRestaurantDetail = findViewById<Button>(R.id.homeButtonRestaurantDetail)
        val homeButtonAddReview = findViewById<Button>(R.id.homeButtonAddReview)
        val homeButtonHomepage = findViewById<Button>(R.id.homeButtonHomepage)
        val homeButtonSearch = findViewById<Button>(R.id.homeButtonSearch)

        homeButtonRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        homeButtonLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        homeButtonUserProfile.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

        homeButtonAddRestaurant.setOnClickListener {
            val intent = Intent(this, AddRestaurant::class.java)
            startActivity(intent)
        }

        homeButtonFavourite.setOnClickListener {
            val intent = Intent(this, Favourites::class.java)
            startActivity(intent)
        }

        homeButtonRestaurantDetail.setOnClickListener {
            val intent = Intent(this, Restaurant_detail::class.java)
            startActivity(intent)
        }

        homeButtonAddReview.setOnClickListener {
            val intent = Intent(this, AddReview::class.java)
            startActivity(intent)
        }

        homeButtonHomepage.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        homeButtonSearch.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }
    }

}