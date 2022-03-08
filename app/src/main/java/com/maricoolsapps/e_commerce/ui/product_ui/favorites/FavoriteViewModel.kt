package com.maricoolsapps.e_commerce.ui.product_ui.favorites

import androidx.lifecycle.*
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
/*

@HiltViewModel
class FavoriteViewModel
@Inject constructor(val repo: FavRepository, val defaultRepo: DefaultRepository) : ViewModel() {

    private val _result = MutableLiveData<List<ProductModel>?>()
    val result: LiveData<List<ProductModel>?> get() = _result

    init {
        viewModelScope.launch(IO) {
            repo.getAllFavoriteCars {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }

}*/
