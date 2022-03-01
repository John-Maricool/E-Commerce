package com.maricoolsapps.e_commerce.ui.product_ui.sell

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.StorageChanges
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellViewModel
@Inject constructor(
    val cloud: CloudQueries,
    val storage: StorageChanges,
    val defaultRepo: DefaultRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    val userId = auth.currentUser?.uid.toString()
    private val _result = MutableLiveData<List<String>?>()
    val result: LiveData<List<String>?> get() = _result

    private val _addResult = MutableLiveData<String?>()
    val addResult: LiveData<String?> get() = _addResult

    fun getAllImages(images: List<Uri>) {
        viewModelScope.launch(Main) {
            storage.getAllImageDownloadUri(images){
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
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
            cloud.addCarToDb(product, userIdOrName) {
                defaultRepo.onResult(it)
                _addResult.postValue(it.data)
            }
        }
    }

    fun goToIntent(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        return intent
    }
}

