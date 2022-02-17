package com.maricoolsapps.e_commerce.ui.product_ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.repositories.ProfileChanges
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel
@Inject constructor(val profileChanges: ProfileChanges): ViewModel(){

    suspend fun changeEmail(user: FirebaseUser, email: String, password: String, newEmail: String): Resource<String> {
        return profileChanges.changeEmail(user, email, password, newEmail)
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

    fun getUser(): LiveData<Resource<FirebaseUser>> {
        return profileChanges.getAuthState()
    }
}