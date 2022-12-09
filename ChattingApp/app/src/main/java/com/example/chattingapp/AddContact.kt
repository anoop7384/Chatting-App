package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class AddContact : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var adduserRecyclerView: RecyclerView
    private lateinit var adapter: AddUser_Adapter
    private lateinit var adduserList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        val loading = Loading(this)
        loading.startLoading()
        mAuth =FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()


        adduserList = ArrayList()
        adapter = AddUser_Adapter(this,adduserList)

        adduserRecyclerView = findViewById(R.id.adduserRecyclerView)
        adduserRecyclerView.layoutManager = LinearLayoutManager(this)
        adduserRecyclerView.adapter = adapter


        mDbRef.child("users").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                adduserList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue((User::class.java))
                    if(mAuth.currentUser?.uid!= currentUser?.uid){
                       adduserList.add(currentUser!!)
                    }
                }
                adduserList.sortWith(Comparator { lhs, rhs ->
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (lhs.name.toString() < rhs.name.toString()) -1 else if (lhs.name.toString() > rhs.name.toString()) 1 else 0
                })
                adapter.notifyDataSetChanged()
                loading.isDismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
//        addBtn.setOnClickListener{
//            val id=addUid.text.toString()
//            val date=Date()
//            mDbRef.child("contacts").child(id).setValue(Contact(id,date)).addOnSuccessListener {
//                val intent = Intent(this@AddContact,MainActivity::class.java)
//                startActivity(intent)
//            }.addOnFailureListener{
//                Toast.makeText(this@AddContact,"There is some error, cannot add this contact", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}