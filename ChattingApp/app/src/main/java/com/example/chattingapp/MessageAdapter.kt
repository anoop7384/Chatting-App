package com.example.chattingapp

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
//import android.graphics.drawable.Drawable
//import android.graphics.drawable.Drawable

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat


class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    private lateinit var storageReference: StorageReference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder((view))
        } else {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder((view))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val activity = context as Activity
        var loading= Loading(activity)
//        loading.startLoading()
        if (holder.javaClass == SentViewHolder::class.java) {
//            do stuf for sent view holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("dd LLL HH:mm")
            val time = simpleDateFormat.format(currentMessage.date!!)

            val df = SimpleDateFormat("dd LLL")
            val date = df.format(currentMessage.date!!)


            holder.senttime.text = time
//            holder.sentdate.text=date

            if(currentMessage.isImage){
//                storageReference= FirebaseStorage.getInstance().getReference("Chats/${currentMessage.senderID}/${currentMessage.messageId}")

//                try {
//                    val localfile = File.createTempFile("tempfile", "${currentMessage.messageId}")
//                    storageReference.getFile(localfile).addOnSuccessListener {
//                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                        holder.sentImage.setImageBitmap(bitmap)
//                        Toast.makeText(context, "Showing image ${currentMessage.message}", Toast.LENGTH_SHORT).show()
////                    loading.isDismiss()
//                    }.addOnFailureListener {
////                    loading.isDismiss()
////                    Toast.makeText(this@MessageAdapter, it.message, Toast.LENGTH_SHORT).show()
//                        Toast.makeText(context, "cannot show image", Toast.LENGTH_SHORT).show()
//                    }
//
//                } catch (e: IOException) {
//                    Toast.makeText(context, "cannot Show image : $e", Toast.LENGTH_SHORT).show()
//                }

                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .override(600,200)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.sentImage)

            }else {
                holder.sentImage.setImageResource(0)
                holder.sentImage.layoutParams = LinearLayout.LayoutParams(0, 0)
//                loading.isDismiss()
            }
//            loading.isDismiss()



//            if(currentMessage.imageUri!=null){
//                val selectedPhotoUri=currentMessage.imageUri
//                try {
//                    selectedPhotoUri?.let {
//                        if(Build.VERSION.SDK_INT < 28) {
//                            val bitmap = MediaStore.Images.Media.getBitmap(
//                                context.contentResolver,
//                                selectedPhotoUri
//                            )
//                            holder.sentImage.setImageBitmap(bitmap)
//                        } else {
//                            val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
//                            val bitmap = ImageDecoder.decodeBitmap(source)
//                            holder.sentImage.setImageBitmap(bitmap)
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            else{
//                holder.sentImage.visibility=View.INVISIBLE
//            }


        } else {
//            do stuff for recieve view holder

            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("dd LLL HH:mm")
            val time = simpleDateFormat.format(currentMessage.date!!)

            val df = SimpleDateFormat("dd LLL")
            val date = df.format(currentMessage.date!!)
            holder.receivetime.text = time
//            holder.receivedate.text=date

            // set seen for received messages

            if(currentMessage.isImage){
                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .override(600,200)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)         //ALL or NONE as your requirement
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.receiveImage)
            }
            else{
                holder.receiveImage.setImageDrawable(null)
                holder.receiveImage.layoutParams = LinearLayout.LayoutParams(0, 0)
            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderID)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val senttime = itemView.findViewById<TextView>(R.id.txt_sent_time)
        val sentImage = itemView.findViewById<ImageView>(R.id.sentImageView)
//        val sentdate=itemView.findViewById<TextView>(R.id.txt_sent_date)

    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receivetime = itemView.findViewById<TextView>(R.id.txt_receive_time)
        val receiveImage = itemView.findViewById<ImageView>(R.id.receiveImageView)
//        val receivedate=itemView.findViewById<TextView>(R.id.txt_receive_date)
    }
}