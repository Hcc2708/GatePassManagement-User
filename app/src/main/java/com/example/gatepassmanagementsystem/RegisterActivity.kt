package com.example.gatepassmanagementsystem
import android.content.ContentValues.TAG
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.gatepassmanagementsystem.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        binding.buttonRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val confirm = binding.editPassword1.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            if(password != confirm){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            else {
                var lastreg: Int
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->

                        if (task.isSuccessful) {
                            db.collection("lastReg").document("lastRegistrationNumber").get()
                                .addOnSuccessListener {document->
                                    lastreg = document.data?.get("last").toString().toInt()
                                    Log.d(TAG, document.data.toString())
                                    val users = hashMapOf(
                                        "user_id" to email,
                                        "reg_no" to lastreg+1
                                    )
                                    val last = hashMapOf(
                                        "last" to lastreg+1
                                    )
                                    db.collection("lastReg").document("lastRegistrationNumber").set(last)
                                    db.collection("users")
                                        .add(users)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this, DashboardActivity::class.java))
                                            binding.progressBar.visibility = View.GONE
                                            finish()
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                    binding.progressBar.visibility = View.GONE
                                }

                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Account Exists... Please Login", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.tvLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
