package me.chunfai.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReviewAdapter(private val reviews: MutableList<Review>, private val sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

    private lateinit var database: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        val review = reviews[position]

//        sharedViewModel.setSelectedReview(review)

        val rating1 = review.rating
        val userReview = review.review
        val username = review.username

        holder.username.text = username
        holder.userReview.text = userReview
        holder.rating.rating = rating1!!.toFloat()

    }

    override fun getItemCount() = reviews.size


    inner class ViewHolder(itemView: View, review: Review) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.username)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val userReview: TextView = itemView.findViewById(R.id.textReview)
        var uid = FirebaseAuth.getInstance().currentUser!!.uid

        internal fun bind(position: Int){
            username.text = reviews[position].username
            rating.rating = reviews[position].rating?.toFloat()!!
            userReview.text = reviews[position].review
            uid = reviews[position].userId.toString()

        }

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
            val activity = itemView.context as MainActivity

            database = FirebaseFirestore.getInstance()
//            val userId = sharedViewModel.selectedReview.value?.userId
//            Log.d("UserId",sharedViewModel.selectedReview.value?.userId.toString())
            val restaurantId = sharedViewModel.selectedRestaurant.value?.id
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val reviewId = restaurantId + "_" + uid

//            if(uid == userId) {
                database.collection("reviews").document(reviewId).delete().addOnSuccessListener {
                    Toast.makeText(
                        itemView.context,
                        "Your review has been removed",
                        Toast.LENGTH_SHORT
                    ).show()

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                        .commit()
                }
//            }else{
//                Toast.makeText(
//                    itemView.context,
//                    "You cannot delete other review.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }

        }

        private fun editReview(){
            val editReview : TextInputLayout = itemView.findViewById(R.id.editReview)
            val displayReview : TextView = itemView.findViewById(R.id.textReview)
            val cancelBtn : Button = itemView.findViewById(R.id.btnCancel)
            val updateBtn : Button = itemView.findViewById(R.id.btnUpdate)
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
//            val userId = sharedViewModel.selectedReview.value?.userId
            val restaurantId = sharedViewModel.selectedRestaurant.value?.id

            val reviewId = restaurantId + "_" + uid

//            if(uid==userId){
                editReview.visibility = View.VISIBLE
                cancelBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.VISIBLE
                displayReview.visibility = View.GONE

                database = FirebaseFirestore.getInstance()

                val reviewRef = FirebaseFirestore.getInstance().collection("reviews").document(reviewId)
                reviewRef.get().addOnSuccessListener {
                    val comment: String = it.getString("review").toString()
                    displayReview.text = comment
                }
                editReview.requestFocus()
//            }else{
//                Toast.makeText(itemView.context, "You cannot edit other review", Toast.LENGTH_SHORT)
//                    .show()
//            }

        }

        private fun updateReview(){
            val activity = itemView.context as MainActivity
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

                Toast.makeText(itemView.context, "Your review has been updated", Toast.LENGTH_SHORT)
                    .show()

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                    .commit()

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