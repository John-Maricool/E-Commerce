package com.maricoolsapps.e_commerce.ui.product_ui.change_email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel
@Inject constructor(val profileChanges: ProfileChanges, val defaultRepo: DefaultRepository) :
    ViewModel() {

    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> get() = _result

    fun changeEmail(email: String, user: User) {
        viewModelScope.launch(IO) {
            profileChanges.changeEmail(email, user) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }
}