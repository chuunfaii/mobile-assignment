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
    }

}