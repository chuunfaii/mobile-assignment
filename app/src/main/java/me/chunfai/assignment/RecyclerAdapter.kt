package me.chunfai.assignment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val restaurants: MutableList<Restaurant>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.test_restaurant_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.btnRestaurant.text = restaurants[position].name
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnRestaurant: Button = itemView.findViewById(R.id.btnRestaurant)

        init {
            btnRestaurant.setOnClickListener {
                val restaurant = restaurants[adapterPosition]
                val context = itemView.context

                val intent = Intent(context, RestaurantDetailsFragment::class.java)
                intent.putExtra("restaurant", restaurant)
                context.startActivity(intent)
            }
        }
    }
}