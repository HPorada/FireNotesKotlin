package com.example.firenoteskotlin.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.firenoteskotlin.MainActivity
import com.example.firenoteskotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    lateinit var lEmail: EditText
    lateinit var lPassword: EditText
    lateinit var loginNow: Button
    var forgetPass: TextView? = null
    lateinit var createAcc: TextView
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Login to FireNotes");
        lEmail = findViewById(R.id.email)
        lPassword = findViewById(R.id.lPassword)
        loginNow = findViewById(R.id.loginBtn)
        spinner = findViewById(R.id.progressBar3)
        forgetPass = findViewById(R.id.forgotPassword)
        createAcc = findViewById(R.id.createAccount)
        user = FirebaseAuth.getInstance().currentUser
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        showWarning()
        loginNow.setOnClickListener(View.OnClickListener {
            val mEmail = lEmail.getText().toString()
            val mPassword = lPassword.getText().toString()
            if (mEmail.isEmpty() || mPassword.isEmpty()) {
                Toast.makeText(this@Login, "Fields are required.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            // delete notes first
            spinner.setVisibility(View.VISIBLE)
            if (fAuth!!.currentUser!!.isAnonymous) {
                val user = fAuth!!.currentUser
                fStore!!.collection("notes").document(user!!.uid).delete().addOnSuccessListener {
                    Toast.makeText(
                        this@Login,
                        "All Temp Notes are Deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // delete Temp user
                user.delete().addOnSuccessListener {
                    Toast.makeText(
                        this@Login,
                        "Temp user Deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            fAuth!!.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener {
                Toast.makeText(this@Login, "Success !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this@Login, "Login Failed. " + e.message, Toast.LENGTH_SHORT)
                    .show()
                spinner.setVisibility(View.GONE)
            }
        })
        createAcc.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Register::class.java
                )
            )
        })
    }

    private fun showWarning() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Linking Existing Account Will delete all the temp notes. Create New Account To Save them.")
            .setPositiveButton(
                "Save Notes"
            ) { dialog, which ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton(
                "It's Ok"
            ) { dialog, which ->
                // do nothing
            }
        warning.show()
    }
}