package com.example.chattingapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class User_Adapter(val context:Context, val userList:ArrayList<Contact>): RecyclerView.Adapter<User_Adapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder((view))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        lateinit var storageReference: StorageReference
        lateinit var localfile:File
        lateinit var bitmap: Bitmap
        val currentUser = userList[position]
        holder.name.text=currentUser.contactName
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+currentUser.contactID.toString())
        try {
            localfile= File.createTempFile("tempfile","")
            storageReference.getFile(localfile).addOnSuccessListener {
                bitmap= BitmapFactory.decodeFile(localfile.absolutePath)
                holder.image.setImageBitmap(bitmap)
            }.addOnFailureListener{

            }

        }catch (e: IOException) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("name",currentUser.contactName )
            intent.putExtra("uid",currentUser.contactID)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.txt_name)
        val image=itemView.findViewById<ImageView>(R.id.image_profile)
    }
}