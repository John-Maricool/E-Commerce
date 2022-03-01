package com.maricoolsapps.e_commerce.data.repositories

import android.net.Uri
import androidx.core.net.toUri
import android.content.Context
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.StorageChanges
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.MessageType
import com.maricoolsapps.e_commerce.data.model.Messages
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.data.source.FirebaseStorageSource
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.convertImageToByteArray
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject

class ChatRepository
@Inject constructor(
    val storageSource: FirebaseStorageSource,
    val cloudSource: FirebaseFirestoreSource,
    @ApplicationContext val context: Context
) {

    suspend fun sendMessage(
        chatChannel: ChatChannel,
        message: Messages,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            if (message.type == MessageType.IMG) {
                storageSource.putFileInStorage(message.text.toUri(), message.text)
                    message.text = context.convertImageToByteArray(message.text.toUri()).toString()
                    cloudSource.sendMessage(chatChannel, message)
                b.invoke(Resource.success(Constants.successful))
            } else {
                cloudSource.sendMessage(chatChannel, message)
                b.invoke(Resource.success(Constants.successful))
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }


}