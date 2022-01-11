package com.maricoolsapps.e_commerce.product_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.ref.SoftReference
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel
   @Inject constructor(val cloud: CloudQueries): ViewModel() {

}