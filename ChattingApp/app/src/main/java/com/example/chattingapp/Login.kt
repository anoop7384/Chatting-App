package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnlogin: Button
    private lateinit var btnsignup: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnforgot: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        editEmail = findViewById(R.id.edit_email)
        editPassword = findViewById(R.id.edit_password)
        btnlogin = findViewById(R.id.btn_login)
        btnsignup = findViewById(R.id.btn_signup)
        btnforgot = findViewById(R.id.forgotPassword)

        btnsignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        btnlogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            login(email, password)
        }

        btnforgot.setOnClickListener {
            val intent = Intent(this@Login, Reset_Password::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        if (email == "" || password == "") {
            Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@Login,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}