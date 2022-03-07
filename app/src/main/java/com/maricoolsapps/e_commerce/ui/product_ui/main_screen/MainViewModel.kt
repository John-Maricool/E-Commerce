package com.maricoolsapps.e_commerce.ui.product_ui.main_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(val auth: ProfileChanges, val cloud: CloudQueries, val defaultRepo: DefaultRepository) : ViewModel() {

    private val _result = MutableLiveData<List<ProductModel>?>()
    val result: LiveData<List<ProductModel>?> = _result

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

    fun TabLayout.getAllCarBrands(){
        val brands = resources.getStringArray(R.array.brands).drop(1)
        brands.forEach {
            val oneTab: TabLayout.Tab = this.newTab()
            oneTab.text = it
            this.addTab(oneTab)
        }
        getCarsFromBrand(brands[0])
    }

    fun toggleUserOnline(status: UserStatus){
        viewModelScope.launch {
            cloud.changeUserStatus(auth.getUserUid(), status)
        }
    }
}