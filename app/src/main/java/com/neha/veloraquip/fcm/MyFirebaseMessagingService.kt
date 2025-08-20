package com.neha.veloraquip.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.neha.veloraquip.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["fromName"] ?: "VeloraQuip"
        val body = remoteMessage.data["text"] ?: "New message"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "chat_msg_channel"
        val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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

        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        mgr.notify((System.currentTimeMillis() % 100000).toInt(), notif)
    }
}
