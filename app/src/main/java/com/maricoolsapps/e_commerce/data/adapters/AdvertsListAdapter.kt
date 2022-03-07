package com.maricoolsapps.e_commerce.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maricoolsapps.e_commerce.data.interfaces.OptionsMenuClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.databinding.AdvertsSingleItemBinding
import com.maricoolsapps.e_commerce.utils.setResource
import javax.inject.Inject


class AdvertsListAdapter
@Inject constructor(
) : RecyclerView.Adapter<AdvertsListAdapter.RecyclerViewHolder>() {

    private var products = listOf<ProductModel>()

    private lateinit var listener: OptionsMenuClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = AdvertsSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(binding)
    }

    fun setOnItemClickListener(mlistener: OptionsMenuClickListener) {
        listener = mlistener
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val product = products[position]
        holder.binding.apply {
            productImage.setResource(product.photo)
            nameOfCar.text = "${product.brand} ${product.type}"
            descOfCar.text = product.description
            priceOfCar.text = "$${product.price}"
        }
    }

    fun getProducts(newProducts: List<ProductModel>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun removeProduct(pos: Int){
        products.toMutableList().removeAt(pos)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class RecyclerViewHolder(var binding: AdvertsSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.select.setOnClickListener {
                listener.onOptionsClick(
                    products[absoluteAdapterPosition].id,
                    absoluteAdapterPosition
                )
            }
        }
    }
}