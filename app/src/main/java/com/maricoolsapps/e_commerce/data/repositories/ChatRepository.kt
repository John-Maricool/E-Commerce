package com.maricoolsapps.e_commerce.data.repositories

import androidx.core.net.toUri
import android.content.Context
import android.util.Log
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.MessageType
import com.maricoolsapps.e_commerce.data.model.Messages
import com.maricoolsapps.e_commerce.data.model.SendNotification
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.data.source.FirebaseStorageSource
import com.maricoolsapps.e_commerce.service.RetrofitApiCalls
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.convertImageToByteArray
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class ChatRepository
@Inject constructor(
    val storageSource: FirebaseStorageSource,
    val cloudSource: FirebaseFirestoreSource,
    val api: RetrofitApiCalls,
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
                val uri = storageSource.getDownloadUri(message.text)
                message.text = uri.toString()
                // message.text = context.convertImageToByteArray(message.text.toUri()).toString()
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

    fun sendNotification(notification: SendNotification, b: (Resource<Boolean>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val result = api.sendNotification(notification)
            result.enqueue(object: Callback<SendNotification>{
                override fun onResponse(call: Call<SendNotification>, response: Response<SendNotification>) {
                    if(response.isSuccessful){
                        b.invoke(Resource.success(response.isSuccessful))
                    }
                }
                override fun onFailure(call: Call<SendNotification>, t: Throwable) {
                    Log.d("sns", t.toString())
                    b.invoke(Resource.error(t.toString(), null))
                }
            })
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

}