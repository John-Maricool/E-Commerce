package com.maricoolsapps.e_commerce.ui.product_ui

import android.app.Activity
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.repositories.CloudQueries
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellViewModel
@Inject constructor(val cloud: CloudQueries) : ViewModel() {

    private val _result = MutableLiveData<List<String>>()
    val result: LiveData<List<String>> get() = _result

    private val _addResult = MutableLiveData<Resource<String>>()
    val addResult: LiveData<Resource<String>> get() = _addResult

    fun getAllImages(images: List<Uri>) {
        viewModelScope.launch(Main) {
            _result.value = cloud.getAllImageDownloadUri(images)
        }
    }

     fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..25)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun addCarToDb(product: Product, userIdOrName: String) {
        viewModelScope.launch(Main) {
            _addResult.value = cloud.addCarToDb(product, userIdOrName)
        }
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

    fun showToast(activity: Activity, it: String){
        Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
    }
}
