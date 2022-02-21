package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.data.repositories.RegisterRepository
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject constructor(
    private val repo: RegisterRepository,
    private val profileChanges: ProfileChanges,
    val defaultRepo: DefaultRepository):
    ViewModel() {

    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> get() = _result

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean> get() = _done

    fun createNewUser(user: User){
        viewModelScope.launch (IO){
            repo.createNewUser(user){
                defaultRepo.onResult(it)
                when(it.status){
                    Status.SUCCESS -> {_result.postValue(true)}
                    Status.ERROR -> {_result.postValue(false)}
                    Status.LOADING -> {_result.postValue(false)}
                }
            }
        }
    }

    fun gotoMedia(): Intent {
        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        return intent
    }

     fun completeRegistration(user: String, carBuyerOrSeller: CarBuyerOrSeller) {
         viewModelScope.launch(IO) {
             repo.changeProfile(user, carBuyerOrSeller){
                 defaultRepo.onResult(it)
                 when(it.status){
                     Status.SUCCESS -> {_done.postValue(true)}
                     Status.ERROR -> {_done.postValue(false)}
                     Status.LOADING -> {_done.postValue(false)}
                 }
             }
         }
    }

    fun getUser(): LiveData<FirebaseUser> {
        return profileChanges.getAuthState()
    }

}