package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.repositories.ProfileChanges
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    var profileChanges: ProfileChanges,
    var defaultRepo: DefaultRepository
) : ViewModel() {

    fun loginUser(user: User) {
        viewModelScope.launch {
            profileChanges.signInUser(user) {
                defaultRepo.onResult(it)
            }
        }
    }

    fun retrievePassword(email: String) {
        viewModelScope.launch {
            profileChanges.resetPassword(email) {

            }
        }
    }
}