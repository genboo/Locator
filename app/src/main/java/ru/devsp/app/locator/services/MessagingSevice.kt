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
import ru.devsp.app.locator.App

class MessagingService : FirebaseMessagingService() {

    private var notificationId = 0

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        showNotification(data["title"] ?: "", data["message"] ?: "", data["action"], data["user"])
    }

    private fun showNotification(title: String, message: String, action: String?, user: String?) {
        val intent = Intent(this, MainActivity::class.java)
        if (action != null) {
            intent.putExtra("action", action)
            intent.putExtra("user", user)
        }
        if ((application as App).isActivityVisible()) {
            startActivity(intent)
        } else {
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val builder = NotificationCompat.Builder(this, "main")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationId++
            notificationManager.notify(notificationId, builder.build())
        }
    }

}