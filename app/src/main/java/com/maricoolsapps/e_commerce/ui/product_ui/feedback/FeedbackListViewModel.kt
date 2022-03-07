package com.maricoolsapps.e_commerce.ui.product_ui.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.FeedbackWithUser
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackListViewModel
@Inject constructor(val defaultRepo: DefaultRepository, val cloud: CloudQueries) : ViewModel() {

    private var _done = MutableLiveData<List<FeedbackWithUser>?>()
    val done: LiveData<List<FeedbackWithUser>?> get() = _done

    fun getFeedbackWithUser(brand: String, id: String){
        viewModelScope.launch {
            cloud.getFeedbacksWithUserForCar(brand, id) {
                _done.postValue(it.data)
                defaultRepo.onResult(it)
            }
        }
    }

}