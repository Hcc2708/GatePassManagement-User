package com.example.gatepassmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.gatepassmanagementsystem.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "You need to register... ", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        binding.textForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        if(Firebase.auth.currentUser != null){
            this.finish()
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        super.onStart()
    }
}
