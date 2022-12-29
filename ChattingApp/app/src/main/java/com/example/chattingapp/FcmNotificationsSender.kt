package com.example.chattingapp

import android.app.Activity
import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


//class FcmNotificationsSender {
//}

class FcmNotificationsSender(
    var userFcmToken: String,
    var title: String,
    var body: String,
    mContext: Context,
    mActivity: Activity
) {
    var mContext: Context
    var mActivity: Activity
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAAf2LWiQs:APA91bFOD982VFQoJ49qQrrChJ1AZnlLb9LYa1DyMcdyNbyiTkJiAkXhFoGstPKzeAOvcmycZ0QJxjv_AQ_1oc0Uz3XQYTsZ0n9z_ye9qUnuVc5XnUZ0JluKLog4aQKcgBEP_VQqHDnM"

    fun SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
//            notiObject.put("senderId", senderId)
            notiObject.put("icon", R.drawable.logo) // enter icon that exists in drawable only
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                postUrl,
                mainObj,
                Response.Listener<JSONObject?> {
                    // code run is got response
                },
                Response.ErrorListener {
                    // code run is got error
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    init {
        this.mContext = mContext
        this.mActivity = mActivity
    }
}