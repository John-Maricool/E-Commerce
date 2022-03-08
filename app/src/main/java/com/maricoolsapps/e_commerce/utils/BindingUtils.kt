package com.maricoolsapps.e_commerce.utils

import android.net.Uri
import android.widget.ImageView
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.AdvertsListAdapter
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.model.ProductModel


@BindingAdapter("setImageCenterCrop")
fun ImageView.setResourceCenterCrop(data: Uri?) {
    if (data != null) {
        Glide.with(this.context)
            .load(data)
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle)
            .into(this)
    }
}

@BindingAdapter("setImageRes")
fun ImageView.setResource(data: String) {
    Glide.with(this.context)
        .load(data)
        .centerCrop()
        .placeholder(R.drawable.car)
        .into(this)
}

@BindingAdapter("tabCarBrands")
fun TabLayout.getAllCarBrands(brands: List<String>){
    brands.forEach {
        val oneTab: TabLayout.Tab = this.newTab()
        oneTab.text = it
        this.addTab(oneTab)
    }
    //getCarsFromBrand(brands[0])
}

@BindingAdapter("bind_product_list")
fun RecyclerView.bindUsersList(items: List<ProductModel>?) {
    items?.let { (this.adapter as ProductListAdapter).getProducts(items) }
}


@BindingAdapter("bind_adverts_list")
fun RecyclerView.bindAdvertsList(items: List<ProductModel>?) {
    items?.let { (this.adapter as AdvertsListAdapter).getProducts(items) }
}

@InverseBindingAdapter(attribute= "selectedItem")
fun TabLayout.getSelectedPos(): String {
        return this[this.selectedTabPosition].toString()
}

@BindingAdapter("onClicked")
fun TabLayout.onClick(listener: TabLayout.OnTabSelectedListener){
    this.addOnTabSelectedListener(listener)
}
