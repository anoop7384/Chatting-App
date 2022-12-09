package com.example.chattingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import kotlin.concurrent.timer

class MessageAdapter(val context:Context,val messageList:ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            // inflate receivw
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder((view))
        }
        else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder((view))
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass==SentViewHolder::class.java){
//            do stuf for sent view holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val time = simpleDateFormat.format(currentMessage.date)

            val df=SimpleDateFormat("dd LLL")
            val date=df.format(currentMessage.date)


            holder.senttime.text=time
            holder.sentdate.text=date
        }else{
//            do stuff for recieve view holder

            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("HH:mm aaa ")
            val time = simpleDateFormat.format(currentMessage.date)

            val df=SimpleDateFormat("dd LLL")
            val date=df.format(currentMessage.date)
            holder.receivetime.text=time
            holder.receivedate.text=date
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage= messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderID)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val senttime=itemView.findViewById<TextView>(R.id.txt_sent_time)
        val sentdate=itemView.findViewById<TextView>(R.id.txt_sent_date)

    }

    class ReceiveViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receivetime=itemView.findViewById<TextView>(R.id.txt_receive_time)
        val receivedate=itemView.findViewById<TextView>(R.id.txt_receive_date)
    }
}