package com.example.chattingapp

import java.util.*

class Message {
    var message: String?=null
    var senderID:String? = null
//    var date:String?=null
//    var time:String?=null
    var date:Date?=null

    constructor(){}

    constructor(message:String?,senderId:String?,date: Date){
        this.message= message
        this.senderID=senderId
        this.date=date
//        this.time=time
    }
}