package com.maricoolsapps.e_commerce.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import android.content.Context
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.ChatList
import com.maricoolsapps.e_commerce.data.model.MessageType
import com.maricoolsapps.e_commerce.databinding.ChatListSingleItemBinding
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import com.maricoolsapps.e_commerce.utils.toTimeAgo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatListAdapter
@Inject constructor(
    @ApplicationContext var context: Context
) : RecyclerView.Adapter<ChatListAdapter.RecyclerViewHolder>() {

    private var chats: List<ChatList> = listOf()
    lateinit var listener: OnItemClickListener<ChatChannel>

    inner class RecyclerViewHolder(var binding: ChatListSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root){

            init {
                binding.root.setOnClickListener {
                val postion = absoluteAdapterPosition
                if (postion != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chats[postion].user.id, null)
                }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = ChatListSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val chat = chats[position]
        holder.binding.apply {
            userImage.setResourceCenterCrop(chat.user.image!!)
            userName.text = chat.user.name
            if (chat.lastMessages.type == MessageType.TEXT) {
                lastMessage.text = chat.lastMessages.text
            }
            lastMessageTime.text = chat.lastMessages.time.toTimeAgo()
        }
    }

    fun setOnItemClickListener(mlistener: OnItemClickListener<ChatChannel>){
        listener = mlistener
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun getNewChatList(newList: List<ChatList>){
        chats = newList
        notifyDataSetChanged()
    }
}