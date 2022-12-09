package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var editEmail:EditText
    private lateinit var editPassword:EditText
    private lateinit var btnlogin:Button
    private lateinit var btnsignup:Button
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.getCurrentUser() != null) {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        editEmail=findViewById(R.id.edit_email)
        editPassword=findViewById(R.id.edit_password)
        btnlogin=findViewById(R.id.btn_login)
        btnsignup=findViewById(R.id.btn_signup)

        btnsignup.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        btnlogin.setOnClickListener{
            val email= editEmail.text.toString()
            val password = editPassword.text.toString()

            login(email,password)
        }
    }

    private fun login(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login,"User does not exist.",Toast.LENGTH_SHORT).show()
                }
            }
    }
}