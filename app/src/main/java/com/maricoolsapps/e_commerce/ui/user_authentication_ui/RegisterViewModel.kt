package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.repositories.CloudQueries
import com.maricoolsapps.e_commerce.data.repositories.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
   @Inject constructor(val profileChanges: ProfileChanges, val cloudQueries: CloudQueries): ViewModel() {

       fun createNewUser(user: User): LiveData<Resource<String>>{
          return profileChanges.createNewUser(user)
       }

        suspend fun completeRegistration(user: String, carBuyerOrSeller: CarBuyerOrSeller){
                cloudQueries.changeProfile(user, carBuyerOrSeller)
        }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

    fun getUser(): LiveData<Resource<FirebaseUser>> {
        return profileChanges.getAuthState()
    }

}