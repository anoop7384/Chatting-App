package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.sql.DatabaseMetaData

//import { getAuth, createUserWithEmailAndPassword } from "firebase/auth";

class Signup : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnGoback: Button
    private lateinit var btnsignup: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()
        val loading = Loading(this)


        mAuth = FirebaseAuth.getInstance()

        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        editPhone = findViewById(R.id.edit_phone)
        editPassword = findViewById(R.id.edit_password)
        btnGoback = findViewById(R.id.btn_goback)
        btnsignup = findViewById(R.id.btn_signup)

        btnGoback.setOnClickListener {
            val intent = Intent(this, Send_Notification::class.java)
            startActivity(intent)
        }

        btnsignup.setOnClickListener {
            loading.startLoading()
            val name = editName.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val bio = "No bio added"

            signup(name, email, password, phone, bio, loading)
        }

    }

    private fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        bio: String,
        loading: Loading
    ) {
        if (name == "" || email == "" || password == "" || phone == "") {
            Toast.makeText(this@Signup, "Cannot leave a field empty", Toast.LENGTH_SHORT).show()
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!, phone, bio)
                    loading.isDismiss()
                    val intent = Intent(this@Signup, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    loading.isDismiss()
                    Toast.makeText(
                        this@Signup,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        phone: String,
        bio: String?
    ) {
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("users").child(uid).setValue(User(name, email, uid, phone, bio))
    }

}