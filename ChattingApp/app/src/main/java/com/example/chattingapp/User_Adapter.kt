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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.io.File
import java.io.IOException

class User_Adapter(val context: Context, val userList: ArrayList<Contact>) :
    RecyclerView.Adapter<User_Adapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder((view))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        lateinit var storageReference: StorageReference
        lateinit var localfile: File
        lateinit var bitmap: Bitmap
        val currentUser = userList[position]
//        holder.name.text = currentUser.contactName


        currentUser.contactID?.let { s ->
            FirebaseDatabase.getInstance().getReference("users").child(s.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.child("status").value?.let {
                            val status = it as Boolean
                            if (status) {
                                holder.status.text = "Online"
                            } else {
                                holder.status.text = "Offline"
                            }
                        }
                        holder.name.text = snapshot.child("name").value.toString()
                        Glide.with(context)
                            .load(snapshot.child("photoUri").value.toString())
                            .override(600,200)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                            .error(R.drawable.profile)
                            .into(holder.image)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        val senderRoom =
            currentUser.contactID + FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseDatabase.getInstance().getReference("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList: ArrayList<Message> = ArrayList()

//                    val q = snapshot.child("messages").orderByKey()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    if (messageList.isNotEmpty()) {
                        holder.lastMessage.text =
                            messageList[messageList.size - 1].message
                                ?: messageList[messageList.size - 1].message
                    }
                    messageList.clear()

                }

                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@User_Adapter, error.message, Toast.LENGTH_SHORT).show()
                }

            })


//
//        storageReference =
//            FirebaseStorage.getInstance().getReference("Users/" + currentUser.contactID.toString())
//        try {
//            localfile = File.createTempFile("tempfile", "")
//            storageReference.getFile(localfile).addOnSuccessListener {
//                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                holder.image.setImageBitmap(bitmap)
//            }.addOnFailureListener {
//
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.contactName)
            intent.putExtra("uid", currentUser.contactID)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txt_name)
        val image: ImageView = itemView.findViewById(R.id.image_profile)
        val status: TextView = itemView.findViewById(R.id.txt_status)
        val lastMessage: TextView = itemView.findViewById(R.id.txt_last_message)
    }
}