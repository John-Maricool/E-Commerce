package com.maricoolsapps.e_commerce.data.model

import com.maricoolsapps.e_commerce.utils.Constants.NOTIFICATION_CHANNEL_ID

data class Data(
    val senderId: String,
    val messageType: String = MessageType.TEXT,
    val chat_channel_id: String,
    val personChatting: String
){
    constructor(): this( "", MessageType.TEXT, "", "")
}

data class NotificationDetails(
    val title: String,
    val body: String,
    val android_channel_id: String = NOTIFICATION_CHANNEL_ID
){
    constructor(): this("", "")
}

data class SendNotification(
    val to: String,
    val data: Data,
    val notification: NotificationDetails
)