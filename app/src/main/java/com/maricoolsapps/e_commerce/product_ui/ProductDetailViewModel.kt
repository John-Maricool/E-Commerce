package com.maricoolsapps.e_commerce.product_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel
@Inject constructor(val cloud: CloudQueries) : ViewModel() {

    private var _result = MutableLiveData<Resource<Product>>()
    val result: LiveData<Resource<Product>> get() = _result

    fun getCar(brand: String, id: String) {
        viewModelScope.launch(IO) {
            val job = viewModelScope.async(IO) {
                cloud.getCar(brand, id)
            }
            _result.postValue(job.await())
        }
    }

}