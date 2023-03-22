package com.maricoolsapps.e_commerce.data.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.SliderProductImageBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.smarteist.autoimageslider.SliderViewAdapter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SliderImageAdapter
@Inject constructor(@ApplicationContext val context: Context):
    SliderViewAdapter<SliderImageAdapter.SliderViewHolder>() {

    private var images: List<String> = listOf()
    private lateinit var listener: OnItemClickListener<List<String>>

    override fun getCount(): Int {
        return images.size
    }

    fun setOnItemClickListener(mlistener: OnItemClickListener<List<String>>) {
        listener = mlistener
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        val binding =
            SliderProductImageBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        val image = images[position]
        if (viewHolder != null) {
            Glide.with(viewHolder.itemView)
                .load(image)
                .placeholder(R.drawable.car)
                .centerCrop()
                .into(viewHolder.binding.productImage)
        }

        viewHolder?.binding?.productImage?.setOnClickListener {
            Log.d("images", position.toString())
            listener.onItemClick(images, position)
        }
    }

    fun getProductsImage(mImages: List<String>) {
        images = mImages
        notifyDataSetChanged()
    }

    inner class SliderViewHolder(var binding: SliderProductImageBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {
        init {
            binding.productImage.setOnClickListener {
                val position = getItemPosition(images)
                Log.d("images", position.toString())
                listener.onItemClick(images, position)
            }
        }
    }
}
