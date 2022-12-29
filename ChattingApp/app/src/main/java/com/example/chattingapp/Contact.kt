package com.example.chattingapp

import java.util.*

class Contact {
    var contactID: String? = null
    var contactName: String? = null
    var lastDate: Date? = null

    constructor() {}

    constructor(contactID: String?, contactName: String?, lastDate: Date?) {
        this.contactID = contactID
        this.contactName = contactName
        this.lastDate = lastDate
    }

}