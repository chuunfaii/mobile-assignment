package me.chunfai.assignment

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File


class FavouriteRestaurantAdapter(private val restaurants: MutableList<Restaurant>, private var sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<FavouriteRestaurantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteRestaurantAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_favourite_restaurant, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouriteRestaurantAdapter.ViewHolder, position: Int) {
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
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)

        init {
            restaurantImage.setOnClickListener {
                val restaurant = restaurants[adapterPosition]

                sharedViewModel.selectedRestaurant = restaurant

                val activity = itemView.context as MainActivity
                val currentFragment = itemView.findFragment<FavouritesFragment>()
                val newFragment = RestaurantDetailFragment()

                currentFragment.lifecycleScope.launch {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, newFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

            favoriteIcon.setOnClickListener {
                val restaurant = restaurants[adapterPosition]

                val activity = itemView.context as MainActivity
                val currentFragment = itemView.findFragment<FavouritesFragment>()
                val newFragment = FavouritesFragment()

                currentFragment.lifecycleScope.launch {
                    currentFragment.removeFromFavourites(restaurant)

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, newFragment)
                        .commit()
                }
            }
        }
    }

}