package com.maricoolsapps.e_commerce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FollowersSingleItemBinding
import com.maricoolsapps.e_commerce.databinding.RecyclerSingleItemBinding
import com.maricoolsapps.e_commerce.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.model.FollowersModel
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.product_ui.MainFragmentDirections
import com.maricoolsapps.e_commerce.repos.ProductDetailRepo
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowersListAdapter
@Inject constructor(@ApplicationContext val context: Context) :
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
        val person = user.get(position)

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

        }
    }
}