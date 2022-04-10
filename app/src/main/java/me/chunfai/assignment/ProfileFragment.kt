package me.chunfai.assignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.chunfai.assignment.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private lateinit var sharedViewModel: SharedViewModel

    private var user: User? = User()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        user = sharedViewModel.user

        binding.editFirstName.editText?.setText(user?.firstName)
        binding.editLastName.editText?.setText(user?.lastName)
        binding.editEmail.editText?.setText(user?.email)

        binding.btnSave.setOnClickListener { updateProfile() }
        binding.btnLogout.setOnClickListener { logout() }

        return binding.root
    }

    private fun updateProfile() {
        val currentUser = Firebase.auth.currentUser
        val originalEmail = currentUser!!.email

        val firstName = binding.editFirstName.editText?.text.toString()
        val lastName = binding.editLastName.editText?.text.toString()
        val email = binding.editEmail.editText?.text.toString()
        val currentPassword = binding.editCurrentPassword.editText?.text.toString()
        val newPassword = binding.editNewPassword.editText?.text.toString()
        val newPasswordConfirmation = binding.editNewPasswordConfirmation.editText?.text.toString()

        // Input validations.
        when {
            firstName.isBlank() -> {
                Toast.makeText(context, "First name is required.", Toast.LENGTH_LONG).show()
                return
            }
            lastName.isBlank() -> {
                Toast.makeText(context, "Last name is required.", Toast.LENGTH_LONG).show()
                return
            }
            email.isBlank() -> {
                Toast.makeText(context, "Email is required.", Toast.LENGTH_LONG).show()
                return
            }
            currentPassword.isBlank() -> {
                Toast.makeText(context, "Current password is required.", Toast.LENGTH_LONG).show()
                return
            }
            (newPassword.isNotBlank() and (newPassword != newPasswordConfirmation)) -> {
                Toast.makeText(
                    context,
                    "New password and new password confirmation do not match.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        auth.signInWithEmailAndPassword(originalEmail!!, currentPassword)
            .addOnCompleteListener(activity as Activity) {
                if (it.isSuccessful) {
                    val userRef = database.collection("users").document(currentUser.uid)

                    // Update user email on Firebase authentication.
                    currentUser.updateEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(
                                    "UserProfile",
                                    "User email address updated on Firebase authentication."
                                )
                            }
                        }

                    // Update user password.
                    if (newPassword.isNotBlank() and (newPassword == newPasswordConfirmation)) {
                        currentUser.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(
                                        "UserProfile",
                                        "User password updated on Firebase authentication."
                                    )
                                }
                            }
                    }

                    // Update user details on Firestore.
                    userRef.update(
                        mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                        )
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                "Account details have been updated.",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("UserProfile", "User details updated on Firestore.")

                            // Update current user in SharedViewModel.
                            sharedViewModel.user = User(firstName, lastName, email)

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, ProfileFragment())
                                .commit()
                        }
                    }
                } else {
                    Toast.makeText(context, "Incorrect current password.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun logout() {
        val intent = Intent(context, Login::class.java)
        startActivity(intent)
        activity?.finish()
    }

}