package com.neha.veloraquip.fcm

import android.content.Context
import com.google.firebase.database.*
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import com.neha.veloraquip.data.FirebaseRepository
import com.neha.veloraquip.R

class NotificationHandler(private val context: Context, private val repo: FirebaseRepository) {

    private val db = FirebaseDatabase.getInstance().reference

    fun startListening() {
        val uid = repo.currentUid() ?: return
        val ref = db.child("notify").child(uid)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.value as? Map<*, *> ?: return
                val text = data["text"]?.toString() ?: "New message"
                val fromName = data["fromName"]?.toString() ?: "VeloraQuip"

                showNotification(fromName, text)
                snapshot.ref.removeValue() // clear after showing
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "chat_msg_channel"
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mgr.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
                )
                mgr.createNotificationChannel(channel)
            }
        }

        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        mgr.notify((System.currentTimeMillis() % 100000).toInt(), notif)
    }
}
