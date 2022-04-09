package me.chunfai.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityLoginBinding
import kotlin.coroutines.CoroutineContext

class Login : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener { login() }

        binding.textSignUp.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val email = binding.editEmail.editText?.text.toString()
        val password = binding.editPassword.editText?.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val uid = FirebaseAuth.getInstance().currentUser!!.uid

                launch {
                    val user = getUser(uid)

                    Toast.makeText(this@Login, "Logged in successfully.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@Login, MainActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)

                    finish()
                }
            } else {
                Toast.makeText(this, "Incorrect login credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getUser(uid: String): User {
        val userRef = database.collection("users").document(uid)
        val snapshot = userRef.get().await()
        val data = snapshot.data!!

        val firstName = data["firstName"].toString()
        val lastName = data["lastName"].toString()
        val email = data["email"].toString()

        return User(firstName, lastName, email)
    }

}
