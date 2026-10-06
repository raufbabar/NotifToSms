package com.example.notiftosms

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var numberInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("notif_to_sms_prefs", MODE_PRIVATE)

        numberInput = findViewById(R.id.editTargetNumber)
        val saveButton: Button = findViewById(R.id.buttonSave)
        val openAccessButton: Button = findViewById(R.id.buttonOpenAccess)
        val statusText: TextView = findViewById(R.id.textStatus)

        numberInput.setText(prefs.getString("target_number", ""))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 100)
        }

        saveButton.setOnClickListener {
            val number = numberInput.text.toString().trim()
            if (number.isEmpty()) {
                Toast.makeText(this, "Number likhein", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putString("target_number", number).apply()
                Toast.makeText(this, "Number save ho gaya: $number", Toast.LENGTH_SHORT).show()
            }
        }

        openAccessButton.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        statusText.text = "1) Apna number save karein.\n" +
                "2) 'Notification Access Kholain' button dabayein aur is app ko allow karein.\n" +
                "3) Ab har social media notification is number par SMS ho jayegi."
    }
}
