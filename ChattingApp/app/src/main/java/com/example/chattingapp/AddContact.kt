package com.example.chattingapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
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
        supportActionBar?.title = "Add Contact"
        val loading = Loading(this)
        loading.startLoading()
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()


        adduserList = ArrayList()
        adapter = AddUser_Adapter(this, adduserList)

        adduserRecyclerView = findViewById(R.id.adduserRecyclerView)
        adduserRecyclerView.layoutManager = LinearLayoutManager(this)
        adduserRecyclerView.adapter = adapter


        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                adduserList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue((User::class.java))
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
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
                Toast.makeText(this@AddContact, error.message, Toast.LENGTH_SHORT).show()
            }


        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        // below line is to get our menu item.
        val searchItem = menu.findItem(R.id.actionSearch)

        val searchView = searchItem.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })

//        // getting search view of our item.
//        val searchView = searchItem.actionView
//
//        // below line is to call set on query text listener method.
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                // inside on query text change method we are
//                // calling a method to filter our recycler view.
//                filter(newText)
//                return false
//            }
//        })
        return true
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<User>()

        // running a for loop to compare elements.
        for (item in adduserList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist)
        }
    }
}