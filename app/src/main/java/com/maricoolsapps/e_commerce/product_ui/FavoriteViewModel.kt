package com.maricoolsapps.e_commerce.product_ui

import android.util.Log
import androidx.lifecycle.*
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
import com.maricoolsapps.e_commerce.repos.FavRepository
import com.maricoolsapps.e_commerce.repos.ProductDetailRepo
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject constructor(val repo: FavRepository) : ViewModel() {

    private val _result = MutableLiveData<Resource<List<ProductModel>?>>()
    val result: LiveData<Resource<List<ProductModel>?>> get() = _result

    private val products = mutableListOf<ProductModel>()

    init {
        viewModelScope.launch(Main) {
            val job = viewModelScope.async {
                repo.getAllFavCarsFromDb()
            }.await()

            job.forEach { product ->
                val prod = repo.getCar(product)
                when(prod.status){
                    Status.SUCCESS -> {
                        products.add(MapperImpl.mapToCache(prod.data!!))
                    }
                    Status.ERROR -> TODO()
                    Status.LOADING -> TODO()
                }
            }
            _result.value = Resource.success(products)
        }
    }
}