package com.example.gatepassmanagementsystem

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatepassmanagementsystem.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        // Set up the toolbar
//        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set up the navigation drawer
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_sign_out -> {
                    signOut()
                }
            }
             true
        }

        // Set up the floating action button
        binding.fab.setOnClickListener {
            startActivity(Intent(this, RequestGatePassActivity::class.java))
        }

        // Show the user's name and email
        auth.currentUser?.let {
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_name).text = it.displayName
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_email).text = it.email
        }

        // Load the user's gate pass requests
        binding.btn.setOnClickListener{
        loadGatePassRequests()}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
           return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun loadGatePassRequests() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated")
            return
        }

        val requestsCollection = db.collection("gate_pass_requests")
        val query = requestsCollection.whereEqualTo("user_id", currentUser.email)
        query.get().addOnSuccessListener { result ->
            binding.txt3.text = """
                User Id : ${result.documents.get(0).data?.get("user_id")}
                Start DateTime : ${result.documents.get(0).data?.get("start_datetime")}
                End DateTime : ${result.documents.get(0).data?.get("end_datetime")}
                Status : Approved
                GateName : Main Gate
                Created At : ${(result.documents.get(0).data?.get("created_at")).toString().trim()}
            """.trimIndent()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error getting gate pass requests", e)
        }
    }

}
