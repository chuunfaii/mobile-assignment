package me.chunfai.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast

class Restaurant_detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)


//        menu()
    }

//    private fun menu(){
//        val menu = findViewById<ImageView>(R.id.option_menu)
//        val popUpMenu =  PopupMenu(applicationContext,menu)
//        popUpMenu.inflate(R.menu.menu)
//        popUpMenu.setOnMenuItemClickListener {
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
//        }

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
    }
}