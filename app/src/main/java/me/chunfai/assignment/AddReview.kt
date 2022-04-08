package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.ActivityAddReviewBinding

class AddReview : AppCompatActivity() {

    private lateinit var binding: ActivityAddReviewBinding

    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        binding.btnSubmit.setOnClickListener { addReview() }
    }

    private fun addReview() {
        val rating = binding.ratingBar.rating.toString()
        val review = binding.editReview.text.toString()

        if (rating.isBlank() or review.isBlank()) {
            Toast.makeText(this, "All fields are required to input.", Toast.LENGTH_LONG).show()
            return
        }

        val restaurantId = intent.getStringExtra("restaurantId").toString()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val reviewId = restaurantId + "_" + uid

        val reviewHaspMap = hashMapOf(
            "rating" to rating,
            "review" to review,
            "userId" to uid,
            "restaurantId" to restaurantId
        )

        database.collection("reviews").document(reviewId).set(reviewHaspMap)

        Toast.makeText(this, "You have posted your review!", Toast.LENGTH_SHORT).show()

        finish()
    }

}