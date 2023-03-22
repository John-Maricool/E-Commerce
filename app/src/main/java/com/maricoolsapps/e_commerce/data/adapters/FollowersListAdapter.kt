package com.maricoolsapps.e_commerce.data.adapters

import android.content.Context
import android.util.Log
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
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject


class FollowersListAdapter
@Inject constructor(@ApplicationContext val context: Context, val status: FollowersStatus) :
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
        status.isUserFollowed(person.id) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("djknaj", it.data.toString())
                    holder.changeBtnToFollowed()
                    /*holder.binding.apply {
                        followBtn.setTextColor(
                            context.resources.getColor(
                                R.color.white,
                                null
                            )
                        )
                        followBtn.text = "Followed"
                        followBtn.background =
                            context.resources.getDrawable(R.drawable.blue_solid, null)
                    }*/
                }
                else -> {
                    holder.changeBtnToDefault()
                }
            }
        }
        holder.binding.apply {
            name.text = person.name
            email.text = person.email
            image.setResourceCenterCrop(person.image!!)
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
            binding.followBtn.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val person = user[bindingAdapterPosition]
                    if (binding.followBtn.text == "Followed") {
                        status.unfollowUser(person.id) {
                            when (it.status) {
                                Status.SUCCESS -> {
                                    changeBtnToDefault()
                                }
                                else -> {
                                }
                            }
                        }
                    } else {
                        status.followUser(person.id) {
                            when (it.status) {
                                Status.SUCCESS -> {
                                    changeBtnToFollowed()
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
            binding.card.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val person = user[bindingAdapterPosition]
                    listener.onItemClick(person.id, null)
                }
            }
        }

         fun changeBtnToFollowed() {
            binding.followBtn.setTextColor(context.resources.getColor(R.color.white, null))
            binding.followBtn.text = "Followed"
            binding.followBtn.background =
                context.resources.getDrawable(R.drawable.blue_solid, null)
        }

         fun changeBtnToDefault() {
            binding.followBtn.setTextColor(context.resources.getColor(R.color.blue, null))
            binding.followBtn.text = "Follow"
            binding.followBtn.background =
                context.resources.getDrawable(R.drawable.blue_border, null)
        }
    }


}