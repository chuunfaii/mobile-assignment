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

        val button_click = findViewById<Button>(R.id.button)
        button_click.setOnClickListener{
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

    }
}