package com.example.chattingapp

import java.util.*
import kotlin.collections.ArrayList


class User {
    var name: String? = null
    var email:String? = null
    var uid:String? = null
    var phone:String? = null
    var bio:String? = null


    constructor(){}

    constructor(name: String? , email:String?, uid : String?,phone:String?,bio:String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.phone=phone
        this.bio=bio
    }
}