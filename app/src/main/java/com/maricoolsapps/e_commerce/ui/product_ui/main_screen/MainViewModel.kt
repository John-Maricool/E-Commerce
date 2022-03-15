package com.maricoolsapps.e_commerce.ui.product_ui.main_screen

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.model.UserStatus
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.SharedPrefs
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(val auth: ProfileChanges,
                    val cloud: CloudQueries,
                    val prefs: SharedPreferences,
                    val defaultRepo: DefaultRepository) : ViewModel() {

    private var _result = MutableLiveData<List<ProductModel>?>()
    var result: LiveData<List<ProductModel>?> = _result

    //val searchFieldTextLiveData = MutableLiveData<String>()

  /*  fun observeSearchChange(){
        _result = Transformations.switchMap(searchFieldTextLiveData) {
            filterCar(it)
        } as MutableLiveData<List<ProductModel>?>
    }*/
  val userId = auth.getUserUid()

    init {
        syncUser(userId)
    }

    private fun syncUser(userId: String) {
        viewModelScope.launch(IO) {
            cloud.getSeller(userId){
                when(it.status){
                    Status.SUCCESS -> {
                        val person = it.data
                        val editor = prefs.edit()
                        editor.putString(SharedPrefs.USER_NAME, person?.name)
                        editor.putString(SharedPrefs.USER_EMAIL, person?.email)
                        editor.putString(SharedPrefs.USER_PIC, person?.image)
                        editor.apply()
                    }
                    else -> return@getSeller
                }
            }
        }
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

/*    private fun filterCar(model: String): MutableLiveData<List<ProductModel>?> {
        val newResult = _result.value?.filter {
            it.type.lowercase().contains(model.lowercase())
        }
        _result.value = newResult
        return _result
    }*/

    fun TabLayout.getAllCarBrands(){
        val brands = resources.getStringArray(R.array.brands).drop(1)
        brands.forEach {
            val oneTab: TabLayout.Tab = this.newTab()
            oneTab.text = it
            this.addTab(oneTab)
        }
        getCarsFromBrand(brands[0])
    }

    fun toggleUserOnline(online: Boolean){
        viewModelScope.launch {
            cloud.changeUserStatus(auth.getUserUid(), UserStatus(online = online))
        }
    }
}