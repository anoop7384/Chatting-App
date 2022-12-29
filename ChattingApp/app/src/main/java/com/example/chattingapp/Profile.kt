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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chattingapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class Profile : AppCompatActivity() {
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
    private lateinit var resetBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
//        val loading = Loading(this)
//        loading.startLoading()
        image = findViewById(R.id.profileImage)
        name = findViewById(R.id.profileName)
        email = findViewById(R.id.profileEmail)
        bio = findViewById(R.id.profileBio)
        phone = findViewById(R.id.profilePhone)
        editBtn = findViewById(R.id.profileEdit)
        resetBtn = findViewById(R.id.profileReset)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        mDbRef.child(mAuth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name.text = snapshot.child("name").value.toString()
                    bio.text = snapshot.child("bio").value.toString()
                    phone.text = snapshot.child("phone").value.toString()
                    Glide.with(this@Profile)
                        .load(snapshot.child("photoUri").value.toString())
                        .override(600,200)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                        .error(R.drawable.profile)
                        .into(image)
//                    loading.isDismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Profile, error.message, Toast.LENGTH_SHORT).show()
                }

            })
        email.text = mAuth.currentUser?.email.toString()
//        storageReference =
//            FirebaseStorage.getInstance().getReference("Users/" + mAuth.currentUser?.uid.toString())
//        try {
//
//            localfile = File.createTempFile("tempfile", "")
//            storageReference.getFile(localfile).addOnSuccessListener {
//                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                image.setImageBitmap(bitmap)
//                loading.isDismiss()
//            }.addOnFailureListener {
//                loading.isDismiss()
//            }
//
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }


        editBtn.setOnClickListener {
            val inten = Intent(this@Profile, EditProfile::class.java)
            startActivity(inten)

        }
        resetBtn.setOnClickListener {
            val inten = Intent(this@Profile, Reset_Password::class.java)
            startActivity(inten)
        }
    }
}