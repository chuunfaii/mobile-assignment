package me.chunfai.assignment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File


class RestaurantAdapter(private val restaurants: MutableList<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_homepage_cardview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) {
        val restaurant = restaurants[position]

        val imageName = restaurant.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val localFile = File.createTempFile("TempImage", "jpg")
        imageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.restaurantImage.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                Toast.makeText(holder.itemView.context, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
            }

        holder.restaurantName.text = restaurant.name
    }

    override fun getItemCount() = restaurants.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)

        init {
            restaurantImage.setOnClickListener {
                val restaurant = restaurants[adapterPosition]
                val context = itemView.context

                val intent = Intent(context, Restaurant_detail::class.java)
                intent.putExtra("restaurant", restaurant)
                context.startActivity(intent)
            }

//            favoriteIcon.setOnClickListener {
//                val restaurant = restaurants[adapterPosition]
//                val context = itemView.context
//
//                (context as Favourites).launch {
//                    context.removeFromFavourites(restaurant)
//                }
//
//                val intent = Intent(context, Favourites::class.java)
//                context.startActivity(intent)
//                (context as Activity).finish()
//            }
        }
    }

}