package com.maricoolsapps.e_commerce.user_authentication_ui

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.firebase.ProfileChanges
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
   @Inject constructor( val profileChanges: ProfileChanges, val cloudQueries: CloudQueries): ViewModel() {

       fun createNewUser(user: User): LiveData<Resource<String>>{
          return profileChanges.createNewUser(user)
       }

        fun completeRegistration(carBuyerOrSeller: CarBuyerOrSeller): Job {
            val auth = FirebaseAuth.getInstance().uid
           return viewModelScope.launch {
                cloudQueries.changeProfile(auth.toString(), carBuyerOrSeller)
            }
        }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

}