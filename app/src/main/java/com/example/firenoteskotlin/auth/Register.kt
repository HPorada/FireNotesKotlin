package com.example.firenoteskotlin.auth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.firenoteskotlin.MainActivity
import com.example.firenoteskotlin.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Register : AppCompatActivity() {
    lateinit var rUserName: EditText
    lateinit var rUserEmail: EditText
    lateinit var rUserPass: EditText
    lateinit var rUserConfPass: EditText
    lateinit var syncAccount: Button
    lateinit var loginAct: TextView
    lateinit var progressBar: ProgressBar
    var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        getSupportActionBar().setTitle("Connect to FireNotes");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rUserName = findViewById(R.id.userName)
        rUserEmail = findViewById(R.id.userEmail)
        rUserPass = findViewById(R.id.password)
        rUserConfPass = findViewById(R.id.passwordConfirm)
        syncAccount = findViewById(R.id.createAccount)
        loginAct = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar4)
        fAuth = FirebaseAuth.getInstance()
        loginAct.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Login::class.java
                )
            )
        })
        syncAccount.setOnClickListener(View.OnClickListener {
            val uUsername = rUserName.getText().toString()
            val uUserEmail = rUserEmail.getText().toString()
            val uUserPass = rUserPass.getText().toString()
            val uConfPass = rUserConfPass.getText().toString()
            if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()) {
                Toast.makeText(this@Register, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (uUserPass != uConfPass) {
                rUserConfPass.setError("Passwords do not match.")
            }
            progressBar.setVisibility(View.VISIBLE)
            val credential = EmailAuthProvider.getCredential(uUserEmail, uUserPass)
            fAuth!!.currentUser!!.linkWithCredential(credential).addOnSuccessListener {
                Toast.makeText(this@Register, "Notes are Synced.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                val usr = fAuth!!.currentUser
                val request = UserProfileChangeRequest.Builder()
                    .setDisplayName(uUsername)
                    .build()
                usr!!.updateProfile(request)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(
                    this@Register,
                    "Failed to Connect. Try Again.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.setVisibility(View.VISIBLE)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}