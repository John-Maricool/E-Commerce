package com.maricoolsapps.e_commerce.product_ui

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.model.CarSellerProfile
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel
    @Inject constructor(val cloud: CloudQueries):ViewModel() {

    fun sellerProfile(userId: String): LiveData<Resource<CarBuyerOrSeller>> {
        return cloud.getSeller(userId)
    }

    fun getFollowers(userId: String): LiveData<Resource<Int>> {
        return cloud.getNumberOfFollowers(userId)
    }

    fun getFollowing(userId: String): LiveData<Resource<Int>> {
           return cloud.getNumberOfFollowing(userId)
    }

    fun getCarsFromSeller(userId: String, brand: String): LiveData<Resource<List<Product>>> {
            return cloud.getCarsFromSeller(userId, brand)
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

    fun followUser(userIdOrName: String, userToFollow: String) = viewModelScope.launch(Main) {
        cloud.followUser(userIdOrName, userToFollow)
    }

    fun unfollowUser(userIdOrName: String, userToUnFollow: String) = viewModelScope.launch(Main){
        cloud.unfollowUser(userIdOrName, userToUnFollow)
    }

    fun isUserFollowed(userId: String, userToFollow: String): LiveData<Resource<Boolean>>{
        return cloud.isUserFollowed(userId, userToFollow)
    }
}
