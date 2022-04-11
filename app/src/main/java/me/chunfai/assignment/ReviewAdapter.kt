package me.chunfai.assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

class ReviewAdapter(private val reviews: MutableList<Review>, private val sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

    private lateinit var database: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_review_adapter, parent, false)
        return ViewHolder(itemView)


    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        val review = reviews[position]

        val rating1 = review.rating
        val userReview = review.review
        val username = review.username

        holder.username.text = username
        holder.userReview.text = userReview
        holder.rating.rating = rating1!!.toFloat()


    }

    override fun getItemCount() = reviews.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.username)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar1)
        val userReview: TextView = itemView.findViewById(R.id.user_review)

        val menuBtn:ImageView = itemView.findViewById(R.id.option_menu)
            init{
                menuBtn.setOnClickListener{
                    val popupMenu = PopupMenu(itemView.context, menuBtn)
                    popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit ->
                                Toast.makeText(
                                    itemView.context,
                                    "You Clicked : " + item.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            //editReview()
                            R.id.action_delete ->
                                /*Toast.makeText(
                                    itemView.context,
                                    "You have deleted your review",
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                deleteReview()
                        }
                        true
                    }
                    popupMenu.show()
                }

            }

        private fun deleteReview() {
            database = FirebaseFirestore.getInstance()
            //val reviewId = itemView.id.toString()
            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val selectedReview = sharedViewModel.selectedReview.value
            val reviewId = restaurantId + "_" + uid
//            val reviewRef = FirebaseFirestore.getInstance().collection("reviews").document(reviewId)
//            reviewRef.delete().addOnSuccessListener {
//                Toast.makeText(itemView.context, "Your review has been removed", Toast.LENGTH_SHORT).show()
//            }
//            database.collection("reviews").document(reviewId).delete()
            database.collection("reviews").document(reviewId).delete().addOnSuccessListener {
                Toast.makeText(itemView.context, "You have deleted your review", Toast.LENGTH_SHORT).show()
            }

        }

        /*private fun editReview(){
            val editReview : TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview : TextView = itemView.findViewById(R.id.user_review)
            val cancelBtn : Button = itemView.findViewById(R.id.btnCancel)
            val updateBtn : Button = itemView.findViewById(R.id.btnUpdate)

            editReview.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE
            displayReview.visibility = View.GONE

            /*val review  = intent.getSerializableExtra("review") as Review
            val reviewId = review.id.toString()*/

            val reviewRef = FirebaseFirestore.getInstance().collection("reviews").document("4mxSeCPHmUzKJcB7pkP0_XSWoHwBbbEfVAzXcTPI8wv7aamb2")
            reviewRef.get().addOnSuccessListener {
                val comment: String? = it.getString("review")
                displayReview.text = comment.toString()
            }
            editReview.requestFocus()

        }*/

    }


}