package com.example.gatepassmanagementsystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.gatepassmanagementsystem.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        auth = Firebase.auth
        db = Firebase.firestore
        // Set up the toolbar
//        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set up the navigation drawer
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
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
        binding.progressBar.visibility = View.VISIBLE
        val currUser = auth.currentUser?.email.toString()
        val imageCode: ImageView = findViewById(R.id.qr)
        if (sharedPref.contains("reg")) {


            val regNo = sharedPref.getString("reg", "")!!
            val qr = sharedPref.getString("qr", "")!!
            binding.tvQr.text = regNo
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_name).text = regNo
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_email).text = currUser
            val imgBytes = Base64.decode(qr, Base64.DEFAULT)
            val decodedImg =
                BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
            imageCode.setImageBitmap(decodedImg)
            binding.progressBar.visibility= View.GONE

        }else {
            db.collection("users").whereEqualTo("user_id", currUser).get()
                .addOnSuccessListener {
                    val mWriter = MultiFormatWriter()
                    try {
//BitMatrix class to encode entered text and set Width & Height
                        val myText = it.documents[0].data?.get("reg_no").toString()
                        binding.tvQr.text = myText
//Text will be entered here to generate QR code
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_name).text = myText
                        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_user_email).text = currUser
                            val mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400, 400)
                            val mEncoder = BarcodeEncoder()
                            val mBitmap = mEncoder.createBitmap(mMatrix) //creating bitmap of code
                            imageCode.setImageBitmap(mBitmap)
                        binding.progressBar.visibility = View.GONE
                            val baos = ByteArrayOutputStream()
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                            val encodedImg =
                                Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                            with(sharedPref.edit()) {
                                putString("qr", encodedImg)
                                putString("reg", myText)
                                apply()
                            }
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        sharedPref.edit().remove("qr").remove("reg").remove("email").apply()
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
