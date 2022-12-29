package com.example.chattingapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messgageBox: EditText
    private lateinit var imageButton: ImageView
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var storageReference: StorageReference
    private lateinit var mDbref: DatabaseReference
    var imageUri: Uri? = null
//    private lateinit var apiService: APIService

    var notify = false;

    var receiverRoom: String? = null
    var senderRoom: String? = null
    var infoUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val chatloading = Loading(this)
//        chatloading.startLoading()

        setStatus(true)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        infoUid = receiverUid
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbref = FirebaseDatabase.getInstance().reference
        supportActionBar?.title = name

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


//        apiService =
//            Client().getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        messgageBox = findViewById(R.id.messageBox)
        imageButton = findViewById(R.id.imageBtn)
        sendButton = findViewById(R.id.sendBtn)
        imageUri = null

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true     // items gravity sticks to bottom
        llm.reverseLayout = false   // item list sorting (new messages start from the bottom)

        chatRecyclerView.layoutManager = llm
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount-1)

        // add data to recyclerview from database

        mDbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    var authUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
//                    chatloading.isDismiss()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
//                    chatloading.isDismiss()
                }

            })
        imageUri = null



//        chatloading.isDismiss()

        imageButton.setOnClickListener {
            selectProfilePic()
//            messgageBox.setText("Send the picture")
        }




        sendButton.setOnClickListener {
            if (messgageBox.text.equals("") && imageUri == null) {
                return@setOnClickListener
            }
//            chatloading.startLoading()
            notify = true
            val message = messgageBox.text.toString()
            val date = Date()

            var senderName: String? = null
            val messageKey = mDbref.child("chats").child(senderRoom!!).child("messages").push().key
            val messageId = name + messageKey
            val messageObject = Message(message, senderUid, messageId, date)
            if (imageUri != null) {
                chatloading.startLoading()
                messageObject.isImage = true
                storageReference =
                    FirebaseStorage.getInstance().getReference("Chats/$senderUid/$messageId")
                storageReference.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        messageObject.imageUrl = it.toString()
                        mDbref.child("chats").child(senderRoom!!).child("messages").child(messageKey!!)
                            .setValue(messageObject).addOnSuccessListener {
                                mDbref.child("chats").child(receiverRoom!!).child("messages").push()
                                    .setValue(messageObject)
                            }
                    }
                    chatloading.isDismiss()
                    Toast.makeText(this@ChatActivity, "Image uploaded", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                mDbref.child("chats").child(senderRoom!!).child("messages").child(messageKey!!)
                    .setValue(messageObject).addOnSuccessListener {
                        mDbref.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
            }




            messgageBox.setText("")

            mDbref.child("users").child(senderUid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        senderName = snapshot.child("name").value.toString()
//                    createNotification(senderName,message)
                        mDbref.child("users").child(receiverUid.toString()).child("contacts")
                            .child(senderUid.toString()).setValue(
                                Contact(
                                    senderUid,
                                    snapshot.child("name").value.toString(),
                                    date
                                )
                            )
                        val suser = snapshot.getValue(User::class.java)
                        if (notify) {
//                        sendNotification(receiverUid,suser?.uid.toString(),message)
                            mDbref.child("Tokens").child(receiverUid.toString())
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val receiverToken: Token? =
                                            snapshot.getValue(Token::class.java)
                                        if (receiverToken!=null){
                                            val notificationsSender = FcmNotificationsSender(
                                                receiverToken?.token!!,
                                                suser?.name!!,
                                                message,
                                                applicationContext,
                                                this@ChatActivity
                                            )
                                            notificationsSender.SendNotifications()
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                })

//                        Toast.makeText(this@ChatActivity,"Sending notification",Toast.LENGTH_SHORT).show()

                        }
                        notify = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
                    }

                })

            mDbref.child("users").child(senderUid.toString()).child("contacts")
                .child(receiverUid.toString()).setValue(Contact(receiverUid, name, date))

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
//                image.setImageURI(selectedImageUri)

                var selectedImageBitmap: Bitmap? = null
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        imageUri
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imageButton.setImageBitmap(selectedImageBitmap)
            }
        }
    }

//    private fun sendNotification(receiverUid: String?, username: String, message: String) {
//        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
//        val query = tokens.orderByKey().equalTo(receiverUid)
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val token = snapshot.getValue(Token::class.java)
//                val data = Data(
//                    FirebaseAuth.getInstance().currentUser?.uid.toString(), R.drawable.logo,
//                    "$username: $message", "New Message", receiverUid
//                )
//                val sender = Sender(data, token?.token)
//                apiService.sendNotification(sender)
//                    ?.enqueue(object : Callback<MyResponse?> {
//                        override fun onResponse(
//                            call: retrofit2.Call<MyResponse?>,
//                            response: Response<MyResponse?>
//                        ) {
//                            if (response.code() == 200) {
//                                if (response.body()?.success != 1) {
//                                    Toast.makeText(this@ChatActivity, "Failed", Toast.LENGTH_SHORT)
//                                        .show()
//                                }
//                            }
//                        }
//
//                        override fun onFailure(call: retrofit2.Call<MyResponse?>, t: Throwable) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.info) {
            val intent = Intent(this@ChatActivity, User_Info::class.java)
//            intent.putExtra("name",infoName)
            intent.putExtra("uid", infoUid)
            startActivity(intent)
//            finish()
            return true
        }
        if (item.itemId == R.id.delete) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(auth.currentUser?.uid.toString())


            infoUid?.let {
                db.child("contacts").child(it).removeValue().addOnSuccessListener {
                    val intent = Intent(this@ChatActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }


            return true
        }
        return true

//        return super.onOptionsItemSelected(item)
    }

    private fun setStatus(status: Boolean) {
        val userData = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        userData.child("status").setValue(status)
    }

    override fun onResume() {
        super.onResume()
        setStatus(true)
    }



}


