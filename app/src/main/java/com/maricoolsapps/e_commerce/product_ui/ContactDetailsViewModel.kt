package com.maricoolsapps.e_commerce.product_ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.firebase.ProfileChanges
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel
@Inject constructor(var cloud: CloudQueries, var profileChanges: ProfileChanges): ViewModel(){

    fun getSeller(): LiveData<Resource<CarBuyerOrSeller>> {
        return cloud.getSeller(profileChanges.uid.toString())
    }

    fun changeProfile(carBuyerOrSeller: CarBuyerOrSeller): Job {
       return viewModelScope.launch (IO){
            cloud.changeProfile(profileChanges.uid.toString(), carBuyerOrSeller)
        }
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

}