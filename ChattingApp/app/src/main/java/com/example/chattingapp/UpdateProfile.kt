package com.example.chattingapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.i
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class UpdateProfile : AppCompatActivity() {

    private lateinit var image : ImageView
    private lateinit var name : TextView
    private lateinit var email: TextView
    private lateinit var bio:TextView
    private lateinit var phone:TextView
    private lateinit var saveBtn: Button
    private lateinit var uploadBtn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var userData: DatabaseReference
    private lateinit var imageUri : Uri
    private lateinit var cancel:Button
    private lateinit var localfile:File
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        val loading = Loading(this)
        loading.startLoading()
        image=findViewById(R.id.profileEditImage)
        uploadBtn=findViewById(R.id.profileUpload)
        name=findViewById(R.id.profileEditName)
        bio=findViewById(R.id.profileEditBio)
        phone=findViewById(R.id.profileEditPhone)
        saveBtn=findViewById(R.id.profileSave)
        cancel=findViewById(R.id.btnCancel)

        mAuth =FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        mDbRef.child(mAuth.currentUser?.uid.toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name.setText(snapshot.child("name").getValue().toString())
                bio.setText(snapshot.child("bio").getValue().toString())
                phone.setText(snapshot.child("phone").getValue().toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+mAuth.currentUser?.uid.toString())
        try {
            localfile = File.createTempFile("tempfile", "")
            storageReference.getFile(localfile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                image.setImageBitmap(bitmap)
                loading.isDismiss()
            }.addOnFailureListener{
                loading.isDismiss()
            }

        }catch (e: IOException) {
            e.printStackTrace()
        }

        uploadBtn.setOnClickListener{
            selectProfilePic()
        }

        saveBtn.setOnClickListener{
//            mDbRef.child(mAuth.currentUser?.uid.toString()).addValueEventListener(object:
//                ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.child("name").setValue(name.text.toString())
//                    bio.setText(snapshot.child("bio").getValue().toString())
//                    phone.setText(snapshot.child("phone").getValue().toString())
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//            mDbRef.child(mAuth.currentUser?.uid.toString()).child("name").setValue(name.text.toString()).addOnCompleteListener{
//                if(it.isSuccessful){
//                    uploadProfilePic()
//                }
//                else{
//                    Toast.makeText(this@UpdateProfile,"Cannot upload Profile Picture", Toast.LENGTH_SHORT).show()
//                }
//            }
            userData= FirebaseDatabase.getInstance().getReference("users").child(mAuth.currentUser?.uid.toString())
            userData.child("name").setValue(name.text.toString())
            userData.child("bio").setValue(bio.text.toString())
            userData.child("phone").setValue(phone.text.toString())
            uploadProfilePic()

        }
        cancel.setOnClickListener{
            val intent = Intent(this@UpdateProfile,Profile::class.java)
            startActivity(intent)
        }
    }

    private fun selectProfilePic() {
        val intent = Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(intent)
    }

    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){ result: ActivityResult ->
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
                    imageUri=selectedImageUri
                }
//                image.setImageURI(selectedImageUri)

                var selectedImageBitmap: Bitmap? = null
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        selectedImageUri
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                image.setImageBitmap(selectedImageBitmap)
            }
        }
    }

    private fun uploadProfilePic() {
        val loading = Loading(this)
        loading.startLoading()
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+mAuth.currentUser?.uid)
        storageReference.putFile(imageUri).addOnSuccessListener{
            Toast.makeText(this@UpdateProfile,"Image uploaded", Toast.LENGTH_SHORT).show()
            loading.isDismiss()
            val intent = Intent(this@UpdateProfile,Profile::class.java)
            startActivity(intent)
        }

    }
}