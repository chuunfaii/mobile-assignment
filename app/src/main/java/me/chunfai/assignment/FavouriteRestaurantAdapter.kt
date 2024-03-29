package me.chunfai.assignment

import android.annotation.SuppressLint
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
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

class FavouriteRestaurantAdapter(
    private val favRestaurants: MutableList<Restaurant>,
    private var sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<FavouriteRestaurantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteRestaurantAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_favourite_restaurant, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavouriteRestaurantAdapter.ViewHolder, position: Int) {
        val restaurant = favRestaurants[position]
        val restaurantOpenTime = restaurant.openTime
        val restaurantCloseTime = restaurant.closeTime

        val imageName = restaurant.imageName
        val imageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val localFile = File.createTempFile("TempImage", "jpg")
        imageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.restaurantImage.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Failed to retrieve the image",
                    Toast.LENGTH_SHORT
                ).show()
            }

        holder.restaurantName.text = restaurant.name
        holder.restaurantHours.text = "$restaurantOpenTime - $restaurantCloseTime"
        holder.restaurantDescription.text = restaurant.description
    }

    override fun getItemCount() = favRestaurants.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantCard: MaterialCardView = itemView.findViewById(R.id.cardRestaurant)
        private val favouriteIcon: ImageView = itemView.findViewById(R.id.favouriteIcon)

        val restaurantImage: ImageView = itemView.findViewById(R.id.imageRestaurant)
        val restaurantName: TextView = itemView.findViewById(R.id.textRestaurantName)
        val restaurantHours: TextView = itemView.findViewById(R.id.textRestaurantHours)
        val restaurantDescription: TextView = itemView.findViewById(R.id.textRestaurantDescription)

        init {
            restaurantCard.setOnClickListener {
                val restaurant = favRestaurants[adapterPosition]

                sharedViewModel.setSelectedRestaurant(restaurant)

                val activity = itemView.context as MainActivity
                val fragment = itemView.findFragment<FavouritesFragment>()

                fragment.lifecycleScope.launch {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }

            favouriteIcon.setOnClickListener {
                val restaurant = favRestaurants[adapterPosition]

                val activity = itemView.context as MainActivity
                val fragment = itemView.findFragment<FavouritesFragment>()

                fragment.lifecycleScope.launch {
                    fragment.removeFromFavourites(restaurant)

                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, FavouritesFragment())
                        .commit()
                }
            }
        }
    }

}