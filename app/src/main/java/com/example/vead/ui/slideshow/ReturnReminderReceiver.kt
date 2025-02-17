package com.example.vead.ui.slideshow

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vead.R

class ReturnReminderReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("bookTitle") ?: ""
        val channelId = "return_due_channel"

        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Devolución",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones para recordar la devolución de libros"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 2) Build Notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.default_img)
            .setContentTitle("Solicitud enviada")
            .setContentText("¡Tu solicitud del libro folio '$title' ha sido enviada!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Show the Notification
        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }
    }
}
