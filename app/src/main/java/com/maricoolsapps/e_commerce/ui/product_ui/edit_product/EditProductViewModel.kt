package com.maricoolsapps.e_commerce.ui.product_ui.edit_product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel
@Inject constructor(val cloud: CloudQueries, val defaultRepo: DefaultRepository) : ViewModel() {

    private val _result = MutableLiveData<Product?>()
    val result: LiveData<Product?> get() = _result

    private val _updated = MutableLiveData<String?>()
    val updated: LiveData<String?> get() = _updated

    fun getProduct(id: String, brand: String) {
        viewModelScope.launch {
            cloud.getCar(brand, id) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }

    fun updateCar(car: Product) {
        viewModelScope.launch {
            cloud.updateCar(car) {
                defaultRepo.onResult(it)
                _updated.postValue(it.data)
            }
        }
    }
}