package com.maricoolsapps.e_commerce.ui.product_ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.repositories.CloudQueries
import com.maricoolsapps.e_commerce.data.repositories.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel
@Inject constructor(var cloud: CloudQueries, var profileChanges: ProfileChanges): ViewModel(){

    private val _result = MutableLiveData<Resource<CarBuyerOrSeller>>()
    val result: LiveData<Resource<CarBuyerOrSeller>> get() = _result

    init {
        viewModelScope.launch {
            val job = viewModelScope.async {
                cloud.getSeller(profileChanges.auth.uid.toString())
            }
            _result.postValue(job.await())
        }
    }

    suspend fun changeProfile(carBuyerOrSeller: CarBuyerOrSeller){
        carBuyerOrSeller.id = profileChanges.auth.uid.toString()
            cloud.changeProfile(profileChanges.auth.uid.toString(), carBuyerOrSeller)
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }
}