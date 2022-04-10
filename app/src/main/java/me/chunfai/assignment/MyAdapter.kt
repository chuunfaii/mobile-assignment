package me.chunfai.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val restaurantList:ArrayList<Restaurant>):RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.activity_homepage_cardview,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = restaurantList[position]

//        holder.name.text = currentitem.name
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

//        val name : TextView = itemView.findViewById(R.id.tvRestaurantName)

    }

}