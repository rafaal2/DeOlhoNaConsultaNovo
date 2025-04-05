package com.recifemecatron.deolhonaconsulta.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FirebaseMessagingService", "Novo token gerado: $token")

        FirebaseTokenManager.updateToken(applicationContext, token)

        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        Log.d("FirebaseMessagingService", "Token enviado ao servidor: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            val title = it.title ?: "Nova Notificação"
            val body = it.body ?: "Você recebeu uma nova mensagem"

            //sendNotification(title, body)
        }
    }

//    private fun sendNotification(title: String, body: String) {
//        val intent = Intent(this, SuasPerguntasActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val channelId = "default_channel"
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelName = "Default Channel"
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
//    }
}
