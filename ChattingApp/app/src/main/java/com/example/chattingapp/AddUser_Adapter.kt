package com.example.chattingapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class AddUser_Adapter(val context: Context, var userList: ArrayList<User>) :
    RecyclerView.Adapter<AddUser_Adapter.UserViewHolder>() {

    fun filterList(filterlist: ArrayList<User>) {
        // below line is to add our filtered
        // list in our course array list.
        userList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.add_user_layout, parent, false)
        return UserViewHolder((view))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        lateinit var storageReference: StorageReference
        lateinit var localfile: File
        lateinit var bitmap: Bitmap
        lateinit var mAuth: FirebaseAuth
        lateinit var mDbRef: DatabaseReference
        val currentUser = userList[position]
        holder.name.text = currentUser.name
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
            .child(mAuth.currentUser?.uid.toString())


        holder.image.setOnClickListener {
            val id = currentUser.uid
            val name = currentUser.name
            val date = Date()
            mDbRef.child("contacts").child(id.toString()).setValue(Contact(id, name, date))
                .addOnSuccessListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("name", currentUser.name)
                    intent.putExtra("uid", currentUser.uid)
                    context.startActivity(intent)
                }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "There is some error, cannot add this contact",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.add_txt_name)
        val image = itemView.findViewById<ImageView>(R.id.add_user_image)

    }
}