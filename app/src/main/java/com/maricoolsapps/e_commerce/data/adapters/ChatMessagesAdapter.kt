package com.maricoolsapps.e_commerce.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.interfaces.ConstraintInstructions
import com.maricoolsapps.e_commerce.data.model.MessageType
import com.maricoolsapps.e_commerce.data.model.Messages
import com.maricoolsapps.e_commerce.databinding.ChatMainSingleItemBinding
import com.maricoolsapps.e_commerce.databinding.ChatMainSingleItemLeftBinding
import com.maricoolsapps.e_commerce.utils.setResource
import com.maricoolsapps.e_commerce.utils.toTimeAgo
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import java.text.SimpleDateFormat
import javax.inject.Inject

class ChatMessagesAdapter
@Inject constructor(val auth: FirebaseAuth) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_MAIN = 1
    private val USER_OTHER = 2

    var messages = mutableListOf<Messages>()

    inner class ChatMessageViewHolderLeft(val binding: ChatMainSingleItemLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Messages) {
            if(data.type == MessageType.TEXT) {
                binding.image.toggleVisibility(false)
                binding.text.toggleVisibility(true)
                binding.text.text = data.text
                val inst = ConstraintInstructions.ConnectConstraint(
                    R.id.time,
                    ConstraintSet.TOP,
                    R.id.text,
                    ConstraintSet.BOTTOM
                )
                binding.constraint.updateConstraints(inst)
            }else{
                binding.image.toggleVisibility(true)
                binding.text.toggleVisibility(false)
                binding.image.setResource(data.text)
               val inst =  ConstraintInstructions.ConnectConstraint(
                    R.id.time,
                    ConstraintSet.TOP,
                    R.id.image,
                    ConstraintSet.BOTTOM
                )
                binding.constraint.updateConstraints(inst)
            }
            val dateFormat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            binding.time.text = dateFormat.format(data.time)
        }
    }


    inner class ChatMessageViewHolderRight(val binding: ChatMainSingleItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages) {
            if(data.type == MessageType.TEXT) {
                binding.image.toggleVisibility(false)
                binding.text.toggleVisibility(true)
                binding.text.text = data.text
                val inst = ConstraintInstructions.ConnectConstraint(
                    R.id.time,
                    ConstraintSet.TOP,
                    R.id.text,
                    ConstraintSet.BOTTOM
                )
                binding.constraint.updateConstraints(inst)

            }else{
                binding.image.toggleVisibility(true)
                binding.text.toggleVisibility(false)
                binding.image.setResource(data.text)
                val inst =  ConstraintInstructions.ConnectConstraint(
                    R.id.time,
                    ConstraintSet.TOP,
                    R.id.image,
                    ConstraintSet.BOTTOM
                )
                binding.constraint.updateConstraints(inst)
            }
            val dateFormat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            binding.time.text = dateFormat.format(data.time)
            if (data.seen) {
                binding.messageState.text = "Seen"
            }else{
                binding.messageState.text = "Not seen"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_OTHER) {
            val binding = ChatMainSingleItemLeftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatMessageViewHolderLeft(binding)
        } else {
            val binding = ChatMainSingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatMessageViewHolderRight(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == auth.currentUser?.uid.toString()) {
            USER_MAIN
        } else {
            USER_OTHER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = messages[position]
        if (holder is ChatMessageViewHolderRight) {
            holder.bind(data)
        } else if (holder is ChatMessageViewHolderLeft) {
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun getMessages(mMessages: List<Messages>) {
        val diffCallback = CoursesCallback(messages, mMessages)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        messages.clear()
        messages.addAll(mMessages)
        diffCourses.dispatchUpdatesTo(this)
    }
}

class CoursesCallback(private val oldList: List<Messages>, private val newList: List<Messages>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].text === newList[newItemPosition].text && oldList[oldItemPosition].time == newList[newItemPosition].time
    }

    override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {
        return oldList[oldCourse].text === newList[newPosition].text && oldList[oldCourse].time == newList[newPosition].time
    }
}

fun ConstraintLayout.updateConstraints(instruction: ConstraintInstructions) {
    ConstraintSet().also {
        it.clone(this)
            if (instruction is ConstraintInstructions.ConnectConstraint) it.connect(instruction.startID, instruction.startSide, instruction.endID, instruction.endSide)
            if (instruction is ConstraintInstructions.DisconnectConstraint) it.clear(instruction.startID, instruction.startSide)
        it.applyTo(this)
    }
}