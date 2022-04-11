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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
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
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val userReview: TextView = itemView.findViewById(R.id.textReview)

        val menuBtn:ImageView = itemView.findViewById(R.id.optionMenu)
            init{
                menuBtn.setOnClickListener{
                    val popupMenu = PopupMenu(itemView.context, menuBtn)
                    popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_edit ->
                                editReview()
                            R.id.action_delete ->
                                deleteReview()
                        }
                        true
                    }
                    popupMenu.show()
                }

                val updateBtn: Button = itemView.findViewById(R.id.btnUpdate)
                updateBtn.setOnClickListener{
                    updateReview()
                }
                val cancelBtn: Button = itemView.findViewById(R.id.btnCancel)
                cancelBtn.setOnClickListener{
                    cancelReview()
                }

            }

        private fun deleteReview() {
            database = FirebaseFirestore.getInstance()

            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val selectedReview = sharedViewModel.selectedReview.value
            val reviewId = restaurantId + "_" + uid

            database.collection("reviews").document(reviewId).delete().addOnSuccessListener {
                Toast.makeText(itemView.context, "Your review has been removed", Toast.LENGTH_SHORT).show()
            }

        }

        private fun editReview(){
            val editReview : TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview : TextView = itemView.findViewById(R.id.textReview)
            val cancelBtn : Button = itemView.findViewById(R.id.btnCancel)
            val updateBtn : Button = itemView.findViewById(R.id.btnUpdate)

            editReview.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE
            displayReview.visibility = View.GONE


            database = FirebaseFirestore.getInstance()

            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            val reviewId = restaurantId + "_" + uid

            val reviewRef = FirebaseFirestore.getInstance().collection("reviews").document(reviewId)
            reviewRef.get().addOnSuccessListener {
                val comment: String = it.getString("review").toString()
                displayReview.text = comment
            }
            editReview.requestFocus()

        }

        private fun updateReview(){
            val editReview : TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview : TextView = itemView.findViewById(R.id.textReview)
            val cancelBtn : Button = itemView.findViewById(R.id.btnCancel)
            val updateBtn : Button = itemView.findViewById(R.id.btnUpdate)

            val reviewText = editReview.editText?.text.toString()

            database = FirebaseFirestore.getInstance()

            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            val reviewId = restaurantId + "_" + uid

            val revRef = database.collection("reviews").document(reviewId)

            val updates = hashMapOf<String, Any>(
                "review" to reviewText
            )

            revRef.update(updates)

            editReview.visibility = View.GONE
            cancelBtn.visibility = View.GONE
            updateBtn.visibility = View.GONE
            displayReview.visibility = View.VISIBLE

            Toast.makeText(itemView.context, "Your review has been updated", Toast.LENGTH_SHORT).show()
        }

        private fun cancelReview(){
            val editReview : TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview : TextView = itemView.findViewById(R.id.textReview)
            val cancelBtn : Button = itemView.findViewById(R.id.btnCancel)
            val updateBtn : Button = itemView.findViewById(R.id.btnUpdate)

            editReview.visibility = View.GONE
            cancelBtn.visibility = View.GONE
            updateBtn.visibility = View.GONE
            displayReview.visibility = View.VISIBLE

            Toast.makeText(itemView.context, "Cancel editing review", Toast.LENGTH_SHORT).show()
        }

    }


}