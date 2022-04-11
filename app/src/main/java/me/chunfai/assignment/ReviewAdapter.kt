package me.chunfai.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReviewAdapter(
    private val reviews: MutableList<Review>,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        val review = reviews[position]

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val rating = review.rating!!
        val userReview = review.review
        val userName = review.username

        if (uid != review.userId) {
            holder.menuBtn.visibility = View.GONE
        }

        holder.userName.text = userName
        holder.userReview.text = userReview
        holder.rating.rating = rating.toFloat()
    }

    override fun getItemCount() = reviews.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuBtn: ImageView = itemView.findViewById(R.id.optionMenu)
        private val updateBtn: Button = itemView.findViewById(R.id.btnUpdate)
        private val cancelBtn: Button = itemView.findViewById(R.id.btnCancel)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val userName: TextView = itemView.findViewById(R.id.userName)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val userReview: TextView = itemView.findViewById(R.id.textReview)

        init {
            menuBtn.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, menuBtn)
                popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_edit ->
                            editReview(reviews[adapterPosition])
                        R.id.action_delete ->
                            deleteReview()
                    }
                    true
                }
                popupMenu.show()
            }

            updateBtn.setOnClickListener { updateReview() }
            cancelBtn.setOnClickListener { cancelReview() }
        }

        private fun editReview(review: Review) {
            val editReview: TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview: TextView = itemView.findViewById(R.id.textReview)

            val updateBtn: Button = itemView.findViewById(R.id.btnUpdate)
            val cancelBtn: Button = itemView.findViewById(R.id.btnCancel)

            editReview.editText?.setText(review.review)

            editReview.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE
            displayReview.visibility = View.GONE

            editReview.requestFocus()
        }

        private fun deleteReview() {
            val activity = itemView.context as MainActivity

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val database = FirebaseFirestore.getInstance()
            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val reviewId = restaurantId + "_" + uid

            database.collection("reviews").document(reviewId).delete().addOnSuccessListener {
                Toast.makeText(
                    itemView.context,
                    "You have removed your review successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                    .commit()
            }
        }

        private fun updateReview() {
            val activity = itemView.context as MainActivity

            val editReview: TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview: TextView = itemView.findViewById(R.id.textReview)
            val updateBtn: Button = itemView.findViewById(R.id.btnUpdate)
            val cancelBtn: Button = itemView.findViewById(R.id.btnCancel)

            val review = editReview.editText?.text.toString()

            val database = FirebaseFirestore.getInstance()

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val reviewId = restaurantId + "_" + uid

            val updates = hashMapOf<String, Any>(
                "review" to review
            )

            database.collection("reviews").document(reviewId).update(updates)

            editReview.visibility = View.GONE
            cancelBtn.visibility = View.GONE
            updateBtn.visibility = View.GONE
            displayReview.visibility = View.VISIBLE

            Toast.makeText(itemView.context, "Your review has been updated.", Toast.LENGTH_SHORT)
                .show()

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                .commit()
        }

        private fun cancelReview() {
            val editReview: TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview: TextView = itemView.findViewById(R.id.textReview)
            val updateBtn: Button = itemView.findViewById(R.id.btnUpdate)
            val cancelBtn: Button = itemView.findViewById(R.id.btnCancel)

            editReview.visibility = View.GONE
            cancelBtn.visibility = View.GONE
            updateBtn.visibility = View.GONE
            displayReview.visibility = View.VISIBLE
        }

    }


}