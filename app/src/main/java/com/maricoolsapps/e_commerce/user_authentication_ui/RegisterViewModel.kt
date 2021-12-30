package com.maricoolsapps.e_commerce.user_authentication_ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maricoolsapps.e_commerce.firebase.ProfileChanges
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
   @Inject constructor(private val profileChanges: ProfileChanges): ViewModel() {

       fun createNewUser(user: User): LiveData<Resource<String>>{
          return profileChanges.createNewUser(user)
       }

      fun changeProfileNameAndPhoto(uri: Uri, name: String): LiveData<Resource<String>>{
         return profileChanges.changeProfilePhotoAndName(uri, name)
      }
}