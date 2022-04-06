package me.chunfai.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.ActivityUserProfileBinding

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            database.collection("users").document(uid).get()
                .addOnSuccessListener {
                    binding.editFirstName.setText(it.get("firstName").toString())
                    binding.editLastName.setText(it.get("lastName").toString())
                    binding.editEmail.setText(it.get("email").toString())
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Error in loading file: $it")
                }
        } else {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

}