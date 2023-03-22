package com.maricoolsapps.e_commerce.ui.product_ui.followers

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel
@Inject constructor(val cloudQueries: CloudQueries, val defaultRepo: DefaultRepository) :
    ViewModel() {

    private val _result = MutableLiveData<List<CarBuyerOrSeller>?>()
    val result: LiveData<List<CarBuyerOrSeller>?> = _result

    fun getFollowers(id: String) {
        viewModelScope.launch {
            cloudQueries.getFollowers(id) {
                _result.postValue(it.data)
                defaultRepo.onResult(it)
            }
        }
    }

    fun getFollowing(id: String) {
        viewModelScope.launch {
            cloudQueries.getFollowing(id) {
                _result.postValue(it.data)
                defaultRepo.onResult(it)
            }
        }
    }
}