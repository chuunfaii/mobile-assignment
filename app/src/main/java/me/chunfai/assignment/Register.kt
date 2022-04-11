package me.chunfai.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener { signUp() }

        binding.textLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp() {
        val firstName = binding.editFirstName.editText?.text.toString()
        val lastName = binding.editLastName.editText?.text.toString()
        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()
        val passwordConfirmation = binding.editPasswordConfirmation.editText?.text.toString()

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) {
            Toast.makeText(this, "All fields are required to input.", Toast.LENGTH_LONG).show()
            return
        }

        if (password != passwordConfirmation) {
            Toast.makeText(
                this,
                "Password and password confirmation do not match.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val uid: String? = FirebaseAuth.getInstance().currentUser?.uid

                writeUser(uid, firstName, lastName, email)

                Toast.makeText(
                    this,
                    "Account has been registered successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "This email already exists. Please try another email.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun writeUser(uid: String?, firstName: String, lastName: String, email: String) {
        val user = User(firstName, lastName, email)

        database.collection("users").document(uid!!).set(user)
    }

}