package com.maricoolsapps.e_commerce.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FollowersSingleItemBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.repositories.FollowersStatus
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowersListAdapter
@Inject constructor(@ApplicationContext val context: Context, val status: FollowersStatus):
    RecyclerView.Adapter<FollowersListAdapter.RecyclerViewHolder>() {

    private var user: List<CarBuyerOrSeller> = listOf()

    private lateinit var listener: OnItemClickListener<CarBuyerOrSeller>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = FollowersSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(binding)
    }

    fun setOnItemClickListener(mlistener: OnItemClickListener<CarBuyerOrSeller>) {
        listener = mlistener
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val person = user[position]

        status.scope.launch(Main) {
            when(status.isUserFollowed(person.id).status){
                Status.SUCCESS -> {holder.changeBtnToFollowed()}
                Status.ERROR -> {holder.changeBtnToDefault()}
                Status.LOADING -> TODO()
            }
        }
        holder.binding.apply {
            name.text = person.name
            email.text = person.email
            Glide.with(context)
                .load(person.image)
                .circleCrop()
                .fitCenter()
                .into(image)
        }
    }

    fun getUsers(newUsers: List<CarBuyerOrSeller>) {
        user = newUsers
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return user.size
    }

    inner class RecyclerViewHolder(var binding: FollowersSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val person = user[absoluteAdapterPosition]
            binding.followBtn.setOnClickListener {
                if (binding.followBtn.text == "Followed"){
                    status.scope.launch(Main) {
                        when(status.unfollowUser(person.id).status){
                            Status.SUCCESS -> {changeBtnToDefault()}
                            Status.ERROR -> {}
                            Status.LOADING -> TODO()
                        }
                    }
                }else{
                    status.scope.launch(Main) {
                        when(status.followUser(person.id).status){
                            Status.SUCCESS -> {changeBtnToFollowed()}
                            Status.ERROR -> {}
                            Status.LOADING -> TODO()
                        }
                    }
                }
            }
            binding.card.setOnClickListener {
                listener.onItemClick(person.id, null)
            }
        }

        fun changeBtnToFollowed(){
            binding.followBtn.setTextColor(context.resources.getColor(R.color.white, null))
            binding.followBtn.text = "Followed"
            binding.followBtn.background = context.resources.getDrawable(R.drawable.blue_solid, null)
        }

        fun changeBtnToDefault(){
            binding.followBtn.setTextColor(context.resources.getColor(R.color.blue, null))
            binding.followBtn.text = "Follow"
            binding.followBtn.background = context.resources.getDrawable(R.drawable.blue_border, null)
        }

    }

}