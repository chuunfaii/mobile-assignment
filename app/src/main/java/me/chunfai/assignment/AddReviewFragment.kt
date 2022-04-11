package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.FragmentAddReviewBinding

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private lateinit var binding: FragmentAddReviewBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_review, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val restaurant = sharedViewModel.selectedRestaurant.value


        binding.btnSubmit.setOnClickListener{
            if (restaurant != null) {
                addReview(restaurant)
            }
        }

        return binding.root
    }

    private fun addReview(restaurant: Restaurant) {
        val rating = binding.ratingBar.rating.toString()
        val review = binding.editReview.text.toString()

        if (rating.isBlank() or review.isBlank()) {
            Toast.makeText(context, "All fields are required to input.", Toast.LENGTH_LONG).show()
            return
        }

        val restaurantId = restaurant.id.toString()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val reviewId = restaurantId + "_" + uid

        val reviewHaspMap = hashMapOf(
            "rating" to rating,
            "review" to review,
            "userId" to uid,
            "restaurantId" to restaurantId
        )

        database.collection("reviews").document(reviewId).set(reviewHaspMap)

        Toast.makeText(context, "You have posted your review!", Toast.LENGTH_SHORT).show()

        (activity as MainActivity).supportFragmentManager.popBackStackImmediate()

    }


}