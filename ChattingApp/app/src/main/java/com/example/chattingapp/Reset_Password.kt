package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Reset_Password : AppCompatActivity() {


    private lateinit var emailReset: EditText
    private lateinit var btnReset: Button
    private lateinit var btnGoBack: Button
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mAuth = FirebaseAuth.getInstance()
        emailReset = findViewById(R.id.reset_email)
        btnReset = findViewById(R.id.btn_reset)
        btnGoBack = findViewById(R.id.btn_gotologin)

        btnReset.setOnClickListener {

            val email = emailReset.text.toString().trim()
            if (emailReset.text.toString() == "") {
                Toast.makeText(this@Reset_Password, "Email is required", Toast.LENGTH_SHORT).show()
            } else {

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@Reset_Password,
                            "Please check your email inbox or spam ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val err = task.exception?.message
                        Toast.makeText(this@Reset_Password, err.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }



        btnGoBack.setOnClickListener {
            finish()
        }


    }
}