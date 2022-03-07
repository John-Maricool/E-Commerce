package com.maricoolsapps.e_commerce.ui.product_ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.ChatList
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel
@Inject constructor(
    val auth: ProfileChanges,
    val defaultRepo: DefaultRepository,
    val cloud: CloudQueries
) : ViewModel() {

    private val _done = MutableLiveData<List<ChatList>?>()
    val done: LiveData<List<ChatList>?> get() = _done

    private val _click = MutableLiveData<ChatChannel?>()
    val click: LiveData<ChatChannel?> get() = _click

    val userId = auth.getUserUid()

    init {
        viewModelScope.launch {
            cloud.getAllChats(userId) {
                _done.postValue(it.data)
                defaultRepo.onResult(it)
            }
        }
    }

    fun getChatChannel(user: String){
        viewModelScope.launch {
            cloud.createOrGetChatChannel(userId, user){
                _click.postValue(it.data)
                defaultRepo.onResult(it)
            }
        }
    }
}