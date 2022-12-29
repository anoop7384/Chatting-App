package com.example.chattingapp

import android.net.Uri
import java.util.*

class Message {
    var message: String? = null
    var senderID: String? = null
    var messageId:String?=null
    var isImage:Boolean=false
    var imageUrl:String?=null
    var date: Date? = null
//    var seen:Boolean?=false

    constructor() {}

    constructor(message: String?, senderId: String?, messageId:String?,date: Date) {
        this.message = message
        this.senderID = senderId
        this.messageId=messageId
        this.date = date
//        this.time=time
    }
}