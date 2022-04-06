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

        binding.btnSave.setOnClickListener { updateProfile() }
    }

    private fun updateProfile() {
        val user = Firebase.auth.currentUser

        val originalEmail = user!!.email

        val firstName = binding.editFirstName.text.toString()
        val lastName = binding.editLastName.text.toString()
        val email = binding.editEmail.text.toString()
        val currentPassword = binding.editCurrentPassword.text.toString()
        val newPassword = binding.editNewPassword.text.toString()
        val newPasswordConfirmation = binding.editNewPasswordConfirmation.text.toString()

        // Input validations.
        when {
            firstName.isBlank() -> {
                Toast.makeText(this, "First name is required.", Toast.LENGTH_LONG).show()
                return
            }
            lastName.isBlank() -> {
                Toast.makeText(this, "Last name is required.", Toast.LENGTH_LONG).show()
                return
            }
            email.isBlank() -> {
                Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
                return
            }
            currentPassword.isBlank() -> {
                Toast.makeText(this, "Current password is required.", Toast.LENGTH_LONG).show()
                return
            }
            (newPassword.isNotBlank() and (newPassword != newPasswordConfirmation)) -> {
                Toast.makeText(this, "New password and new password confirmation do not match.", Toast.LENGTH_LONG).show()
                return
            }
        }

        auth.signInWithEmailAndPassword(originalEmail!!, currentPassword).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val userRef = database.collection("users").document(user.uid)

                // Update user email on Firebase authentication.
                user.updateEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("UserProfile", "User email address updated on Firebase authentication.")
                        }
                    }

                // Update user password.
                if (newPassword.isNotBlank() and (newPassword == newPasswordConfirmation)) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("UserProfile", "User password updated on Firebase authentication.")
                            }
                        }
                }

                // Update user details on Firestore.
                userRef.update(mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                )).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account details have been updated.", Toast.LENGTH_LONG).show()
                        Log.d("UserProfile", "User details updated on Firestore.")
                    }
                }

                // Clear other input fields.
                binding.editCurrentPassword.setText("")
                binding.editNewPassword.setText("")
                binding.editNewPasswordConfirmation.setText("")
            } else {
                Toast.makeText(this, "Incorrect current password.", Toast.LENGTH_SHORT).show()
            }
        }
        return
    }

}