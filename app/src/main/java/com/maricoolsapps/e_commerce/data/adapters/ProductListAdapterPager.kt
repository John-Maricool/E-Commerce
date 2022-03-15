package com.maricoolsapps.e_commerce.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.RecyclerSingleItemBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.ProductDetailRepo
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.content.Context
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.utils.setResource
import java.text.NumberFormat

class ProductListAdapterPager
@Inject constructor(
    val repo: ProductDetailRepo,
    @ApplicationContext val context: Context
) : PagingDataAdapter<ProductModel, ProductListAdapterPager.RecyclerViewHolder>(CAR_COMPARATOR) {

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
        if (repo.isCarAddedToFav(product.id)) {
            holder.binding.fav.setBackgroundResource(R.drawable.ic_favorite)
        } else {
            holder.binding.fav.setBackgroundResource(R.drawable.ic_favorite_border)
        }

        holder.binding.apply {
            Glide.with(context)
                .load(product.photo)
                .centerCrop()
                .placeholder(R.drawable.car)
                .into(productImage)
            state.text = product.condition
            nameOfCar.text = "${product.brand} ${product.type}"
            locationOfCar.text = "Location: ${product.town}, Nigeria"
            descOfCar.text = product.description
            priceOfCar.text = "₦${NumberFormat.getIntegerInstance().format(product.price)}"

        }
    }

    fun getProducts(newProducts: List<ProductModel>) {
        products = newProducts
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
                if (repo.isCarAddedToFav(product.id)) {
                    it.setBackgroundResource(R.drawable.ic_favorite_border)
                    repo.deleteCar(product.id)
                } else {
                    it.setBackgroundResource(R.drawable.ic_favorite)
                    repo.insertCarToDb(favProduct)
                }
            }
        }
    }


    companion object {
        private val CAR_COMPARATOR = object : DiffUtil.ItemCallback<ProductModel>() {
            override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel) =
                oldItem == newItem


        }
    }
}
