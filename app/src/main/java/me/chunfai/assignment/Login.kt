package me.chunfai.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val user = User()

                if (uid != null) {
                    database.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            user.firstName = doc.get("firstName").toString()
                            user.lastName = doc.get("lastName").toString()
                        }
                        .addOnFailureListener { doc ->
                            Log.e("Firestore", "Error in loading file: $doc")
                        }
                }

                Toast.makeText(this, "Logged in successfully.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, UserProfile::class.java)
                intent.putExtra("user", user)
                startActivity(intent)

                finish()
            } else {
                Toast.makeText(this, "Incorrect login credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}