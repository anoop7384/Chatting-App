package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messgageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbref:DatabaseReference

    var receiverRoom: String? = null
    var senderRoom:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbref = FirebaseDatabase.getInstance().getReference()
        supportActionBar?.title = name

        senderRoom = receiverUid+senderUid
        receiverRoom = senderUid+receiverUid

        chatRecyclerView= findViewById(R.id.chatRecyclerView)
        messgageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendBtn)
        messageList = ArrayList()
        messageAdapter= MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // add data to recyclerview from database

        mDbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })





        sendButton.setOnClickListener{
            val message = messgageBox.text.toString()
//            val tf= SimpleDateFormat("'HH:mm'")
//            val time=tf.format(Date())
//            val df= SimpleDateFormat("'dd-MM-yyyy'")
//            val dateee=df.format(Date())
            val date=Date()
            val messageObject = Message(message,senderUid,date)

            mDbref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbref.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            messgageBox.setText("")
            mDbref.child("users").child(senderUid.toString()).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mDbref.child("users").child(receiverUid.toString()).child("contacts").child(senderUid.toString()).setValue(Contact(senderUid,snapshot.child("name").getValue().toString(),date))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            mDbref.child("users").child(senderUid.toString()).child("contacts").child(receiverUid.toString()).setValue(Contact(receiverUid,name,date))

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.info) {
//            val intent = Intent(this@ChatActivity, Profile::class.java)
//            startActivity(intent)
////            finish()
//            return true
        }
        return true

//        return super.onOptionsItemSelected(item)
    }
}