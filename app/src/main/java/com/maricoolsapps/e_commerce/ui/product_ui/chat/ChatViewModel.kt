package com.maricoolsapps.e_commerce.ui.product_ui.chat

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.Messages
import com.maricoolsapps.e_commerce.data.model.UserStatus
import com.maricoolsapps.e_commerce.data.repositories.ChatRepository
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel
@Inject constructor(
    val auth: FirebaseAuth,
    val defaultRepo: DefaultRepository,
    val cloud: CloudQueries,
    val repo: ChatRepository
) : ViewModel() {

    val userID = auth.currentUser?.uid.toString()

    private val _status = MutableLiveData<UserStatus?>()
    val status: LiveData<UserStatus?> get() = _status

    private val _chat = MutableLiveData<CarBuyerOrSeller?>()
    val chat: LiveData<CarBuyerOrSeller?> get() = _chat

    private val _messages = MutableLiveData<List<Messages>?>()
    val messages: LiveData<List<Messages>?> get() = _messages

    fun fillViews(channel: ChatChannel) {
        viewModelScope.launch {
            cloud.tagAllMessagesSeen(auth.currentUser?.uid.toString(), channel) {
                defaultRepo.onResult(it)
            }
            cloud.getSeller(channel.personChatting) {
                defaultRepo.onResult(it)
                _chat.postValue(it.data)
            }
        }
        cloud.getAllMessages(channel) {
            defaultRepo.onResult(it)
            _messages.postValue(it.data)
        }

        cloud.getUserOnlineStatus(channel.personChatting) {
            defaultRepo.onResult(it)
            _status.value = it.data
        }
    }

    fun callChat(phoneNo: String): Intent {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNo")
        return callIntent
    }

    fun goToGallery(): Intent{
        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        return intent
    }

    fun sendMessage(channel: ChatChannel, message: Messages) {
        viewModelScope.launch {
            repo.sendMessage(channel, message) {
                defaultRepo.onResult(it)
            }
        }
    }
}