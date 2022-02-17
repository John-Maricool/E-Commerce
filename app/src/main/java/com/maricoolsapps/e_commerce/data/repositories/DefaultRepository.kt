package com.maricoolsapps.e_commerce.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status

abstract class DefaultRepository {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> get() = _dataLoading

    private val _resultData = MutableLiveData<String>()
    val resultData: LiveData<String> get() = _resultData

     fun <T> onResult(b: Resource<T>){
        when(b.status){
            Status.SUCCESS -> {
                _dataLoading.postValue(false)
               //_resultData.postValue(b.data.toString())
            }
            Status.ERROR -> {
                _dataLoading.postValue(false)
                _resultData.postValue(b.message.toString())
            }
            Status.LOADING -> {
                _dataLoading.postValue(true)
            }
        }
    }
}