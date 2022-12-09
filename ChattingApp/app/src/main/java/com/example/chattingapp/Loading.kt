package com.example.chattingapp

import android.app.Activity
import android.app.AlertDialog

class Loading(val mActivity: Activity) {
    private lateinit var isdialog:AlertDialog
    fun startLoading(){
        val inflater= mActivity.layoutInflater
        val dialogView= inflater.inflate(R.layout.progress_bar,null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}