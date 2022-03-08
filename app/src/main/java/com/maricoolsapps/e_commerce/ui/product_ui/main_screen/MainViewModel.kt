package com.maricoolsapps.e_commerce.ui.product_ui.main_screen

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.model.UserStatus
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    @ApplicationContext val app: Application,
    val auth: ProfileChanges,
    val cloud: CloudQueries,
    val defaultRepo: DefaultRepository
) : AndroidViewModel(app) {

    private val _result = MutableLiveData<List<ProductModel>?>()
    val result: LiveData<List<ProductModel>?> = _result
    val item = MutableLiveData<String>()
    val brands = app.resources.getStringArray(R.array.brands).drop(1).toList()
    lateinit var listener: TabLayout.OnTabSelectedListener

    init {
        getCarsFromBrand(brands[0])
    }


    fun getCarsFromBrand(brand: String) {
        viewModelScope.launch {
            cloud.getCarsFromBrand(brand) {
                defaultRepo.onResult(it)
                when (it.status) {
                    Status.SUCCESS -> _result.postValue(it.data)
                    else -> _result.postValue(null)
                }
            }
        }
    }

    /*fun TabLayout.getAllCarBrands() {
        val brands = resources.getStringArray(R.array.brands).drop(1)
        brands.forEach {
            val oneTab: TabLayout.Tab = this.newTab()
            oneTab.text = it
            this.addTab(oneTab)
        }
        getCarsFromBrand(brands[0])
    }*/

    fun getCarsFromBrandFromRetry(){
        getCarsFromBrand(item.value!!)
    }

    fun toggleUserOnline(status: UserStatus) {
        viewModelScope.launch {
            cloud.changeUserStatus(auth.getUserUid(), status)
        }
    }
}