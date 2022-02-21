package com.maricoolsapps.e_commerce.ui.product_ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.model.Report
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject constructor(
    val auth: FirebaseAuth,
    val cloud: CloudQueries,
    val defaultRepo: DefaultRepository
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    val userId = auth.currentUser?.uid

    fun report(report: Report, product: Product) {
        viewModelScope.launch {
            cloud.reportProduct(report, product) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }
}