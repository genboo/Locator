package ru.devsp.app.locator.services

import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import ru.devsp.app.locator.R
import ru.devsp.app.locator.view.MainActivity
import android.app.PendingIntent

class MessagingService : FirebaseMessagingService(){

    private var notificationId = 0

    override fun onMessageReceived(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val data = message.data
        val builder = NotificationCompat.Builder(this, "main")
                .setContentTitle(data["title"])
                .setContentText(data["message"])
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationId++
        notificationManager.notify(notificationId, builder.build())
    }

}