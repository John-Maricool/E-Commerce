package com.maricoolsapps.e_commerce.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ChatChannel(val channelId: String, val personChatting: String) : Parcelable {
    constructor() : this("", "")
}

data class Messages(
    var text: String, val time: Long, val senderId: String, val  receiverId: String, val senderName: String,
    var seen: Boolean, var type: String,
) {
    constructor() : this("", 0,"","", "", false, MessageType.TEXT)
}

data class UserStatus(val online: Boolean, val lastSeen: Long = Date().time) {
    constructor() : this(false)
}

data class ChatList(var user: CarBuyerOrSeller, var lastMessages: Messages)

object MessageType {
    var TEXT = "text"
    var IMG = "image"
}