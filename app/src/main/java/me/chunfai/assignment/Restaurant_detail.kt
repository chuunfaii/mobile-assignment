package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.ActivityRestaurantDetailBinding

class Restaurant_detail : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuBtn = findViewById<ImageView>(R.id.option_menu)
        menuBtn.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, menuBtn)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit ->
                        Toast.makeText(
                            this@Restaurant_detail,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                    R.id.action_delete ->
                        Toast.makeText(
                            this@Restaurant_detail,
                            "You Clicked : " + item.title,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                true
            })
            popupMenu.show()
        }

        binding.icFavorite.setOnClickListener { store() }
    }

    private fun store(){
        Toast.makeText(this, "Image View Clicked", Toast.LENGTH_LONG).show()
    }
}

//    private fun menu(){
////        val popupMenu: PopupMenu = PopupMenu(this,button)
////        popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
//
//        val testMenu = findViewById<ImageView>(R.id.option_menu)
//        val popUpMenu =  PopupMenu(this, testMenu)
//        popUpMenu.menuInflater.inflate(R.menu.menu, popUpMenu.menu)
//        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
//            when(it.itemId){
//                R.id.action_edit->{
//                    Toast.makeText(applicationContext,"Edit Pressed",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_delete->{
//                    Toast.makeText(applicationContext,"Delete Pressed",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else ->true
//            }
//            true
//        })
//        popUpMenu.show()

//        menu.setOnLongClickListener{
//            try{
//                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
//                popup.isAccessible = true
//                val menus = popup.get(popUpMenu)
//                menus.javaClass
//                    .getDeclaredMethod("setForcedShowIcon",Boolean::class.java)
//                    .invoke(menus,true)
//
//            }catch(e:Exception){
//                e.printStackTrace()
//            }finally {
//                popUpMenu.show()
//            }
//            true
//        }
//    }
//}