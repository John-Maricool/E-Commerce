package com.maricoolsapps.e_commerce.ui.product_ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilteredListViewModel
@Inject constructor(val defaultRepo: DefaultRepository, val cloud: CloudQueries) : ViewModel() {

    private val _result = MutableLiveData<List<ProductModel>?>()
    val result: LiveData<List<ProductModel>?> = _result

    fun showAdverts(
        brand: String, type: String, location: String, condition: String,
        lowestPrice: Long, highestPrice: Long
    ) {
        viewModelScope.launch(IO) {
            cloud.getFilteredCars(brand, type, location, condition, lowestPrice, highestPrice) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }
}
