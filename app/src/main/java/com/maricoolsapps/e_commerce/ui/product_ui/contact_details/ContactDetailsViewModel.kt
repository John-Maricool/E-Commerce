package com.maricoolsapps.e_commerce.ui.product_ui.contact_details

import androidx.lifecycle.*
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.data.repositories.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel
@Inject constructor(
    var cloud: CloudQueries,
    var auth: ProfileChanges,
    var defaultRepo: DefaultRepository,
    var repo: RegisterRepository
) : ViewModel() {

    val userID = auth.getUserUid()
    val email = auth.getEmail()
    private val _result = MutableLiveData<CarBuyerOrSeller?>()
    val result: LiveData<CarBuyerOrSeller?> get() = _result

    private val _changedProfile = MutableLiveData<String?>()
    val changedProfile: LiveData<String?> get() = _changedProfile


    init {
        viewModelScope.launch {
            cloud.getSeller(userID) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }

     fun changeProfile(carBuyerOrSeller: CarBuyerOrSeller) {
         viewModelScope.launch {
             repo.changeProfile(userID, carBuyerOrSeller){
                 defaultRepo.onResult(it)
                 _changedProfile.postValue(it.data)
             }
         }
    }
}