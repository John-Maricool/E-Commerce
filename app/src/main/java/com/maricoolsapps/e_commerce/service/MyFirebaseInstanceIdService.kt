package com.maricoolsapps.e_commerce.service

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.ui.product_ui.chat.ChatFragmentArgs
import javax.inject.Inject


class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    @Inject
    lateinit var source: FirebaseFirestoreSource

    lateinit var auth: FirebaseAuth

    override fun onNewToken(token: String) {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            source.addTokenToFirebase(auth.currentUser?.uid!!, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        auth = FirebaseAuth.getInstance()
        val personChatting = message.data["personChatting"]
        if (message.notification != null) {
            if (auth.currentUser?.uid!! != personChatting) {
                return
            }else{
                sendNotification(message)
            }
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        val channelId = message.data["chat_channel_id"]
        val personChatting = message.data["personChatting"]
        val chatChannel = ChatChannel(channelId!!, personChatting!!)
        val args = ChatFragmentArgs(chatChannel).toBundle()

        val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(args)
            .createPendingIntent()

        val notification = message.notification ?: return
        val notificationBuilder =
            NotificationCompat.Builder(this.applicationContext, notification.channelId!!)
                .setContentTitle(notification.title)
                .setColorized(true)
                .setContentText(notification.body)
                .setSmallIcon(R.drawable.ic_message)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder)
    }
}