package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.validateEmail
import com.maricoolsapps.e_commerce.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    var profileChanges: ProfileChanges,
    var defaultRepo: DefaultRepository
) : ViewModel() {

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean> get() = _done

    @Bindable
    val email = MutableLiveData<String>()
    @Bindable
    val password = MutableLiveData<String>()

    private fun loginUser() {
        val user = User(email.value!!, password.value!!)
        viewModelScope.launch {
            profileChanges.signInUser(user) {
                defaultRepo.onResult(it)
                when (it.status) {
                    Status.SUCCESS -> {
                        _done.postValue(true)
                    }
                    Status.ERROR -> {
                        _done.postValue(false)
                    }
                    Status.LOADING -> {
                        _done.postValue(false)
                    }
                }
            }
        }
    }

    private fun retrievePassword(email: String) {
        viewModelScope.launch {
            profileChanges.resetPassword(email) {
                defaultRepo.onResult(it)
            }
        }
    }

    fun userLogin() {
        if (!validateEmail(email.value.toString())) {
            defaultRepo._resultData.value = "Error, Please check your Email Address"
            return
        }

        if (!validatePassword(password.value.toString())) {
            defaultRepo._resultData.value = "Error, Please check your password"
            return
        }
        loginUser()
    }

    fun forgotPassword() {
        val userEmail = email.value
        if (!validateEmail(userEmail.toString())) {
            defaultRepo._resultData.value = "Error, Please check your Email"
            return
        }
        retrievePassword(userEmail.toString())
    }
}