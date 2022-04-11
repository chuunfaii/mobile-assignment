package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.FragmentAddReviewBinding

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private lateinit var binding: FragmentAddReviewBinding

    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var restaurant: Restaurant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_review, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        restaurant = sharedViewModel.selectedRestaurant.value!!

        val bottomNavigation =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.visibility = View.GONE

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "Add New Review"

        binding.btnSubmit.setOnClickListener { addReview() }

        return binding.root
    }

    private fun addReview() {
        val rating = binding.ratingBar.rating.toString()
        val review = binding.editReview.text.toString()

        if (rating.isBlank() or review.isBlank()) {
            Toast.makeText(context, "All fields are required.", Toast.LENGTH_LONG).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val restaurantId = restaurant.id.toString()
        val reviewId = restaurantId + "_" + uid

        val reviewHashMap = hashMapOf(
            "rating" to rating,
            "review" to review,
            "userId" to uid,
            "restaurantId" to restaurantId
        )

        database.collection("reviews").document(reviewId).set(reviewHashMap)

        Toast.makeText(context, "You have posted your review successfully.", Toast.LENGTH_SHORT)
            .show()

        requireActivity().supportFragmentManager.popBackStack()
    }

}