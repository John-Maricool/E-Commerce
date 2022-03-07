package com.maricoolsapps.e_commerce.ui.product_ui.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.Feedback
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedBackViewModel
@Inject constructor(
    val auth: ProfileChanges,
    val defaultRepo: DefaultRepository,
    val cloud: CloudQueries
) : ViewModel() {

    val userId = auth.getUserUid()

    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> get() = _result

    fun addToDb(brand: String, feedback: Feedback) {
        viewModelScope.launch {
            cloud.addFeedback(brand, feedback) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }
}