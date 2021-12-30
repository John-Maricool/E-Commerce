package com.maricoolsapps.e_commerce.user_authentication_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maricoolsapps.e_commerce.firebase.ProfileChanges
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(private val profileChanges: ProfileChanges):ViewModel() {

    fun loginUser(user: User): LiveData<Resource<String>> {
       return profileChanges.signInUser(user)
    }

    fun retrievePassword(email: String): LiveData<Resource<String>>{
        return profileChanges.resetPassword(email)
    }
}