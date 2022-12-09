package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Contact>
    private lateinit var adapter: User_Adapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var addContact:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loading = Loading(this)
        loading.startLoading()

        mAuth =FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = User_Adapter(this,userList)
        addContact=findViewById(R.id.addContact)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mDbRef.child("users").child(mAuth.currentUser?.uid.toString()).child("contacts").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue((Contact::class.java))
                    if(mAuth.currentUser?.uid!= currentUser?.contactID){
                        userList.add(currentUser!!)
                    }
                }
                userList.sortWith(Comparator { lhs, rhs ->
                    rhs.lastDate!!.compareTo(lhs.lastDate)
                })
                adapter.notifyDataSetChanged()
                loading.isDismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                loading.isDismiss()
            }


        })

        addContact.setOnClickListener{
            val intent = Intent(this@MainActivity,AddContact::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profile){
            val intent = Intent(this@MainActivity,Profile::class.java)
            startActivity(intent)
//            finish()
            return true
        }
        if(item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this@MainActivity,Login::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true

//        return super.onOptionsItemSelected(item)
    }

}