package me.chunfai.assignment

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

    private lateinit var user: User

    private lateinit var binding: ActivityUserProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        user = intent.getSerializableExtra("user") as User

        binding.editFirstName.setText(user.firstName)
        binding.editLastName.setText(user.lastName)
        binding.editEmail.setText(user.email)

        binding.btnSave.setOnClickListener { updateProfile() }
    }

    private fun updateProfile() {
        val currentUser = Firebase.auth.currentUser

        val originalEmail = currentUser!!.email

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
                val userRef = database.collection("users").document(currentUser.uid)

                // Update user email on Firebase authentication.
                currentUser.updateEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("UserProfile", "User email address updated on Firebase authentication.")
                        }
                    }

                // Update user password.
                if (newPassword.isNotBlank() and (newPassword == newPasswordConfirmation)) {
                    currentUser.updatePassword(newPassword)
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

                // Update current user model.
                user.firstName = firstName
                user.lastName = lastName
                user.email = email

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