package me.chunfai.assignment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.findFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class RestaurantAdapter(
    private val restaurants: MutableList<Restaurant>,
    private val sharedViewModel: SharedViewModel
) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>(), Filterable {

    var restaurantsFilterList = mutableListOf<Restaurant>()

    private lateinit var mContext: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantCard: MaterialCardView = itemView.findViewById(R.id.cardRestaurant)
        val restaurantImage: ImageView = itemView.findViewById(R.id.imageRestaurant)
        val restaurantName: TextView = itemView.findViewById(R.id.textRestaurantName)
        val restaurantHours: TextView = itemView.findViewById(R.id.textRestaurantHours)
        val restaurantDescription: TextView = itemView.findViewById(R.id.textRestaurantDescription)

        init {
            restaurantCard.setOnClickListener {
                val restaurant = restaurantsFilterList[adapterPosition]

                sharedViewModel.setSelectedRestaurant(restaurant)

                val activity = itemView.context as MainActivity
                val fragment = itemView.findFragment<HomeFragment>()

                fragment.lifecycleScope.launch {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RestaurantDetailsFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    init {
        restaurantsFilterList = restaurants
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_restaurant, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return restaurantsFilterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) {
        val restaurant = restaurantsFilterList[position]
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
                    mContext,
                    "Failed to retrieve a restaurant image.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        holder.restaurantName.text = restaurant.name
        holder.restaurantHours.text =
            "$restaurantOpenTime - $restaurantCloseTime"
        holder.restaurantDescription.text = restaurant.description
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                restaurantsFilterList = if (charSearch.isEmpty()) {
                    restaurants
                } else {
                    val resultList = mutableListOf<Restaurant>()
                    for (row in restaurants) {
                        if (row.name!!.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = restaurantsFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                restaurantsFilterList = results?.values as MutableList<Restaurant>
                notifyDataSetChanged()
            }
        }
    }

}