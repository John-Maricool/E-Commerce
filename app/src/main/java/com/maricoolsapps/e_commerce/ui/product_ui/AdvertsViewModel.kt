package com.maricoolsapps.e_commerce.ui.product_ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maricoolsapps.e_commerce.data.repositories.CloudQueries
import com.maricoolsapps.e_commerce.data.repositories.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvertsViewModel
@Inject constructor(val profileChanges: ProfileChanges,
                    val cloudQueries: CloudQueries
): ViewModel(){

                        val user = profileChanges.auth.currentUser!!.uid

    fun getCarsFromSeller(brand: String): LiveData<Resource<List<ProductModel>>> {
        return cloudQueries.getCarsFromSeller(user, brand)
    }

}