package com.example.chattingapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.example.chattingapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class User_Info : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var bio: TextView
    private lateinit var phone: TextView
    private lateinit var editBtn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var ImageUri: Uri
    private lateinit var localfile: File
    private lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val loading = Loading(this)
        loading.startLoading()
        image = findViewById(R.id.userInfoImage)
        name = findViewById(R.id.userInfoName)
        email = findViewById(R.id.userInfoEmail)
        bio = findViewById(R.id.userInfoBio)
        phone = findViewById(R.id.userInfoPhone)

        val infoUid = intent.getStringExtra("uid")

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        if (infoUid != null) {
            mDbRef.child(infoUid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name.setText(snapshot.child("name").getValue().toString())
                    bio.setText(snapshot.child("bio").getValue().toString())
                    phone.setText(snapshot.child("phone").getValue().toString())
                    email.setText(snapshot.child("email").getValue().toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@User_Info, error.message, Toast.LENGTH_SHORT).show()
                }

            })
        }
        storageReference = FirebaseStorage.getInstance().getReference("Users/$infoUid")
        try {

            localfile = File.createTempFile("tempfile", "")
            storageReference.getFile(localfile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                image.setImageBitmap(bitmap)
                loading.isDismiss()
            }.addOnFailureListener {
                loading.isDismiss()
            }


        } catch (e: IOException) {
            e.printStackTrace()
        }


    }
}