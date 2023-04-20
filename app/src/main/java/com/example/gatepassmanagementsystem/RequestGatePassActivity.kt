package com.example.gatepassmanagementsystem

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gatepassmanagementsystem.databinding.ActivityRequestGatePassBinding
import java.util.Timer
import kotlin.concurrent.schedule
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.api.BackendRule.AuthenticationCase.valueOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.ServerTimestamps.valueOf
import com.google.firebase.ktx.Firebase
import java.lang.Character.valueOf
import java.lang.String.valueOf
import java.math.BigInteger.valueOf
import java.math.RoundingMode.valueOf
import java.security.Timestamp
import java.sql.Date.valueOf
import java.sql.Time.valueOf
import java.sql.Timestamp.valueOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.text.CharCategory.Companion.valueOf

class RequestGatePassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestGatePassBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestGatePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        // Set up the toolbar
//        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the date and time pickers
        val currentDate = Calendar.getInstance()
        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select start date")
            .setSelection(currentDate.timeInMillis)
            .build()
        val endDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setSelection(currentDate.timeInMillis)
            .build()
        val startTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select start time")
            .setHour(currentDate.get(Calendar.HOUR_OF_DAY))
            .setMinute(currentDate.get(Calendar.MINUTE))
            .build()
        val endTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select end time")
            .setHour(currentDate.get(Calendar.HOUR_OF_DAY))
            .setMinute(currentDate.get(Calendar.MINUTE))
            .build()

        // Set up the start date and time pickers
        binding.btnStartDate.setOnClickListener {
            startDatePicker.show(supportFragmentManager, "start_date_picker")
        }
        startDatePicker.addOnPositiveButtonClickListener {
            binding.btnStartDate.text = startDatePicker.headerText
        }
        binding.btnStartTime.setOnClickListener {
            startTimePicker.show(supportFragmentManager, "start_time_picker")
        }
        startTimePicker.addOnPositiveButtonClickListener {
            binding.btnStartTime.setText("${startTimePicker.hour.toString()} : ${startTimePicker.minute.toString()} ")
        }

        // Set up the end date and time pickers
        binding.btnEndDate.setOnClickListener {
            endDatePicker.show(supportFragmentManager, "end_date_picker")
        }
        endDatePicker.addOnPositiveButtonClickListener {
            binding.btnEndDate.text = endDatePicker.headerText
        }
        binding.btnEndTime.setOnClickListener {
            endTimePicker.show(supportFragmentManager, "end_time_picker")
        }
        endTimePicker.addOnPositiveButtonClickListener {
            binding.btnEndTime.setText("${startTimePicker.hour.toString()} : ${startTimePicker.minute.toString()}")
        }

        // Set up the submit button
        binding.btnSubmit.setOnClickListener {
            submitGatePassRequest()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitGatePassRequest() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to submit a gate pass request.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardActivity::class.java))
        }




        val startDate = binding.btnStartDate.text.toString().trim()
        if (startDate.isEmpty()) {
            Toast.makeText(this, "Start date is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val startTime = binding.btnStartTime.text.toString().trim()
        if (startTime.isEmpty()) {
            Toast.makeText(this, "Start time is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val endDate = binding.btnEndDate.text.toString().trim()
        if (endDate.isEmpty()) {
            Toast.makeText(this, "End date is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val endTime = binding.btnEndTime.text.toString().trim()
        if (endTime.isEmpty()) {
            Toast.makeText(this, "End time is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val startDateTime = "$startDate  $startTime"
        val endDateTime = "$endDate  $endTime"



        val gatePassRequest = hashMapOf(
            "user_id" to currentUser?.email,
            "start_datetime" to startDateTime,
            "end_datetime" to endDateTime,
            "status" to "Pending",
            "created_at" to FieldValue.serverTimestamp()
        )

        db.collection("gate_pass_requests")
            .add(gatePassRequest)
            .addOnSuccessListener {
                Toast.makeText(this, "Gate pass request submitted successfully.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit gate pass request: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
