package me.chunfai.assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

class ReviewAdapter(private val reviews: MutableList<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

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
                val popupMenu: PopupMenu = PopupMenu(itemView.context, menuBtn)
                popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_edit ->
                            Toast.makeText(
                                itemView.context,
                                "You Clicked : " + item.title,
                                Toast.LENGTH_SHORT
                            ).show()
                            //editReview()
                            R.id.action_delete->
                        Toast.makeText(
                                itemView.context,
                                "You Clicked : " + item.title,
                                Toast.LENGTH_SHORT
                            ).show()
                        //deleteReview()
                    }
                    true
            })
                popupMenu.show()
        }


    }


}
}