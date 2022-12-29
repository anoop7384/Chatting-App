package com.example.chattingapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class EditProfile : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var bio: TextView
    private lateinit var phone: TextView
    private lateinit var saveBtn: Button
    private lateinit var uploadBtn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var userData: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var cancel: Button
    private lateinit var localfile: File
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
//
//        val loading = Loading(this)
//        loading.startLoading()
        image = findViewById(R.id.profileEditImage)
        uploadBtn = findViewById(R.id.profileUpload)
        name = findViewById(R.id.profileEditName)
        bio = findViewById(R.id.profileEditBio)
        phone = findViewById(R.id.profileEditPhone)
        saveBtn = findViewById(R.id.profileSave)
        cancel = findViewById(R.id.btnCancel)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        mDbRef.child(mAuth.currentUser?.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name.text = snapshot.child("name").value.toString()
                bio.text = snapshot.child("bio").value.toString()
                phone.text = snapshot.child("phone").value.toString()
                Glide.with(this@EditProfile)
                    .load(snapshot.child("photoUri").value.toString())
                    .override(600,200)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                    .error(R.drawable.profile)
                    .into(image)
//                loading.isDismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfile, error.message, Toast.LENGTH_SHORT).show()
            }

        })

//        storageReference =
//            FirebaseStorage.getInstance().getReference("Users/" + mAuth.currentUser?.uid.toString())
//        try {
//            localfile = File.createTempFile("tempfile", "")
//            storageReference.getFile(localfile).addOnSuccessListener {
//                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                image.setImageBitmap(bitmap)
//                loading.isDismiss()
//            }.addOnFailureListener {
//                loading.isDismiss()
//                Toast.makeText(this@EditProfile, it.message, Toast.LENGTH_SHORT).show()
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        uploadBtn.setOnClickListener {
            selectProfilePic()
        }

        saveBtn.setOnClickListener {
            userData = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.currentUser?.uid.toString())
            userData.child("name").setValue(name.text.toString())
            userData.child("bio").setValue(bio.text.toString())
            userData.child("phone").setValue(phone.text.toString())
            uploadProfilePic()

        }
        cancel.setOnClickListener {
            val intent = Intent(this@EditProfile, Profile::class.java)
            startActivity(intent)
        }
    }

    private fun selectProfilePic() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(intent)
    }

    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == RESULT_OK
        ) {
            val data = result.data
            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri
                }
////                image.setImageURI(selectedImageUri)
//
//                var selectedImageBitmap: Bitmap? = null
//                try {
//                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
//                        this.contentResolver,
//                        selectedImageUri
//                    )
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                image.setImageBitmap(selectedImageBitmap)
                Glide.with(this@EditProfile)
                    .load(selectedImageUri)
                    .override(600,200)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(image)
            }
        }
    }

    private fun uploadProfilePic() {
        val loading = Loading(this)
        loading.startLoading()
        storageReference =
            FirebaseStorage.getInstance().getReference("Users/" + mAuth.currentUser?.uid)
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                userData = FirebaseDatabase.getInstance().getReference("users")
                    .child(mAuth.currentUser?.uid.toString())
                userData.child("photoUri").setValue(it.toString())
            }
            Toast.makeText(this@EditProfile, "Image uploaded", Toast.LENGTH_SHORT).show()
            loading.isDismiss()
            val intent = Intent(this@EditProfile, Profile::class.java)
            startActivity(intent)
            finish()
        }

    }
}