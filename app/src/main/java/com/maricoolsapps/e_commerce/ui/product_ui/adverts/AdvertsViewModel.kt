package com.maricoolsapps.e_commerce.ui.product_ui.adverts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvertsViewModel
@Inject constructor(
    val auth: FirebaseAuth,
    val cloudQueries: CloudQueries,
    val defaultRepo: DefaultRepository
) : ViewModel() {

    val user = auth.currentUser!!.uid

    private val _result = MutableLiveData<List<ProductModel>>()
    val result: LiveData<List<ProductModel>> get() = _result


    fun getCarsFromSeller(brand: String){
        viewModelScope.launch (IO){
            cloudQueries.getCarsFromSeller(user, brand){
                defaultRepo.onResult(it)
                when(it.status){
                    Status.SUCCESS -> {_result.postValue(it.data)}
                }
            }
        }
    }

}