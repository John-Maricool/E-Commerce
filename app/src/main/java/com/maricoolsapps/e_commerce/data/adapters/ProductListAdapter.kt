package com.maricoolsapps.e_commerce.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.RecyclerSingleItemBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.utils.setResource
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProductListAdapter
@Inject constructor(
    var repo: ProductDetailRepo
) : RecyclerView.Adapter<ProductListAdapter.RecyclerViewHolder>() {

    private var products = listOf<ProductModel>()

    private lateinit var listener: OnItemClickListener<ProductModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = RecyclerSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(binding)
    }

    fun setOnItemClickListener(mlistener: OnItemClickListener<ProductModel>) {
        listener = mlistener
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val product = products[position]
       /* if (repo.isCarAddedToFav(product.id)) {
            holder.binding.fav.setBackgroundResource(R.drawable.ic_favorite)
        } else {
            holder.binding.fav.setBackgroundResource(R.drawable.ic_favorite_border)
        }*/

        holder.binding.apply {
            productImage.setResource(product.photo)
            state.text = product.condition
            nameOfCar.text = "${product.brand} ${product.type}"
            locationOfCar.text = "Location: ${product.town}, Nigeria"
            descOfCar.text = product.description
            priceOfCar.text = "₦${product.price}"
        }
    }

    fun getProducts(newProducts: List<ProductModel>) {
        products = newProducts/*
        val diffCallback = ProductsCallBack(products, newProducts)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newProducts)
        diffCourses.dispatchUpdatesTo(this)
   */
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class RecyclerViewHolder(var binding: RecyclerSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buy.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(
                        products[absoluteAdapterPosition].id,
                        products[absoluteAdapterPosition].brand
                    )
                }
            }
            binding.fav.setOnClickListener {
                val product = products[bindingAdapterPosition]
                val favProduct = FavoriteProductEntity(product.brand, product.id)
                /*if (repo.isCarAddedToFav(product.id)) {
                    it.setBackgroundResource(R.drawable.ic_favorite_border)
                    repo.deleteCar(product.id)
                } else {
                    it.setBackgroundResource(R.drawable.ic_favorite)
                    repo.insertCarToDb(favProduct)
                }*/
            }
        }
    }
}

class ProductsCallBack(
    private val oldList: List<ProductModel>,
    private val newList: List<ProductModel>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList[newItemPosition].id && oldList[oldItemPosition].brand === newList[newItemPosition].brand
    }

    override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {
        return oldList[oldCourse].id === newList[newPosition].id && oldList[oldCourse].brand === newList[newPosition].brand
    }
}